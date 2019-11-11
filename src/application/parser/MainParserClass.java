package application.parser;

import application.Main;
import application.lib.classes.PostCount;
import application.parser.components.*;
import org.apache.poi.xwpf.usermodel.*;
import java.io.*;
import java.util.*;


public class MainParserClass {
    public static StringBuilder out = new StringBuilder();
    private static final String NAME_HTML_FILE = "read.html";
    private static final String PATH_HTML_FILE = "E:\\JAVA\\FX\\publicator\\src\\application\\user_files\\tmp\\html_file\\";
    public static String postName = "";
    public static boolean isBlockquote = false;

    public static void main(XWPFDocument document) {
        try {
            TagConstructor.tagsConstructor ();
            Translit.translitUrlConstructor();

            Map linksMap = GetFragmentsDocx.getLinks(document); // Получить все ссылки
            ArrayDeque tableOrParagraph = GetFragmentsDocx.getTypeFragment(document); // Получаем тип элемента - таблица или параграф
            ArrayDeque typeParagraph = GetFragmentsDocx.getTypeParagraph(document); // Получаем тип параграфа - абзац или список
            ArrayDeque codeParagraph = GetFragmentsDocx.getCodeParagraph(document); // Получаем код параграфа, с которым продолжим работу
            ArrayDeque textParagraph = GetFragmentsDocx.getTextParagraph(document); // Получить просто текст параграфов
            ArrayList getImages = Images.getAllImages(document); // Получить все картинки из документа
            ArrayDeque tableCellCode = Tables.getCodeTableCell(document); // Получить просто текст параграфов

            try{ // Проверка на корректность полученных данных!
                if(typeParagraph.size() != codeParagraph.size() || typeParagraph.size() != textParagraph.size()){
                    throw new Exception("Неверное количество параграфов!");
                }
            }catch (Exception e) {
                e.printStackTrace();
            }

            boolean firstElementList = true;
            boolean isBullet = false;
            boolean isDecimal = false;

            for(Object fragment : tableOrParagraph){
                if(fragment == "PARAGRAPH"){
                    StringBuilder potentialTitle = new StringBuilder(textParagraph.getFirst().toString()); ;
                    if(typeParagraph.getFirst() == "null"){
                        if(isBullet){// Поставить закрывающий тег у списка и сбросить все значения
                            out.append(TagConstructor.bullet.get("end"));
                        }
                        if(isDecimal){
                            out.append(TagConstructor.numeric.get("end"));
                        }
                        isBullet = false;
                        isDecimal = false;
                        firstElementList = true;

                        if(potentialTitle.indexOf(TagConstructor.titleH1.get("search")) == 0){ // H1
                            potentialTitle.delete(0, TagConstructor.titleH1.get("search").length());
                            PostCount postCount = new PostCount();
                            postName = String.valueOf(potentialTitle);
                            Main.postH1 = postName;
                            Main.postUrl = Translit.translitUrl(postName) + postCount.getPostCount();

                        }else if(potentialTitle.indexOf(TagConstructor.titleH2.get("search")) == 0){ // H2
                            potentialTitle.delete(0, TagConstructor.titleH2.get("search").length());

                            out.append(TagConstructor.titleH2.get("start"))
                                .append(potentialTitle)
                                .append(TagConstructor.titleH2.get("end"));

                        }else if(potentialTitle.indexOf(TagConstructor.titleH3.get("search")) == 0){ // H3
                            potentialTitle.delete(0, TagConstructor.titleH3.get("search").length());

                            out.append(TagConstructor.titleH3.get("start"))
                                .append(potentialTitle)
                                .append(TagConstructor.titleH3.get("end"));

                        }else{
                            //imageSrc = getAllImages(document, urlPost);
                            if(!ParserCode.getFragmentWithTags(codeParagraph.getFirst().toString(), linksMap, getImages).equals("")){
                                if(isBlockquote){

                                    out.append(TagConstructor.quote.get("start"))
                                            .append(ParserCode.getFragmentWithTags(codeParagraph.getFirst().toString(), linksMap, getImages))
                                            .append(TagConstructor.quote.get("end"));

                                    isBlockquote = false;
                                }else {

                                    out.append(TagConstructor.paragraph.get("start"))
                                            .append(ParserCode.getFragmentWithTags(codeParagraph.getFirst().toString(), linksMap, getImages))
                                            .append(TagConstructor.paragraph.get("end"));

                                }
                            }
                        }

                    }else if(typeParagraph.getFirst() == "bullet"){ // Маркированные списки
                        if(firstElementList || !isBullet){
                            out.append(TagConstructor.bullet.get("start"));
                            firstElementList = false;
                        }

                        out.append(TagConstructor.list.get("start"))
                                .append(ParserCode.getFragmentWithTags(codeParagraph.getFirst().toString(), linksMap, getImages))
                                .append(TagConstructor.list.get("end"));

                        isBullet = true;
                        isDecimal = false;

                    }else if(typeParagraph.getFirst() == "decimal"){ // Нумерованные списки
                        if(firstElementList || !isDecimal){
                            out.append(TagConstructor.numeric.get("start"));
                            firstElementList = false;
                        }

                        out.append(TagConstructor.list.get("start"))
                                .append(ParserCode.getFragmentWithTags(codeParagraph.getFirst().toString(), linksMap, getImages))
                                .append(TagConstructor.list.get("end"));

                        isDecimal = true;
                        isBullet = false;
                    }

                    typeParagraph.removeFirst();
                    codeParagraph.removeFirst();
                    textParagraph.removeFirst();

                }else if(fragment == "TABLE"){
                    if(isBullet){// Поставить закрывающий тег у списка и сбросить все значения
                        out.append(TagConstructor.bullet.get("end"));
                    }
                    if(isDecimal){
                        out.append(TagConstructor.numeric.get("end"));
                    }
                    isBullet = false;
                    isDecimal = false;
                    firstElementList = true;
                    String tmp = (String) tableCellCode.getFirst();
                    tableCellCode.removeFirst();
                    StringTokenizer st = new StringTokenizer(tmp, Tables.TABLE_SEPARATOR);
                    int lineCount = 0;
                    while (st.hasMoreTokens()){
                        if(lineCount%2 == 1){
                            out.append(ParserCode.getFragmentWithTags(st.nextToken(), linksMap, getImages));
                        }else{
                            out.append(st.nextToken());
                        }
                        lineCount++;
                    }
                }
                tableOrParagraph.removeFirst();
            }

            String result = String.valueOf(out)
                    .replaceAll("[\\s]{2,}", " ")
                    .replaceAll("> <", "><")
                    .replaceAll("<sub></sub>", "")
                    .replaceAll("<sup></sup>", "")
                    .replaceAll("<strong></strong>", "")
                    .replaceAll(TagConstructor.quote.get("end"), TagConstructor.quote.get("end") + "\n")
                    .replaceAll(TagConstructor.paragraph.get("end"), TagConstructor.paragraph.get("end") + "\n")
                    .replaceAll(TagConstructor.bullet.get("end"), TagConstructor.bullet.get("end") + "\n")
                    .replaceAll(TagConstructor.numeric.get("end"), TagConstructor.numeric.get("end") + "\n")
                    .replaceAll(TagConstructor.table.get("end"), TagConstructor.table.get("end") + "\n")
                    .replaceAll(TagConstructor.titleH1.get("end"), TagConstructor.titleH1.get("end") + "\n")
                    .replaceAll(TagConstructor.titleH2.get("end"), TagConstructor.titleH2.get("end") + "\n")
                    .replaceAll(TagConstructor.titleH3.get("end"), TagConstructor.titleH3.get("end") + "\n")
                    .replaceAll(TagConstructor.list.get("end"), TagConstructor.list.get("end") + "\n");

            // Добавлнение табуляции
            result = result
                    .replaceAll(TagConstructor.paragraph.get("start"),TagConstructor.paragraph.get("start") + "\n\t")
                    .replaceAll(TagConstructor.paragraph.get("end"), "\n" + TagConstructor.paragraph.get("end"))
                    .replaceAll(TagConstructor.quote.get("start"), TagConstructor.quote.get("start") + "\n\t")
                    .replaceAll(TagConstructor.quote.get("end"), "\n" + TagConstructor.quote.get("end"))
                    .replaceAll(TagConstructor.bullet.get("start"), TagConstructor.bullet.get("start") + "\n")
                    .replaceAll(TagConstructor.bullet.get("end"), TagConstructor.bullet.get("end"))
                    .replaceAll(TagConstructor.numeric.get("start"), TagConstructor.numeric.get("start") + "\n")
                    .replaceAll(TagConstructor.numeric.get("end"), TagConstructor.numeric.get("end"))
                    .replaceAll(TagConstructor.list.get("start"), "\t" + TagConstructor.list.get("start"))
                    .replaceAll(TagConstructor.list.get("end"), TagConstructor.list.get("end"))
                    .replaceAll(TagConstructor.table.get("start"), "<table>\n\t<tbody>\n")
                    .replaceAll(TagConstructor.table.get("end"), "\t</tbody>\n</table>")
                    .replaceAll(TagConstructor.tableRow.get("start"), "\t\t" + TagConstructor.tableRow.get("start") + "\n")
                    .replaceAll(TagConstructor.tableRow.get("end"), "\t\t" + TagConstructor.tableRow.get("end"))
                    .replaceAll(TagConstructor.tableTitle.get("start"), "\t\t\t" + TagConstructor.tableTitle.get("start"))
                    .replaceAll(TagConstructor.tableTitle.get("end"), TagConstructor.tableTitle.get("end"))
                    .replaceAll(TagConstructor.tableCell.get("start"), "\t\t\t" + TagConstructor.tableCell.get("start"))
                    .replaceAll(TagConstructor.tableCell.get("end"), TagConstructor.tableCell.get("end"))
            ;

            PrintWriter pw = new PrintWriter(new File(PATH_HTML_FILE + NAME_HTML_FILE));
            Main.html = result;
            pw.write(result);
            pw.close();

            File htmlFile = new File(PATH_HTML_FILE + NAME_HTML_FILE);
            if(htmlFile.exists()){
                System.out.println("Файл создан успешно!");
            }else {
                System.out.println("Файл не был создан");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}