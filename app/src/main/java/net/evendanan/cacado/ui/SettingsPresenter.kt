package net.evendanan.cacado.ui

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.AdapterView
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.view.*
import net.evendanan.cacado.prefs.GameSettings

class SettingsPresenter(private val gameOwner: GameOwner, private val rootSettingsView: View, private val gameLayout: View, private val settingsButton: View, private val gameSettings: GameSettings) {
    interface GameOwner {
        fun resetGame()
    }

    private val language = rootSettingsView.languages
    private val level = rootSettingsView.level

    init {
        language.setSelection(when (gameSettings.language) {
            GameSettings.Language.Hebrew -> 1
            GameSettings.Language.English -> 0
        })

        language.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                gameSettings.language = when (position) {
                    0 -> GameSettings.Language.English
                    else -> GameSettings.Language.Hebrew
                }
                gameOwner.resetGame()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                parent?.setSelection(0)
            }
        }

        level.progress = when (gameSettings.lettersCount) {
            GameSettings.LettersCount.Few -> 0
            GameSettings.LettersCount.Some -> 1
            GameSettings.LettersCount.More -> 2
            GameSettings.LettersCount.All -> 3
        }

        level.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                gameSettings.lettersCount = when (progress) {
                    0 -> GameSettings.LettersCount.Few
                    1 -> GameSettings.LettersCount.Some
                    2 -> GameSettings.LettersCount.More
                    else -> GameSettings.LettersCount.All
                }
                gameOwner.resetGame()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
    }

    fun flipPaneVisibility() {
        val showSettings = rootSettingsView.visibility != View.VISIBLE

        settingsButton.animate().rotation(120f * if (showSettings) -1f else 1f).setDuration(200).setInterpolator(AccelerateDecelerateInterpolator()).start()
        rootSettingsView.visibility = if (showSettings) View.VISIBLE else View.GONE
        gameLayout.isEnabled = !showSettings
        gameLayout.animate().scaleY(if (showSettings) .7f else 1f).scaleX(if (showSettings) .7f else 1f).alpha(if (showSettings) .5f else 1f).setInterpolator(AccelerateDecelerateInterpolator()).setDuration(200).start()
    }

}