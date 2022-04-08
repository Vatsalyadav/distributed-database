package parser;

import java.util.ArrayList;
import java.util.List;

import QueryContainer.CreateQueryProcessor;
import QueryContainer.DeleteQueryProcessor;
import QueryContainer.InsertQueryProcessor;
import QueryContainer.SelectQueryProcessor;
import parser.exception.InvalidQueryException;
import query.container.CreateQuery;

public class QueryParserExecutor {

	private QueryParser queryParser;

	private CreateQueryProcessor createQueryProcessor;

	private InsertQueryProcessor insertQueryProcessor;

	private DeleteQueryProcessor deleteQueryProcessor;
	
	private SelectQueryProcessor selectQueryProcessor;

	private CreateQuery createQuery;

	public QueryParserExecutor() {
		this.queryParser = new QueryParser();

	}

	public boolean processQuery(String query) throws InvalidQueryException {

		boolean isQueryProcessed = false;

		isQueryProcessed = this.queryParser.parseQuery(query);

		if (!isQueryProcessed) {
			throw new InvalidQueryException(this.queryParser.getErrorMessage());
		}

		if (query.toLowerCase().contains("create")) {
			this.createQueryProcessor = new CreateQueryProcessor();
			createQueryProcessor.parseCreateQuery(query);
			// create object of createQuery using createQueryProcessor
			// this.createQuery=new CreateQuery(createQueryProcessor.getColumns(),);
			System.out.println(createQueryProcessor.toString());
		}

		if (query.toLowerCase().contains("insert")) {
			this.insertQueryProcessor = new InsertQueryProcessor();
			insertQueryProcessor.parseInsertQuery(query);
			System.out.println(insertQueryProcessor.toString());
		}

		if (query.toLowerCase().contains("delete")) {
			this.deleteQueryProcessor = new DeleteQueryProcessor();
			deleteQueryProcessor.parseDeleteQuery(query);
			System.out.println(deleteQueryProcessor.toString());
		}
		
		
		if (query.toLowerCase().contains("select")) {
			this.selectQueryProcessor = new SelectQueryProcessor();
			selectQueryProcessor.parseSelectQuery(query);
			System.out.println(selectQueryProcessor.toString());
		}
		 

		return isQueryProcessed;
	}

	public CreateQueryProcessor getCreateQueryProcessor() {
		return createQueryProcessor;
	}

	public InsertQueryProcessor getInsertQueryProcessor() {
		return insertQueryProcessor;
	}

	public DeleteQueryProcessor getDeleteQueryProcessor() {
		return deleteQueryProcessor;
	}

	public SelectQueryProcessor getSelectQueryProcessor() {
		return selectQueryProcessor;
	}

}
