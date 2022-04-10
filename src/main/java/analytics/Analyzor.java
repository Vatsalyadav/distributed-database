package analytics;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import DiskHandler.DistributedManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utils.UtilsConstant;

public class Analyzor {
    String logPath = UtilsConstant.LOG_ROOT_FOLDER;
    String path = UtilsConstant.ANALYTIC_ROOT__FOLDER;


    public Set<String> getDatabases() throws FileNotFoundException, IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(logPath + "/databaseLogs.json"));
        JSONObject logsJsonObject = (JSONObject) obj;
        JSONArray databaseLogArray = (JSONArray) logsJsonObject.get("databases");
        Set<String> databaseSet = new HashSet<String>();
        for (Object ob : databaseLogArray) {
            JSONObject jsOb = (JSONObject)ob;
            databaseSet.add((String)jsOb.get("databaseName"));
        }
        return databaseSet;
    }
    public Set<String> getTables() throws FileNotFoundException, IOException, ParseException {
        JSONParser parser = new JSONParser();
        List<String> log = DistributedManager.readFile("", logPath + "/queryLogs.json",
                "queryLogs.json");
        Object obj = parser.parse(log.get(0));
        JSONObject logsJsonObject = (JSONObject) obj;
        JSONArray queryLogArray = (JSONArray) logsJsonObject.get("querys");
        Set<String> querySet = new HashSet<String>();
        for (Object ob : queryLogArray) {
            JSONObject jsOb = (JSONObject) ob;
            querySet.add((String) jsOb.get("table"));
        }
        return querySet;}

    public void printAnalyse(String userName) throws IOException, ParseException {
        String vm= "";
        File file =new File("./instances/local.txt");
        BufferedReader buffReader=new BufferedReader(new FileReader(file));
        vm=buffReader.readLine();
        reportDatasbe(userName,vm);
        reporTabel("create");
    }

    public void reportDatasbe(String user, String vm) throws IOException, ParseException {
        Set<String> databases = getDatabases();
        String result = "";
        for (String database : databases) {
            int numberOfQuerys = countNumberOfQuery(user, database);
            result = result+"user " + user + " submitted " + numberOfQuerys + " queries for " + database + " running on "
                    + "Virtual Machine" + vm+"\n";

            System.out.print(result);
        }
        DistributedManager.writeFile("", path+"/analytics_Database.txt", "analytics_Database.txt", result);

    }

    public void reporTabel(String sqlType) throws FileNotFoundException, IOException, ParseException {
        Set<String> tables = getTables();
        String result="";

        for (String table : tables) {
            int numberOfSuccessfulQuery = countNumberOfSuccessfulQuery(table, sqlType);
            result=result+ "Total " + numberOfSuccessfulQuery + " " + sqlType + " operations are performed on "
                    + table+"\n";
            System.out.print(result);
        }
        System.out.println(path+"/analytics_Tables.txt");
        DistributedManager.writeFile("", path + "/analytics_Tables.txt", "analytics_Tables.txt", result);



    }

    private int countNumberOfSuccessfulQuery(String table, String sqlType)
            throws FileNotFoundException, IOException, ParseException {
        int count = 0;
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(logPath + "/queryLogs.json"));
        JSONObject logsJsonObject = (JSONObject) obj;
        JSONArray queryArray = (JSONArray) logsJsonObject.get("querys");
        for (Object object : queryArray) {
            JSONObject ob = (JSONObject) object;
            boolean matchTable = ob.get("table").equals(table);
            boolean isSuccessful = ob.get("isSuccessful").toString().equals("SUCCESS");
            boolean checkType = ob.get("type").equals(sqlType);
            if (matchTable && isSuccessful && checkType) {
                count++;
            }
        }
        return count;
    }

    public int countNumberOfQuery(String user, String database) throws IOException, ParseException {
        int count = 0;
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(logPath + "/queryLogs.json"));
        JSONObject logsJsonObject = (JSONObject) obj;
        JSONArray queryArray = (JSONArray) logsJsonObject.get("querys");
        for (Object object : queryArray) {
            JSONObject ob = (JSONObject) object;
            if (ob.get("user").equals(user) && ob.get("database").equals(database)) {
                count++;
            }
        }
        return count;
    }

}
