package parser;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import QueryContainer.*;
import loger.Loger;
import loger.LogsParameters;
import org.json.simple.parser.ParseException;
import parser.exception.InvalidQueryException;
import query.container.*;
import query.container.SelectQuery;
import query.manager.QueryHandler;
import query.manager.SchemaHandler;
import query.response.Response;
import query.response.ResponseType;
import utils.UtilsMetadata;

public class WriteQueries {
	public static String dbName = "";
	public static String query = "";
	QueryParserExecutor queryParserExecutor;
	public boolean dbCreated=false;
	private LogsParameters logsParameters;
	private Instant startTime;
	private String username="";

	public WriteQueries(String userName) {
    	 queryParserExecutor=new    QueryParserExecutor();
		 this.username=userName;
		System.out.println("username is "+userName);
    }
	int takeNoOfTables(String DbName) throws IOException {

		return UtilsMetadata.fetchDBData(DbName,"metadata_").size();

	}
	public List<String> takeTableName(String DbName) throws IOException {
		ArrayList<String> names = new ArrayList<>(UtilsMetadata.fetchDBData(DbName,"metadata_").keySet());
		 return names;
	}
	public ArrayList<Integer> takeNoOfRows(String DbName) throws IOException {
		ArrayList<Integer> sizeRow=new ArrayList<>();
		HashMap<String, List<String>> hm = UtilsMetadata.fetchDBData(DbName,"table_");
		for (Map.Entry<String, List<String>> entry : hm.entrySet()) {

			ArrayList<String>  value = new ArrayList<>();

			if(entry.getValue() != null){
				value.addAll(entry.getValue());

			}
			if(value.size() !=0){
				sizeRow.add(value.size());
			}

		}
		return sizeRow;

	}

	void takeInput() throws IOException {
		Scanner sc = new Scanner(System.in);

		System.out.println("Write the query()");
		query = sc.nextLine();
		this.startTime = Instant.now();
		try {
			if(dbName.equals("") && !query.toLowerCase().contains("use") && !query.toLowerCase().contains("create")) {
				System.out.println("Database is not selected");
			}
			else {
				boolean success=queryParserExecutor.processQuery(query);
				if(success) {

					setDataAndExecuteQuery(query);
					//if trans start, called commit,
					//end trans
				}
			}


		} catch (InvalidQueryException e) {
			System.out.println(e.getErrorMsg());
		} catch (ParseException e) {
			e.printStackTrace();
		}


	}

