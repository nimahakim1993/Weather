package com.example.nima.weather;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class WeatherFragment extends Fragment {

    TextView tv_city, tv_details , tv_temp;
    TextViewWeather  tv_weatherIcon;
    RecyclerView rv_forcast;

    String cityName , details;
    long sunset , sunrise , cityId;
    double temperature;
    int weatherId;

    private boolean forecastLoaded = false;
    JSONArray forecastData = new JSONArray();


    private static final String Tag = WeatherFragment.class.getSimpleName();



    public static WeatherFragment newInstance(Bundle args){
        WeatherFragment fragment = new WeatherFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        assert args != null;
        cityName = args.getString("cityName");
        details = args.getString("details");
        sunrise = args.getLong("sunrise");
        sunset = args.getLong("sunset");
        temperature = args.getDouble("temperature");
        weatherId = args.getInt("weatherId");
        cityId = args.getLong("cityId");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.weather_card , container , false);

        tv_city = view.findViewById(R.id.city);
        tv_details = view.findViewById(R.id.details);
        tv_weatherIcon = view.findViewById(R.id.weather_icon);
        tv_temp = view.findViewById(R.id.temp);
        rv_forcast = view.findViewById(R.id.rv_forecast);
        fill();

        return  view;
    }

    private void fill() {
        tv_city.setText(cityName);
        tv_details.setText(details);
        tv_temp.setText(String.format(Locale.getDefault(),"%.0f %s" ,temperature,Html.fromHtml("&#8451;")));
        tv_weatherIcon.setWeatherIcon(weatherId,sunrise,sunset);
        if (!forecastLoaded)
        requestForcastData();

        updateDisplay();
    }

    private void requestForcastData(){
        String url = String.format(Locale.getDefault(), App.Format_URL_Forecast_BY_ID, cityId);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Forecast" ,"response \n" +response);
                        try {
                            if (response.getString("cod").equals("200")){
                                JSONArray jsonList = response.getJSONArray("list");
                                handleForecastData(jsonList);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Forecast" ,"error: "+error.getMessage());
            }
        });
        App.requestQueue.add(request);
    }

    private void handleForecastData(JSONArray jsonList) throws JSONException {
        WeatherPack[] wpk = new WeatherPack[3];

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int offset = c.get(Calendar.HOUR_OF_DAY);
        offset = (offset<12)? 0 :1;

        for (int i=0; i<wpk.length * 8; i++){
            if (wpk[i/8] == null)
                wpk[i/8] = new WeatherPack();

           JSONObject object = jsonList.getJSONObject(i);
          double temp = object.getJSONObject("main").getDouble("temp");
          JSONObject weather = object.getJSONArray("weather").getJSONObject(0);
          String main = weather.getString("main");
          int id = weather.getInt("id");

          wpk[i/8].ids[i%8] = (id==800)? 800 :(id/100);
          wpk[i/8].temps[i%8] = temp;
          wpk[i/8].mains[i%8] = main;
        }

        forecastData = new JSONArray();
        for (int i=0 ;i<wpk.length ;i++){
            int dayNumber = (dayOfWeek + offset +i) %7;
            if (dayNumber ==0)
                dayNumber = 7;

            JSONObject obj = new JSONObject();
            obj.put("temp" ,wpk[i].getMeanTemp());
            obj.put("id" ,wpk[i].getWeatherId());
            obj.put("main" ,wpk[i].getMain());
            obj.put("day" ,WeatherPack.DAYS[dayNumber -1]);
            forecastData.put(obj);
        }
        updateDisplay();
    }

    private void updateDisplay(){
        ForecastRecyclerViewAdapter adapter = new ForecastRecyclerViewAdapter(forecastData);
        rv_forcast.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_forcast.setItemAnimator(new DefaultItemAnimator());
        rv_forcast.setAdapter(adapter);

        forecastLoaded = true;
    }


    static class WeatherPack{
        int[] ids = new int[8];
        double[] temps =new double[8];
        String[] mains = new String[8];

        public static final String[] DAYS =
                {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        int modeIndex= -1;

        public double getMeanTemp(){
            double sum = 0.0;
            for (double x : temps){
                sum += x;
            }
            return sum/temps.length;
        }

        public int getWeatherId(){
            HashMap<Integer,Integer> hm = new HashMap<>();
            int max =0;

            for (int i=0 ; i<ids.length ;i++){
                int count =0;
                if (hm.containsKey(ids[i])){
                    count =hm.get(ids[i]) +1;
                    hm.put(ids[i], count);
                }
                else {
                    count =1;
                    hm.put(ids[i] ,count);
                }
                    if (count>max){
                    max = count;
                    modeIndex = i;
                    }

            }
            return ids[modeIndex];
        }
        public String getMain(){
            if (modeIndex != -1){
                return mains[modeIndex];
            }
            else return "";
        }
    }
}
