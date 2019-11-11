package application.lib.classes;

import application.lib.MainController;

import java.io.IOException;

public class SetImagesDirectory {
    private static final String URL_SITE = "http://erubleva.ru/";
    private static final String DIRECTORY = MainController.userSettings.get("imgDir").replaceAll("/www/", "");
    private static final String NEW_FOLDER = "post";
    private static int POST_COUNT;
    private static String DATE = GetDate.getDate();
    private static PostCount postCount = new PostCount();

    public static String setImagesUrlDirectory() throws IOException {
        POST_COUNT = postCount.getPostCount();
        return URL_SITE + DIRECTORY + NEW_FOLDER + POST_COUNT + "-" + DATE + "/";
    }

    static String setImagesDirectory() throws IOException {
        POST_COUNT = postCount.getPostCount();
        return MainController.userSettings.get("imgDir") + SetImagesDirectory.NEW_FOLDER + POST_COUNT + "-" + DATE;
    }
}
