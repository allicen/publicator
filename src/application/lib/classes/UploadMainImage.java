package application.lib.classes;

import application.Main;
import application.lib.MainController;
import application.lib.classes.ValidateFile;
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
    private static String fileName = "";
    public static boolean mainImgUploaded = false;
    private static final int MAX_WIDTH_IMG = 1000;
    private static final int MAX_WIDTH_PREVIEW = 200;
    private static BufferedImage resizeImage;
    private static InputStream stream = null;
    private static int width;
    private static int height;
    public static ImageView imageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}


    @FXML
    public void selectMainImages() {
        boolean isValidateFile = false;
        JFileChooser window = new JFileChooser();
        int returnValue = window.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                File file = window.getSelectedFile();
                fileName = file.getName();
                isValidateFile = ValidateFile.validateImg(file);
                if (isValidateFile) {
                    Logs.addLogs("Выбран файл для главной картинки " + file.getName());
                    String formatName = getFileExtension(file);
                    BufferedImage image = ImageIO.read(file);
                    resizeImg(image);
                    File newImgFile = new File(Images.PATH_DIRECTORY_IMG + fileName);
                    ImageIO.write(resizeImage, formatName, newImgFile);
                    Main.postImage = fileName;
                    mainImgUploaded = true;
                    printImg();
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException ex) {
                            try {
                                Logs.addLogs("Ошибка с закрытием потока загрузки главного изображения: " + ex.getMessage());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    Logs.addLogs("Попытка выбрать файл " + file.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
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
        width = (image.getWidth() > MAX_WIDTH_IMG) ? 1000 : image.getWidth();
        height = (width >= MAX_WIDTH_IMG) ? width * image.getHeight() / image.getWidth() : image.getHeight();
        try {
            stream = new FileInputStream(Images.PATH_DIRECTORY_IMG + fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        resizeImage = Images.resizeImage(image, width, height, image.getType());
    }

    private static void printImg() throws FileNotFoundException {
        Image printImg = new Image(new FileInputStream(Images.PATH_DIRECTORY_IMG + fileName));
        imageView = new ImageView(printImg);
        imageView.setFitWidth(MAX_WIDTH_PREVIEW);
        imageView.setFitHeight(height * MAX_WIDTH_PREVIEW / width);
    }
}
