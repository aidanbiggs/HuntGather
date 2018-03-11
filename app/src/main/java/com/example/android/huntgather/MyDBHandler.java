package com.example.android.huntgather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Aidan on 11/03/2018.
 */

public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "HuntAndGather.db";
    private static final String TABLE_HUNTS = "huntTimers";
    private static final String COLUMN_HUNT_CODE = "huntCode";
    private static final String COLUMN_START_TIME = "startTime";
    private static final String COLUMN_END_TIME = "endTime";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_HUNTS + " (" +
                COLUMN_HUNT_CODE + " TEXT PRIMARY KEY, " +
                COLUMN_START_TIME + " TEXT, " +
                COLUMN_END_TIME + " TEXT );";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_HUNTS);
        onCreate(sqLiteDatabase);
    }

    //Add a new row to the database
    public void addHuntTimer( String huntCode){
        ContentValues values = new ContentValues();

        values.put(COLUMN_HUNT_CODE, huntCode);

        Date currentTime = Calendar.getInstance().getTime();
        values.put(COLUMN_START_TIME, String.valueOf(currentTime));

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_HUNTS,null,values);
        db.close();
    }


    //Delete huntTimer from db
    public void updateHuntTimer(String huntCode){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        Date currentTime = Calendar.getInstance().getTime();
        values.put(COLUMN_END_TIME, String.valueOf(currentTime));

        db.update(TABLE_HUNTS,values,"huntCode="+ huntCode,null);

    }

    //Delete huntTimer from db
    public void deleteHuntTimer(String huntCode){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_HUNTS + " WHERE " + COLUMN_HUNT_CODE + " =\"" + huntCode + "\";" );

    }

    String TAG = "DbHelper";

    /**
     * Helper function that parses a given table into a string
     * and returns it for easy printing. The string consists of
     * the table name and then each row is iterated through with
     * column_name: value pairs printed out.
     *
     *
     * @return the table tableName as a string
     */
    public String getTableAsString() {
        SQLiteDatabase db = getWritableDatabase();
        Log.d(TAG, "getTableAsString called");
        String tableString = String.format("Table %s:\n", TABLE_HUNTS);
        Cursor allRows  = db.rawQuery("SELECT * FROM " + TABLE_HUNTS, null);
        if (allRows.moveToFirst() ){
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name: columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }

        return tableString;
    }

    public void clearDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        String clearDBQuery = "DELETE FROM "+TABLE_HUNTS;
        db.execSQL(clearDBQuery);
    }

}



