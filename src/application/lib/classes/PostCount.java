package application.lib.classes;

import java.io.*;
import java.util.Scanner;

public class PostCount {
    private final String FILE_NAME = "files/logs/postCount.txt";
    private JarFilePath filePath = new JarFilePath();

    public static int count = 1;

    public int getPostCount() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath.getFilePath(FILE_NAME)));
        if (br.readLine() != null) {
            FileReader file = new FileReader(filePath.getFilePath(FILE_NAME));
            Scanner sc = new Scanner(file);
            count = sc.nextInt();
        }
        return count;
    }

    public void setPostCount(int count) throws IOException {
        PrintWriter pw = new PrintWriter(new File(filePath.getFilePath(FILE_NAME)));
        pw.print(String.valueOf(count));
        pw.close();
    }
}
