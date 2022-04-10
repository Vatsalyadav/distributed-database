package localmetadata;


import query.container.CreateQuery;
import query.response.Response;
import query.response.ResponseType;
import utils.UtilsConstant;
import query.container.*;
import query.container.CreateQuery;
import query.container.CreateSchema;
import query.container.InsertQuery;
import query.container.SelectQuery;
import query.response.Response;
import query.response.ResponseType;
import query.transaction.Transaction;
import utils.UtilsCondition;
import utils.UtilsConstant;
import utils.UtilsFileHandler;
import utils.UtilsMetadata;

import javax.swing.text.Utilities;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class LocalMetdataHandler {

    public static Response createTableMetadata(CreateQuery createQuery, String path) throws FileNotFoundException, UnsupportedEncodingException {

        String filePath = path + UtilsConstant.PREFIX_LOCAL_METADATA + createQuery.getTableName() + ".txt";
        PrintWriter writer = new PrintWriter(filePath, "UTF-8");

        for (int i = 0; i < createQuery.getColumns().size(); i++) {
            String line = createQuery.getColumns().get(i) + UtilsConstant.SEPERATOR +
                    createQuery.getColumnsDataType().get(i) + UtilsConstant.SEPERATOR +
                    createQuery.getColumnsNotNullStatus().get(i);

            line += UtilsConstant.SEPERATOR;

            if (createQuery.getColumns().get(i).equals(createQuery.getPrimaryKey())) {
                line += "PK";
            }

            line += UtilsConstant.SEPERATOR;

            if (createQuery.getColumns().get(i).equals(createQuery.getForeignKey())) {
                if (UtilsMetadata.fkRefExists(path + UtilsConstant.PREFIX_LOCAL_METADATA + createQuery.getForeignKeyRefTable() + ".txt", createQuery.getForeignKeyRefCol())) {
                    line += "FK" +
                            UtilsConstant.SEPERATOR + createQuery.getForeignKeyRefTable() +
                            UtilsConstant.SEPERATOR + createQuery.getForeignKeyRefCol();
                } else {
                    File file = new File(filePath);
                    file.delete();
                    return new Response(ResponseType.FAILED, "Reference for foreign key " + createQuery.getForeignKeyRefTable() + "." + createQuery.getForeignKeyRefCol() + " doesn't exists");
                }
            } else {
                line += UtilsConstant.SEPERATOR +
                        UtilsConstant.SEPERATOR;
            }

            writer.println(line);

        }
        writer.close();
        return new Response(ResponseType.SUCCESS, "Query OK, 0 rows affected");
    }


    public static Response insertRows(InsertQuery insertQuery, String path) {

        String filePath = path + UtilsConstant.PREFIX_TABLE + insertQuery.getTableName() + ".txt";
        String filePathMeta = path + UtilsConstant.PREFIX_LOCAL_METADATA + insertQuery.getTableName() + ".txt";
        Writer fileOutput = null;
        try {
            fileOutput = new BufferedWriter(new FileWriter(filePath, true));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String primaryKey = UtilsMetadata.getPrimarykey(filePathMeta);

        if (!primaryKey.isEmpty()) {
            String primaryKeyVal = "";
            for (int i = 0; i < insertQuery.getColumns().size(); i++) {
                if (primaryKey.equals(insertQuery.getColumns().get(i))) {
                    primaryKeyVal = (String) insertQuery.getValues().get(i);
                }
            }

            if (UtilsMetadata.primaryKeyViolation(filePathMeta, filePath, primaryKeyVal))
                return new Response(ResponseType.FAILED, "Row with same primary key value already exists");
        }

        String foreignKey = UtilsMetadata.getForeignkey(filePathMeta);
        String foreignKeyRef = UtilsMetadata.getForeignKeyReference(filePathMeta);
        if (!foreignKeyRef.isEmpty())       //foreign key exists if true
        {
            String array[] = foreignKeyRef.split("[.]");
            String refTable = array[0];
            String refCol = array[1];

            String refMetaPath = path + UtilsConstant.PREFIX_LOCAL_METADATA + refTable + ".txt";
            String refTablePath = path + UtilsConstant.PREFIX_TABLE + refTable + ".txt";

            int index = UtilsMetadata.getIndexOfColumnInTable(refMetaPath, refCol);

            String foreignKeyVal = "";
            for (int i = 0; i < insertQuery.getColumns().size(); i++) {
                if (foreignKey.equals(insertQuery.getColumns().get(i))) {
                    foreignKeyVal = (String) insertQuery.getValues().get(i);
                }
            }

            if (UtilsMetadata.foreignKeyViolation(refTablePath, foreignKeyVal, index))
                return new Response(ResponseType.FAILED, "Cannot add or update a child row: a foreign key constraint fails");
        }


        Scanner in = null;
        List orderCol = new ArrayList<>();

        try {       //create order
            in = new Scanner(new FileReader(filePathMeta));
            while (in.hasNext()) {
                String line = in.next();
                String array[] = line.split("[|]");
                orderCol.add(array[0]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String newLine = "";
        for (int i = 0; i < orderCol.size(); i++) {
            String col = (String) orderCol.get(i);

            String value = searchInInsert(col, insertQuery.getColumns(), insertQuery.getValues());

            newLine += value + UtilsConstant.SEPERATOR;
        }

        newLine = newLine.substring(0, newLine.length() - 1);

        try {
            fileOutput.append(newLine + "\n");
            fileOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Response(ResponseType.SUCCESS, "Inserted 1 row");
    }

    private static String searchInInsert(String input, List<String> cols, List<String> values) {
        for (int i = 0; i < cols.size(); i++) {
            if (cols.get(i).equals(input)) {
                return values.get(i);
            }
        }

        return "";
    }

    public static Response createSchemaMetadata(CreateSchema schemaHandler, String path) {
        new File(path + "/" + schemaHandler.getDatabase()).mkdirs();
        return new Response(ResponseType.SUCCESS, "Database created");
    }


    public static Response checkSchemaQuery(CreateSchema schemaHandler, String path) {
        File f = new File(path + "/" + schemaHandler.getDatabase());
        if (f.exists() && f.isDirectory()) {
            return new Response(ResponseType.SUCCESS, "Database exists");
        }

        return new Response(ResponseType.FAILED, "Database doesn't exist");
    }

    public static Response executeSelectQuery(SelectQuery selectQuery, String path) {

        String fileTablePath = path + UtilsConstant.PREFIX_TABLE + selectQuery.getTableName() + ".txt";
        String fileMetaPath = path + UtilsConstant.PREFIX_LOCAL_METADATA + selectQuery.getTableName() + ".txt";

        List<Integer> indexOfColumns = new ArrayList<>();

        String result = "";

        int indexOfLHS = -1;

        if(!selectQuery.getColumnInWhere().isEmpty())
            indexOfLHS = UtilsMetadata.getIndexOfColumnInTable(fileMetaPath , selectQuery.getColumnInWhere());

        for(Object col : selectQuery.getColumns())
        {
            result += (String) col + UtilsConstant.SEPERATOR;
            indexOfColumns.add(UtilsMetadata.getIndexOfColumnInTable(fileMetaPath , (String) col));
        }

        result = result.substring(0,result.length()-1);
        result += "\n";

        try {
            Scanner in = new Scanner(new FileReader(fileTablePath));
            while(in.hasNext()) {
                String line = in.next();
                String elements[] = line.split("[|]");

                for(int i = 0;i < indexOfColumns.size() ; i++)
                {
                    if(indexOfLHS == -1 || UtilsCondition.evaluateCondition(selectQuery.getWhereCond(), elements[indexOfLHS] , selectQuery.getFactor()))
                        result +=  elements[indexOfColumns.get(i)] + UtilsConstant.SEPERATOR;
                }

                result = result.substring(0,result.length()-1);
                result += "\n";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return new Response(ResponseType.SUCCESS,result);
    }

    public static Response executeDeleteQuery(DeleteQuery deleteQuery, String path) {

        String fileTablePath = path + UtilsConstant.PREFIX_TABLE + deleteQuery.getTableName() + ".txt";
        String fileMetaPath = path + UtilsConstant.PREFIX_LOCAL_METADATA + deleteQuery.getTableName() + ".txt";

        List<Integer> indexOfColumns = new ArrayList<>();

        int indexOfLHS = -1;

        String result = "";

        if(!deleteQuery.getColumnInWhere().isEmpty())
            indexOfLHS = UtilsMetadata.getIndexOfColumnInTable(fileMetaPath , deleteQuery.getColumnInWhere());


        try {
            Scanner in = new Scanner(new FileReader(fileTablePath));
            while(in.hasNext()) {
                String line = in.next();
                String elements[] = line.split("[|]");

                if(indexOfLHS != -1 &&
                        !UtilsCondition.evaluateCondition(deleteQuery.getWhereCond(), elements[indexOfLHS] , deleteQuery.getFactor()))
                {
                    result += line;
                    result += "\n";
                }
            }
            if(!result.isEmpty())
                result = result.substring(0,result.length()-1);

            UtilsFileHandler.writeToFile(fileTablePath, result);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            new Response(ResponseType.INTERNAL_ERROR, "System error");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            new Response(ResponseType.INTERNAL_ERROR, "System error");
        }

        return new Response(ResponseType.SUCCESS,"Record(s) deleted");
    }


    public static Response executeTransactionQuery(TransactionQuery transactionQuery, String path) {
        // TODO: Write your code here autocommit off and create temp folder
        UtilsConstant.DATABASE_ROOT_FOLDER = "database/temp_";
        Transaction.autoCommit = false;
        return new Response(ResponseType.SUCCESS, "Query OK "+UtilsConstant.SEPERATOR + " Transaction Successfully Started!");
    }

    public static Response executeCommitQuery(CommitQuery commitQuery, String path) {
        // TODO: Write your code here for autocommit on and creating temp folder deletion
        UtilsConstant.DATABASE_ROOT_FOLDER = "database/temp_"; // TODO: Won't work
        Transaction.autoCommit = false;
        return new Response(ResponseType.SUCCESS, "Query OK "+UtilsConstant.SEPERATOR + " Transaction Successfully Started!");
    }

}
