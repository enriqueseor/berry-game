package cat.teknos.berry.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import cat.teknos.berry.R
import cat.teknos.berry.view.util.GameEventListener
import cat.teknos.berry.view.util.OnBerryCollectedListener
import java.util.Random

class GameCanvas(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {
    private var canvasWidth: Int = 0
    private var canvasHeight: Int = 0
    private var radius: Int = 0
    private var posPikachuX: Int = 0
    private var posPikachuY: Int = 0
    private var posBerryX: Int = 0
    var posBerryY: Int = 0
    private var posRockX: Int = 0
    var posRockY: Int = 0
    private var posHeartX: Int = 0
    var posHeartY: Int = 0
    private var berryType = 0

    private val rectForPikachu = RectF()
    private val rectForBerry = RectF()
    private val rectForRock = RectF()
    private val rectForHeart = RectF()

    private var pikachuDrawable: Drawable? = null
    private var rockDrawable: Drawable? = null
    private var heartDrawable: Drawable? = null
    private lateinit var berriesDrawable: Array<Drawable?>

    private var gameEventListener: GameEventListener? = null
    private var onBerryCollectedListener: OnBerryCollectedListener? = null
    private val random = Random()

    init {
        init()
    }

    private fun init() {
        pikachuDrawable = ResourcesCompat.getDrawable(resources, R.drawable.pikachu, null)
        berriesDrawable = arrayOfNulls(5)
        berriesDrawable[0] = ResourcesCompat.getDrawable(resources, R.drawable.razz_berry, null)
        berriesDrawable[1] = ResourcesCompat.getDrawable(resources, R.drawable.pinap_berry, null)
        berriesDrawable[2] = ResourcesCompat.getDrawable(resources, R.drawable.nanap_berry, null)
        berriesDrawable[3] =
            ResourcesCompat.getDrawable(resources, R.drawable.pinap_berry_silver, null)
        berriesDrawable[4] =
            ResourcesCompat.getDrawable(resources, R.drawable.razz_berry_golden, null)
        rockDrawable = ResourcesCompat.getDrawable(resources, R.drawable.rock, null)
        heartDrawable = ResourcesCompat.getDrawable(resources, R.drawable.heart, null)
    }

    private val berriesPositions = mutableListOf<Pair<Int, Int>>()
    private val berriesTypes = mutableListOf<Int>()
    private val rectsForBerries = mutableListOf<RectF>()

    private val rocksPositions = mutableListOf<Pair<Int, Int>>()
    private val rectsForRocks = mutableListOf<RectF>()

    private fun initObjects() {
        for (i in 0 until 3) { // 3 berries
            berriesPositions.add(Pair(random.nextInt(canvasWidth), 0))
            berriesTypes.add(customRandomBerryType())
            rectsForBerries.add(RectF())
        }
        for (i in 0 until 3) { // 3 rocas
            rocksPositions.add(Pair(random.nextInt(canvasWidth), 0))
            rectsForRocks.add(RectF())
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)

        canvasWidth = w
        canvasHeight = h

        posPikachuX = canvasWidth / 2
        posPikachuY = canvasHeight - 100

        radius = 100
        posBerryX = random.nextInt(canvasWidth)
        posRockX = random.nextInt(canvasWidth)
        posHeartX = random.nextInt(canvasWidth)
        initObjects()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_MOVE) {
            posPikachuX = event.x.toInt()
            this.invalidate()
        } else if (event.action == MotionEvent.ACTION_UP) {
            posPikachuX = event.x.toInt()
            this.invalidate()
            performClick()
        }
        return true
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //PIKACHU
        pikachuDrawable!!.setBounds(
            posPikachuX - radius,
            posPikachuY - radius,
            posPikachuX + radius,
            posPikachuY + radius
        )
        pikachuDrawable!!.draw(canvas)
        rectForPikachu[(posPikachuX - radius).toFloat(), (posPikachuY - radius).toFloat(), (posPikachuX + radius).toFloat()] =
            (posPikachuY + radius).toFloat()

        // Dibujar y actualizar berries
        for (i in berriesPositions.indices) {
            val (x, y) = berriesPositions[i]
            berriesDrawable[berriesTypes[i]]!!.setBounds(
                x - radius,
                y - radius,
                x + radius,
                y + radius
            )
            berriesDrawable[berriesTypes[i]]!!.draw(canvas)
            rectsForBerries[i].set(
                (x - radius).toFloat(),
                (y - radius).toFloat(),
                (x + radius).toFloat(),
                (y + radius).toFloat()
            )
            updateBerry(i)
            checkBerryCollision(i)
        }

        // Dibujar y actualizar rocas
        for (i in rocksPositions.indices) {
            val (x, y) = rocksPositions[i]
            rockDrawable!!.setBounds(
                x - radius,
                y - radius,
                x + radius,
                y + radius
            )
            rockDrawable!!.draw(canvas)
            rectsForRocks[i].set(
                (x - radius).toFloat(),
                (y - radius).toFloat(),
                (x + radius).toFloat(),
                (y + radius).toFloat()
            )
            updateRock(i)
            checkRockCollision(i)
        }

        //HEART
        heartDrawable!!.setBounds(
            posHeartX - radius,
            posHeartY - radius,
            posHeartX + radius,
            posHeartY + radius
        )
        heartDrawable!!.draw(canvas)
        rectForHeart[(posHeartX - radius).toFloat(), (posHeartY - radius).toFloat(), (posHeartX + radius).toFloat()] =
            (posHeartY + radius).toFloat()
        onNewHeartGenerated()
        onHeartCollected()
    }

    // Actualizar posiciones de berries
    private fun updateBerry(index: Int) {
        val (x, y) = berriesPositions[index]
        if (y > canvasHeight) {
            berriesPositions[index] = Pair(random.nextInt(canvasWidth), 0)
            berriesTypes[index] = customRandomBerryType()
        } else {
            berriesPositions[index] = Pair(x, y + 10) // Velocidad de caída
        }
    }

    // Verificar colisiones con berries
    private fun checkBerryCollision(index: Int) {
        if (RectF.intersects(rectForPikachu, rectsForBerries[index])) {
            berriesPositions[index] = Pair(random.nextInt(canvasWidth), 0)
            berriesTypes[index] = customRandomBerryType()
            onBerryCollectedListener?.onBerryCollected(berriesTypes[index])
        }
    }

    // Actualizar posiciones de rocas
    private fun updateRock(index: Int) {
        val (x, y) = rocksPositions[index]
        if (y > canvasHeight) {
            rocksPositions[index] = Pair(random.nextInt(canvasWidth), 0)
        } else {
            rocksPositions[index] = Pair(x, y + 10) // Velocidad de caída
        }
    }

    // Verificar colisiones con rocas
    private fun checkRockCollision(index: Int) {
        if (RectF.intersects(rectForPikachu, rectsForRocks[index])) {
            rocksPositions[index] = Pair(random.nextInt(canvasWidth), 0)
            gameEventListener?.onRockCollision()
        }
    }

    fun setGameEventListener(listener: GameEventListener?) {
        this.gameEventListener = listener
    }

    fun setOnBerryCollectedListener(listener: OnBerryCollectedListener?) {
        this.onBerryCollectedListener = listener
    }

    private fun customRandomBerryType(): Int {
        val probabilities = doubleArrayOf(0.60, 0.20, 0.10, 0.050, 0.025)
        val rand = random.nextDouble()
        var cumulativeProbability = 0.0
        for (i in probabilities.indices) {
            cumulativeProbability += probabilities[i]
            if (rand <= cumulativeProbability) {
                return i
            }
        }
        return 0
    }

    private fun newBerry() {
        if (posBerryY > canvasHeight) {
            posBerryY = 0
            posBerryX = random.nextInt(canvasWidth)
            berryType = customRandomBerryType()
        }
    }

    private fun onBerryCollected() {
        if (RectF.intersects(rectForPikachu, rectForBerry)) {
            posBerryY = 0
            posBerryX = random.nextInt(canvasWidth)
            if (onBerryCollectedListener != null) {
                onBerryCollectedListener!!.onBerryCollected(berryType)
            }
            berryType = customRandomBerryType()
        }
    }

    private fun newRock() {
        if (posRockY > canvasHeight) {
            posRockY = 0
            posRockX = random.nextInt(canvasWidth)
        }
    }

    private fun onRockCollision() {
        if (RectF.intersects(rectForPikachu, rectForRock)) {
            posRockY = 0
            posRockX = random.nextInt(canvasWidth)
            if (gameEventListener != null) {
                gameEventListener!!.onRockCollision()
            }
        }
    }

    private fun onNewHeartGenerated() {
        if (posHeartY > canvasHeight) {
            posHeartY = 0
            posHeartX = random.nextInt(canvasWidth)
            if (gameEventListener != null) {
                gameEventListener!!.onNewHeartGenerated()
            }
        }
    }

    private fun onHeartCollected() {
        if (RectF.intersects(rectForPikachu, rectForHeart)) {
            posHeartY = 0
            posHeartX = random.nextInt(canvasWidth)
            if (gameEventListener != null) {
                gameEventListener!!.onHeartCollected()
            }
        }
    }
}