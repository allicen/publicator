package application.lib.classes;

import java.io.File;
import java.util.ArrayDeque;
import java.util.StringTokenizer;

public class ValidateFile {
    public static boolean validateFile(File file){
        ArrayDeque<String> queue = new ArrayDeque<>();
        StringTokenizer st = new StringTokenizer(file.getName(), ".");
        while (st.hasMoreTokens()){
            queue.addLast(st.nextToken());
        }
        return queue.getLast().equals("docx");
    }
}
