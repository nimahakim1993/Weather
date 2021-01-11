package com.example.nima.weather;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.nima.weather.data.CityModel;
import com.example.nima.weather.data.CitydbHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddCityFragment extends DialogFragment {

    androidx.appcompat.widget.SearchView searchView;
    RecyclerView recyclerView;
    MyAdapter adapter;
    List<CityModel> cityList;
    CitydbHelper dbHelper;

    private AddCityInterface iactivity;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_city,container,false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        try {
            dbHelper = new CitydbHelper(getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                cityList = dbHelper.searchCityByName(newText,"20");
                updateDisplay();
                return false;
            }
        });

        updateDisplay();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        changeDisplaySize();
    }

    private void updateDisplay() {
        if (cityList == null){
            cityList = new ArrayList<>();
        }
        adapter = new MyAdapter(cityList);
        recyclerView.setAdapter(adapter);
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{

        private List<CityModel> cityList;

        public MyAdapter(List<CityModel> cityList) {
            this.cityList = cityList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_layout,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final CityModel cityModel = cityList.get(position);
            holder.tvCIty.setText(cityModel.toString());
            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iactivity.addCity(cityModel.getId());
                    dismiss();
                }
            });

        }

        @Override
        public int getItemCount() {
            return cityList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tvCIty;
            Button btn;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                tvCIty = itemView.findViewById(R.id.tv_city_name);
                btn = itemView.findViewById(R.id.btn);
                btn.setText("Add");
                btn.setTextColor(ContextCompat.getColor(itemView.getContext(),R.color.addColor));
                btn.setBackgroundResource(R.drawable.city_btn_add_bg);
            }
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.iactivity = (AddCityInterface) activity;
    }

    static interface AddCityInterface{
        void addCity(long id);
    }

    private void changeDisplaySize(){
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Window window = getDialog().getWindow();
        if (window !=null){
            window.setLayout((int) (ViewGroup.LayoutParams.MATCH_PARENT),ViewGroup.LayoutParams.WRAP_CONTENT);
        }

    }
}


