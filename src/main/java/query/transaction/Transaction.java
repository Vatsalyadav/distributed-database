package query.transaction;

import java.util.ArrayList;

public class Transaction {

    public static boolean autoCommit = true;
    private static ArrayList<String> transactionQueryList = new ArrayList<>();

    public void setAutoCommitTrue() {
        autoCommit = true;
    }

    public void setAutoCommitFalse() {
        autoCommit = false;
    }

    public static void feedTransactionArray(String query) {
        transactionQueryList.add(query);
    }

    public static ArrayList<String> commitTransaction() {
//        for (String transactionQuery : transactionQueryList)
//            System.out.println("T: " + transactionQuery);
//        TransactionExecution transactionExecution =new TransactionExecution();
//        transactionExecution.replaceCurrentDatabaseWithTemp("TODO");
//        autoCommit = true;
        return transactionQueryList;
    }

    public static void refreshTransactionQueryList() {
        transactionQueryList = new ArrayList<>();
    }

}
