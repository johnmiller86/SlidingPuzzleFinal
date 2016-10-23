package com.johnmillercoding.slidingpuzzle.utilities;

/**
 * Class to handle all puzzle DB functions.
 * @author John D. Miller.
 * @version 1.0.1
 * @since 09/10/2016
 */
public class LevelFunctions {

//    // Table name
//    static final String LEVELS_TABLE = "level";
//
//    // Column names
//    private static final String LEVEL_ID = "level_id";
//    private static final String USER_ID = "user_id";
//    private static final String LEVEL_NUM = "level_num";
//    private static final String COLUMNS = "columns";
//    private static final String ROWS = "rows";
//    private static final String IMAGE_BYTES = "image_bytes";
//
//
//    /**
//     * Builds the puzzles table create statement.
//     * @return the SQL statement.
//     */
//    static String createTable(){
//        return "CREATE TABLE " + LEVELS_TABLE  + "("
//                + LEVEL_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//                + USER_ID + " INTEGER, "
//                + LEVEL_NUM + " INTEGER, "
//                + COLUMNS + " INTEGER, "
//                + ROWS + " INTEGER, "
//                + IMAGE_BYTES + " BLOB, "
//                + "FOREIGN KEY(" + USER_ID + ") REFERENCES " + UserFunctions.USERS_TABLE + "(" + USER_ID + "))";
//    }
//
//
//    public void insert(User user, Level level) {
//        SQLiteDatabase db = DatabaseManager.getDatabaseManager().openDatabase();
//        ContentValues values = new ContentValues();
//        values.put(USER_ID, user.getUserId());
//        values.put(LEVEL_NUM, level.getLevelNum());
//        values.put(COLUMNS, level.getCols());
//        values.put(ROWS, level.getRows());
//
//
//        // Inserting Row
////        db.insert(PUZZLES_TABLE, null, values);
//        DatabaseManager.getDatabaseManager().closeDatabase();
//    }

//    /**
//     * Updates the puzzle in the database.
//     * @param user the user.
//     * @param puzzle the new puzzle.
//     */
//    public void update(User user, Level level){
//
//        // Database
//        SQLiteDatabase db = DatabaseManager.getDatabaseManager().openDatabase();
//
//        // Values
//        ContentValues values = new ContentValues();
//        values.put(USER_ID, user.getUserId());
//        values.put(LEVEL_NUM, level.getLevelNum());
//
//        // Where
//        String where = USER_ID + " = ? AND " + LEVEL_NUM + " = ?";
//        String[] id = {String.valueOf(puzzle.getPuzzleId()), };
//
//        // Inserting Row
//        db.update(PUZZLES_TABLE, values, where, id);
//        DatabaseManager.getDatabaseManager().closeDatabase();
//    }
//
//    /**
//     * Gets the puzzles that have been added to the DB.
//     * @return the puzzle list.
//     */
//    public Puzzle getPuzzle(User user){
//
//        Puzzle puzzle = new Puzzle();
//        SQLiteDatabase db = DatabaseManager.getDatabaseManager().openDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM " + PUZZLES_TABLE + " WHERE " + USER_ID + " = ?", new String[]{String.valueOf(user.getUserId())});
//
//        while (cursor.moveToNext()){
//
//            puzzle.setPuzzleId(cursor.getInt(cursor.getColumnIndex(PUZZLE_ID)));
//            puzzle.setPuzzlePath(cursor.getString(cursor.getColumnIndex(PUZZLE_PATH)));
//        }
//        cursor.close();
//        DatabaseManager.getDatabaseManager().closeDatabase();
//        return puzzle;
//    }
}