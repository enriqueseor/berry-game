package cat.teknos.berry.view.activity

import android.content.Intent
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cat.teknos.berry.R
import cat.teknos.berry.model.PlaylistManager
import cat.teknos.berry.view.GameCanvas
import cat.teknos.berry.view.util.GameEventListener

class GameActivity : AppCompatActivity(), GameEventListener {
    private var levelNumber = 0
    private var score = 0
    private var numLives = 3
    private val maxLives = 3
    private var game: GameCanvas? = null
    private val handler = Handler(Looper.getMainLooper())
    private var playlistManager: PlaylistManager? = null
    private var live1: ImageView? = null
    private var live2: ImageView? = null
    private var live3: ImageView? = null
    private var playerName: String? = null
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var soundPool: SoundPool
    private var soundMap: Map<Int, Int> = mapOf()
    private var currentPlayingSound: Int? = null
    private var currentPriority: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        game = findViewById(R.id.Screen)
        game?.setGameEventListener(this)

        val intent = intent
        levelNumber = intent.getIntExtra("levelNumber", 2)
        game?.setDifficultyLevel(levelNumber)
        playerName = getIntent().getStringExtra("playerName")

        live1 = findViewById(R.id.live1)
        live2 = findViewById(R.id.live2)
        live3 = findViewById(R.id.live3)

        soundPool = SoundPool.Builder().setMaxStreams(5).build()
        soundMap = mapOf(
            R.raw.geodude to soundPool.load(this, R.raw.geodude, 2),
            R.raw.heart to soundPool.load(this, R.raw.heart, 3),
            R.raw.berry to soundPool.load(this, R.raw.berry, 1)
        )

        playList()
        observer()
        timer()
    }

    private fun playSound(soundResource: Int, priority: Int) {
        if (currentPlayingSound != null) {
            if (currentPriority > priority) {
                return
            }
        }
        soundMap[soundResource]?.let { soundId ->
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
            currentPlayingSound = soundResource
        }
    }

    override fun onBerryCollected(berryType: Int) {
        val textView = findViewById<TextView>(R.id.points)
        var berryPoints = 0
        when (berryType) {
            0 -> berryPoints = 1
            1 -> berryPoints = 2
            2 -> berryPoints = 3
            3 -> berryPoints = 5
            4 -> berryPoints = 10
        }
        val points = textView.text.toString().toInt()
        score = points + berryPoints
        textView.text = String.format(score.toString())
        playSound(R.raw.berry, 1)
    }

    override fun onRockCollision() {
        if (numLives > 0) {
            numLives--
            updateLifeIconsVisibility()
            playSound(R.raw.geodude, 2)
        }
        if (numLives == 0) {
            onGameFinished()
        }
    }

    override fun onHeartCollected() {
        if (numLives < maxLives) {
            numLives++
            updateLifeIconsVisibility()
        }
        playSound(R.raw.heart, 2)
    }

    private fun updateLifeIconsVisibility() {
        live1!!.visibility =
            if (numLives >= 1) View.VISIBLE else View.INVISIBLE
        live2!!.visibility =
            if (numLives >= 2) View.VISIBLE else View.INVISIBLE
        live3!!.visibility =
            if (numLives == 3) View.VISIBLE else View.INVISIBLE
    }

    override fun onPause() {
        super.onPause()
        if (playlistManager != null) {
            playlistManager!!.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (playlistManager != null) {
            playlistManager!!.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (playlistManager != null) {
            playlistManager!!.release()
        }
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
        }
    }

    private fun onGameFinished() {
        val levelNumber = intent.getIntExtra("levelNumber", 2)
        val levelName = intent.getStringExtra("levelName") ?: "medium"
        val intent = Intent(this, ResultsActivity::class.java)
        intent.putExtra("levelNumber", levelNumber)
        intent.putExtra("levelName", levelName)
        intent.putExtra("playerName", playerName)
        intent.putExtra("playerScore", score)
        startActivity(intent)
        finish()
    }

    private fun playList() {
        val songResources = intArrayOf(
            R.raw.route_101,
            R.raw.route_104,
            R.raw.route_110,
            R.raw.route_113,
            R.raw.route_119,
            R.raw.route_120,
        )
        val randomIndex = (songResources.indices).random()
        val shuffledSongs = songResources.toMutableList()
        shuffledSongs.add(0, shuffledSongs.removeAt(randomIndex))
        playlistManager = PlaylistManager(this, shuffledSongs.toIntArray())
        playlistManager!!.start()
    }

    private fun observer() {
        val observer = game!!.viewTreeObserver
        observer.addOnGlobalLayoutListener {}
    }

    private fun timer() {
        handler.post(object : Runnable {
            override fun run() {
                game!!.invalidate()
                handler.postDelayed(this, 16)
            }
        })
    }
}