package cat.teknos.berry.presenter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import java.util.Random;

import cat.teknos.berry.view.util.GameEventListener;
import cat.teknos.berry.R;

public class GamePresenter extends View {

    public int width, height, radio;
    public int posPikachuX, posPikachuY, posBerryX, posBerryY, posRockX, posRockY;
    private int currentBerryType = 0;

    private final Random random = new Random();

    private final RectF rectForPikachu = new RectF();
    private final RectF rectForBerry = new RectF();
    private final RectF rectForRock = new RectF();

    private Drawable backgroundDrawable;
    private Drawable pikachuDrawable;
    private Drawable pokemonDrawable;

    private Drawable[] berriesDrawable;

    public GamePresenter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        backgroundDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.background, null);
        pikachuDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.pikachu, null);
        berriesDrawable = new Drawable[3];
        berriesDrawable[0] = ResourcesCompat.getDrawable(getResources(), R.drawable.razz_berry, null);
        berriesDrawable[1] = ResourcesCompat.getDrawable(getResources(), R.drawable.nanap_berry, null);
        berriesDrawable[2] = ResourcesCompat.getDrawable(getResources(), R.drawable.pinap_berry, null);
        pokemonDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.rock, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = w;
        height = h;

        posPikachuX = width / 2;
        posPikachuY = height - 100;

        radio = 100;
        posBerryY = 50;
        posRockX = random.nextInt(width);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            posPikachuX = (int) event.getX();
            this.invalidate();
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //BACKGROUND
        backgroundDrawable.setBounds(0, 0, getWidth(), getHeight());
        backgroundDrawable.draw(canvas);

        //PIKACHU
        pikachuDrawable.setBounds(posPikachuX - radio, posPikachuY - radio, posPikachuX + radio, posPikachuY + radio);
        pikachuDrawable.draw(canvas);
        rectForPikachu.set(posPikachuX - radio, posPikachuY - radio, posPikachuX + radio, posPikachuY + radio);

        //BERRY
        berriesDrawable[currentBerryType].setBounds(posBerryX - radio, posBerryY - radio, posBerryX + radio, posBerryY + radio);
        berriesDrawable[currentBerryType].draw(canvas);
        rectForBerry.set(posBerryX - radio, posBerryY - radio, posBerryX + radio, posBerryY + radio);
        newBerry();
        onBerryCollected();

        //POKEMON
        pokemonDrawable.setBounds(posRockX - radio, posRockY - radio, posRockX + radio, posRockY + radio);
        pokemonDrawable.draw(canvas);
        rectForRock.set(posRockX - radio, posRockY - radio, posRockX + radio, posRockY + radio);
        newRock();
        onRockCollision();
    }

    private GameEventListener gameEventListener;

    public void setGameEventListener(GameEventListener listener) {
        this.gameEventListener = listener;
    }

    private void newBerry() {
        if (posBerryY > height) {
            posBerryY = 50;
            posBerryX = random.nextInt(width);
        }
    }

    private void onBerryCollected() {
        if (RectF.intersects(rectForPikachu, rectForBerry)) {
            posBerryY = 50;
            posBerryX = random.nextInt(width);
            currentBerryType = random.nextInt(3);
            if (gameEventListener != null) {
                gameEventListener.onBerryCollected();
            }
        }
    }

    private void newRock() {
        if (posRockY > height) {
            posRockY = 50;
            posRockX = random.nextInt(width);
        }
    }

    private void onRockCollision() {
        if (RectF.intersects(rectForPikachu, rectForRock)) {
            posRockY = 50;
            posRockX = random.nextInt(width);
            if (gameEventListener != null) {
                gameEventListener.onRockCollision();
            }
        }
    }
}