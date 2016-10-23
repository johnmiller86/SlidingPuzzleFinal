package com.johnmillercoding.slidingpuzzle.models;

public class Settings {

    // Instance vars
    private int settingId, columns, rows;
    private String puzzlePath;

    /**
     * Gets the setting id.
     * @return the id.
     */
    public int getSettingId() {
        return settingId;
    }

    /**
     * Sets the setting id.
     * @param settingId the id.
     */
    public void setSettingId(int settingId) {
        this.settingId = settingId;
    }

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

//    /**
//     * Sets the free play puzzle's path.
//     * @return the puzzle path.
//     */
//    public String getPuzzlePath(){
//        return puzzlePath;
//    }
//
//    /**
//     * Sets the free play puzzle's path.
//     * @param puzzlePath the puzzle path.
//     */
//    public void setPuzzlePath(String puzzlePath){
//        this. puzzlePath = puzzlePath;
//    }
}