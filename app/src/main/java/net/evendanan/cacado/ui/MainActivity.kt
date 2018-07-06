package net.evendanan.cacado.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.support.annotation.RawRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayout
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import net.evendanan.cacado.*


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
                        tag = RevealHideCardHandler(applicationContext, this, memoryCard)
                    }
                }
            }
        }

        override fun showCard(index: Int, withLetter: Boolean, withAudio: Boolean) {
            gridLayout.getChildAt(index).run {
                val handler = this.tag as RevealHideCardHandler
                handler.showCard(withLetter)
                if (withAudio) playAudio(handler.memoryCard.audio)
            }
        }

        override fun hideCard(index: Int) {
            gridLayout.getChildAt(index).run {
                (this.tag as RevealHideCardHandler).hideCard()
                mediaPlayer.release()
            }
        }

        override fun highlightCard(index: Int, type: ViewBinder.HighlightType) {
            gridLayout.getChildAt(index).run {
                (this.tag as RevealHideCardHandler).highlightCard(type)
            }
        }

        override fun showWinMessage() {
            Toast.makeText(applicationContext, "🎉 WIN 🎉", Toast.LENGTH_LONG).run {
                setGravity(Gravity.CENTER, 0, 0)
                show()
            }
            
            for (childIndex in 0 until gridLayout.childCount) {
                (gridLayout.getChildAt(childIndex).tag as RevealHideCardHandler).highlightCard(ViewBinder.HighlightType.Match)
            }
        }
    }

    private var game = Game(NoDealer, viewConnector)

    private lateinit var gridLayout: GridLayout

    private var mediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.start_button).setOnClickListener {
            game = Game(HebrewCards(6), viewConnector)
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
