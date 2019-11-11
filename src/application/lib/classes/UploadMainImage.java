package application.lib.classes;

import application.Main;
import application.lib.MainController;
import application.lib.controllers.Logs;
import application.parser.components.Images;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class UploadMainImage extends MainController {
    public static String fileName = "";
    public static boolean mainImgUploaded = false;
    private static final int MAX_WIDTH_IMG = 1000;
    public static final int MAX_WIDTH_PREVIEW = 200;
    private static BufferedImage resizeImage;
    public static int width;
    public static int height;
    public static ImageView imageView;
    private static Logs logs = new Logs();

    @Override
    public void initialize(URL location, ResourceBundle resources) {}


    @FXML
    public void selectMainImages() throws IOException {
        boolean isValidateFile = false;
        JFileChooser window = new JFileChooser();
        int returnValue = window.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            DeleteImgFiles.deleteOldImgFolder(UploadImages.PATH_DIRECTORY);
            File file = window.getSelectedFile();
            fileName = file.getName();
            isValidateFile = ValidateFile.validateImg(file);
            if (isValidateFile) {
                logs.addLogs("Выбран файл для главной картинки " + file.getName());
                String formatName = getFileExtension(file);
                BufferedImage image = ImageIO.read(file);
                resizeImg(image);
                File newImgFile = new File(Images.PATH_DIRECTORY_IMG + fileName);
                ImageIO.write(resizeImage, formatName, newImgFile);
                Main.postImage = fileName;
                mainImgUploaded = true;
            }
        }
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

    private static void resizeImg(BufferedImage image){
        width = (image.getWidth() > MAX_WIDTH_IMG) ? MAX_WIDTH_IMG : image.getWidth();
        height = (width >= MAX_WIDTH_IMG) ? width * image.getHeight() / image.getWidth() : image.getHeight();
        resizeImage = Images.resizeImage(image, width, height, image.getType());
    }
}
