package application.lib.classes;

import application.lib.MainController;
import application.lib.controllers.Logs;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class UploadImages{
    private static ArrayList<String> filesName = new ArrayList<>();
    private FTPClient ftp = null;
    private static final String FTP_LOCAL_DIRECTORY = "E:\\JAVA\\FX\\publicator\\src\\application\\user_files\\tmp\\images\\";
    public static final String PATH_DIRECTORY = "E:\\JAVA\\FX\\publicator\\src\\application\\user_files\\tmp\\images";

    private static void getFiles(){
        File pathDir = null;
        String[] pathsFilesAndDir;
        try {
            pathDir = new File(PATH_DIRECTORY);
            pathsFilesAndDir = pathDir.list();
            Collections.addAll(filesName, Objects.requireNonNull(pathsFilesAndDir));
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private UploadImages(String host, String user, String pwd) throws Exception{
        ftp = new FTPClient();
        //ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out))); // Вывод подробного состояния загрузки
        int reply;
        ftp.connect(host);
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new Exception("Не удалось установить соединение с FTP-сервером.");
        }
        ftp.login(user, pwd);
        ftp.makeDirectory(SetImagesDirectory.setImagesDirectory());
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.enterLocalPassiveMode();
    }

    private void uploadFile(String localFileFullName, String fileName) throws Exception {
        try(InputStream input = new FileInputStream(new File(localFileFullName))){
            this.ftp.storeFile(SetImagesDirectory.setImagesDirectory() + "/" + fileName, input);
        }
    }

    private void disconnect(){
        if (this.ftp.isConnected()) {
            try {
                this.ftp.logout();
                this.ftp.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void uploadImages() throws Exception {
        getFiles();
        System.out.println("Начинаю...");
        UploadImages ftpUploader = new UploadImages(MainController.userSettings.get("ftphost"), MainController.userSettings.get("ftpuser"), MainController.userSettings.get("ftppassword"));
        for(String file : filesName){
            ftpUploader.uploadFile(FTP_LOCAL_DIRECTORY + file, file);
        }
        ftpUploader.disconnect();
        System.out.println("Отключен от сервера.");
        filesName.clear();
    }
}