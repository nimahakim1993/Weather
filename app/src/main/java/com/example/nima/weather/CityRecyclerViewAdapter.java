package com.example.nima.weather;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.nima.weather.data.CityModel;
import com.example.nima.weather.data.CitydbHelper;

import java.util.List;

public class CityRecyclerViewAdapter extends RecyclerView.Adapter<CityRecyclerViewAdapter.ViewHolder> {

    List<CityModel> cityList;
    CitydbHelper dbHelper;

    public CityRecyclerViewAdapter(List<CityModel> cityList , CitydbHelper dbHelper) {
        this.cityList = cityList;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public CityRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityRecyclerViewAdapter.ViewHolder holder, final int position) {

        final CityModel cityModel = cityList.get(position);
        holder.tvCIty.setText(cityModel.toString());
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.updateCitySelected(cityModel.getId(),false);
                cityList.remove(cityModel);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

       TextView tvCIty;
       Button btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
           tvCIty = itemView.findViewById(R.id.tv_city_name);
           btn = itemView.findViewById(R.id.btn);
           btn.setText("Delete");
        }
    }
}
