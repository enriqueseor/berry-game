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
import java.util.Random

class GameCanvas(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var canvasWidth: Int = 0
    private var canvasHeight: Int = 0
    private var radius: Int = 0
    private var posPikachuX: Int = 0
    private var posPikachuY: Int = 0
    private var posBerryX: Int = 0
    private var posRockX: Int = 0
    private var posHeartX: Int = 0
    private var posHeartY: Int = -15000
    private var level: Int = 2
    private val rectForPikachu = RectF()
    private val rectForHeart = RectF()
    private val berriesTypes = mutableListOf<Int>()
    private val berriesPositions = mutableListOf<Pair<Int, Int>>()
    private val rectForBerries = mutableListOf<RectF>()
    private val rocksPositions = mutableListOf<Pair<Int, Int>>()
    private val rectForRocks = mutableListOf<RectF>()
    private var pikachuDrawable: Drawable? = null
    private var rockDrawable: Drawable? = null
    private var heartDrawable: Drawable? = null
    private var berriesDrawable: Array<Drawable?>
    private var gameEventListener: GameEventListener? = null
    private val random = Random()

    init {
        pikachuDrawable = ResourcesCompat.getDrawable(resources, R.drawable.pikachu, null)
        berriesDrawable = arrayOfNulls(5)
        berriesDrawable[0] = ResourcesCompat.getDrawable(resources, R.drawable.razz_berry, null)
        berriesDrawable[1] = ResourcesCompat.getDrawable(resources, R.drawable.pinap_berry, null)
        berriesDrawable[2] = ResourcesCompat.getDrawable(resources, R.drawable.nanap_berry, null)
        berriesDrawable[3] = ResourcesCompat.getDrawable(resources, R.drawable.pinap_berry_silver, null)
        berriesDrawable[4] = ResourcesCompat.getDrawable(resources, R.drawable.razz_berry_golden, null)
        rockDrawable = ResourcesCompat.getDrawable(resources, R.drawable.golem, null)
        heartDrawable = ResourcesCompat.getDrawable(resources, R.drawable.heart, null)
    }

    private fun initObjects() {
        val berryHeights = generateUniqueNegativeHeights(3)
        for (i in 0 until 3) {
            berriesPositions.add(Pair(random.nextInt(canvasWidth), berryHeights[i]))
            berriesTypes.add(customRandomBerryType())
            rectForBerries.add(RectF())
        }

        val rockHeights = generateUniqueNegativeHeights(3)
        for (i in 0 until 3) {
            rocksPositions.add(Pair(random.nextInt(canvasWidth), rockHeights[i]))
            rectForRocks.add(RectF())
        }
    }

    private fun generateUniqueNegativeHeights(count: Int): List<Int> {
        val uniqueHeights = mutableSetOf<Int>()
        while (uniqueHeights.size < count) {
            val randomHeight = -random.nextInt(1500)
            uniqueHeights.add(randomHeight)
        }
        return uniqueHeights.toList()
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

        /******************************************************
        *                       PIKACHU                       *
        *******************************************************/
        pikachuDrawable!!.setBounds(
            posPikachuX - radius,
            posPikachuY - radius,
            posPikachuX + radius,
            posPikachuY + radius
        )
        pikachuDrawable!!.draw(canvas)
        rectForPikachu.set(
            (posPikachuX - radius).toFloat(),
            (posPikachuY - radius).toFloat(),
            (posPikachuX + radius).toFloat(),
            (posPikachuY + radius).toFloat()
        )

        /******************************************************
         *                       BERRIES                      *
         ******************************************************/
        for (i in berriesPositions.indices) {
            val (x, y) = berriesPositions[i]
            berriesDrawable[berriesTypes[i]]!!.setBounds(
                x - radius,
                y - radius,
                x + radius,
                y + radius
            )
            berriesDrawable[berriesTypes[i]]!!.draw(canvas)
            rectForBerries[i].set(
                (x - radius).toFloat(),
                (y - radius).toFloat(),
                (x + radius).toFloat(),
                (y + radius).toFloat()
            )
            updateBerry(i)
        }

        /*****************************************************
         *                        ROCKS                      *
         *****************************************************/
        for (i in rocksPositions.indices) {
            val (x, y) = rocksPositions[i]
            rockDrawable!!.setBounds(
                x - radius,
                y - radius,
                x + radius,
                y + radius
            )
            rockDrawable!!.draw(canvas)
            rectForRocks[i].set(
                (x - radius).toFloat(),
                (y - radius).toFloat(),
                (x + radius).toFloat(),
                (y + radius).toFloat()
            )
            updateRock(i)
        }

        /*****************************************************
         *                        HEART                      *
         *****************************************************/
        heartDrawable!!.setBounds(
            posHeartX - radius,
            posHeartY - radius,
            posHeartX + radius,
            posHeartY + radius
        )
        heartDrawable!!.draw(canvas)
        rectForHeart.set(
            (posHeartX - radius).toFloat(),
            (posHeartY - radius).toFloat(),
            (posHeartX + radius).toFloat(),
            (posHeartY + radius).toFloat()
        )
        updateHeart()
    }

    private fun updateBerry(index: Int) {
        val (x, y) = berriesPositions[index]
        val speed = 10 * level
        if (y > canvasHeight) {
            berriesPositions[index] = Pair(random.nextInt(canvasWidth), 0)
            berriesTypes[index] = customRandomBerryType()
        } else {
            berriesPositions[index] = Pair(x, y + speed)
        }
        if (RectF.intersects(rectForPikachu, rectForBerries[index])) {
            val berryType = berriesTypes[index]
            berriesPositions[index] = Pair(random.nextInt(canvasWidth), 0)
            berriesTypes[index] = customRandomBerryType()
            gameEventListener?.onBerryCollected(berryType)
        }
    }

    private fun updateRock(index: Int) {
        val (x, y) = rocksPositions[index]
        val speed = 10 * level
        if (y > canvasHeight) {
            rocksPositions[index] = Pair(random.nextInt(canvasWidth), 0)
        } else {
            rocksPositions[index] = Pair(x, y + speed)
        }
        if (RectF.intersects(rectForPikachu, rectForRocks[index])) {
            rocksPositions[index] = Pair(random.nextInt(canvasWidth), 0)
            gameEventListener?.onRockCollision()
        }
    }

    private fun updateHeart() {
        val speed = 10 * level
        if (posHeartY > canvasHeight) {
            posHeartY = (-17500..-12500).random()
            posHeartX = random.nextInt(canvasWidth)
        } else {
            posHeartY += speed
        }
        if (RectF.intersects(rectForPikachu, rectForHeart)) {
            posHeartY = (-17500..-12500).random()
            posHeartX = random.nextInt(canvasWidth)
            gameEventListener?.onHeartCollected()
        }
    }

    fun setGameEventListener(listener: GameEventListener?) {
        this.gameEventListener = listener
    }

    fun setDifficultyLevel(level: Int) {
        this.level = level
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
}