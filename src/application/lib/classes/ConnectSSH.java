package application.lib.classes;

import application.lib.MainController;
import application.lib.controllers.Logs;
import com.jcraft.jsch.JSch;

import java.io.IOException;

public class ConnectSSH {
    public static int lport;
    public static String rhost;
    private static int rport;

    public static void connectSSH() throws IOException {
        // Соединение по SSH
        try
        {
            JSch jsch = new JSch();
            MainController.session = jsch.getSession(MainController.userSettings.get("sshuser"), MainController.userSettings.get("sshhost"), Integer.valueOf(MainController.userSettings.get("sshport")));
            lport = 4321;
            rhost = MainController.userSettings.get("mysqlhost");
            rport = 3306;
            MainController.session.setPassword(MainController.userSettings.get("sshpassword"));
            MainController.session.setConfig("StrictHostKeyChecking", "no");
            System.out.println("Устанавливаю соединение...");
            Logs.addLogs("Устанавливаю соединение...");
            MainController.session.connect();
            int assinged_port = MainController.session.setPortForwardingL(lport, rhost, rport);
            System.out.println("localhost:"+assinged_port+" -> "+rhost+":"+rport);
            Logs.addLogs("localhost:"+assinged_port+" -> "+rhost+":"+rport);
        }
        catch(Exception e){
            System.err.print(e);
        }
    }
}
