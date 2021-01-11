package com.example.nima.weather;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class ForecastRecyclerViewAdapter extends RecyclerView.Adapter<ForecastRecyclerViewAdapter.MyViewHolder> {

    JSONArray weatherData;
    ForecastRecyclerViewAdapter(JSONArray weatherData){
        this.weatherData = weatherData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            JSONObject data = weatherData.getJSONObject(position);

            holder.tv_temp.setText(String.format(Locale.getDefault(),"%.0f %s",
                    data.getDouble("temp"),Html.fromHtml("&#8451;")));
            holder.tv_details.setText(data.getString("main"));
            holder.tv_day.setText(data.getString("day"));
            holder.tv_weather.setWeatherIcon(data.getInt("id"),Long.MIN_VALUE,Long.MAX_VALUE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return weatherData.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_temp ,tv_day ,tv_details;
        TextViewWeather tv_weather;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_day = itemView.findViewById(R.id.tv_day);
            tv_details= itemView.findViewById(R.id.tv_details);
            tv_temp = itemView.findViewById(R.id.tv_temp);
            tv_weather =itemView.findViewById(R.id.tv_weather);
        }
    }
}
