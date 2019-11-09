package application.lib;

import application.Main;
import application.lib.controllers.Logs;
import application.lib.controllers.UserSettings;
import application.parser.MainParserClass;
import com.jcraft.jsch.JSch;

import java.sql.*;
import com.jcraft.jsch.Session;
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
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
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

public class MainController implements Initializable {
    public static Timer timer = new Timer();

    // Пользовательские настройки
    public static Map<String, String> userSettings = new HashMap<>();
    public static Map<String, Boolean> userImportSettings = new HashMap<>();

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



    @Override
    public void initialize(URL location, ResourceBundle resources) { }

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
                Logs.addLogs("Выбран файл " + file.getName());
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
        return String.format("%tF", date);
    }

    private Map<TextField, String> getCustomSettings()throws IOException{
        UserSettings userSettings = new UserSettings();
        return userSettings.getCustomSettings();
    }


    @FXML
    private void connectSSH() throws IOException {
        // Соединение по SSH
        try
        {
            JSch jsch = new JSch();
            session = jsch.getSession(userSettings.get("sshuser"), userSettings.get("sshhost"), Integer.valueOf(userSettings.get("sshport")));
            lport = 4321;
            rhost = userSettings.get("mysqlhost");
            rport = 3306;
            session.setPassword(userSettings.get("sshpassword"));
            session.setConfig("StrictHostKeyChecking", "no");
            System.out.println("Устанавливаю соединение...");
            Logs.addLogs("Устанавливаю соединение...");
            session.connect();
            int assinged_port=session.setPortForwardingL(lport, rhost, rport);
            System.out.println("localhost:"+assinged_port+" -> "+rhost+":"+rport);
            Logs.addLogs("localhost:"+assinged_port+" -> "+rhost+":"+rport);
        }
        catch(Exception e){
            System.err.print(e);
        }
    }

    private void connectDB() throws IOException {
        connectSSH();

        // Соединение с БД
        Connection con = null;
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://" + rhost +":" + lport + "/";
        String db = userSettings.get("mysqldb");
        String dbUser = userSettings.get("mysqluser");
        String dbPasswd = userSettings.get("mysqlpassword");

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
                Logs.addLogs(e.toString());
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
                Logs.addLogs("Запись \"" + postH1.getText() + "\" добавлена.\nЗапрос: " + sql);
                published.setDisable(true);
                readyInfo.setVisible(false);
                success.setText("Статья успешно добавлена!");
                success.setStyle("-fx-font-size: 16");
            }
            else{
                System.out.println("Информация не добавлена");
                Logs.addLogs("Запись \"" + postH1.getText() + "\" не добавлена.\nЗапрос: " + sql);
            }
        }catch (SQLException s){
            System.out.println("SQL запрос не выполнен - ошибки!");
            Logs.addLogs("SQL запрос \""+sql+"\" не выполнен - ошибки!");
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
            Logs.addLogs("Данные "+category+" получены.\nЗапрос: " + sql);
            success.setText("Данные получены!");
            return category;

        }catch (SQLException s){
            System.out.println("SQL запрос не выполнен - ошибки!");
            Logs.addLogs("SQL запрос \""+sql+"\" не выполнен - ошибки!");
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
                Logs.addLogs("Категория \"" + categoryName.getText() + "\" добавлена.\nЗапрос: " + sql);
                success.setText("Категория успешно добавлена!");
                success.setStyle("-fx-font-size: 16");
            }
            else{
                System.out.println("Информация не добавлена");
                Logs.addLogs("Категория \"" + categoryName.getText() + "\" не добавлена.\nЗапрос :" + sql);
            }
        }catch (SQLException s){
            System.out.println("SQL запрос не выполнен - ошибки!");
            Logs.addLogs("SQL запрос \""+sql+"\" не выполнен - ошибки!");
        }
    }

    @FXML
    public void finishPublication() throws IOException {
        insertPost = true;
        connectDB();
    }

    private void assertsFill(){
        for(String st : userSettings.keySet()){
            if(userSettings.get(st).equals("")){
                assertsServerAndDbIsFill = false;
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
                if(userImportSettings.get("requiredH1") && postH1.getText().isEmpty()){
                    emptyFields.append("H1").append(", ");
                }
                if(userImportSettings.get("requiredUrl") && postUrl.getText().isEmpty()){
                    emptyFields.append("URL").append(", ");
                }
                if(userImportSettings.get("requiredAnons") && postAnons.getText().isEmpty()){
                    emptyFields.append("анонс").append(", ");
                }
                if(userImportSettings.get("requiredTitle") && postTitle.getText().isEmpty()){
                    emptyFields.append("title").append(", ");
                }
                if(userImportSettings.get("requiredDescription") && postDescription.getText().isEmpty()){
                    emptyFields.append("description").append(", ");
                }
                if(userImportSettings.get("requiredHtml") && redactorhtml.getText().isEmpty()){
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
    public void getCategory() throws IOException {
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
    private void addCategorySql() throws IOException {
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

}
