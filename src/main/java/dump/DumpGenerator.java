package dump;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import QueryContainer.UseDatabaseQueryProc;
import parser.QueryParserExecutor;
import parser.exception.InvalidQueryException;
import query.container.CreateSchema;
import query.manager.SchemaHandler;
import query.response.Response;
import query.response.ResponseType;
import reverseEngineering.ReverseEngineering;
import utils.UtilsConstant;
import DiskHandler.DistributedManager;
import utils.UtilsMetadata;

public class DumpGenerator {

    String databasePath = UtilsConstant.DATABASE_ROOT_FOLDER;
    String prefixMetadata = UtilsConstant.PREFIX_LOCAL_METADATA;
    String prefixTable = UtilsConstant.PREFIX_TABLE;
    String dumpPath = UtilsConstant.DUMP_ROOT_FOLDER;
    ReverseEngineering reverseEngineering = new ReverseEngineering();
    String[] sortedTables;

    String databaseName;
    private QueryParserExecutor queryParserExecutor;

    private UseDatabaseQueryProc useDatabaseQueryProc;

    public DumpGenerator() {
        this.queryParserExecutor = new QueryParserExecutor();
    }

    String takeInput() {
        Scanner sc = new Scanner(System.in);

        System.out.println("Please select database:");
        String query = sc.nextLine();
        return query;
    }

    public void createBothDump() throws IOException {

        String query = takeInput();

        if (query == null) {
            System.out.println("Database is not selected");
        } else {

            try {
                this.queryParserExecutor.processQuery(query);
                this.useDatabaseQueryProc=this.queryParserExecutor.getUseDatabaseQueryProc();
                CreateSchema createSchema = new CreateSchema(useDatabaseQueryProc.getDbName());
                Response response = SchemaHandler.checkSchemaQuery(createSchema);
                if (response.getResponseType().toString().equals(ResponseType.SUCCESS.toString())) {
                    this.databaseName = useDatabaseQueryProc.getDbName();
                }
                printResponse(response.getResponseType().toString(), response.getDescription());
            } catch (InvalidQueryException e) {
                System.out.println(e.getErrorMsg());
            }

            String out = "";
            out = out + createStructureDump(databaseName);
            out = out + createDataDump(databaseName);
            System.out.println(out);
            DistributedManager.writeFile(databaseName, dumpPath + "/dump.sql", "dump.sql", out);
        }
    }

    public String createDataDump(String databaseName) throws IOException {

        HashMap<String, List<String>> tables = UtilsMetadata.fetchDBData(databaseName, prefixTable);
        HashMap<String, List<String>> localMetaData = UtilsMetadata.fetchDBData(databaseName, prefixMetadata);
        // sortedTables = reverseEngineering.getRankOrder(databaseName);
        String out = "";
        for (String tableName : sortedTables) {
            out = out + "INSERT INTO " + tableName + " ( ";
            List<String> records = localMetaData.get(tableName);
            List<String> valueRecords = tables.get(tableName);

            for (int i = 0; i < records.size() - 1; i++) {
                String[] cols = records.get(i).split("\\|");
                out = out + " " + cols[0] + ", ";
            }

            out = out + records.get(records.size() - 1).split("\\|")[0] + " )";
            out = out + " VALUES ( ";

            for (int i = 0; i < valueRecords.size() - 1; i++) {
                String[] cols = valueRecords.get(i).split("\\|");
                for (int j = 0; j < cols.length - 1; j++) {
                    out = out + " " + cols[j] + ", ";
                }
                out = out + " " + cols[cols.length - 1];

            }
            out = out + " )\n";

        }
        return out;

    }

    public String createStructureDump(String databaseName) throws IOException {
        HashMap<String, List<String>> localMetaData = UtilsMetadata.fetchDBData(databaseName, prefixMetadata);
        sortedTables = reverseEngineering.getRankOrder(databaseName);
        String out = "CREATE DATABASE " + databaseName + ";\n";
        out = out + "USE " + databaseName+";\n";
        for (String tableName : sortedTables) {
            out = out + "CREATE TABLE " + tableName + " ( ";
            List<String> attributes = localMetaData.get(tableName);
            out = out + createScriput(attributes);
        }
        return out;

    }

    public String createScriput(List<String> attributes) throws IOException {
        String primaryKey = "";
        String forigenkey = "";
        String out = "";
        for (String attribute : attributes) {
            String[] columns = attribute.split("\\|", -1);
            for (int i = 0; i < columns.length; i++) {
                out = out + " " + columns[0] + " " + columns[1] + " ";
                if (columns[2].equals("true")) {
                    out = out + "NOT NULL, ";
                }
                if (!columns[3].equals("")) {
                    primaryKey = "PRIMARY KEY (" + columns[0] + ")";
                }
                if (!columns[4].equals("")) {
                    forigenkey = "FOREIGN KEY (" + columns[6] + ") REFERENCES " + columns[5] + "(" + columns[6] + ")";
                }
            }

        }

        if (!primaryKey.equals("")) {
            out = out + ", " + primaryKey;
        }
        if (!forigenkey.equals("")) {
            out = out + ", " + forigenkey;
        }
        out = out + " );\n";

        return out;

    }

    private void printResponse(String status, String desc) {
        System.out.println(status + ":" + desc);
    }


}