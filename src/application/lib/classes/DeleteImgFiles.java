package application.lib.classes;

import java.io.File;
import java.util.Objects;

public class DeleteImgFiles {
    public static void deleteAllFilesFolder(String path) {
        for (File myFile : Objects.requireNonNull(new File(path).listFiles())){
            if(!myFile.getName().equals(".gitkeep")){
                if (myFile.isFile()) myFile.delete();
            }
        }
    }
}
