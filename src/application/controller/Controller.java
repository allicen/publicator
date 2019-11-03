package application.controller;

import application.Main;
import application.parser.MainParserClass;
import com.jcraft.jsch.IO;
import com.jcraft.jsch.JSch;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import com.jcraft.jsch.Session;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import org.apache.poi.xwpf.usermodel.XWPFDocument;


import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.Date;
import java.util.Timer;

public class Controller implements Initializable {
    public static Timer timer = new Timer();

    // Соединение с MySQL
    private static int lport;
    private static String rhost;
    private static int rport;
    private static Session session = null;
    private static boolean assertsServerAndDbIsFill = true;

    private static String categoryId = "-1";
    private static boolean isValidateFile;

    // Запросы к БД
    private static String sql = null;
    private static boolean insertPost = false;
    private static boolean selectCategory = false;
    private static boolean insertCategory = false;
    private static Map<String, String> sqlSelectCategory;

    // Логи
    public Label infoLogs;
    public WebView allLogs;
    public Label logActionResult;

    @FXML
    public Label preview;
    public Label previewH1;
    public TextArea redactorhtml;
    public HTMLEditor redactorvisual;
    public TextField postH1;
    public TextField postUrl;
    public TextField postTitle;
    public TextArea postAnons;
    public TextArea postDescription;
    public VBox text;
    public TabPane tabs;
    public VBox settings;
    public VBox showLogs;
    public WebView about;
    public WebView manual;
    public WebView faq;
    public WebView feedback;
    public BorderPane main;
    public StackPane fileUpload;
    public StackPane saveSuccess;
    public Tab tabGoThen;
    public Tab tabPublication;
    public VBox readyInfo;
    public Button published;
    public ProgressIndicator load;
    public Label errors;
    public HBox categoryItems;
    public ToggleGroup category;
    public Label success;

    // Добавить категорию
    public HBox addCategoryForm;
    public TextField categoryName;
    public TextField categoryUrl;
    public TextField categoryTitle;
    public TextField categoryDescription;

    // Готовность к публикации
    public StackPane chechH1;
    public StackPane checkUrl;
    public StackPane checkAnons;
    public StackPane checkTitle;
    public StackPane checkDescription;
    public StackPane checkImage;
    public StackPane checkHtml;


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

    // Настройки импорта
    public CheckBox requiredH1;
    public CheckBox requiredUrl;
    public CheckBox requiredAnons;
    public CheckBox requiredTitle;
    public CheckBox requiredDescription;
    public CheckBox requiredImage;
    public CheckBox requiredHtml;

    private ArrayList<TextField> addFields(){
        return new ArrayList<>(Arrays.asList(ftphost, ftpuser, ftppassword, sshhost, sshuser, sshpassword, sshport, mysqlhost, mysqluser, mysqlpassword, mysqldb, imgDir));
    }

    private ArrayList<StackPane> addMessage(){
        return new ArrayList<>(Arrays.asList(ftphostnext, ftpusernext, ftppasswordnext, sshhostnext, sshusernext, sshpasswordnext, sshportnext, mysqlhostnext, mysqlusernext, mysqlpasswordnext, mysqldbnext, imgDirnext));
    }

