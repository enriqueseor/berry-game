package com.example.berrygame.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.berrygame.R;
import com.example.berrygame.singleton.Singleton;

import java.util.Random;

public class Game extends View {

    public int width, height, pikachuPOSX, pikachuPOSY, radium, berryPOSX, berryPOSY, cherubiPOSX, cherubiPOSY, puntuation;
    private final Random random = new Random();
    private MediaPlayer gameloop = new MediaPlayer();

    public Game(Context context) {
        super(context);
    }

    public Game(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        gameloop = MediaPlayer.create(context, R.raw.gameloop);
        gameloop.start();
        gameloop.setOnCompletionListener(mp -> gameloop.start());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            pikachuPOSX = (int) event.getX();
            radium = 75;
            this.invalidate();
        }
        return true;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint background = new Paint();
        Paint pikachu = new Paint();
        Paint berry = new Paint();
        Paint cherubi = new Paint();
        Paint points = new Paint();
        Paint player = new Paint();

        Singleton singleton = new Singleton();

        //BACKGROUND
        background.setColor(Color.BLACK);
        background.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRect(new Rect(0,0,(width),(height)),background);

        //PIKACHU
        RectF pikachuCollider = new RectF((
                pikachuPOSX - radium),
                (pikachuPOSY - radium),
                (pikachuPOSX + radium),
                (pikachuPOSY + radium)
        );
        Bitmap pikachuBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pikachu);
        canvas.drawBitmap(pikachuBitmap, null , pikachuCollider, pikachu);

        //BERRY
        if (berryPOSY > height) {
            berryPOSY =50;
            berryPOSX = random.nextInt(width);
        }
        RectF berryCollider = new RectF(
                (berryPOSX - radium),
                (berryPOSY - radium),
                (berryPOSX + radium),
                (berryPOSY + radium)
        );

        if (RectF.intersects(pikachuCollider, berryCollider)) {
            puntuation += 1;
            berryPOSY = 50;
            berryPOSX = random.nextInt(width);
        }

        Bitmap berrybitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bayaframbu);
        canvas.drawBitmap(berrybitmap, null , berryCollider, berry);

        //CHERUBI
        if (cherubiPOSY > height) {
            cherubiPOSY =50;
            cherubiPOSX = random.nextInt(width);
        }
        RectF cherubiCollider = new RectF((cherubiPOSX - radium),
                (cherubiPOSY - radium),
                (cherubiPOSX + radium),
                (cherubiPOSY + radium)
        );

        if (RectF.intersects(pikachuCollider, cherubiCollider)) {
            puntuation -= 1;
            cherubiPOSY =50;
            cherubiPOSX = random.nextInt(width);
        }

        Bitmap cherubibitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cherubi);
        canvas.drawBitmap(cherubibitmap, null , cherubiCollider, cherubi);

        //POINTS
        points.setTextAlign(Paint.Align.RIGHT);
        points.setTextSize(100);
        points.setColor(Color.WHITE);
        canvas.drawText(String.valueOf(puntuation), 150,150,points);

        //NAME
        player.setTextAlign(Paint.Align.RIGHT);
        player.setTextSize(100);
        player.setColor(Color.WHITE);
        //canvas.drawText(singleton.getPlayer(), 500, 500, player);
    }
}
