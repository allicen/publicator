package application.lib.classes;

import java.util.Date;

public class GetDate {
    public static String getDate(){
        Date date = new Date();
        return String.format("%tF", date);
    }
}
