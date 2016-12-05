package com.johnmillercoding.slidingpuzzle.models;

public class LeaderboardEntry {

    // Instance vars
    private int level_num, moves, score;
    private String time, email;

    /**
     * Gets the leaderboard entry's email.
     * @return the email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the leaderboard entry's email.
     * @param email the email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the leaderboard entry's moves.
     * @return the moves.
     */
    public int getMoves() {
        return moves;
    }

    /**
     * Sets the leaderboard entry's moves.
     * @param moves the moves.
     */
    public void setMoves(int moves) {
        this.moves = moves;
    }

    /**
     * Gets the leaderboard entry's score.
     * @return the score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the leaderboard entry's score.
     * @param score the score.
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Gets the leaderboard entry's time.
     * @return the time.
     */
    public String getTime() {
        return time;
    }

    /**
     * Sets the leaderboard entry's time.
     * @param time the time.
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Gets the leaderboard entry's level number.
     * @return the level number.
     */
    public int getLevel_num() {
        return level_num;
    }

    /**
     * Sets the leaderboard entry's level number.
     * @param level_num the level number.
     */
    public void setLevel_num(int level_num) {
        this.level_num = level_num;
    }
}