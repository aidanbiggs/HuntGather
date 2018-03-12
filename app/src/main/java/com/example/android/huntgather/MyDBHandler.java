package com.example.android.huntgather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

        Date currentTimeCalendar = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");
        String currentTime = formatter.format(currentTimeCalendar);
        values.put(COLUMN_START_TIME, String.valueOf(currentTime));

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_HUNTS,null,values);
        db.close();
    }


    //update huntTimer from db
    public void updateHuntTimer(String huntCode){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        Date currentTimeCalendar = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");
        String currentTime = formatter.format(currentTimeCalendar);
        values.put(COLUMN_END_TIME, String.valueOf(currentTime));

        db.update(TABLE_HUNTS,values,"huntCode=\""+ huntCode + "\";",null);
        db.close();

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

    public String timeDifference() {
        SQLiteDatabase db = getWritableDatabase();
        Log.d(TAG, "getTableAsString called");
        String tableString = String.format("Table %s:\n", TABLE_HUNTS);
        String startTime = "";
        String endTime = "";

        Cursor allRows = db.rawQuery("SELECT * FROM " + TABLE_HUNTS, null);
        if (allRows.moveToFirst()) {
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name : columnNames) {

                    if (name.equals(COLUMN_START_TIME)) {
                        startTime = allRows.getString(allRows.getColumnIndex(name));

                    }
                    if (name.equals(COLUMN_END_TIME)) {
                        endTime = allRows.getString(allRows.getColumnIndex(name));

                    }

                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss");
        Date startTimeDate = new Date();
        Date endTimeDate = new Date();
        try {
            startTimeDate = formatter.parse(startTime.trim());
            endTimeDate = formatter.parse(endTime.trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }



        return printDifference(startTimeDate,endTimeDate);

    }


    public void clearDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        String clearDBQuery = "DELETE FROM "+TABLE_HUNTS;
        db.execSQL(clearDBQuery);
    }

    public String printDifference(Date startDate, Date endDate) {

        String returnDifference = "";

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

//        System.out.println("startDate : " + startDate);
//        System.out.println("endDate : "+ endDate);
//        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        //System.out.printf(  "%d days, %d hours, %d minutes, %d seconds%n",elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);


        if((elapsedDays != 0) && (elapsedHours != 0) && (elapsedMinutes !=0) && (elapsedSeconds !=0)){

            returnDifference = elapsedDays + " days, " + elapsedHours + " hours, " + elapsedMinutes + "minutes, "  + elapsedSeconds+ " seconds";

        }else if((elapsedHours != 0) && (elapsedMinutes !=0) && (elapsedSeconds !=0)){

            returnDifference = elapsedHours + " hours, " + elapsedMinutes + " minutes, "  + elapsedSeconds + " seconds";

        }else if((elapsedMinutes !=0) && (elapsedSeconds !=0)){

            returnDifference = elapsedMinutes + " minutes, "  + elapsedSeconds + " seconds";

        }else if((elapsedSeconds !=0)){

        returnDifference =  elapsedSeconds + " seconds";

        }

        return returnDifference;



    }


    public void openDB(){

        SQLiteDatabase db = getWritableDatabase();

    }

}



