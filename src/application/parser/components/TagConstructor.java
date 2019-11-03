package application.parser.components;

import java.util.HashMap;
import java.util.Map;

public class TagConstructor {
    // Теги
    static Map<String, String> bold = new HashMap<>();
    static Map<String, String> index = new HashMap<>();
    static Map<String, String> pow = new HashMap<>();
    static Map<String, String> link = new HashMap<>();
    public static Map<String, String> quote = new HashMap<>(); // Будет тег курсива для блока цитирования
    public static Map<String, String> table = new HashMap<>();
    static Map<String, String> tableTitle = new HashMap<>();
    static Map<String, String> tableRow = new HashMap<>();
    static Map<String, String> tableCell = new HashMap<>();
    public static Map<String, String> paragraph = new HashMap<>();
    public static Map<String, String> bullet = new HashMap<>();
    public static Map<String, String> numeric = new HashMap<>();
    public static Map<String, String> list = new HashMap<>();
    public static Map<String, String> titleH1 = new HashMap<>();
    public static Map<String, String> titleH2 = new HashMap<>();
    public static Map<String, String> titleH3 = new HashMap<>();
    static Map<String, String> img = new HashMap<>();

    public static void tagsConstructor(){ // Все теги
        bold.put("search", "<w:b/>");
        bold.put("start", "<strong>");
        bold.put("end", "</strong>");

        index.put("search", "<w:vertAlign w:val=\"subscript\"/>");
        index.put("start", "<sub>");
        index.put("end", "</sub>");

        pow.put("search", "<w:vertAlign w:val=\"superscript\"/>");
        pow.put("start", "<sup>");
        pow.put("end", "</sup>");

        link.put("search", "<w:hyperlink");
        link.put("start", "<a href=\"");
        link.put("startLoad", "\" target=\"_blank\">");
        link.put("end", "</a>");

        quote.put("search", "<w:i/>");
        quote.put("start", "<blockquote>");
        quote.put("end", "</blockquote>");

        paragraph.put("search", "");
        paragraph.put("start", "<p>");
        paragraph.put("end", "</p>");


        bullet.put("search", "");
        bullet.put("start", "<ul>");
        bullet.put("end", "</ul>");

        numeric.put("search", "");
        numeric.put("start", "<ol>");
        numeric.put("end", "</ol>");

        list.put("search", "");
        list.put("start", "<li>");
        list.put("end", "</li>");

        table.put("search", "");
        table.put("start", "<table><tbody>");
        table.put("end", "</tbody></table>");

        tableRow.put("search", "");
        tableRow.put("start", "<tr>");
        tableRow.put("end", "</tr>");

        tableTitle.put("search", "");
        tableTitle.put("start", "<th>");
        tableTitle.put("end", "</th>");

        tableCell.put("search", "");
        tableCell.put("start", "<td>");
        tableCell.put("end", "</td>");

        titleH1.put("search", "H1 - ");
        titleH1.put("start", "<h1>");
        titleH1.put("end", "</h1>");

        titleH2.put("search", "H2 – ");
        titleH2.put("start", "<h2>");
        titleH2.put("end", "</h2>");

        titleH3.put("search", "H3 – ");
        titleH3.put("start", "<h3>");
        titleH3.put("end", "</h3>");

        img.put("search", "<w:drawing>");
        img.put("start", "<img src=\"");
        img.put("startLoad", "\" alt=\"");
        img.put("startLoadEnd", "\"/>");
        img.put("end", "");
    }
}
