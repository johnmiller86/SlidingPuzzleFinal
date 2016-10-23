//package com.johnmillercoding.slidingpuzzle.utilities;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//public class DBHelper extends SQLiteOpenHelper{
//
//    // Defining the database and tables
//    private static final int DATABASE_VERSION = 2;
//    private static final String DATABASE_NAME = "Sliding_Puzzle";
//
//    // Constructor
//    public DBHelper(Context context){
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//
//    // Create database
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(UserFunctions.createTable());
////        db.execSQL(PuzzleFunctions.createTable());
////        db.execSQL(SettingFunctions.createTable());
////        db.execSQL(LeaderboardFunctions.createTable());
//    }
//
//    // Upgrade database
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + UserFunctions.USERS_TABLE);
////        db.execSQL("DROP TABLE IF EXISTS " + PuzzleFunctions.PUZZLES_TABLE);
////        db.execSQL("DROP TABLE IF EXISTS " + SettingFunctions.SETTINGS_TABLE);
//        db.execSQL("DROP TABLE IF EXISTS " + LeaderboardFunctions.LEADERBOARDS_TABLE);
//        onCreate(db);
//    }
//}
