package application.lib.classes;

import java.io.*;
import java.util.Scanner;

public class PostCount {
    private static final String FILE_DIRECTORY = "../../logs/postCount.txt";
    public static int count = 1;

    public int getPostCount() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(getClass().getResource(FILE_DIRECTORY).getPath()));
        if (br.readLine() != null) {
            FileReader file = new FileReader(getClass().getResource(FILE_DIRECTORY).getPath());
            Scanner sc = new Scanner(file);
            count = sc.nextInt();
        }
        return count;
    }

    public void setPostCount(int count) throws IOException {
        PrintWriter pw = new PrintWriter(new File(getClass().getResource(FILE_DIRECTORY).getPath()));
        pw.print(String.valueOf(count));
        pw.close();
    }
}
