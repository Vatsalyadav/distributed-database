package query.manager;

import localmetadata.LocalMetdataHandler;
import query.container.CommitQuery;
import query.response.Response;
import utils.UtilsConstant;

public class CommitHandler {

    public static Response executeCommitQuery(CommitQuery commitQuery)
    {
        // TODO:
        return LocalMetdataHandler.executeCommitQuery(commitQuery,UtilsConstant.DATABASE_ROOT_FOLDER + "/" + commitQuery.getDatabase() + "/");

    }
}
