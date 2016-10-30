package com.johnmillercoding.slidingpuzzle.models;

public class NavDrawerItem {

    // Instance var
    private String title;


    // Constructor
    public NavDrawerItem() {
        title = "";
    }

    /**
     * The drawer item's title.
     * @return the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the drawer item's title.
     * @param title the title.
     */
    public void setTitle(String title) {
        this.title = title;
    }
}