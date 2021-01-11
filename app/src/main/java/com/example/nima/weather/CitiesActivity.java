package com.example.nima.weather;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.nima.weather.data.CityModel;
import com.example.nima.weather.data.CitydbHelper;

import java.io.IOException;
import java.util.List;

public class CitiesActivity extends AppCompatActivity implements AddCityFragment.AddCityInterface{

    CityRecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    CitydbHelper dbHelper;
    List<CityModel> cityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


            AddCityFragment fragment = new AddCityFragment();
            fragment.show(getSupportFragmentManager(),"addcity");


        TextView tvActivityCity = findViewById(R.id.tv_activity_city_content);
        tvActivityCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddCityFragment fragment = new AddCityFragment();
                fragment.show(getSupportFragmentManager(),"addcity");
            }
        });


        recyclerView = findViewById(R.id.city_recycler_view);
        try {
            dbHelper = new CitydbHelper(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateDisplay();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       if (item.getItemId() == android.R.id.home);
       startActivity(new Intent(CitiesActivity.this,MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(CitiesActivity.this,MainActivity.class));
    }

    private void updateDisplay() {
        cityList = dbHelper.getCities("selected = 1",null);
        adapter = new CityRecyclerViewAdapter(cityList,dbHelper);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void addCity(long id) {
        dbHelper.updateCitySelected(id,true);
        updateDisplay();
    }
}