    private ArrayList<CheckBox> addCheckedSettings(){
        return new ArrayList<>(Arrays.asList(requiredH1, requiredUrl, requiredAnons, requiredTitle, requiredDescription, requiredImage, requiredHtml));
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
        addLogs("Изменены пользовательские настройки");
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
                    }else {
                        userCheck.put(elem, "false");
                    }
                }
            }
        }
        addLogs("Получены пользовательские настройки");
        return userCheck;
    }

    private void saveCustomerCheckedImportSettings() throws IOException{ // Записать настройки импорта
        StringBuilder newSettingsOptions = new StringBuilder();
        ArrayList<CheckBox> listCheck = addCheckedSettings();

        for (CheckBox aListCheck : listCheck) {
            boolean newSelect = aListCheck.isSelected();
            aListCheck.setSelected(newSelect);
            newSettingsOptions.append(aListCheck.getId()).append("=").append(newSelect).append("\n");

        }

        PrintWriter pw = new PrintWriter(new File("E:\\JAVA\\FX\\publicator\\src\\application\\user_settings\\selectedOptions.txt"));
        pw.print(newSettingsOptions);
        pw.close();
        addLogs("Изменены настройки импорта");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Map <TextField, String> userSettings = getCustomSettings();
            for(TextField key : userSettings.keySet()){
                key.setText(userSettings.get(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Map <CheckBox, String> userCheckSettings = getCustomerCheckedImportSettings();
            for(CheckBox key : userCheckSettings.keySet()){
                key.setSelected(Boolean.parseBoolean(userCheckSettings.get(key)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        checkSettings(new StringBuilder());
        try {
            infoLogs.setText(getLogFileSize());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void checkSettings(StringBuilder newSettings){ // Проверка полей настроек на заполненность
        ArrayList<TextField> listFields = addFields();
        ArrayList<StackPane> listMessage = addMessage();

        for(int i = 0; i< listFields.size(); i++){
            String newText = listFields.get(i).getText();
            listFields.get(i).setText(newText);

            newSettings.append(listFields.get(i).getId()).append("=").append(newText).append("\n");

            if(listFields.get(i).getText().trim().isEmpty()){
                try {
                    Parent error = FXMLLoader.load(getClass().getResource("../gui/components/marks/error.fxml"));
                    listMessage.get(i).getChildren().clear();
                    listMessage.get(i).getChildren().addAll(error);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    Parent success = FXMLLoader.load(getClass().getResource("../gui/components/marks/success.fxml"));
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

    @FXML
    private void closePublishedTab(){
        published.setDisable(true);
        readyInfo.setVisible(false);
        errors.setText("");
        success.setText("");
    }



    private boolean validateFile(File file){
        ArrayDeque<String> queue = new ArrayDeque<>();
        StringTokenizer st = new StringTokenizer(file.getName(), ".");
        while (st.hasMoreTokens()){
            queue.addLast(st.nextToken());
        }
        return queue.getLast().equals("docx");
    }


    // Загрузка файла
    @FXML
    public void upload(){
        fileUpload.getChildren().clear();
        //closePublishedTab();
        JFileChooser window = new JFileChooser();
        int returnValue = window.showOpenDialog(null);
        if(returnValue == JFileChooser.APPROVE_OPTION){
            XWPFDocument document = null;
            try {
                File file = window.getSelectedFile();
                isValidateFile = validateFile(file);
                addLogs("Выбран файл " + file.getName());
                if(isValidateFile){
                    FileInputStream getFile = new FileInputStream(file);
                    document = new XWPFDocument(getFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            load.setVisible(true);
            if(isValidateFile){
                MainParserClass.main(document);
                previewH1.setText(Main.postH1);
                preview.setText(Main.html);
                redactorhtml.setText(Main.html);
                redactorvisual.setHtmlText(Main.html);
                postH1.setText(Main.postH1);
                postTitle.setText(Main.postH1);
                postUrl.setText(Main.postUrl);

                try {
                    assert document != null;
                    Parent success = FXMLLoader.load(getClass().getResource("../gui/components/marks/fileUpload.fxml"));
                    fileUpload.getChildren().clear();
                    fileUpload.getChildren().addAll(success);
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                Label uploadError = new Label();
                uploadError.setText("Выбран файл неверного формата! Выберите файл .docx");
                uploadError.setStyle("-fx-text-fill: red; -fx-font-size: 16");
                fileUpload.getChildren().add(uploadError);
            }
            load.setVisible(false);
        }
    }

    @FXML
    public void cancelUpload(){
        preview.setText("Нет данных *");
        redactorhtml.setText("");
        redactorvisual.setHtmlText("Нет данных *");
        previewH1.setText("Нет данных *");
        postH1.setText("");
        postUrl.setText("");
        postAnons.setText("");
        postTitle.setText("");
        postDescription.setText("");
        fileUpload.getChildren().clear();
        closePublishedTab();
    }



    // Переключение страниц
    private void navTexts(){
        text.setVisible(true);
        tabs.setVisible(false);
        settings.setVisible(false);
        about.setVisible(false);
        manual.setVisible(false);
        faq.setVisible(false);
        feedback.setVisible(false);
        showLogs.setVisible(false);
    }


    @FXML
    public void converter(){
        text.setVisible(false);
        tabs.setVisible(true);
        closePublishedTab();
    }

    @FXML
    public void settings(){
        navTexts();
        settings.setVisible(true);
       closePublishedTab();
    }

    @FXML
    private void showLogs(){
        navTexts();
        showLogs.setVisible(true);
        closePublishedTab();
    }



    @FXML
    public void about(){
        navTexts();
        about.setVisible(true);
        about.getEngine().load(getClass().getResource("../template_html/about.html").toExternalForm());
       closePublishedTab();
    }

    @FXML
    public void manual(){
        navTexts();
        manual.setVisible(true);
        manual.getEngine().load(getClass().getResource("../template_html/manual.html").toExternalForm());
       closePublishedTab();
    }

    @FXML
    public void faq(){
        navTexts();
        faq.setVisible(true);
        faq.getEngine().load(getClass().getResource("../template_html/faq.html").toExternalForm());
       closePublishedTab();
    }

    @FXML
    public void feedback(){
        navTexts();
        feedback.setVisible(true);
        feedback.getEngine().load(getClass().getResource("../template_html/contacts.html").toExternalForm());
       closePublishedTab();
    }

    @FXML
    public void goNext(){
        tabs.getSelectionModel().selectNext();
       closePublishedTab();
    }

    @FXML
    public void goPrev(){
        tabs.getSelectionModel().selectPrevious();
       closePublishedTab();
    }

    @FXML
    public void goThen(){
        tabs.getSelectionModel().select(tabGoThen);
        closePublishedTab();
    }

    @FXML
    public void toHtmlButton(){
        redactorhtml.setText(redactorvisual.getHtmlText());
        preview.setText(redactorvisual.getHtmlText());
       closePublishedTab();
    }

    private String getDate(){
        Date date = new Date();
        return String.format("%tF", date); //test
    }


    @FXML
    private void connectSSH(){
        // Соединение по SSH
        try
        {
            JSch jsch = new JSch();
            session = jsch.getSession(sshuser.getText(), sshhost.getText(), Integer.valueOf(sshport.getText()));
            lport = 4321;
            rhost = mysqlhost.getText();
            rport = 3306;
            session.setPassword(sshpassword.getText());
            session.setConfig("StrictHostKeyChecking", "no");
            System.out.println("Устанавливаю соединение...");
            addLogs("Устанавливаю соединение...");
            session.connect();
            int assinged_port=session.setPortForwardingL(lport, rhost, rport);
            System.out.println("localhost:"+assinged_port+" -> "+rhost+":"+rport);
            addLogs("localhost:"+assinged_port+" -> "+rhost+":"+rport);
        }
        catch(Exception e){
            System.err.print(e);
        }
    }

    private void connectDB(){
        try{
            connectSSH();
        } catch(Exception ex){
            ex.printStackTrace();
        }

        // Соединение с БД
        Connection con = null;
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://" + rhost +":" + lport + "/";
        String db = mysqldb.getText();
        String dbUser = mysqluser.getText();
        String dbPasswd = mysqlpassword.getText();

        try{
            Class.forName(driver);
            con = DriverManager.getConnection(url+db, dbUser, dbPasswd);

            if(insertPost){
                sqlInsertPost(con);
            }
            if(selectCategory){
                sqlSelectCategory = sqlSelectCategory(con);
            }
            if(insertCategory){
                sqlInsertCategory(con);
            }
        }
        catch (Exception e){
            errors.setText("Не могу подключиться!\nПроверьте корректность доступов.");
            try {
                addLogs(e.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        insertPost = false;
        selectCategory = false;
        insertCategory = false;
        session.disconnect();
    }


    private void sqlInsertPost(Connection con) throws IOException{
        try{
            Statement st = con.createStatement();
            String today = getDate();
            // Запрос к БД
            sql = "INSERT INTO blog_posts (`id`, `h1`, `text`, `anons`, `category_id`, `picture`, `date`, `title`, `description`, `link`) " +
                    "VALUES (NULL, '"+
                    postH1.getText()+"', '"+
                    redactorhtml.getText()+"', '"+
                    postAnons.getText()+"', '"+
                    categoryId+"', '', '"+
                    today+"', '"+
                    postTitle.getText()+"', '"+
                    postDescription.getText()+"', '"+
                    postUrl.getText()+"');";

            int update = st.executeUpdate(sql);
            if(update >= 1){
                System.out.println("Информация добавлена");
                addLogs("Запись \"" + postH1.getText() + "\" добавлена.\nЗапрос: " + sql);
                published.setDisable(true);
                readyInfo.setVisible(false);
                success.setText("Статья успешно добавлена!");
                success.setStyle("-fx-font-size: 16");
            }
            else{
                System.out.println("Информация не добавлена");
                addLogs("Запись \"" + postH1.getText() + "\" не добавлена.\nЗапрос: " + sql);
            }
        }catch (SQLException s){
            System.out.println("SQL запрос не выполнен - ошибки!");
            addLogs("SQL запрос \""+sql+"\" не выполнен - ошибки!");
        }
    }


    private Map<String, String> sqlSelectCategory(Connection con) throws IOException{
        try{
            Statement st = con.createStatement();
            Map<String, String> category = new HashMap<>();

            // Запрос к БД
            sql = "SELECT id, name FROM blog_category";
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                String id = rs.getString(1);
                String name = rs.getString(2);
                category.put(id, name);
            }
            System.out.println("Данные получены.");
            addLogs("Данные "+category+" получены.\nЗапрос: " + sql);
            success.setText("Данные получены!");
            return category;

        }catch (SQLException s){
            System.out.println("SQL запрос не выполнен - ошибки!");
            addLogs("SQL запрос \""+sql+"\" не выполнен - ошибки!");
            return null;
        }
    }


    private void sqlInsertCategory(Connection con) throws IOException{
        try{
            Statement st = con.createStatement();
            String today = getDate();
            // Запрос к БД
            sql = "INSERT INTO blog_category (`id`, `link`, `name`, `title`, `description`) VALUES (NULL, '"+
                    categoryUrl.getText()+"', '"+
                    categoryName.getText()+"', '"+
                    categoryTitle.getText()+"', '"+
                    categoryDescription.getText()+"');";

            int update = st.executeUpdate(sql);
            if(update >= 1){
                System.out.println("Информация добавлена");
                addLogs("Категория \"" + categoryName.getText() + "\" добавлена.\nЗапрос: " + sql);
                success.setText("Категория успешно добавлена!");
                success.setStyle("-fx-font-size: 16");
            }
            else{
                System.out.println("Информация не добавлена");
                addLogs("Категория \"" + categoryName.getText() + "\" не добавлена.\nЗапрос :" + sql);
            }
        }catch (SQLException s){
            System.out.println("SQL запрос не выполнен - ошибки!");
            addLogs("SQL запрос \""+sql+"\" не выполнен - ошибки!");
        }
    }


    @FXML
    public void finishPublication(){
        insertPost = true;
        connectDB();
    }

    private void assertsFill(){
        ArrayList<TextField> asserts = addFields();
        for(TextField field : asserts){
            if(field.getText().isEmpty()){
                assertsServerAndDbIsFill = false;
                System.out.println(field.getText());
            }
        }
    }

    @FXML
    public void postReadyCheck() throws IOException {
        readyInfo.setVisible(true);
        assertsServerAndDbIsFill = true;
        errors.setText("");
        success.setText("");

        Map<TextField, StackPane> emptyFieldsCheck = new HashMap<>();
        Map<TextArea, StackPane> emptyTextareaCheck = new HashMap<>();
        emptyFieldsCheck.put(postH1, chechH1);
        emptyFieldsCheck.put(postUrl, checkUrl);
        emptyFieldsCheck.put(postTitle, checkTitle);
        emptyTextareaCheck.put(postDescription, checkDescription);
        emptyTextareaCheck.put(postAnons, checkAnons);


        for(TextField key : emptyFieldsCheck.keySet()){
            Parent icon = null;
            if(key.getText().isEmpty()){
                icon = FXMLLoader.load(getClass().getResource("../gui/components/marks/error.fxml"));
            }else {
                icon = FXMLLoader.load(getClass().getResource("../gui/components/marks/success.fxml"));
            }
            emptyFieldsCheck.get(key).getChildren().clear();
            emptyFieldsCheck.get(key).getChildren().addAll(icon);
        }

        for(TextArea key : emptyTextareaCheck.keySet()){
            Parent icon = null;
            if(key.getText().isEmpty()){
                icon = FXMLLoader.load(getClass().getResource("../gui/components/marks/error.fxml"));
            }else {
                icon = FXMLLoader.load(getClass().getResource("../gui/components/marks/success.fxml"));
            }
            emptyTextareaCheck.get(key).getChildren().clear();
            emptyTextareaCheck.get(key).getChildren().addAll(icon);
        }


        Parent icon = null;
        if(redactorhtml.getText().isEmpty()){
            icon = FXMLLoader.load(getClass().getResource("../gui/components/marks/error.fxml"));
        }else {
            icon = FXMLLoader.load(getClass().getResource("../gui/components/marks/success.fxml"));
        }
        checkHtml.getChildren().clear();
        checkHtml.getChildren().addAll(icon);

        assertsFill();
        checkedCategory();

        if(assertsServerAndDbIsFill){ // Не добавлена проверка на картинку!!!!!
            if(categoryId.equals("-1")){
                published.setDisable(true);
                errors.setText("Не выбрана категория!\nВыберите категорию и повторите попытку.");
            }else{
                StringBuilder emptyFields = new StringBuilder();
                if(requiredH1.isSelected() && postH1.getText().isEmpty()){
                    emptyFields.append("H1").append(", ");
                }
                if(requiredUrl.isSelected() && postUrl.getText().isEmpty()){
                    emptyFields.append("URL").append(", ");
                }
                if(requiredAnons.isSelected() && postAnons.getText().isEmpty()){
                    emptyFields.append("анонс").append(", ");
                }
                if(requiredTitle.isSelected() && postTitle.getText().isEmpty()){
                    emptyFields.append("title").append(", ");
                }
                if(requiredDescription.isSelected() && postDescription.getText().isEmpty()){
                    emptyFields.append("description").append(", ");
                }
                if(requiredHtml.isSelected() && redactorhtml.getText().isEmpty()){
                    emptyFields.append("текст статьи").append(", ");
                }
                if(emptyFields.length() == 0){
                    published.setDisable(false);
                    success.setText("Статья готова к публикации!");
                }else {
                    published.setDisable(true);
                    String errorFields = String.valueOf(emptyFields).substring(0, emptyFields.length()-2);
                    errors.setText("Не заполнены обязательные поля: " + errorFields + ".\nЗаполните их и повторите попытку.");
                }
            }
        }else {
            published.setDisable(true);
            errors.setText("Есть пустые поля в настройках!\nЗаполните их и повторите попытку.");
        }
    }

    @FXML
    public void clickMouse(MouseEvent event){ // Если клик по вкладке
        SingleSelectionModel<Tab> selectionModel = tabs.getSelectionModel();
        if(!selectionModel.isSelected(4)){
            closePublishedTab();
        }
        errors.setText("");
        success.setText("");
    }

    @FXML
    public void getCategory(){
        assertsFill();
        if(assertsServerAndDbIsFill){
            selectCategory = true;
            connectDB();
            Map<String, String> categoryItemsMap = sqlSelectCategory;

            categoryItems.getChildren().clear();
            for (String categoryItem : categoryItemsMap.keySet()){
                RadioButton item = new RadioButton();
                item.setToggleGroup(category);
                item.setText(categoryItemsMap.get(categoryItem));
                item.setId(categoryItem);
                //item.setSelected(true);
                categoryItems.getChildren().add(item);
            }
            errors.setText("");
            success.setText("");
        }else {
            errors.setText("Не все настройки заполнены.\nПроверьте настройки!");
        }
        assertsServerAndDbIsFill = true;
    }

    private void checkedCategory(){
        categoryId = "-1";
        for (Node item : categoryItems.getChildren()){
            RadioButton btn = new RadioButton();
            btn = (RadioButton) item;
            if(btn.isSelected()){
                categoryId = btn.getId();
            }
        }
        errors.setText("");
        success.setText("");
    }

    @FXML
    private void addCategory(){
        addCategoryForm.setVisible(true);
        errors.setText("");
        success.setText("");
    }

    @FXML
    private void addCategorySql(){
        errors.setText("");
        success.setText("");
        if(!categoryName.getText().isEmpty() && !categoryUrl.getText().isEmpty()){
            assertsFill();
            if(assertsServerAndDbIsFill){
                insertCategory = true;
                connectDB();
            }else {
                errors.setText("Не все настройки подключения заполнены.\nЗаполните настройки и повторите попытку.");
                assertsServerAndDbIsFill = true;
            }
        }else {
            System.out.println("ошибка");
            errors.setText("Ошибка при добавлении категории!\nНе заполнены обязательные поля.");
        }
    }


    // Запись логов
    private void addLogs(String text) throws IOException{
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


    private String getLogFileSize() throws IOException {
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
