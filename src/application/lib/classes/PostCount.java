package application.lib.classes;

import java.io.*;
import java.util.Scanner;

public class PostCount {
    private static final String FILE_DIRECTORY = "E:\\JAVA\\FX\\publicator\\src\\application\\logs\\postCount.txt";
    public static int count = 1;

    public int getPostCount() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(FILE_DIRECTORY));
        if (br.readLine() != null) {
            FileReader file = new FileReader(FILE_DIRECTORY);
            Scanner sc = new Scanner(file);
            count = sc.nextInt();
        }
        return count;
    }

    public void setPostCount(int count) throws IOException {
        PrintWriter pw = new PrintWriter(new File(FILE_DIRECTORY));
        pw.print(String.valueOf(count));
        pw.close();
    }
}
