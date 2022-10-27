package com.example.berrygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.berrygame.singleton.Singleton;

public class MainActivity extends AppCompatActivity {

    EditText tvName;
    Button btnEasy;
    Button btnMedium;
    Button btnHard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvName = findViewById(R.id.tvName);
        btnEasy = findViewById(R.id.btnEasy);
        btnMedium = findViewById(R.id.btnMedium);
        btnHard = findViewById(R.id.btnHard);
    }

    public void play(View view) {
        Singleton singleton = Singleton.getInstance();
        singleton.setPlayer(tvName.getText().toString());
        if (btnEasy.isPressed()) {
            singleton.setDifficulty(1);
        } else if (btnMedium.isPressed()){
            singleton.setDifficulty(2);
        } else if (btnHard.isPressed()) {
            singleton.setDifficulty(3);
        }

        Intent i = new Intent(this, GameActivity.class);
        startActivity(i);
    }
}
