package kr.ac.kmu.cs.airpollution.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by pabel on 2016-07-27.
 */
public class airDatabaseOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "test.db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    public static final String AIRDATATABLENAME = "AIRDATA";
    public static final String LATLNGTABLENAME = "LATLNG";
    public static final String HEARTRATETABLENAME = "HEARTRATE";

    private static airDatabaseOpenHelper mDBHelper;
    private Context mCtx;

    public airDatabaseOpenHelper(Context context) {
        super(context, "test.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        String sql_Air_Upgrade = "DROP TABLE IF EXISTS " + AIRDATATABLENAME;
        String sql_LATLNG_Upgrade = "DROP TABLE IF EXISTS " + LATLNGTABLENAME;
        String sql_Heart_Upgrade = "DROP TABLE IF EXISTS " + HEARTRATETABLENAME;
        db.execSQL(sql_Air_Upgrade);
        db.execSQL(sql_LATLNG_Upgrade);
        db.execSQL(sql_Heart_Upgrade);
        onCreate(db);
    }

    public void createTable(SQLiteDatabase db){
        String sql_create_air_table = "CREATE TABLE IF NOT EXISTS " + AIRDATATABLENAME + " (time INTEGER primary key, "+
                "CO REAL, SO2 REAL, NO2 REAL, PM REAL, O3 REAL, TEMP INTEGER);";

        String sql_create_LatLng_info = "CREATE TABLE IF NOT EXISTS " + LATLNGTABLENAME + " (time INTEGER primary key, "+
                "lat REAL, lng REAL);";

        String sql_create_Heart_rate = "CREATE TABLE IF NOT EXISTS " + HEARTRATETABLENAME + " (time INTEGER primary key, "+
                "heartrate INTEGER);";

        try{
            db.execSQL(sql_create_air_table);
            db.execSQL(sql_create_LatLng_info);
            db.execSQL(sql_create_Heart_rate);
        } catch (SQLException e){
            Log.e("SQLite","db is not creation");
        }
    }

    public String PrintData() {
        SQLiteDatabase db = getReadableDatabase();
        String str = "";

        Cursor cursor = db.rawQuery("select * from "+AIRDATATABLENAME, null);
        while (cursor.moveToNext()) {
            str += cursor.getInt(0)
                    +" : TIME"
                    +cursor.getInt(1)
                    +" : CO"
                    +cursor.getDouble(2)
                    +" : SO2"
                    +cursor.getDouble(3)
                    +" : NO2"
                    +cursor.getDouble(4)
                    +" : PM"
                    +cursor.getDouble(5)
                    +" : O3"
                    +cursor.getInt(6)
                    +" : TEMP"
                    + "\n";
        }
        return str;
    }

    public void reset() {
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + AIRDATATABLENAME);
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + LATLNGTABLENAME);
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + HEARTRATETABLENAME);
        createTable(getWritableDatabase());
    }
}
