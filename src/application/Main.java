package application;

import application.controller.Controller;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class Main extends Application {
    public static String postH1 = "";
    public static String html = "";
    public static String postAnons = "";
    public static String postCategoryId = "";
    public static String postImage = "";
    public static String postDate = "";
    public static String postTitle = "";
    public static String postDescription = "";
    public static String postUrl = "";

    public static void main(String[] args){
        launch(args);
    }

    public void start(Stage stage) throws Exception{
        Parent content = FXMLLoader.load(getClass().getResource("gui/gui.fxml"));

        BorderPane root = new BorderPane();
        root.setCenter(content);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Публикатор");
        stage.getIcons().add(new Image(Main.class.getResourceAsStream( "images/icon.png" )));
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Controller.timer.cancel();
            }
        });
    }
}
