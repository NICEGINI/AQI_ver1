package kr.ac.kmu.cs.airpollution.database;

/**
 * Created by pabel on 2016-07-28.
 */
public class airDataQuery {
    //this is static..
    private float CO,SO2,NO2,O3,PM;
    private double lat,lng;
    private int TIME,TEMP;
    public static void insert_airData(String jsonObj){
        //json 파일을 받아서 파싱후 넣어줌

        //cursor = db.rawQuery("SELECT * FROM students;",null)// 선택
        //db.execSQL("INSERT INTO students VALUES ('"+stunum+"', '"+ name +"', '" + phone+ "');"); //디비추가
    }
}
