package com.example.nima.weather;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class AboutMe extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tvAbout = findViewById(R.id.tv_about_me);

        tvAbout.setText(
                "Born in 1371\n" +
                "Android developer\n" +
                "Interested in programing\n" +
                "Other skills:\n"+
                "java-android-sql-volley\n" +
                "Tel: 09367356585\n" +
                "Telegram ID: nimahakim93"
        );
    }

    public void callMe(View view) {
        Intent call = new Intent();
        call.setAction(Intent.ACTION_DIAL);
        call.setData(Uri.parse("tel:+989367356585"));
        startActivity(call);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }
}
