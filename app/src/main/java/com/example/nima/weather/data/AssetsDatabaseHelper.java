package com.example.nima.weather.data;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AssetsDatabaseHelper {

    private Context context;
    private String dbname = "db_city";
    public AssetsDatabaseHelper(Context context){
        this(context , "db_city");
    }
    public AssetsDatabaseHelper(Context context , String dbname){
        this.context = context;
        this.dbname = dbname;
    }
    public void ckeckdb(){
       File dbFile = context.getDatabasePath(dbname);
       if (!  dbFile.exists()){
           try {
               copyDatabase(dbFile);
               Log.i("AssetsDatabaseHelper" , "database copied");
           } catch (IOException e) {
               throw new RuntimeException("error creating source database",e);
           }
       }
    }

    private void copyDatabase(File dbFile) throws IOException {
        InputStream is = context.getAssets().open(dbname);
        dbFile.getParentFile().mkdirs();
        OutputStream os = new FileOutputStream(dbFile);

         int len;
         byte[] buffer = new byte[1024];
         while ((len = is.read(buffer)) > 0){
             os.write(buffer ,0 ,len);
         }
         os.flush();
         os.close();
         is.close();
    }
}
