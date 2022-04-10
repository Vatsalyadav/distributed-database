package query.container;

/**
 * Contains the information on select query
 */
public class TransactionQuery {


    private String database;        //name of the schema

    public TransactionQuery() {
    }

    public TransactionQuery(String database) {
        this.database = database;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
