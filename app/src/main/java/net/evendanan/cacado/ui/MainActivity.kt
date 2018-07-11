package net.evendanan.cacado.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.support.annotation.RawRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayout
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import net.evendanan.cacado.Dealer
import net.evendanan.cacado.EnglishCards
import net.evendanan.cacado.Game
import net.evendanan.cacado.HebrewCards
import net.evendanan.cacado.MemoryCard
import net.evendanan.cacado.NoDealer
import net.evendanan.cacado.R
import net.evendanan.cacado.ViewBinder
import net.evendanan.cacado.prefs.GameSettings


class MainActivity : AppCompatActivity() {

    private val gameOwner: SettingsPresenter.GameOwner = object : SettingsPresenter.GameOwner {
        override fun resetGame() {
            game = Game(createDealer(), viewConnector)
            game.start()
        }
    }

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

    private val gridLayout by lazy { findViewById<GridLayout>(R.id.cards_grid) }
    private val prefs by lazy { GameSettings(applicationContext) }
    private val settingsPane by lazy { findViewById<View>(R.id.game_settings_layout) }
    private val settingsPresenter by lazy { SettingsPresenter(gameOwner, settingsPane, gridLayout, findViewById<View>(R.id.settings_button), prefs) }

    private var game = Game(NoDealer, viewConnector)

    private var mediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.start_button).setOnClickListener {
            gameOwner.resetGame()
        }

        findViewById<View>(R.id.settings_button).setOnClickListener {
            settingsPresenter.flipPaneVisibility()
        }

        gameOwner.resetGame()
    }

    private fun createDealer(): Dealer = prefs.lettersCount.letters.let {
        when (prefs.language) {
            GameSettings.Language.Hebrew -> HebrewCards(it)
            GameSettings.Language.English -> EnglishCards(it)
        }
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
