package application.controller.handlers;

import application.controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.TimerTask;

public class Logs extends Controller {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            infoLogs.setText(getLogFileSize());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Запись логов
    public static void addLogs(String text) throws IOException {
        String filePath = "E:\\JAVA\\FX\\publicator\\src\\application\\logs\\logs.txt";
        Date date = new Date();
        String today = String.format("%tF %tT", date, date);
        text = today + "\n" + text + "\n\n";
        try {
            Files.write(Paths.get(filePath), text.getBytes(), StandardOpenOption.APPEND);
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    @FXML
    private void getLogs() throws IOException {

        StringBuilder content = new StringBuilder();

        FileReader file = new FileReader("E:\\JAVA\\FX\\publicator\\src\\application\\logs\\logs.txt");
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()){
            content.append(sc.nextLine()).append("<br>");
        }
        WebEngine webEngine = allLogs.getEngine();
        webEngine.loadContent(String.valueOf(content), "text/html");
        infoLogs.setText(getLogFileSize());

        logActionResult.setVisible(true);
        logActionResult.setText("Логи получены!");
        timer.schedule( // Таймер для скрытия уведомлений
                new TimerTask() {
                    @Override
                    public void run() {
                        logActionResult.setVisible(false);
                    }
                }, 500);
    }

    @FXML
    private void deleteLogs() throws IOException {
        PrintWriter pw = new PrintWriter(new File("E:\\JAVA\\FX\\publicator\\src\\application\\logs\\logs.txt"));
        pw.print("");
        pw.close();
        getLogs();
        infoLogs.setText(getLogFileSize());

        logActionResult.setText("Логи удалены!");
        timer.schedule( // Таймер для скрытия уведомлений
                new TimerTask() {
                    @Override
                    public void run() {
                        logActionResult.setVisible(false);
                    }
                }, 500);
    }

    @FXML
    private void downloadLogs() throws IOException {
        URL fileLogUrl = new URL("https://github.com/allicen/Java-10000/blob/master/alibaba/README.md");
        ReadableByteChannel rbc = Channels.newChannel(fileLogUrl.openStream());
        FileOutputStream fos = new FileOutputStream("logs.txt");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();

        logActionResult.setVisible(true);
        logActionResult.setText("Файл скачан!");
        timer.schedule( // Таймер для скрытия уведомлений
                new TimerTask() {
                    @Override
                    public void run() {
                        logActionResult.setVisible(false);
                    }
                }, 500);
    }

    @FXML
    private static String getLogFileSize() throws IOException {
        File logFile = new File("E:\\JAVA\\FX\\publicator\\src\\application\\logs\\logs.txt");
        String size = "";
        if(logFile.exists()){
            size = String.valueOf(logFile.length());
        }else {
            addLogs("Запрос к несуществующему файлу " + logFile + ".");
            return null;
        }
        return "Размер файла логов: " + size + " bytes";
    }



}
