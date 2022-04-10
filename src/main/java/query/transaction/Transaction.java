package query.transaction;

import java.util.ArrayList;

public class Transaction {

    public static boolean autoCommit = true;
    private ArrayList<String> transactionQueryList = new ArrayList<>();

    public void updateAutoCommitState(String query) {

        autoCommit = false;
        // Remove below
//        String[] queryArray = {"non transaction query", "start transaction;", "do this", "do that", "just do it!", "commit;", "non transaction query 2"};
//        for (String query : queryArray) {
            if (query.equalsIgnoreCase("start transaction;") || query.equalsIgnoreCase("start transaction")) {
                autoCommit = false;
            } else if ((query.equalsIgnoreCase("commit;") || query.equalsIgnoreCase("commit")) && !autoCommit) {
                commitTransaction();
                autoCommit = true;
            }

            if (autoCommit) {
                if (!(query.equalsIgnoreCase("commit;") || query.equalsIgnoreCase("commit")))
                    System.out.println(query);
            } else {
                feedTransactionArray(query);
            }
//        }

    }


    private void feedTransactionArray(String query) {
        transactionQueryList.add(query);
    }

    private void commitTransaction() {
        for (String transactionQuery : transactionQueryList)
            System.out.println("T: " + transactionQuery);
        transactionQueryList = new ArrayList<>();
        TransactionExecution transactionExecution =new TransactionExecution();
        transactionExecution.replaceCurrentDatabaseWithTemp("TODO");
        autoCommit = true;
    }


}
