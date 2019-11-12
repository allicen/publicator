package application.lib;

import application.Main;
import application.lib.classes.*;
import application.lib.controllers.Logs;
import application.lib.classes.UploadMainImage;
import application.parser.MainParserClass;

import java.sql.*;

import application.parser.components.Images;
import com.jcraft.jsch.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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
import java.util.Timer;

import static application.lib.classes.UploadMainImage.imageView;
import static application.lib.classes.ValidateFile.validateFile;

public class MainController implements Initializable {
    public static Timer timer = new Timer();
    private static Logs logs = new Logs();
    private PostCount postCount = new PostCount();

    // Пользовательские настройки
    public static Map<String, String> userSettings = new HashMap<>();
    public static Map<String, Boolean> userImportSettings = new HashMap<>();

    // Соединение с MySQL
    public static Session session = null;
    public static boolean assertsServerAndDbIsFill = true;

    // Проверка файла и выбора категории
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
    public ScrollPane scrollPreview;
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
    public StackPane web;
    public BorderPane main;
    public StackPane fileUpload;
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

    // Загрузка главной картинки
    public VBox mainImg;
    public Text noticeMainImg;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scrollPreview.setFitToWidth(true);
    }

    @FXML
    public void upload(){ // Загрузка файла
        fileUpload.getChildren().clear();
        JFileChooser window = new JFileChooser();
        int returnValue = window.showOpenDialog(null);
        if(returnValue == JFileChooser.APPROVE_OPTION){
            XWPFDocument document = null;
            try {
                File file = window.getSelectedFile();
                isValidateFile = validateFile(file);
                logs.addLogs("Выбран файл " + file.getName());
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
    public void cancelUpload(){ // Сброс загрузки файла
        preview.setText("Нет данных");
        redactorhtml.setText("");
        redactorvisual.setHtmlText("Нет данных *");
        previewH1.setText("Нет данных *");
        postH1.setText("");
        postUrl.setText("");
        postAnons.setText("");
        postTitle.setText("");
        postDescription.setText("");
        fileUpload.getChildren().clear();
        mainImg.getChildren().clear();
        noticeMainImg.setText("");
        closePublishedTab();
    }

    @FXML
    private void closePublishedTab(){
        published.setDisable(true);
        readyInfo.setVisible(false);
        errors.setText("");
        success.setText("");
    }

    private void navTexts(){ // Переключение страниц
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
        web.setVisible(false);
        closePublishedTab();
    }

    @FXML
    private void showLogs(){
        navTexts();
        showLogs.setVisible(true);
        web.setVisible(false);
        closePublishedTab();
    }

    @FXML
    public void about(){
        navTexts();
        web.setVisible(true);
        about.setVisible(true);
        about.getEngine().load(getClass().getResource("../template_html/about.html").toExternalForm());
        closePublishedTab();
    }

    @FXML
    public void manual(){
        navTexts();
        web.setVisible(true);
        manual.setVisible(true);
        manual.getEngine().load(getClass().getResource("../template_html/manual.html").toExternalForm());
        closePublishedTab();
    }

    @FXML
    public void faq(){
        navTexts();
        web.setVisible(true);
        faq.setVisible(true);
        faq.getEngine().load(getClass().getResource("../template_html/faq.html").toExternalForm());
       closePublishedTab();
    }

    @FXML
    public void feedback(){
        navTexts();
        web.setVisible(true);
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
    public void toHtmlButton() {
        redactorhtml.setText(redactorvisual.getHtmlText());
        preview.setText(redactorvisual.getHtmlText());
        closePublishedTab();
    }

    @FXML
    public void fromHtmlButton(){
        redactorvisual.setHtmlText(redactorhtml.getText());
        preview.setText(redactorvisual.getHtmlText());
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

    private void connectDB() throws IOException {// Соединение с БД
        ConnectSSH.connectSSH();
        Connection con = null;
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://" + ConnectSSH.rhost +":" + ConnectSSH.lport + "/";
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
        }catch (Exception e){
            errors.setText("Не могу подключиться!\nПроверьте корректность доступов.");
            try {
                logs.addLogs(e.toString());
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

    private void sqlInsertPost(Connection con) throws IOException{ // Публикация записи
        try{
            Statement st = con.createStatement();
            String today = GetDate.getDate();
            sql = "INSERT INTO blog_posts (`id`, `h1`, `text`, `anons`, `category_id`, `picture`, `date`, `title`, `description`, `link`) " +
                    "VALUES (NULL, '"+
                    postH1.getText()+"', '"+
                    redactorhtml.getText()+"', '"+
                    postAnons.getText()+"', '"+
                    categoryId+"', '"+
                    SetImagesDirectory.setImagesUrlDirectory() + Main.postImage+"', '"+
                    today+"', '"+
                    postTitle.getText()+"', '"+
                    postDescription.getText()+"', '"+
                    postUrl.getText()+"');";

            int update = st.executeUpdate(sql);

            if(update >= 1){ // Если статья добавлена
                UploadImages.uploadImages(); // Грузим картинки
                int count = PostCount.count + 1; // Увеличиваем счетчик на 1
                postCount.setPostCount(count);
                System.out.println("Статья успешно добавлена");
                logs.addLogs("Запись \"" + postH1.getText() + "\" добавлена.");
                published.setDisable(true);
                readyInfo.setVisible(false);
                success.setText("Статья успешно добавлена!");
                success.setStyle("-fx-font-size: 16");
                Clear.clear();
                cancelUpload();
                DeleteImgFiles.deleteAllFilesFolder(UploadImages.PATH_DIRECTORY); // Удалить все картинки после добавления записи
            }
            else{
                System.out.println("Статья не добавлена");
                logs.addLogs("Запись \"" + postH1.getText() + "\" не добавлена.\nЗапрос: " + sql);
            }
        }catch (Exception s){
            System.out.println("SQL запрос отправлен. Но что-то пошло не так!");
            logs.addLogs("SQL запрос \""+sql+"\" отправлен. Но что-то пошло не так!");
        }
    }

    private Map<String, String> sqlSelectCategory(Connection con) throws IOException{ // Получение категорий
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
            logs.addLogs("Данные "+category+" получены.\nЗапрос: " + sql);
            success.setText("Данные получены!");
            System.out.println("Данные получены.");
            return category;
        }catch (SQLException s){
            System.out.println("SQL запрос отправлен. Но что-то пошло не так!");
            logs.addLogs("SQL запрос \""+sql+"\" отправлен. Но что-то пошло не так!");
            return null;
        }
    }

    private void sqlInsertCategory(Connection con) throws IOException{ // Добавление категории
        try{
            Statement st = con.createStatement();
            // Запрос к БД
            sql = "INSERT INTO blog_category (`id`, `link`, `name`, `title`, `description`) VALUES (NULL, '"+
                    categoryUrl.getText()+"', '"+
                    categoryName.getText()+"', '"+
                    categoryTitle.getText()+"', '"+
                    categoryDescription.getText()+"');";

            int update = st.executeUpdate(sql);
            if(update >= 1){
                System.out.println("Категория добавлена");
                logs.addLogs("Категория \"" + categoryName.getText() + "\" добавлена.\nЗапрос: " + sql);
                success.setText("Категория успешно добавлена!");
                success.setStyle("-fx-font-size: 16");
            }
            else{
                System.out.println("Категория не добавлена");
                logs.addLogs("Категория \"" + categoryName.getText() + "\" не добавлена.\nЗапрос :" + sql);
            }
        }catch (SQLException s){
            System.out.println("SQL запрос отправлен. Но что-то пошло не так!");
            logs.addLogs("SQL запрос \""+sql+"\" отправлен. Но что-то пошло не так!");
        }
    }

    @FXML
    public void getCategory() throws IOException { // Получение категорий
        UserSettingsFill.assertsFill();
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
        }else {
            errors.setText("Не все настройки заполнены.\nПроверьте настройки!");
        }
        assertsServerAndDbIsFill = true;
    }

    private void checkedCategory(){ // Выбор категории
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
    private void addCategory(){ // Вывод формы добавления категории
        addCategoryForm.setVisible(true);
        errors.setText("");
        success.setText("");
    }

    @FXML
    private void addCategorySql() throws IOException { // Добавление категории
        errors.setText("");
        success.setText("");
        if(!categoryName.getText().isEmpty() && !categoryUrl.getText().isEmpty()){
            UserSettingsFill.assertsFill();
            if(assertsServerAndDbIsFill){
                insertCategory = true;
                connectDB();
            }else {
                errors.setText("Не все настройки подключения заполнены.\nЗаполните настройки и повторите попытку.");
                assertsServerAndDbIsFill = true;
            }
        }else {
            errors.setText("Ошибка при добавлении категории!\nНе заполнены обязательные поля.");
        }
    }

    @FXML
    public void postReadyCheck() throws IOException { // Проверка готовности публикации
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

        if(Main.postImage.isEmpty()){
            icon = FXMLLoader.load(getClass().getResource("../gui/components/marks/error.fxml"));
        }else {
            icon = FXMLLoader.load(getClass().getResource("../gui/components/marks/success.fxml"));
        }
        checkImage.getChildren().clear();
        checkImage.getChildren().addAll(icon);

        UserSettingsFill.assertsFill();
        checkedCategory();

        if(assertsServerAndDbIsFill){
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
                if(userImportSettings.get("requiredImage") && Main.postImage.isEmpty()){
                    emptyFields.append("главная картинка").append(", ");
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

    public void finishPublication() throws IOException { // Завершение публикации
        insertPost = true;
        connectDB();
    }

    @FXML
    public void selectMainImages() throws IOException { // Вывод миниатюры главной картинки
        mainImg.getChildren().clear();
        UploadMainImage uploadMainImage = new UploadMainImage();
        uploadMainImage.selectMainImages();
        if(UploadMainImage.mainImgUploaded){
            noticeMainImg.setText("Выбран файл " + Main.postImage);
            noticeMainImg.setStyle("-fx-fill: green");
            FileInputStream fileInputStream = new FileInputStream(Images.PATH_DIRECTORY_IMG + UploadMainImage.fileName);
            Image printImg = new Image(fileInputStream);
            imageView = new ImageView(printImg);
            imageView.setFitWidth(UploadMainImage.MAX_WIDTH_PREVIEW);
            imageView.setFitHeight(UploadMainImage.height * UploadMainImage.MAX_WIDTH_PREVIEW / UploadMainImage.width);
            mainImg.getChildren().add(imageView);
            fileInputStream.close();
        }else {
            noticeMainImg.setText("Выбран файл неверного формата!");
            noticeMainImg.setStyle("-fx-fill: red");
        }
    }
}