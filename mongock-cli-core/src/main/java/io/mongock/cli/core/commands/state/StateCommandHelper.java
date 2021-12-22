package io.mongock.cli.core.commands.state;

import com.diogonunes.jcolor.Attribute;
import io.mongock.cli.util.output.OutputFormatter;
import io.mongock.professional.runner.common.executor.operation.state.StateOpResultItem;
import io.mongock.professional.runner.common.executor.operation.state.StateOpResultItemOrigin;
import io.mongock.professional.runner.common.executor.operation.state.StateOpResultItemState;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.diogonunes.jcolor.Attribute.BOLD;
import static com.diogonunes.jcolor.Attribute.CYAN_TEXT;
import static com.diogonunes.jcolor.Attribute.GREEN_TEXT;
import static com.diogonunes.jcolor.Attribute.RED_TEXT;
import static com.diogonunes.jcolor.Attribute.YELLOW_TEXT;

public class StateCommandHelper {
  
  private static final String COLUMN_CHANGE_ID = "changeId";
  private static final String COLUMN_AUTHOR = "author";
  private static final String COLUMN_STATE = "state";
  private static final String COLUMN_DETAILS = "details";
  private static final String COLUMN_UPDATED_AT = "updated at";
  private static final String COLUMN_UNDOABLE = "undoable";
  private static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
  
  
  public static void printDbTable(List<StateOpResultItem> items) {

    String[][] header = new String[][]{{COLUMN_CHANGE_ID, COLUMN_AUTHOR, COLUMN_STATE, COLUMN_DETAILS, COLUMN_UPDATED_AT}};

    // Print only items that exists in db
    String[][] rows = items.stream()
                           .filter(item -> !item.getOrigin().equals(StateOpResultItemOrigin.CHANGE_SET))
            .map(c -> new String[]{
                            c.getId(),
                            c.getAuthor(),
                            formatState(c.getState()),
                            getDetails(c, false),
                            formatExecutionDate(c.getTimestamp())
            })
            .toArray(String[][]::new);

    printTable(header, rows);
  }
  
  public static void printCodeBaseTable(List<StateOpResultItem> items) {

    String[][] header = new String[][]{{COLUMN_CHANGE_ID, COLUMN_AUTHOR, COLUMN_STATE, COLUMN_DETAILS, COLUMN_UPDATED_AT, COLUMN_UNDOABLE}};

    // Print only items that exists in code
    String[][] rows = items.stream()
                           .filter(item -> !item.getOrigin().equals(StateOpResultItemOrigin.CHANGE_ENTRY))
            .map(c -> new String[]{
                            c.getId(),
                            c.getAuthor(),
                            formatState(c.getState()),
                            getDetails(c, false),
                            formatExecutionDate(c.getTimestamp()),
                            formatUndoable(c.isUndoable())
            })
            .toArray(String[][]::new);

    printTable(header, rows);
  }
  
  public static void printCompareTable(List<StateOpResultItem> items) {

    String[][] header = new String[][]{{COLUMN_CHANGE_ID, COLUMN_AUTHOR, COLUMN_STATE, COLUMN_DETAILS, COLUMN_UPDATED_AT, COLUMN_UNDOABLE}};

    // Print all items
    String[][] rows = items.stream()
            .map(c -> new String[]{
                            c.getId(),
                            c.getAuthor(),
                            formatState(c.getState()),
                            getDetails(c, true),
                            formatExecutionDate(c.getTimestamp()),
                            formatUndoable(c.isUndoable())
            })
            .toArray(String[][]::new);

    printTable(header, rows);
  }
  
  private static void printTable(String[][] header, String [][] rows) {
    
    String[][] table = Stream.concat(Arrays.stream(header), Arrays.stream(rows)).toArray(String[][]::new);

    System.out.println("");
    OutputFormatter.printTable(table, 0, System.out::print, StateCommandHelper::customColumnFormatter);
    System.out.println("");
  }
  
  private static String formatState(StateOpResultItemState state) {
    return state != null ? state.toString() : null;
  }
  
  private static String formatExecutionDate(Date date) {
    return date != null ? TIMESTAMP_FORMAT.format(date) : null;
  }
  
  private static String getDetails(StateOpResultItem item, boolean isCompareOp) {
    List<String> details = new ArrayList<>();
    
    if (isCompareOp && item.getOrigin().equals(StateOpResultItemOrigin.CHANGE_ENTRY)) {
      details.add("Not found in code");
    }
    
    if (item.getChangeEntryState() != null) {
      switch (item.getChangeEntryState()) {
        case FAILED:
          details.add("Execution failed");
          break;
        case ROLLBACK_FAILED:
          details.add("Rollback failed");
          break;
        case ROLLED_BACK:
          details.add("Has been rolled back");
          break;
      }
    }
    
    return details.size() > 0 
            ? details.stream().collect(Collectors.joining(", ")) 
            : null;
  }
  
  private static String formatUndoable(boolean isUndoable) {
    return isUndoable ? "[X]" : "[ ]";
  }

  private static Attribute customColumnFormatter(String columnName, boolean isHeaderRow, String value) {
    if (isHeaderRow) {
      return BOLD();
    }
    else if (columnName.equals(COLUMN_CHANGE_ID)) {
      return CYAN_TEXT();
    }
    else if (columnName.equals(COLUMN_STATE) && value != null) {
      switch (StateOpResultItemState.valueOf(value)) {
        case EXECUTED:
          return GREEN_TEXT();
        case PENDING:
          return YELLOW_TEXT();
        case FAILED:
          return RED_TEXT();
      }
    }
    return null;
  }
}
