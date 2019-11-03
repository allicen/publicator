package application.parser.components;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Images {
    private static final int MAX_WIDTH_IMAGE = 1000; // Максимальная ширина картинки
    private static final String PATH_DIRECTORY_IMG = "E:\\JAVA\\FX\\publicator\\src\\application\\user_files\\tmp\\images\\"; // Директория, в которую сохраняется картинка

    private static BufferedImage resizeImage(BufferedImage originalImage, int width, int height, int type){
        BufferedImage resizeImage = new BufferedImage(width, height, type);
        Graphics2D graphics = resizeImage.createGraphics();
        graphics.drawImage(originalImage, 0, 0, width, height, null);
        graphics.dispose();
        return resizeImage;
    }

    public static ArrayList<String> getAllImages(XWPFDocument document) throws IOException { // ВАЖНО!!! Все картинки должны быть разными!!! Копии одной и той же картинки не поддерживаются
        ArrayList<String> getImages = new ArrayList<>();
        List<XWPFPictureData> piclist = document.getAllPictures(); // Картинки
        Iterator<XWPFPictureData> iterator = piclist.iterator();
        int i = 0;
        while(iterator.hasNext()){
            XWPFPictureData pic = iterator.next();
            byte[] bytepic = pic.getData();
            BufferedImage imag =  ImageIO.read(new ByteArrayInputStream(bytepic));
            String formatName = pic.suggestFileExtension();
            int width = (imag.getWidth() > MAX_WIDTH_IMAGE) ? MAX_WIDTH_IMAGE : imag.getWidth(); // Пересчет размеров
            int height = (width >= MAX_WIDTH_IMAGE) ? width * imag.getHeight() / imag.getWidth() : imag.getHeight();
            BufferedImage resizeImage = resizeImage(imag, width, height, imag.getType());
            ImageIO.write(resizeImage, formatName, new File(PATH_DIRECTORY_IMG + pic.getFileName()));
            getImages.add(PATH_DIRECTORY_IMG + pic.getFileName());
        }
        Collections.sort(getImages);
        return getImages;
    }
}
