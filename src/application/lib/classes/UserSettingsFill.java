package application.lib.classes;

import application.lib.MainController;

public class UserSettingsFill {
    public static void assertsFill(){
        for(String st : MainController.userSettings.keySet()){
            if(MainController.userSettings.get(st).equals("")){
                MainController.assertsServerAndDbIsFill = false;
            }
        }
    }
}
