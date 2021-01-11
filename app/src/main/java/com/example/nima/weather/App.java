package com.example.nima.weather;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.nima.weather.data.AssetsDatabaseHelper;

public class App extends Application {

    public static final String Api_key = "1e432f00cdbee5cc4cf1da281ffdc47a";
    public static final String Format_URL_BY_NAME = "https://api.openweathermap.org/data/2.5/" +
            "weather?q=%s&units=metric&APPID=" + Api_key;
    public static final String Format_URL_BY_ID = "https://api.openweathermap.org/data/2.5/" +
            "weather?id=%d&units=metric&APPID=" + Api_key;
    public static final String Format_URL_Forecast_BY_ID = "https://api.openweathermap.org/data/2.5/" +
            "forecast?id=%d&units=metric&APPID="+ Api_key;

    public static RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        new AssetsDatabaseHelper(getApplicationContext()).ckeckdb();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }
}
