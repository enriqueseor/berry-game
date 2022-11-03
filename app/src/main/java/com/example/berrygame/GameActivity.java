package com.example.berrygame;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewTreeObserver;

import com.example.berrygame.game.Game;
import com.example.berrygame.singleton.Singleton;

import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    private Game game;
    private final Handler handler = new Handler();
    private Singleton singleton;
    private MediaPlayer pokemonsong = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        game = (Game) findViewById(R.id.Screen);
        ViewTreeObserver obs = game.getViewTreeObserver();

        obs.addOnGlobalLayoutListener(() -> {
            game.width = game.getWidth();
            game.height = game.getHeight();
            game.pikachuPOSX = game.width / 2;
            game.pikachuPOSY = game.height - 100;

            game.radium = 75;
            game.berryPOSY = 50;
            game.cherubiPOSX = 200;
        });

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    game.berryPOSY +=10;
                    game.cherubiPOSY +=10;
                    game.invalidate();
                });
            }
        }, 0, 20);

        //pokemonsong = MediaPlayer.create(GameActivity.this, R.raw.pokemonsong);
        //pokemonsong.start();
        //pokemonsong.setOnCompletionListener(mp -> pokemonsong.start());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pokemonsong.reset();
    }
}
