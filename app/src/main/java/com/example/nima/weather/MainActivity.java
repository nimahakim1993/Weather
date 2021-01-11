package com.example.nima.weather;

import android.content.Intent;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.nima.weather.data.CityModel;
import com.example.nima.weather.data.CitydbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    TabLayout tabLayout;

    ViewPager viewPager;
    List<Fragment> weatherFragment;
    List<String> titles;
    MyPagerAdapter pagerAdapter;
    CitydbHelper dbHelper;
    List<CityModel> cityList;

    JsonObjectRequest jsonRequest;

    ProgressBar pb;

    Handler updateHandler;
    Runnable runnable;

    LinearLayout addCityLayout;
    RelativeLayout layoutForFragments;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        tabLayout =findViewById(R.id.tab_layout);
        getSupportActionBar().setElevation(0);

        addCityLayout = findViewById(R.id.add_linear_layout);
         layoutForFragments = findViewById(R.id.layout_for_fragments);

        FloatingActionButton fab = findViewById(R.id.floating_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this,CitiesActivity.class));
                finish();
            }
        });


        viewPager = findViewById(R.id.view_pager);
        pb = findViewById(R.id.pb);

        init();
        lunchToAddCity();
        loadWeatherData();

        updateHandler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                loadWeatherData();
                updateHandler.postDelayed(this ,10*60*1000);
            }
        };

    }


    @Override
    protected void onResume() {
        super.onResume();
//        cityList.clear();
//        weatherFragment.clear();
//        init();
//        lunchToAddCity();
//        updateHandler.post(runnable);

    }


    @Override
    protected void onPause() {
        super.onPause();
        updateHandler.removeCallbacks(runnable);

    }

    private void init() {

        try {
            dbHelper = new CitydbHelper(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cityList = dbHelper.getCities("selected = 1" , null);
        weatherFragment = new ArrayList<>();
        titles = new ArrayList<>();
    }

    private void lunchToAddCity() {

        if (cityList.size() == 0){
            layoutForFragments.setVisibility(View.GONE);
            addCityLayout.setVisibility(View.VISIBLE);

        }
        else {
            layoutForFragments.setVisibility(View.VISIBLE);
            addCityLayout.setVisibility(View.GONE);
        }
        addCityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,CitiesActivity.class));
                finish();
//                        addCityLayout.setVisibility(View.INVISIBLE);
//                        layoutForFragments.setVisibility(View.VISIBLE);

            }
        });


    }

    private void updateDisplay() {
        if (weatherFragment == null)
            weatherFragment = new ArrayList<>();
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), weatherFragment ,titles);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        if (tabLayout.getTabCount()<5){
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        }else {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        }
    }

    private void loadWeatherData() {

        String url = prepareUrl();
        jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pb.setVisibility(View.INVISIBLE);
                        weatherFragment.clear();
                        Log.i("Weather" , "Response: \n" + response);
                        try {
                            int cnt = response.getInt("cnt");
                            if (cnt ==0) return;

                            JSONArray jsonList = response.getJSONArray("list");
                            for (int i=0 ;i<jsonList.length() ;i++) {
                                JSONObject res = jsonList.getJSONObject(i);


                                String cityName = res.getString("name").toUpperCase() + ","
                                        + res.getJSONObject("sys").getString("country");

                                double temperature = res.getJSONObject("main").getDouble("temp");

                                JSONObject jsonDetails = res.getJSONArray("weather").getJSONObject(0);
                                String details = jsonDetails.getString("description");

                                JSONObject sys = res.getJSONObject("sys");
                                long sunrise = sys.getLong("sunrise");
                                long sunset = sys.getLong("sunset");
                                int weatherId = jsonDetails.getInt("id");

                                Bundle args = new Bundle();
                                args.putString("cityName", cityName);
                                args.putDouble("temperature", temperature);
                                args.putString("details", details);
                                args.putLong("sunrise", sunrise);
                                args.putLong("sunset", sunset);
                                args.putInt("weatherId", weatherId);
                                args.putLong("cityId", res.getLong("id"));
                                weatherFragment.add(WeatherFragment.newInstance(args));
                                titles.add(res.getString("name").toUpperCase());
//                                pagerAdapter.notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        updateDisplay();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pb.setVisibility(View.INVISIBLE);
                Log.e("Weather" , "Error :" + error.getMessage());

                if (cityList.size() !=0)
                Toast.makeText(MainActivity.this, "We don't have access to the internet", Toast.LENGTH_LONG).show();

//                coordinatorLayout=findViewById(R.id.coodinator_layout);
//                Snackbar snackbar = Snackbar.make(coordinatorLayout,"Waiting for network...",Snackbar.LENGTH_LONG);
//                snackbar.setDuration(20*1000);
//                snackbar.show();

            }
        });
        pb.setVisibility(View.VISIBLE);
        App.requestQueue.add(jsonRequest);
    }

    private String prepareUrl(){
        StringBuilder sb = new StringBuilder("http://api.openweathermap.org/data/2.5/group?id=");
        for (int i=0 ; i<cityList.size(); i++){
            sb.append(String.valueOf(cityList.get(i).getId()));
            if (i < cityList.size()-1)
                sb.append(",");
        }
        sb.append("&units=metric&APPID="+App.Api_key);
        return sb.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuItem refresh = menu.add("");
        refresh.setIcon(R.drawable.ic_refresh_black_24dp);
        refresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        refresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                init();
                lunchToAddCity();
                loadWeatherData();
                return false;
            }
        });
        MenuItem about = menu.add("About");
        about.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        about.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(new Intent(MainActivity.this,AboutMe.class));
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    class MyPagerAdapter extends FragmentPagerAdapter{

        List<Fragment> fragments ;
        List<String> titles;

        private MyPagerAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
            super(fm);
            this.fragments = fragments;
            this.titles = titles;

        }

        @Override
        public Fragment getItem(int position) {

            return fragments.get(position);

        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}