	private Response setDataAndExecuteQuery(String query) throws IOException, ParseException {
		String[] arrayStr={};
		int[] arrayInt={};

		Response response = null;
		if (this.queryParserExecutor.isCreDbQuery(query)) {

			CreateDatabaseProcessor createDatabaseProc = this.queryParserExecutor.getCreateDatabaseProc();
			CreateSchema createSchema = new CreateSchema(createDatabaseProc.getDbName().toLowerCase());
			response = SchemaHandler.executeSchemaCreateQuery(createSchema);
			printResponse(response.getResponseType().toString(), response.getDescription());
			Instant end = Instant.now();

			logsParameters=new LogsParameters("create",String.valueOf(Duration.between(this.startTime, end)),query,this.username,String.valueOf(this.startTime),"create", "","", "",this.dbName,"",0,takeNoOfTables(this.dbName),takeTableName(this.dbName).toArray(new String[0]),takeNoOfRows(this.dbName).stream().mapToInt(Integer::intValue).toArray(),response.getResponseType().toString(),response.getDescription());
			Loger log = new Loger();
			log.wirteLogs(logsParameters);

		}
		else if (query.toLowerCase().contains("create")) {
			CreateQueryProcessor createQueryProc = this.queryParserExecutor.getCreateQueryProcessor();
			ArrayList<String> clmnNull = new ArrayList<>();
			for(int kk=0; kk<=createQueryProc.getColumns().size();kk++){
				             clmnNull.add("false");
			}
			CreateQuery createQuery = new CreateQuery(createQueryProc.getColumns(), createQueryProc.getDatatype(), clmnNull,
					createQueryProc.getTableName().toLowerCase(), this.dbName, createQueryProc.getPrimaryKey(),
					createQueryProc.getForeginKey(), createQueryProc.getRefTable(), createQueryProc.getRefId());

			response = QueryHandler.executeQuery(createQuery, SqlType.CREATE);
			printResponse(response.getResponseType().toString(), response.getDescription());
			Instant end = Instant.now();
			logsParameters=new LogsParameters("create",String.valueOf(Duration.between(this.startTime, end)),query,this.username,String.valueOf(this.startTime),"create", "",createQueryProc.getColumns().toString(), "",this.dbName,createQueryProc.getTableName().toLowerCase(),1,takeNoOfTables(this.dbName),takeTableName(this.dbName).toArray(new String[0]),takeNoOfRows(this.dbName).stream().mapToInt(Integer::intValue).toArray(),response.getResponseType().toString(),response.getDescription());
			Loger log = new Loger();
			log.wirteLogs(logsParameters);

		}

		else if (query.toLowerCase().contains("select")) {
			SelectQueryProcessor selectQueryProc = this.queryParserExecutor.getSelectQueryProcessor();
			SelectQuery selectQuery = new SelectQuery(selectQueryProc.getColumns(), selectQueryProc.getTableName().toLowerCase(),
					this.dbName, selectQueryProc.getColumnInWhere(), selectQueryProc.getWhereCond(),
					selectQueryProc.getFactor(), selectQueryProc.isAllColumn());

			response = QueryHandler.executeQuery(selectQuery, SqlType.SELECT);
			printResponse(response.getResponseType().toString(), response.getDescription());
			Instant end = Instant.now();
			logsParameters=new LogsParameters("select",String.valueOf(Duration.between(this.startTime, end)),query,this.username,String.valueOf(this.startTime),"select", selectQueryProc.getWhereCond().toString(),selectQueryProc.getColumns().toString(), selectQueryProc.getFactor(),this.dbName,selectQueryProc.getTableName().toLowerCase(),0,takeNoOfTables(this.dbName),takeTableName(this.dbName).toArray(new String[0]),takeNoOfRows(this.dbName).stream().mapToInt(Integer::intValue).toArray(),response.getResponseType().toString(),response.getDescription());
			Loger log = new Loger();
			log.wirteLogs(logsParameters);

		}
		else if (query.toLowerCase().contains("insert")) {
			InsertQueryProcessor insertQueryProc = this.queryParserExecutor.getInsertQueryProcessor();
			InsertQuery insertQuery = new InsertQuery(insertQueryProc.getColumns(),insertQueryProc.getTableName().toLowerCase(),this.dbName,insertQueryProc.getValues());

			response = QueryHandler.executeQuery(insertQuery, SqlType.INSERT);
			printResponse(response.getResponseType().toString(), response.getDescription());
			Instant end = Instant.now();
			logsParameters=new LogsParameters("insert",String.valueOf(Duration.between(this.startTime, end)),query,this.username,String.valueOf(this.startTime),"insert", "",insertQueryProc.getColumns().toString(), insertQueryProc.getValues().toString(),this.dbName,insertQueryProc.getTableName().toLowerCase(),1,takeNoOfTables(this.dbName),takeTableName(this.dbName).toArray(new String[0]),takeNoOfRows(this.dbName).stream().mapToInt(Integer::intValue).toArray(),response.getResponseType().toString(),response.getDescription());
			Loger log = new Loger();
			log.wirteLogs(logsParameters);

		}
		else if (query.toLowerCase().contains("delete")) {
			DeleteQueryProcessor delQueryProc = this.queryParserExecutor.getDeleteQueryProcessor();

			DeleteQuery delQuery = new DeleteQuery(delQueryProc.getTableName().toLowerCase(),this.dbName,delQueryProc.getColumns(),WhereCond.EQUALS,delQueryProc.getValue());

			response = QueryHandler.executeQuery(delQuery, SqlType.DELETE);
			printResponse(response.getResponseType().toString(), response.getDescription());
			Instant end = Instant.now();
			logsParameters=new LogsParameters("delete",String.valueOf(Duration.between(this.startTime, end)),query,this.username,String.valueOf(this.startTime),"delete", WhereCond.EQUALS.toString(),delQueryProc.getColumns(),delQueryProc.getValue(),this.dbName,delQueryProc.getTableName().toLowerCase(),1,takeNoOfTables(this.dbName),takeTableName(this.dbName).toArray(new String[0]),takeNoOfRows(this.dbName).stream().mapToInt(Integer::intValue).toArray(),response.getResponseType().toString(),response.getDescription());
			Loger log = new Loger();
			log.wirteLogs(logsParameters);

		}
		else if (query.toLowerCase().contains("use")) {
			UseDatabaseQueryProc useDatabaseQueryProc = this.queryParserExecutor.getUseDatabaseQueryProc();
			CreateSchema createSchema = new CreateSchema(useDatabaseQueryProc.getDbName().toLowerCase());
			response = SchemaHandler.checkSchemaQuery(createSchema);
			if (response.getResponseType().toString().equals(ResponseType.SUCCESS.toString())) {
				this.dbName = useDatabaseQueryProc.getDbName().toLowerCase();
				this.dbCreated = true;
			}
			printResponse(response.getResponseType().toString(), response.getDescription());
			Instant end = Instant.now();
			logsParameters=new LogsParameters("use",String.valueOf(Duration.between(this.startTime, end)),query,this.username,String.valueOf(this.startTime),"", "","", "",this.dbName,"",0,takeNoOfTables(this.dbName),takeTableName(this.dbName).toArray(new String[0]),takeNoOfRows(this.dbName).stream().mapToInt(Integer::intValue).toArray(),response.getResponseType().toString(),response.getDescription());
			Loger log = new Loger();
			log.wirteLogs(logsParameters);

		}


		return response;
	}
	private void printResponse(String status, String desc) {
		System.out.println(status + ":" + desc);
	}
	public boolean manager() throws IOException {
		takeInput();

		return true;
	}

}
