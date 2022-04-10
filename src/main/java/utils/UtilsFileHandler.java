package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.jcraft.jsch.Buffer;

public class UtilsFileHandler {

    public static void writeToFile(String filepath, String content) throws IOException {
        FileWriter fileWriter = new FileWriter(new File(filepath));
        fileWriter.append(content);
        fileWriter.close();
    }

    public static List<String> readFile(String filepath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filepath));
        String line = br.readLine();
        List<String> content = new ArrayList<>();
        while (line!=null) {
            content.add(line);
            line = br.readLine();
        }
        return content;
    }
}
