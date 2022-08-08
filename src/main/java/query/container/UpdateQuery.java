package query.container;

import java.util.List;

/**
 * Contains the information on select query
 */
public class UpdateQuery {

    private List columns;           //Keep it empty for all the columns

    private List values;           //Keep it empty for all the columns

    private String tableName;       //name of the table

    private String database;        //name of the schema

    private String columnInWhere;   //L.H.S of the where clause

    private WhereCond whereCond;    //Condition of the where clause

    private String factor;          //R.H.S in where clause


    public UpdateQuery() {
    }

    public UpdateQuery(List columns, List values, String tableName, String database, String columnInWhere, WhereCond whereCond, String factor) {
        this.columns = columns;
        this.values = values;
        this.tableName = tableName;
        this.database = database;
        this.columnInWhere = columnInWhere;
        this.whereCond = whereCond;
        this.factor = factor;
    }

    public List getColumns() {
        return columns;
    }

    public void setColumns(List columns) {
        this.columns = columns;
    }

    public List getValues() {
        return values;
    }

    public void setValues(List values) {
        this.values = values;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getColumnInWhere() {
        return columnInWhere;
    }

    public void setColumnInWhere(String columnInWhere) {
        this.columnInWhere = columnInWhere;
    }

    public WhereCond getWhereCond() {
        return whereCond;
    }

    public void setWhereCond(WhereCond whereCond) {
        this.whereCond = whereCond;
    }

    public String getFactor() {
        return factor;
    }

    public void setFactor(String factor) {
        this.factor = factor;
    }
}
