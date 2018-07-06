package net.evendanan.cacado.ui

import android.animation.AnimatorInflater
import android.content.Context
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import net.evendanan.cacado.MemoryCard
import net.evendanan.cacado.R
import net.evendanan.cacado.ViewBinder
import java.util.*

internal class RevealHideCardHandler(context: Context, rootView: View, val memoryCard: MemoryCard) {
    private val cardFront = rootView.findViewById<View>(R.id.card_front)
    private val cardBack = rootView.findViewById<View>(R.id.card_back)
    private val letter = rootView.findViewById<View>(R.id.letter)
    private val highlightText = rootView.findViewById<TextView>(R.id.card_notice_text)
    private val revealCardAnimation = AnimatorInflater.loadAnimator(context, R.animator.reveal_card_animation_on_y)
    private val hideCardAnimation = AnimatorInflater.loadAnimator(context, R.animator.hide_card_animation_on_y)
    private val random = Random()
    private val highlightTranslation = context.resources.getDimension(R.dimen.highlight_translation_y)

    fun showCard(withLetter: Boolean) {
        letter.visibility = if (withLetter) View.VISIBLE else View.INVISIBLE

        hideCardAnimation.setTarget(cardBack)
        revealCardAnimation.setTarget(cardFront)
        hideCardAnimation.start()
        revealCardAnimation.start()

    }

    fun hideCard() {
        hideCardAnimation.setTarget(cardFront)
        revealCardAnimation.setTarget(cardBack)
        hideCardAnimation.start()
        revealCardAnimation.start()
    }

    fun highlightCard(type: ViewBinder.HighlightType) {
        when (type) {
            ViewBinder.HighlightType.Match -> highlightText.highlightMatch()
            ViewBinder.HighlightType.NotMatch -> highlightText.highlightNotMatch()
            ViewBinder.HighlightType.None -> highlightText.visibility = View.INVISIBLE
        }
    }

    private fun View.highlightAnimation(long: Boolean) {
        alpha = 0f
        scaleX = 0f
        scaleY = 0f
        translationY = top + highlightTranslation
        animate().apply {
            alpha(1f)
            scaleX(1f)
            scaleY(1f)
            translationYBy(-highlightTranslation)
            duration = 600
            interpolator = DecelerateInterpolator()
        }.withEndAction {
            animate().apply {
                alpha(0f)
                translationYBy(-highlightTranslation.div(2))
                interpolator = AccelerateInterpolator()
                duration = 200
                startDelay = if (long) 1000 else 500
            }.start()
        }.start()
    }

    private fun TextView.highlightMatch() {
        visibility = View.VISIBLE
        text = randomString("🤗", "🤩", "👍", "👌", "🤘", "💖", "🎉")
        highlightAnimation(true)
    }

    private fun TextView.highlightNotMatch() {
        visibility = View.VISIBLE
        text = randomString("😫", "😡", "👿", "❌")
        highlightAnimation(false)
    }

    private fun randomString(vararg possibilities: String) = possibilities[random.nextInt(possibilities.size)]
}