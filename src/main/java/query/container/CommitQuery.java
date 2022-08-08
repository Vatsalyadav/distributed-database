package query.container;

/**
 * Contains the information on select query
 */
public class CommitQuery {


    private String database;        //name of the schema

    public CommitQuery() {
    }

    public CommitQuery(String database) {
        this.database = database;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
