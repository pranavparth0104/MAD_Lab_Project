package com.example.spacegram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Error extends AppCompatActivity {
    private Button error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        error = findViewById(R.id.buttonError);
        error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Error.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}