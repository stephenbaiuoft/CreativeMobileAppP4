package com.example.stephenbai.assignment4;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
// ********************Menu Implmentation**********SEGMENT***************END

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent screen1 = new Intent(com.example.stephenbai.assignment4.MainActivity.this,
                com.example.stephenbai.assignment4.screen1.class);

        //screen1.putExtra(EXTRA_PATH, imageFilePath);
        startActivity(screen1);

    }





}

