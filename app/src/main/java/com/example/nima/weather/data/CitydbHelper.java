package com.example.nima.weather.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CitydbHelper extends SQLiteOpenHelper {
    Context context;

    public static final int DBVersion = 2;
    public static final String DBNAME = "db_city";
    public static final String TABLE_City = "tb_city";
    public static final String[] columns_of_database= {"id","name","lat","lon","countryCode","selected"};

    private static final String CMD_CREATE_CITY_TB = "CREATE TABLE "+ TABLE_City+
            " (id INTEGER PRIMARY KEY NOT NULL, name TEXT, lat DOUBLE, lon DOUBLE, countryCode TEXT, selected INTEGER)";

    public CitydbHelper(Context context) throws IOException {
        super(context, DBNAME, null, DBVersion);
        this.context = context;
//        if (getCities(null , null).isEmpty())
//            initContents();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CMD_CREATE_CITY_TB);
        Log.i("dbhelper" , "database is created.");
//            initContents();


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int olderVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_City);
        onCreate(db);
    }

//    public void initContents() {
//       Thread thread = new Thread(new Runnable() {
//           @Override
//           public void run() {
//               try {
//                   InputStream stream = context.getResources().openRawResource(R.raw.city_list);
//                   BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
//                   String line = " ";
//                   SQLiteDatabase db = getWritableDatabase();
//                   while ((line = reader.readLine()) != null) {
//                       String[] data = line.split("\t");
//                       if (data.length < 5)
//                           continue;
//
//                       CityModel city = new CityModel();
//                       city.setId(Long.valueOf(data[0]));
//                       city.setName(data[1]);
//                       city.setLat(Double.valueOf(data[2]));
//                       city.setLon(Double.valueOf(data[3]));
//                       city.setCountryCode(data[4]);
//
//                      long insertId = db.insert(TABLE_City, null, CityModel.createContentValues(Long.valueOf(data[0]),
//                               data[1], Double.valueOf(data[2]), Double.valueOf(data[3]), data[4], false));
//                      Log.i("dbhelper" , "city inserted "+insertId);
//                   }
//                   db.close();
//               }catch (IOException e) {
//                   e.printStackTrace();
//               }
//           }
//       });
//       thread.start();
//    }

    public void updateCitySelected(long id , boolean selected){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("selected" , selected ? 1 :0);
        db.update(TABLE_City, cv, "id = " + id , null);
        db.close();

    }

    private void insertCity(CityModel city) {
        SQLiteDatabase db = this.getWritableDatabase();
        CityModel cityModel = new CityModel();
        db.insert(TABLE_City, null , cityModel.getContentValuesForDb());
//        Log.i("dbhelper" , "city inserted with id" + insert );
        db.close();
    }

    public List<CityModel> getCities(String selections , String[] selectionArgs){
        List<CityModel> cityList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_City,columns_of_database,selections,selectionArgs , null,null,"countryCode, name");
        Log.d("dbhelper","Cursor returned " + cursor.getCount() +" records" );
        if (cursor.moveToFirst()){
            do {
                cityList.add(CityModel.fromCursor(cursor));
            }while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return cityList;
    }

    public List<CityModel> searchCityByName(String cityname ,String limit){
        List<CityModel> cityList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
       Cursor cursor = db.query(TABLE_City,columns_of_database ,
                "name LIKE '%" +cityname + "%'",
                null,null,null,
                "countryCode, name",limit);
        Log.d("dbhelper","Cursor returned " + cursor.getCount() +" records" );

        if (cursor.moveToFirst()){
            do {
                cityList.add(CityModel.fromCursor(cursor));
            }while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return cityList;
    }
}
