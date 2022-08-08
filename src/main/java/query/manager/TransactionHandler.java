package query.manager;

import localmetadata.LocalMetdataHandler;
import query.container.TransactionQuery;
import query.response.Response;
import utils.UtilsConstant;

public class TransactionHandler {

    public static Response executeTransactionQuery(TransactionQuery transactionQuery)
    {

        return LocalMetdataHandler.executeTransactionQuery(transactionQuery,UtilsConstant.DATABASE_ROOT_FOLDER + "/" + transactionQuery.getDatabase() + "/");

    }
}
