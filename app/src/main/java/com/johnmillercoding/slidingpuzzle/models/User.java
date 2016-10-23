package com.johnmillercoding.slidingpuzzle.models;

public class User {

    // Instance vars
    private int userId;
    private String email, password;

    /**
     * Gets the user's id.
     * @return the id.
     */
    public int getUserId() {

        return userId;
    }

    /**
     * Sets the user's id.
     * @param userId the id.
     */
    public void setUserId(int userId){
        this.userId = userId;
    }

    /**
     * Gets the user's email.
     * @return the email.
     */
    public String getEmail() {

        return email;
    }

    /**
     * Sets the user's email.
     * @param email the email.
     */
    public void setEmail(String email) {

        this.email = email;
    }

    /**
     * Gets the user's password.
     * @return the password.
     */
    public String getPassword() {

        return password;
    }

    /**
     * Sets the user's password.
     * @param password the password.
     */
    public void setPassword(String password) {

        this.password = password;
    }
}