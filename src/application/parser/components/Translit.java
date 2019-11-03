package application.parser.components;

import java.util.HashMap;
import java.util.Map;

public class Translit {
    private static Map<String, String> translit = new HashMap<>();
    public static void translitUrlConstructor(){ // Конструктор URLa
        translit.put("а", "a");
        translit.put("б", "b");
        translit.put("в", "v");
        translit.put("г", "g");
        translit.put("д", "d");
        translit.put("е", "e");
        translit.put("ё", "e");
        translit.put("ж", "zh");
        translit.put("з", "z");
        translit.put("и", "i");
        translit.put("й", "y");
        translit.put("к", "k");
        translit.put("л", "l");
        translit.put("м", "m");
        translit.put("н", "n");
        translit.put("о", "o");
        translit.put("п", "p");
        translit.put("р", "r");
        translit.put("с", "s");
        translit.put("т", "t");
        translit.put("у", "u");
        translit.put("ч", "ch");
        translit.put("ш", "sh");
        translit.put("щ", "shch");
        translit.put("ъ", "");
        translit.put("ы", "y");
        translit.put("ь", "");
        translit.put("э", "e");
        translit.put("ю", "yu");
        translit.put("я", "ya");
        translit.put(" ", "-");
    }

    public static String translitUrl(String namePost){
        StringBuilder translitUrl = new StringBuilder();
        char[] namePostArr = namePost.toCharArray();
        for(char a : namePostArr){
            String symbol = String.valueOf(a).toLowerCase();
            if(translit.containsKey(symbol)){
                translitUrl.append(translit.get(symbol));
            }
        }
        return String.valueOf(translitUrl);
    }
}
