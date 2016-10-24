package com.johnmillercoding.slidingpuzzle.models;

public class Settings {

    // Instance vars
    private int settingId, columns, rows;
    // --Commented out by Inspection (10/23/2016 1:25 AM):private String puzzlePath;

// --Commented out by Inspection START (10/23/2016 1:25 AM):
//    /**
//     * Gets the setting id.
//     * @return the id.
//     */
//    public int getSettingId() {
//        return settingId;
//    }
// --Commented out by Inspection STOP (10/23/2016 1:25 AM)

// --Commented out by Inspection START (10/23/2016 1:25 AM):
//    /**
//     * Sets the setting id.
//     * @param settingId the id.
//     */
//    public void setSettingId(int settingId) {
//        this.settingId = settingId;
//    }
// --Commented out by Inspection STOP (10/23/2016 1:25 AM)

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
//     * Sets the free play puzzleFunctions's path.
//     * @return the puzzleFunctions path.
//     */
//    public String getPuzzlePath(){
//        return puzzlePath;
//    }
//
//    /**
//     * Sets the free play puzzleFunctions's path.
//     * @param puzzlePath the puzzleFunctions path.
//     */
//    public void setPuzzlePath(String puzzlePath){
//        this. puzzlePath = puzzlePath;
//    }
}