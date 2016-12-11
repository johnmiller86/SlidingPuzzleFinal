package com.johnmillercoding.slidingpuzzle.models;

public class Level {


    // Current number of levels
    public static final int NUM_LEVELS = 18;

    // Instance vars
    private int levelNum, columns, rows, timeLimit, moveLimit;
    private String url;

    // Constructor
    public Level(int levelNum){
        this.levelNum = levelNum;
        columns = 0;
        rows = 0;
        timeLimit = 0;
        moveLimit = 0;
    }

    /**
     * Gets the level's number.
     * @return sets the number.
     */
    public int getLevelNum() {
        return levelNum;
    }

    /**
     * Sets the level's number.
     * @param levelNum the number.
     */
    public void setLevelNum(int levelNum) {
        this.levelNum = levelNum;
    }

    /**
     * Gets the level's columns.
     * @return the columns.
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Sets the level's columns.
     * @param columns the columns.
     */
    public void setColumns(int columns) {
        this.columns = columns;
    }

    /**
     * Gets the level's rows.
     * @return the rows.
     */
    public int getRows() {
        return rows;
    }

    /**
     * Sets the level's rows.
     * @param rows the rows.
     */
    public void setRows(int rows) {
        this.rows = rows;
    }

    /**
     * Gets the level's time limit.
     * @return the time limit.
     */
    public int getTimeLimit() {
        return timeLimit;
    }

    /**
     * Sets the level's time limit.
     * @param timeLimit the time limit.
     */
    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    /**
     * Gets the level's move limit.
     * @return the moves.
     */
    public int getMoveLimit() {
        return moveLimit;
    }

    /**
     * Sets the level's move limit.
     * @param moveLimit the moves.
     */
    public void setMoveLimit(int moveLimit) {
        this.moveLimit = moveLimit;
    }

    /**
     * Gets the level's url.
     * @return the url.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the level's url.
     * @param url the url.
     */
    public void setUrl(String url) {
        this.url = url;
    }
}
