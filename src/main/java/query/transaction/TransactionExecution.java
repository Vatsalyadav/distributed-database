package query.transaction;

import utils.UtilsConstant;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TransactionExecution {

    public void createTempDatabase(String database) {
        String databasePath = UtilsConstant.DATABASE_ROOT_FOLDER + "/" + database;
        String tempDatabasePath = UtilsConstant.DATABASE_ROOT_FOLDER + "/temp_" + database;

        try {
            Files.walk(Paths.get(databasePath))
                    .forEach(source -> {
                        Path destination = Paths.get(tempDatabasePath, source.toString()
                                .substring(databasePath.length()));
                        try {
                            Files.copy(source, destination);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void replaceCurrentDatabaseWithTemp(String database) {
        String databasePath = UtilsConstant.DATABASE_ROOT_FOLDER + "/" + database;
        String tempDatabasePath = UtilsConstant.DATABASE_ROOT_FOLDER + "/temp_" + database;
        File databasePathFile = new File(databasePath);
        deleteDirectory(databasePathFile);

        File destFile = new File(tempDatabasePath);
        destFile.renameTo(new File(databasePath));

    }

    private boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

}
