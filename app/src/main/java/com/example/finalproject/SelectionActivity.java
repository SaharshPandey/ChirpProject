package com.example.finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectionActivity extends AppCompatActivity {

    Button login,authenticate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        login = findViewById(R.id.login);
        authenticate = findViewById(R.id.authenticate);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent l = new Intent(SelectionActivity.this,BasicActivity.class);
                startActivity(l);
            }
        });

        authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent l = new Intent(SelectionActivity.this,MainActivity.class);
                startActivity(l);
            }
        });
    }
}
