package application.lib.controllers;

import application.lib.MainController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Logs extends MainController {
    private static final String LOGS_PATH = "E:\\JAVA\\FX\\publicator\\src\\application\\logs\\logs.txt";

    // Логи
    public Label infoLogs;
    public WebView allLogs;
    public Label logActionResult;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            infoLogs.setText(getLogFileSize());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Запись логов
    public void addLogs(String text) throws IOException {
        Date date = new Date();
        String today = String.format("%tF %tT", date, date);
        text = today + "\n" + text + "\n\n";
        try {
            Files.write(Paths.get(LOGS_PATH), text.getBytes(), StandardOpenOption.APPEND);
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    @FXML
    private void getLogs() throws IOException {

        StringBuilder content = new StringBuilder();

        FileReader file = new FileReader(LOGS_PATH);
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
        PrintWriter pw = new PrintWriter(new File(LOGS_PATH));
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
    private String getLogFileSize() throws IOException {
        File logFile = new File(LOGS_PATH);
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
