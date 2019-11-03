package application.parser.components;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.util.ArrayDeque;
import java.util.List;

public class Tables {
    public static final String TABLE_SEPARATOR = "{{{"; // Разделитель для таблицы

    public static ArrayDeque<String> getCodeTableCell(XWPFDocument document){ // БЕЗ УЧЕТА ОБЪЕДИНЕНИЯ ЯЧЕЕК!!!
        List<XWPFTable> tables = document.getTables();
        ArrayDeque<String> codeTableCell = new ArrayDeque<>();
        StringBuilder tableConstructor = new StringBuilder();
        boolean isHeader;
        for (XWPFTable xwpfTable : tables) {
            List<XWPFTableRow> row = xwpfTable.getRows();
            tableConstructor.append(TagConstructor.table.get("start"));
            isHeader = true;
            for (XWPFTableRow xwpfTableRow : row) {
                List<XWPFTableCell> cell = xwpfTableRow.getTableCells();
                tableConstructor.append(TagConstructor.tableRow.get("start"));
                for (XWPFTableCell xwpfTableCell : cell) {
                    if(xwpfTableCell != null){
                        StringBuilder tmp = new StringBuilder(String.valueOf(xwpfTableCell.getCTTc()));
                        tmp.delete(tmp.indexOf("<xml-fragment"), tmp.indexOf("\">")+"\">".length());
                        if(isHeader){
                            tableConstructor
                                    .append(TagConstructor.tableTitle.get("start"))
                                    .append(TABLE_SEPARATOR)
                                    .append(tmp)
                                    .append(TABLE_SEPARATOR)
                                    .append(TagConstructor.tableTitle.get("end"))
                                    .append("\n");
                        }else{
                            tableConstructor
                                    .append(TagConstructor.tableCell.get("start"))
                                    .append(TABLE_SEPARATOR)
                                    .append(tmp)
                                    .append(TABLE_SEPARATOR)
                                    .append(TagConstructor.tableCell.get("end"))
                                    .append("\n");
                        }
                    }
                }
                tableConstructor
                        .append(TagConstructor.tableRow.get("end"))
                        .append("\n");
                isHeader = false;
            }
            tableConstructor
                    .append(TagConstructor.table.get("end"))
                    .append("\n");
            codeTableCell.add(String.valueOf(tableConstructor));
            tableConstructor.delete(0, tableConstructor.length());
        }
        return codeTableCell;
    }
}
