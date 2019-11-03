package application.parser.components;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHyperlink;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetFragmentsDocx {
    public static Map<String, String> getLinks(XWPFDocument document){ // Получить ссылки из документа
        XWPFHyperlink[] links = document.getHyperlinks(); // Получение ссылки
        Map<String, String> linksMap = new HashMap<>();
        for(XWPFHyperlink a : links){
            linksMap.put(a.getId(), a.getURL());
        }
        return linksMap;
    }

    public static ArrayDeque<String> getTypeFragment(XWPFDocument document){ // Получить очередь фрагментов (параграф или таблица)
        List<IBodyElement> allElements = document.getBodyElements();
        ArrayDeque<String> tableOrParagraph = new ArrayDeque<>();
        for(IBodyElement line : allElements){
            tableOrParagraph.add(String.valueOf(line.getElementType()));
        }
        return tableOrParagraph;
    }

    public static ArrayDeque<String> getTypeParagraph(XWPFDocument document){ // Получить тип параграфа: список и какой или просто параграф
        List<XWPFParagraph> paragraph = document.getParagraphs();
        ArrayDeque<String> typeParagraph = new ArrayDeque<>();
        for(XWPFParagraph line : paragraph){
            typeParagraph.add(String.valueOf(line.getNumFmt()));
        }
        return typeParagraph;
    }

    public static ArrayDeque<String> getCodeParagraph(XWPFDocument document){ // Получить исходный код параграфа
        List<XWPFParagraph> paragraph = document.getParagraphs();
        ArrayDeque<String> codeParagraph = new ArrayDeque<>();
        for(XWPFParagraph line : paragraph){
            String tmp = String.valueOf(line.getCTP());
            int replaceStart = tmp.indexOf("<xml-fragment"); // Вырезать длинный ненужный тег
            int replaceEnd = tmp.indexOf("\">");
            StringBuilder tmpString = new StringBuilder(tmp);
            tmpString.delete(replaceStart, replaceEnd + "\">".length());
            codeParagraph.add(String.valueOf(tmpString));
        }
        return codeParagraph;
    }

    public static ArrayDeque<String> getTextParagraph(XWPFDocument document){  // Получить текст параграфа (для заголовков)
        List<XWPFParagraph> paragraph = document.getParagraphs();
        ArrayDeque<String> textParagraph = new ArrayDeque<>();
        for(XWPFParagraph line : paragraph){
            textParagraph.add(String.valueOf(line.getText()));
        }
        return textParagraph;
    }
}
