package net.evendanan.cacado

import org.junit.Assert
import org.junit.Test

class CardsDealerTest {

    @Test
    fun testCreatesEnglishCards() {
        assertCardsCreatedCorrectly(EnglishCards(1000).cards, 26)
    }

    @Test
    fun testCreatesHebrewCards() {
        assertCardsCreatedCorrectly(HebrewCards(1000).cards, 22)
    }

    private fun assertCardsCreatedCorrectly(cards: List<MemoryCard>, expectedLettersCount: Int) {
        Assert.assertEquals(expectedLettersCount.times(2), cards.size)

        val seenLetters = HashMap<String, Int>()
        val seenAudios = HashMap<Int, Int>()

        cards.forEach {
            seenLetters.compute(it.text) { _, u -> u?.plus(1) ?: 1 }
            seenAudios.compute(it.audio) { _, u -> u?.plus(1) ?: 1 }
        }

        Assert.assertEquals(expectedLettersCount, seenLetters.size)
        seenLetters.forEach { t, u -> Assert.assertEquals("The letter $t has $u count", 2, u) }
        Assert.assertEquals(expectedLettersCount, seenAudios.size)
        seenAudios.forEach { t, u -> Assert.assertEquals("The audio $t has $u count", 2, u) }
    }

    @Test
    fun testGetShuffledSubCards() {
        val dealerUnderTest = EnglishCards(12)
        Assert.assertNotSame(dealerUnderTest.cards, dealerUnderTest.cards)

        val cards = dealerUnderTest.cards
        Assert.assertEquals(24, cards.size)

        val cardsCounter = HashMap<MemoryCard, Int>()
        cards.forEach {
            if (cardsCounter.containsKey(it)) {
                cardsCounter[it] = cardsCounter[it]!!.plus(1)
            } else {
                cardsCounter[it] = 1
            }
        }

        cardsCounter.forEach { card, count -> if (count != 2) throw IllegalArgumentException("Card ${card.text} has $count instances. It should only have two.") }
    }
}