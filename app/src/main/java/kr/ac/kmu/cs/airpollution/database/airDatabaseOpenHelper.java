package kr.ac.kmu.cs.airpollution.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class airDatabaseOpenHelper extends SQLiteOpenHelper {
    public static final String AIRDATA_TABLE_NAME = "AIRDATA";
    public static final String LATLNG_TABLE_NAME = "LATLNG";
    public static final String HEARTRATE_TABLE_NAME = "HEARTRATE";
    public static final String USER_TABLE_NAME = "USER";

    public airDatabaseOpenHelper(Context context) {
        super(context, "Airdata.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        String sql_Air_Upgrade = "DROP TABLE IF EXISTS " + AIRDATA_TABLE_NAME;
        String sql_LATLNG_Upgrade = "DROP TABLE IF EXISTS " + LATLNG_TABLE_NAME;
        String sql_Heart_Upgrade = "DROP TABLE IF EXISTS " + HEARTRATE_TABLE_NAME;
        db.execSQL(sql_Air_Upgrade);
        db.execSQL(sql_LATLNG_Upgrade);
        db.execSQL(sql_Heart_Upgrade);
        onCreate(db);
    }

    public void createTable(SQLiteDatabase db) {
        String sql_create_air_table = "CREATE TABLE IF NOT EXISTS " + AIRDATA_TABLE_NAME + " (time INTEGER primary key, " +
                "CO REAL, SO2 REAL, NO2 REAL, PM REAL, O3 REAL, TEMP INTEGER);";

        String sql_create_LatLng_info = "CREATE TABLE IF NOT EXISTS " + LATLNG_TABLE_NAME + " (time INTEGER primary key, " +
                "lat REAL, lng REAL);";

        String sql_create_Heart_rate = "CREATE TABLE IF NOT EXISTS " + HEARTRATE_TABLE_NAME + " (time INTEGER primary key, " +
                "heartrate INTEGER);";

        String sql_create_User_table = "CREATE TABLE IF NOT EXISTS " + USER_TABLE_NAME + " (EMAIL TEXT primary key, " +
               "user_circlesize INTEGER, user_realtime_chart_range INTEGER, user_UDOO_Receive_Time );";

        try {
            db.execSQL(sql_create_air_table);
            db.execSQL(sql_create_LatLng_info);
            db.execSQL(sql_create_Heart_rate);
            db.execSQL(sql_create_User_table);
        } catch (SQLException e) {
            Log.e("SQLite", "db is not creation");
        }
    }

    public void reset() {
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + AIRDATA_TABLE_NAME);
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + LATLNG_TABLE_NAME);
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + HEARTRATE_TABLE_NAME);
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        createTable(getWritableDatabase());
    }

    public void insert_air_data(SQLiteDatabase db, long time, float co, float so2, float no2, float pm, float o3, int temp) {
        ContentValues values = new ContentValues();
        values.put("TIME", time);
        values.put("CO", co);
        values.put("SO2", so2);
        values.put("NO2", no2);
        values.put("PM", pm);
        values.put("O3", o3);
        values.put("TEMP", temp);
        db.insert(AIRDATA_TABLE_NAME, null, values);
    }

    public void insert_heartrate_data(SQLiteDatabase db, long time, int heartrate) {
        ContentValues values = new ContentValues();
        values.put("TIME", time);
        values.put("", heartrate);
        db.insert(HEARTRATE_TABLE_NAME, null, values);
    }

    public void insert_LATLNG_data(SQLiteDatabase db, long time, double LAT, double LNG) {
        ContentValues values = new ContentValues();
        values.put("TIME", time);
        values.put("lat", LAT);
        values.put("lng", LNG);
        db.insert(LATLNG_TABLE_NAME, null, values);
    }

    public void insert_User_data(SQLiteDatabase db, String email ,int circlesize, int realtimerange, int UDOOboardRtime) {
        ContentValues values = new ContentValues();
        values.put("EMAIL", email);
        values.put("user_circlesize", circlesize);
        values.put("user_realtime_chart_range", realtimerange);
        values.put("user_UDOO_Receive_Time", UDOOboardRtime);
        db.insert(LATLNG_TABLE_NAME, null, values);
    }

    //delete row after 24 Hour
    public void delete_data(SQLiteDatabase db, long currentTime) {
        db.delete(AIRDATA_TABLE_NAME, "time" + "<" + Long.toString(currentTime) + "- 86400", null);
        db.delete(HEARTRATE_TABLE_NAME, "time" + "<" + Long.toString(currentTime) + "- 86400", null);
        db.delete(LATLNG_TABLE_NAME, "time" + "<" + Long.toString(currentTime) + "- 86400", null);
    }

    public void delete_user_data(SQLiteDatabase db, String email){
        db.delete(USER_TABLE_NAME, "EMAIL = " + email, null);
    }
}
