package net.evendanan.cacado

import android.media.MediaPlayer
import android.os.Bundle
import android.support.annotation.RawRes
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayout
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private var viewConnector: ViewBinder = object : ViewBinder {

        override fun keepScreenAwake(enabled: Boolean) {
            if (enabled)
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            else
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        override fun renderCards(cards: List<MemoryCard>) {
            gridLayout.removeAllViews()
            val columnCount = Math.ceil(Math.max(1.toDouble(), Math.sqrt((cards.size / 2).toDouble())))
            gridLayout.columnCount = columnCount.toInt()
            gridLayout.rowCount = (2 * columnCount).toInt()

            cards.forEachIndexed { index, memoryCard ->
                run {
                    layoutInflater.inflate(R.layout.memory_card, gridLayout, false).run {
                        findViewById<TextView>(R.id.letter).text = memoryCard.text
                        setOnClickListener { game.onPlayerClicked(index) }
                        gridLayout.addView(this)
                        tag = memoryCard
                    }
                }
            }
        }

        override fun showCard(index: Int, withLetter: Boolean, withAudio: Boolean) {
            gridLayout.getChildAt(index).run {
                if (withLetter) findViewById<TextView>(R.id.letter).visibility = View.VISIBLE
                if (withAudio) playAudio((this.tag as MemoryCard).audio)
                setBackgroundColor(colorCardFrontColor)
            }
        }

        override fun hideCard(index: Int) {
            gridLayout.getChildAt(index).run {
                findViewById<TextView>(R.id.letter).visibility = View.INVISIBLE
                setBackgroundColor(colorCardBackColor)
                mediaPlayer.release()
            }
        }

        override fun highlightCard(index: Int, type: ViewBinder.HighlightType) {
            gridLayout.getChildAt(index).setBackgroundColor(when (type) {
                ViewBinder.HighlightType.Match -> colorHighlightMatch
                ViewBinder.HighlightType.NotMatch -> colorHighlightNotMatch
            })
        }

        override fun showWinMessage() {
            Toast.makeText(applicationContext, "🎉 WIN 🎉", Toast.LENGTH_LONG).run {
                setGravity(Gravity.CENTER, 0, 0)
                show()
            }
        }
    }

    private var game = Game(HebrewCards(6), viewConnector)

    private lateinit var gridLayout: GridLayout
    private var colorHighlightMatch = 0
    private var colorHighlightNotMatch = 0
    private var colorCardBackColor = 0
    private var colorCardFrontColor = 0
    private var mediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        colorHighlightMatch = ResourcesCompat.getColor(resources, R.color.card_match, theme)
        colorHighlightNotMatch = ResourcesCompat.getColor(resources, R.color.card_not_match, theme)
        colorCardBackColor = ResourcesCompat.getColor(resources, R.color.card_back, theme)
        colorCardFrontColor = ResourcesCompat.getColor(resources, R.color.card_front, theme)

        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.start_button).setOnClickListener {
            game.start()
        }

        gridLayout = findViewById(R.id.cards_grid)
    }

    fun playAudio(@RawRes audio: Int) {
        mediaPlayer.release()
        mediaPlayer = MediaPlayer.create(applicationContext, audio)
        mediaPlayer.start()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.release()
    }
}
