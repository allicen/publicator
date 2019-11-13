package application.lib.classes;

import application.Main;
import application.parser.MainParserClass;

public class Clear {
    public static void clear(){
        Main.postH1 = "";
        Main.html = "";
        Main.postAnons = "";
        Main.postCategoryId = "";
        Main.postImage = "";
        Main.postDate = "";
        Main.postTitle = "";
        Main.postDescription = "";
        MainParserClass.out = new StringBuilder("");
        MainParserClass.postName = "";
        DeleteFiles.deleteAllFilesFolder(UploadImages.PATH_DIRECTORY);
        DeleteFiles.deleteHtmlFile(MainParserClass.PATH_HTML_FILE);
    }
}
