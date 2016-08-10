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

        try {
            db.execSQL(sql_create_air_table);
            db.execSQL(sql_create_LatLng_info);
            db.execSQL(sql_create_Heart_rate);
        } catch (SQLException e) {
            Log.e("SQLite", "db is not creation");
        }
    }

    public void reset() {
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + AIRDATA_TABLE_NAME);
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + LATLNG_TABLE_NAME);
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + HEARTRATE_TABLE_NAME);
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
        //db.close();
    }

    public void insert_heartrate_data(SQLiteDatabase db, long time, int heartrate) {
        ContentValues values = new ContentValues();
        values.put("TIME", time);
        values.put("", heartrate);
        db.insert(HEARTRATE_TABLE_NAME, null, values);
        //database.close();
    }

    public void insert_LATLNG_data(SQLiteDatabase db, long time, double LAT, double LNG) {
        ContentValues values = new ContentValues();
        values.put("TIME", time);
        values.put("lat", LAT);
        values.put("lng", LNG);
        db.insert(LATLNG_TABLE_NAME, null, values);
    }

    public void delete_data(SQLiteDatabase db, long currentTime) {
        db.delete(AIRDATA_TABLE_NAME, "time" + "<" + Long.toString(currentTime) + "- 86400", null);
        db.delete(HEARTRATE_TABLE_NAME, "time" + "<" + Long.toString(currentTime) + "- 86400", null);
        db.delete(LATLNG_TABLE_NAME, "time" + "<" + Long.toString(currentTime) + "- 86400", null);
    }
}
