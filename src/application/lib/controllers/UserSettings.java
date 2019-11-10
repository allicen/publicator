package application.lib.controllers;

import application.lib.MainController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.*;

public class UserSettings extends MainController {

    // Проверка полей на заполненность
    public TextField ftphost;
    public StackPane ftphostnext;
    public TextField ftpuser;
    public StackPane ftpusernext;
    public PasswordField ftppassword;
    public StackPane ftppasswordnext;
    public TextField sshhost;
    public StackPane sshhostnext;
    public TextField sshuser;
    public StackPane sshusernext;
    public PasswordField sshpassword;
    public StackPane sshpasswordnext;
    public TextField sshport;
    public StackPane sshportnext;
    public TextField mysqlhost;
    public StackPane mysqlhostnext;
    public TextField mysqluser;
    public StackPane mysqlusernext;
    public PasswordField mysqlpassword;
    public StackPane mysqlpasswordnext;
    public TextField mysqldb;
    public StackPane mysqldbnext;
    public TextField imgDir;
    public StackPane imgDirnext;
    public StackPane saveSuccess;

    // Настройки импорта
    public CheckBox requiredH1;
    public CheckBox requiredUrl;
    public CheckBox requiredAnons;
    public CheckBox requiredTitle;
    public CheckBox requiredDescription;
    public CheckBox requiredImage;
    public CheckBox requiredHtml;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Map <TextField, String> userSettings = null;

        try {
            userSettings = getCustomSettings();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert userSettings != null;
        for(TextField key : userSettings.keySet()){
            key.setText(userSettings.get(key));
        }

        Map <CheckBox, String> userCheckSettings = null;
        try {
            userCheckSettings = getCustomerCheckedImportSettings();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert userCheckSettings != null;
        for(CheckBox key : userCheckSettings.keySet()){
            key.setSelected(Boolean.parseBoolean(userCheckSettings.get(key)));
        }
        try {
            checkSettings(new StringBuilder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<TextField> addFields(){
        return new ArrayList<>(Arrays.asList(ftphost, ftpuser, ftppassword, sshhost, sshuser, sshpassword, sshport, mysqlhost, mysqluser, mysqlpassword, mysqldb, imgDir));
    }

    private ArrayList<StackPane> addMessage(){
        return new ArrayList<>(Arrays.asList(ftphostnext, ftpusernext, ftppasswordnext, sshhostnext, sshusernext, sshpasswordnext, sshportnext, mysqlhostnext, mysqlusernext, mysqlpasswordnext, mysqldbnext, imgDirnext));
    }

    private ArrayList<CheckBox> addCheckedSettings(){
        return new ArrayList<>(Arrays.asList(requiredH1, requiredUrl, requiredAnons, requiredTitle, requiredDescription, requiredImage, requiredHtml));
    }

    private Map<String, String> changeCustomSettings(){
        return MainController.userSettings;
    }


    private Map<TextField, String> getCustomSettings() throws IOException{ // Выгрузка настроек подключений из файла
        ArrayList<TextField> fields = addFields();
        Map<TextField, String> userSettings = new HashMap<>();
        FileReader file = new FileReader("E:\\JAVA\\FX\\publicator\\src\\application\\user_settings\\settings.txt");
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()){
            ArrayList<String> tokensArr = new ArrayList<>();
            String str = sc.nextLine();
            StringTokenizer st = new StringTokenizer(str, "=");
            while (st.hasMoreTokens()){
                tokensArr.add(st.nextToken());
            }

            for(TextField elem : fields){
                if(elem.getId().equals(tokensArr.get(0))){
                    if(tokensArr.size() == 2){
                        userSettings.put(elem, tokensArr.get(1));
                        MainController.userSettings.put(String.valueOf(elem.getId()), tokensArr.get(1));
                    }else {
                        userSettings.put(elem, "");
                    }
                }
            }
        }
        return userSettings;
    }

    private void saveCustomSettings(String newSettings) throws IOException{ // Запись настроек подключения
        PrintWriter pw = new PrintWriter(new File("E:\\JAVA\\FX\\publicator\\src\\application\\user_settings\\settings.txt"));
        pw.print(newSettings);
        pw.close();
        Logs.addLogs("Обновлены настройки");
    }

    private Map<CheckBox, String> getCustomerCheckedImportSettings() throws IOException { // Получить настройки импорта
        ArrayList<CheckBox> checksSettings = addCheckedSettings();
        Map<CheckBox, String> userCheck = new HashMap<>();
        FileReader file = new FileReader("E:\\JAVA\\FX\\publicator\\src\\application\\user_settings\\selectedOptions.txt");
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()){
            ArrayList<String> tokensArr = new ArrayList<>();
            String str = sc.nextLine();
            StringTokenizer st = new StringTokenizer(str, "=");
            while (st.hasMoreTokens()){
                tokensArr.add(st.nextToken());
            }
            for(CheckBox elem : checksSettings){
                if(elem.getId().equals(tokensArr.get(0))){
                    if(tokensArr.size() == 2){
                        userCheck.put(elem, tokensArr.get(1));
                        MainController.userImportSettings.put(String.valueOf(elem.getId()), Boolean.valueOf(tokensArr.get(1)));
                    }else {
                        userCheck.put(elem, "false");
                    }
                }
            }
        }
        return userCheck;
    }

    private void saveCustomerCheckedImportSettings() throws IOException{ // Записать настройки импорта
        StringBuilder newSettingsOptions = new StringBuilder();
        ArrayList<CheckBox> listCheck = addCheckedSettings();

        for (CheckBox aListCheck : listCheck) {
            boolean newSelect = aListCheck.isSelected();
            aListCheck.setSelected(newSelect);
            newSettingsOptions.append(aListCheck.getId()).append("=").append(newSelect).append("\n");
            MainController.userImportSettings.put(aListCheck.getId(), newSelect);
        }

        PrintWriter pw = new PrintWriter(new File("E:\\JAVA\\FX\\publicator\\src\\application\\user_settings\\selectedOptions.txt"));
        pw.print(newSettingsOptions);
        pw.close();
        Logs.addLogs("Изменены настройки импорта");
    }

    private void checkSettings(StringBuilder newSettings) throws IOException { // Проверка полей настроек на заполненность
        ArrayList<TextField> listFields = addFields();
        ArrayList<StackPane> listMessage = addMessage();

        for(int i = 0; i< listFields.size(); i++){
            String newText = listFields.get(i).getText();
            listFields.get(i).setText(newText);

            newSettings.append(listFields.get(i).getId()).append("=").append(newText).append("\n");
            MainController.userSettings.put(listFields.get(i).getId(), newText);

            if(listFields.get(i).getText().trim().isEmpty()){
                try {
                    Parent error = FXMLLoader.load(getClass().getResource("../../gui/components/marks/error.fxml"));
                    listMessage.get(i).getChildren().clear();
                    listMessage.get(i).getChildren().addAll(error);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    Parent success = FXMLLoader.load(getClass().getResource("../../gui/components/marks/success.fxml"));
                    listMessage.get(i).getChildren().clear();
                    listMessage.get(i).getChildren().addAll(success);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    @FXML
    public void saveSettings() throws IOException { // Проверка заполнения настроек
        StringBuilder newSettings = new StringBuilder();
        checkSettings(newSettings);
        saveCustomSettings(String.valueOf(newSettings));
        saveCustomerCheckedImportSettings();
        saveSuccess.setVisible(true);
        timer.schedule( // Таймер для скрытия уведомлений
                new TimerTask() {
                    @Override
                    public void run() {
                        saveSuccess.setVisible(false);
                    }
                }, 500);
    }

}


