package application.lib.classes;

import application.lib.MainController;

import java.io.IOException;

public class SetImagesDirectory {
    public static final String URL_SITE = "http://erubleva.ru/";
    public static final String DIRECTORY = MainController.userSettings.get("imgDir").replaceAll("/www/", "");
    public static final String NEW_FOLDER = "post";
    public static int POST_COUNT;
    public static String DATE = GetDate.getDate();

    public static String setImagesUrlDirectory() throws IOException {
        POST_COUNT = PostCount.getPostCount();
        return URL_SITE + DIRECTORY + NEW_FOLDER + POST_COUNT + "-" + DATE + "/";
    }

    public static String setImagesDirectory() throws IOException {
        POST_COUNT = PostCount.getPostCount();
        return MainController.userSettings.get("imgDir") + SetImagesDirectory.NEW_FOLDER + POST_COUNT + "-" + DATE;
    }
}
