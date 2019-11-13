package application.lib.classes;

import java.io.File;
import java.util.Objects;

class DeleteFiles {
    static void deleteAllFilesFolder(String path) {
        for (File myFile : Objects.requireNonNull(new File(path).listFiles())){
            if(!myFile.getName().equals(".gitkeep")){
                if (myFile.isFile()) myFile.delete();
            }
        }
    }
    static void deleteOldImgFolder(String path) {
        for (File myFile : Objects.requireNonNull(new File(path).listFiles())){
            if(myFile.getName().equals(UploadMainImage.fileName)){
                if (myFile.isFile()) myFile.delete();
            }
        }
    }
    static void deleteHtmlFile(String path){
        for (File myFile : Objects.requireNonNull(new File(path).listFiles())){
            if(!myFile.getName().equals(".gitkeep")){
                if (myFile.isFile()) myFile.delete();
            }
        }
    }
}
