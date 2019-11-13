package application.lib.controllers;

import application.lib.MainController;
import application.lib.classes.JarFilePath;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Logs extends MainController {
    private static JarFilePath filePath = new JarFilePath();

    private static final String LOGS_PATH = filePath.getFilePath("files/logs/logs.txt");

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
            Files.write(Paths.get(LOGS_PATH), text.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    @FXML
    private void getLogs() throws IOException {

        StringBuilder content = new StringBuilder();

        InputStreamReader file = new InputStreamReader(new FileInputStream(LOGS_PATH), StandardCharsets.UTF_8);
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
