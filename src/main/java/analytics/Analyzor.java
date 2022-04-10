package analytics;

<<<<<<< HEAD
import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import DiskHandler.DistributedManager;
=======
>>>>>>> 51b1250861bf6a3d4cd0edfd0771bf88154b47fa
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utils.UtilsConstant;

public class Analyzor {
    String logPath = UtilsConstant.LOG_ROOT_FOLDER;
    String path = UtilsConstant.ANALYTIC_ROOT__FOLDER;

<<<<<<< HEAD

=======
>>>>>>> 51b1250861bf6a3d4cd0edfd0771bf88154b47fa
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
<<<<<<< HEAD
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
=======
    }
    public Set<String> getTables() throws FileNotFoundException, IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(logPath + "/queryLogs.json"));
        JSONObject logsJsonObject = (JSONObject) obj;
        JSONArray queryLogArray = (JSONArray) logsJsonObject.get("querys");
        Set<String> querySet = new HashSet<String>();
        for (Object ob : queryLogArray) {
            JSONObject jsOb = (JSONObject) ob;
            querySet.add((String) jsOb.get("table"));
        }
        return querySet;
    }

    public void reportDatasbe(String user, String vm) throws IOException, ParseException {
        Set<String> databases = getDatabases();
        FileWriter fileWriter = new FileWriter(path+"/analytics_Database.txt");
        for (String database : databases) {
            int numberOfQuerys = countNumberOfQuery(user, database);
            String result = "user " + user + " submitted " + numberOfQuerys + " queries for " + database + " running on "
                    + "Virtual Machine" + vm;
            fileWriter.append(result+"\n");
            System.out.println(result);
        }
        fileWriter.close();
    }

    public void reporTabel(String sqlType) throws FileNotFoundException, IOException, ParseException {
        Set<String> tables = getTables();
        FileWriter fileWriter = new FileWriter(path+"/analytics_Tables.txt");
        for (String table : tables) {
            int numberOfSuccessfulQuery = countNumberOfSuccessfulQuery(table, sqlType);
            String result = "Total " + numberOfSuccessfulQuery + " " + sqlType + " operations are performed on "
                    + table;
            fileWriter.append(result+"\n");
            System.out.println(result);
        }
        fileWriter.close();

    }

    private int countNumberOfSuccessfulQuery(String table, String sqlType)
            throws FileNotFoundException, IOException, ParseException {
>>>>>>> 51b1250861bf6a3d4cd0edfd0771bf88154b47fa
        int count = 0;
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(logPath + "/queryLogs.json"));
        JSONObject logsJsonObject = (JSONObject) obj;
        JSONArray queryArray = (JSONArray) logsJsonObject.get("querys");
        for (Object object : queryArray) {
            JSONObject ob = (JSONObject) object;
<<<<<<< HEAD
            if (ob.get("user").equals(user) && ob.get("database").equals(database)) {
=======
            boolean matchTable = ob.get("table").equals(table);
            boolean isSuccessful = ob.get("isSuccessful").toString().equals("true");
            boolean checkType = ob.get("type").equals(sqlType);
            if (matchTable && isSuccessful && checkType) {
>>>>>>> 51b1250861bf6a3d4cd0edfd0771bf88154b47fa
                count++;
            }
        }
        return count;
    }

<<<<<<< HEAD
=======
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

>>>>>>> 51b1250861bf6a3d4cd0edfd0771bf88154b47fa
}
