package application.lib.classes;

import java.io.*;
import java.util.Scanner;

public class PostCount {
    private static String fileDirectory = "E:\\JAVA\\FX\\publicator\\src\\application\\logs\\postCount.txt";
    public static int count = 1;

    public static int getPostCount() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileDirectory));
        if (br.readLine() != null) {
            FileReader file = new FileReader(fileDirectory);
            Scanner sc = new Scanner(file);
            count = sc.nextInt();
        }
        return count;
    }

    public static void setPostCount(int count) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(new File(fileDirectory));
        pw.print(String.valueOf(count));
        pw.close();
    }
}
