package io.mongock.cli.util.output;

import com.diogonunes.jcolor.Attribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.diogonunes.jcolor.Ansi.colorize;

public class OutputFormatter {

  private static final String LINE_SEPARATOR = System.lineSeparator();

  public static void printTable(String[][] table, int maxWidth, Consumer<String> printer, CustomColumnFormatter customColumnFormatter) {
    
    if (table == null || table.length == 0) {
      return;
    }
    
    /*
	 * leftJustifiedRows - If true, it will add "-" as a flag to format string to
	 * make it left justified. Otherwise right justified.
     */
    boolean leftJustifiedRows = true;
    
    /*
         * Custom column formatter
     */
    int rows = table.length;
    int columns = table[0].length;
    Attribute[][] attributes = new Attribute[rows][columns];
    if (customColumnFormatter != null) {
      Stream.iterate(0, i -> ++i)
            .limit(rows)
            .forEach(i -> Stream.iterate(0, j -> ++j)
                                .limit(columns)
                                .forEach(j -> {
                                  attributes[i][j] = customColumnFormatter.get(table[0][j], i == 0, table[i][j]);
                                })
            );
    }
    
    /*
         * Check nulls
    */
    Arrays.stream(table).forEach(a -> Stream.iterate(0, (i -> ++i)).limit(a.length).forEach(i -> {
      if (a[i] == null) {
        a[i] = "";
      }
    }));

    /*
	 * Create new table array with wrapped rows
     */
    String[][] finalTable;
    Attribute[][] finalAttributes;
    if (maxWidth > 0) {
      List<String[]> tableList = new ArrayList<>(Arrays.asList(table));
      List<String[]> finalTableList = new ArrayList<>();
      List<Attribute[]> finalAttributeList = new ArrayList<>();
      //for (String[] row : tableList) {
      for (int i=0; i<rows; i++) {
        String[] row = tableList.get(i);
        // If any cell data is more than max width, then it will need extra row.
        boolean needExtraRow = false;
        // Count of extra split row.
        int splitRow = 0;
        do {
          needExtraRow = false;
          String[] newRow = new String[row.length];
          for (int j = 0; j < row.length; j++) {
            // If data is less than max width, use that as it is.
            if (row[j].length() < maxWidth) {
              newRow[j] = splitRow == 0 ? row[j] : "";
            } else if ((row[j].length() > (splitRow * maxWidth))) {
              // If data is more than max width, then crop data at maxwidth.
              // Remaining cropped data will be part of next row.
              int end = row[j].length() > ((splitRow * maxWidth) + maxWidth)
                      ? (splitRow * maxWidth) + maxWidth
                      : row[j].length();
              newRow[j] = row[j].substring((splitRow * maxWidth), end);
              needExtraRow = true;
            } else {
              newRow[j] = "";
            }
          }
          finalTableList.add(newRow);
          finalAttributeList.add(attributes[i]);
          if (needExtraRow) {
            splitRow++;
          }
        } while (needExtraRow);
      }
      finalTable = new String[finalTableList.size()][finalTableList.get(0).length];
      for (int i = 0; i < finalTable.length; i++) {
        finalTable[i] = finalTableList.get(i);
      }
      rows = finalTable.length;
      finalAttributes = new Attribute[finalAttributeList.size()][columns];
      for (int i = 0; i < finalAttributes.length; i++) {
        finalAttributes[i] = finalAttributeList.get(i);
      }
    }
    else {
      finalTable = table;
      finalAttributes = attributes;
    }

    /*
	 * Calculate appropriate Length of each column by looking at width of data in
	 * each column.
	 * 
	 * Map columnLengths is <column_number, column_length>
     */
    Map<Integer, Integer> columnLengths = new HashMap<>();
    Arrays.stream(finalTable).forEach(a -> Stream.iterate(0, (i -> ++i)).limit(a.length).forEach(i -> {
      if (columnLengths.get(i) == null) {
        columnLengths.put(i, 0);
      }
      if (columnLengths.get(i) < a[i].length()) {
        columnLengths.put(i, a[i].length());
      }
    }));

    /*
         * Default column formatter (size)
     */
    String flag = leftJustifiedRows ? "-" : "";
    Arrays.stream(finalTable)
            .forEach(a -> Stream.iterate(0, (i -> ++i)).limit(a.length).forEach(i -> {
      a[i] = String.format("%" + flag + columnLengths.get(i) + "s", a[i]);
    }));

    /*
         * Custom column formatter
     */
    if (finalAttributes != null) {
      Stream.iterate(0, i -> ++i)
            .limit(rows)
            .forEach(i -> Stream.iterate(0, j -> ++j)
                                .limit(columns)
                                .forEach(j -> {
                                  if (finalAttributes[i][j] != null) {
                                    finalTable[i][j] = colorize(finalTable[i][j], finalAttributes[i][j]);
                                  }
                                })
            );
    }

    /*
	 * Prepare format String
     */
    final StringBuilder formatString = new StringBuilder("");
    columnLengths.entrySet().stream().forEach(e -> formatString.append("| %s "));
    formatString.append("|");
    formatString.append(LINE_SEPARATOR);

    /*
	 * Prepare line for top, bottom & below header row.
     */
    String line = columnLengths.entrySet().stream().reduce("", (ln, b) -> {
      String templn = "+-";
      templn = templn + Stream.iterate(0, (i -> ++i))
              .limit(b.getValue())
              .reduce("", (ln1, b1) -> ln1 + "-", (a1, b1) -> a1 + b1);
      templn = templn + "-";
      return ln + templn;
    }, (a, b) -> a + b);
    line = line + "+" + LINE_SEPARATOR;

    /*
	 * Generate table
     */
    StringBuilder result = new StringBuilder();
    result.append(line);

    // Header
    Arrays.stream(finalTable)
            .limit(1)
            .forEach(a -> result.append("| ").append(String.join(" | ", a)).append(" |").append(LINE_SEPARATOR));
    result.append(line);
    // Rows
    Stream.iterate(1, (i -> ++i))
            .limit(finalTable.length - 1)
            .forEach(a -> result.append("| ").append(String.join(" | ", finalTable[a])).append(" |").append(LINE_SEPARATOR));
    result.append(line);

    // Print to consumer
    printer.accept(result.toString());
  }
}
