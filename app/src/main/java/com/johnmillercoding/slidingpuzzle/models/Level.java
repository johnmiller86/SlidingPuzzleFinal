package com.johnmillercoding.slidingpuzzle.models;

public class Level {

    private int levelNum, columns, rows, timeLimit, moveLimit;
    private String url;

    public Level(int levelNum){
        this.levelNum = levelNum;
        columns = 0;
        rows = 0;
        timeLimit = 0;
        moveLimit = 0;
    }

    public int getLevelNum() {
        return levelNum;
    }

    public void setLevelNum(int levelNum) {
        this.levelNum = levelNum;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getMoveLimit() {
        return moveLimit;
    }

    public void setMoveLimit(int moveLimit) {
        this.moveLimit = moveLimit;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
