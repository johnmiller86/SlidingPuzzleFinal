package com.johnmillercoding.slidingpuzzle.models;

public class Settings {

    // Instance vars
    private int columns, rows;

    /**
     * Gets the column settings.
     * @return the number of columns.
     */
    public int getColumns() {

        return columns;
    }

    /**
     * Sets the column settings.
     * @param columns the number of columns.
     */
    public void setColumns(int columns) {

        this.columns = columns;
    }

    /**
     * Gets the row settings.
     * @return the number of rows.
     */
    public int getRows() {

        return rows;
    }

    /**
     * Sets the row settings.
     * @param rows the number of rows.
     */
    public void setRows(int rows) {

        this.rows = rows;
    }
}