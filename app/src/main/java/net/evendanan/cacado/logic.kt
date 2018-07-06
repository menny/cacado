package net.evendanan.cacado

interface ViewBinder {
    fun keepScreenAwake(enabled: Boolean)
    fun renderCards(cards: List<MemoryCard>)
    fun showCard(index: Int, withLetter: Boolean, withAudio: Boolean)
    fun hideCard(index: Int)
    fun highlightCard(index: Int, type: HighlightType)

    enum class HighlightType {
        Match,
        NotMatch
    }

    fun showWinMessage()
}

class Game(private val dealer: Dealer, private val viewBinder: ViewBinder) {

    private var cards = listOf<MemoryCard>()
    private val mCardsInPlay = ArrayList<Int>()
    private val mOpenCards = HashSet<Int>()

    fun start() {
        mCardsInPlay.clear()
        mOpenCards.clear()

        val dealtCards = dealer.cards
        if (dealtCards.isEmpty()) throw IllegalArgumentException("Cards list can not be empty")
        val cardsCounter = HashMap<MemoryCard, Int>()
        dealtCards.forEach {
            if (cardsCounter.containsKey(it)) {
                cardsCounter[it] = cardsCounter[it]!!.plus(1)
            } else {
                cardsCounter[it] = 1
            }
        }

        cardsCounter.forEach { card, count -> if (count != 2) throw IllegalArgumentException("Card ${card.text} has $count instances. It should only have two.") }

        cards = dealtCards
        viewBinder.keepScreenAwake(true)
        viewBinder.renderCards(cards)
    }

    internal fun onPlayerClicked(index: Int) {
        if (mCardsInPlay.size == 2) {
            //closing everything
            mCardsInPlay.forEach {
                mOpenCards.remove(it)
                viewBinder.hideCard(it)
            }
            mCardsInPlay.clear()
        } else if (!mOpenCards.contains(index)) {
            viewBinder.showCard(index, true, true)
            mOpenCards.add(index)
            mCardsInPlay.add(index)

            when (mCardsInPlay.pairMatch()) {
                Game.PlayResult.Match -> {
                    mCardsInPlay.forEach { viewBinder.highlightCard(it, ViewBinder.HighlightType.Match) }
                    if (mOpenCards.size == cards.size) {
                        viewBinder.showWinMessage()
                        viewBinder.keepScreenAwake(false)
                    }

                    mCardsInPlay.clear()
                }
                Game.PlayResult.NotMatch -> mCardsInPlay.forEach { viewBinder.highlightCard(it, ViewBinder.HighlightType.NotMatch) }
                else -> {
                }
            }
        }
    }

    private enum class PlayResult {
        Match,
        NotMatch,
        Incomplete
    }

    private fun ArrayList<Int>.pairMatch(): PlayResult {
        return when (this.size) {
            2 -> if (cards[this[0]] == cards[this[1]]) PlayResult.Match else PlayResult.NotMatch
            else -> PlayResult.Incomplete
        }
    }
}