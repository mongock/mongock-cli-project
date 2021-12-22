package io.mongock.cli.util.output;

import com.diogonunes.jcolor.Attribute;

@FunctionalInterface
public interface CustomColumnFormatter {
    public Attribute get(String columnName, boolean isHeaderRow, String value);
}