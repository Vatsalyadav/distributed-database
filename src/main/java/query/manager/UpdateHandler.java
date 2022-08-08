package query.manager;

import localmetadata.LocalMetdataHandler;
import query.container.SelectQuery;
import query.container.UpdateQuery;
import query.response.Response;
import utils.UtilsConstant;

public class UpdateHandler {

    public static Response executeUpdateQuery(UpdateQuery updateQuery)
    {
        return LocalMetdataHandler.executeUpdateQuery(updateQuery,UtilsConstant.DATABASE_ROOT_FOLDER + "/" + updateQuery.getDatabase() + "/");
    }
}
