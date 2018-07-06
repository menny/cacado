package net.evendanan.cacado

interface ViewBinder {
    fun renderCards(cards: List<CardCell>)
    fun showCard(card: CardCell)
    fun hideCard(card: CardCell)
    fun showWinMessage()
    val horizontalCellsCount: Int
    val verticalCellsCount: Int
}

class Dealer(private val cards: List<MemoryCard>, private val viewBinder: ViewBinder) {
    fun start() {

    }

    fun stop() {

    }

    internal fun onPlayerClicked(cardCell: CardCell) {

    }
}