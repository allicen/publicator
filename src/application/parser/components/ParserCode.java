package application.parser.components;

import application.parser.MainParserClass;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserCode {
    public static String getFragmentWithTags(String code, Map linksMap, ArrayList getImages) { // Собственно сам парсер
        StringBuilder tmp = new StringBuilder();
        char[] codeArray = code.toCharArray();
        StringBuilder tag = new StringBuilder();
        StringBuilder tagText = new StringBuilder();
        ArrayDeque<String> closeTag = new ArrayDeque<>(); // Закрывающие теги
        String prevTag = "";

        boolean isTag = false;
        boolean isBold = false;
        boolean isIndex = false;
        boolean isPow = false;
        boolean isQuote = false;
        boolean isLink = false;
        boolean isPic = false;

        for (char aCodeArray : codeArray) {
            String symbol = Character.toString(aCodeArray);
            if (symbol.equals("<")) { // Начало формирования тега
                prevTag = String.valueOf(tag);
                tag.delete(0, tag.length());
                tag.append("<");
                isTag = true;
            } else {
                if (isTag) {
                    if (symbol.equals(">")) { // Конец формирования тега
                        tag.append(">");
                        isTag = false;
                        boolean isLinkInline = tag.toString().equals("</w:instrText>");

                        if (tag.toString().equals("</w:t>") || isLinkInline) {
                            String verification = String.valueOf(tagText).replaceAll("[\\s]{2,}", " ");
                            if (verification.equals(" ")) { // Если ничего нет в строке
                                tag.delete(0, tag.length());
                                tagText.delete(0, tagText.length());
                            } else {
                                if (isLinkInline) { // Если это встроенная ссылка
                                    int start = verification.indexOf("\"") + 1;
                                    int end = verification.lastIndexOf("\"");
                                    String href = verification.substring(start, end);
                                    tmp.append(" ").append(TagConstructor.link.get("start")).append(href).append(TagConstructor.link.get("startLoad"));
                                    tagText.delete(0, tagText.length());
                                    closeTag.addFirst(TagConstructor.link.get("end"));
                                } else {
                                    tmp.append(verification.trim()); // Добавление текста
                                    while (!closeTag.isEmpty()) { // Добавление закрывающих тегов
                                        if (closeTag.getFirst().equals(TagConstructor.pow.get("end")) && !prevTag.equals("<w:t>")) {
                                            tmp.delete(tmp.indexOf(TagConstructor.pow.get("start")) - 1, TagConstructor.pow.get("start").length() + 1); // Удалить лишний тег с пробелами
                                        } else {
                                            tmp.append(closeTag.getFirst()).append(" ");
                                        }
                                        if (closeTag.getFirst().equals(TagConstructor.index.get("end"))) { // Определение типа тега, снятие маркера
                                            isIndex = false;
                                        } else if (closeTag.getFirst().equals(TagConstructor.pow.get("end")) && prevTag.equals("<w:t>")) {
                                            isPow = false;
                                        } else if (closeTag.getFirst().equals(TagConstructor.bold.get("end"))) {
                                            isBold = false;
                                        } else if (closeTag.getFirst().equals(TagConstructor.link.get("end"))) {
                                            isLink = false;
                                        }
                                        closeTag.removeFirst();
                                    }
                                }
                                if (isLinkInline) {
                                    isLink = true;
                                }
                                isLinkInline = false;
                                tagText.delete(0, tagText.length());
                            }
                        } else {
                            if (tag.toString().equals(TagConstructor.bold.get("search"))) { // Сравнение тега с тегом-меткой
                                if (!isBold) {
                                    isBold = true;
                                    tmp.append(" ").append(TagConstructor.bold.get("start"));
                                    closeTag.addFirst(TagConstructor.bold.get("end"));
                                }
                            } else if (tag.toString().equals(TagConstructor.index.get("search"))) {
                                isIndex = true;
                                tmp.append(" ").append(TagConstructor.index.get("start"));
                                closeTag.addFirst(TagConstructor.index.get("end"));
                            } else if (tag.toString().equals(TagConstructor.pow.get("search"))) {
                                isPow = true;
                                tmp.append(" ").append(TagConstructor.pow.get("start"));
                                closeTag.addFirst(TagConstructor.pow.get("end"));
                            } else if (tag.toString().contains(TagConstructor.link.get("search"))) {
                                isLink = true;
                                String href = "";
                                Pattern p = Pattern.compile("\"([^\"]*)\"");
                                Matcher m = p.matcher(tag);
                                while (m.find()) {
                                    if (linksMap.containsKey(m.group(1))) {
                                        href = (String) linksMap.get(m.group(1));
                                        break;
                                    }
                                }
                                if (!href.equals("")) {
                                    tmp.append(" ")
                                            .append(TagConstructor.link.get("start"))
                                            .append(href)
                                            .append(TagConstructor.link.get("startLoad"));
                                    closeTag.addFirst(TagConstructor.link.get("end"));
                                }

                            } else if (tag.toString().equals(TagConstructor.quote.get("search"))) {
                                MainParserClass.isBlockquote = true;
                            } else if (tag.toString().equals(TagConstructor.img.get("search"))) {
                                if (!getImages.isEmpty()) {
                                    MainParserClass.out
                                            .append(TagConstructor.paragraph.get("start"))
                                            .append(TagConstructor.img.get("start"))
                                            .append(getImages.get(0))
                                            .append(TagConstructor.img.get("startLoad"))
                                            .append(MainParserClass.postName)
                                            .append(TagConstructor.img.get("startLoadEnd"))
                                            .append(TagConstructor.paragraph.get("end"));
                                    getImages.remove(0);
                                }
                            } else {
                                tagText.delete(0, tagText.length());
                            }
                        }
                    } else {
                        tag.append(symbol);
                    }
                } else {
                    if (!symbol.equals("\r") && !symbol.equals("\n")) {
                        tagText.append(symbol); // Всё что не тег
                    }
                }
            }
        }
        return String.valueOf(tmp).replaceAll("[\\s]{2,}", " ");
    }
}
