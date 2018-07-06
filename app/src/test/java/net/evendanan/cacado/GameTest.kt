package net.evendanan.cacado

import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test

class GameTest {

    private lateinit var mDealer: Dealer
    private lateinit var mViewBinder: ViewBinder
    private lateinit var mGameUnderTest: Game
    private var mCards = listOf(
            generateCard("A"), generateCard("B"), generateCard("C"),
            generateCard("D"), generateCard("E"), generateCard("F"),
            generateCard("A"), generateCard("B"), generateCard("C"),
            generateCard("D"), generateCard("E"), generateCard("F"),
            generateCard("M"), generateCard("M"))

    @Before
    fun setUp() {
        mDealer = mock {
            on { cards } doReturn mCards
        }

        mViewBinder = mock()
        mGameUnderTest = Game(mDealer, mViewBinder)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testStartVerifyPairsMissing() {
        mDealer = mock {
            on { cards } doReturn listOf(generateCard("A"), generateCard("A"), generateCard("C"))
        }
        Game(mDealer, mock()).start()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testStartVerifyNoPairs() {
        mDealer = mock {
            on { cards } doReturn listOf<MemoryCard>()
        }
        Game(mDealer, mock()).start()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testStartVerifyPairsExtra() {
        mDealer = mock {
            on { cards } doReturn listOf(generateCard("A"), generateCard("A"), generateCard("C"), generateCard("C"), generateCard("C"))
        }

        Game(mDealer, mock()).start()
    }

    @Test
    fun testInitializeViewOnStart() {
        verifyZeroInteractions(mViewBinder)
        mGameUnderTest.start()

        verify(mViewBinder).renderCards(same(mCards))
    }

    @Test
    fun testGetsCardsUponStart() {
        verifyZeroInteractions(mDealer)
        mGameUnderTest.start()
        verify(mDealer).cards

        mGameUnderTest.start()
        verify(mDealer, times(2)).cards
    }

    @Test
    fun testHappyPathGame() {
        mGameUnderTest.start()

        mGameUnderTest.onPlayerClicked(0)
        mGameUnderTest.onPlayerClicked(2)
        //will hide both open cards. Will not open a new
        mGameUnderTest.onPlayerClicked(2)

        mGameUnderTest.onPlayerClicked(2)
        mGameUnderTest.onPlayerClicked(8)
        //will keep 2&8 open, since they are a match

        mGameUnderTest.onPlayerClicked(9)
        mGameUnderTest.onPlayerClicked(1)
        //will hide both open cards. Will not open a new
        mGameUnderTest.onPlayerClicked(1)

        //2 is already open, so nothing happens
        mGameUnderTest.onPlayerClicked(2)

        mGameUnderTest.onPlayerClicked(4)
        mGameUnderTest.onPlayerClicked(4)
        mGameUnderTest.onPlayerClicked(3)

        mGameUnderTest.onPlayerClicked(5)

        val inOrder = inOrder(mViewBinder)
        inOrder.verify(mViewBinder).keepScreenAwake(true)
        inOrder.verify(mViewBinder).renderCards(any())

        inOrder.verify(mViewBinder).showCard(eq(0), any(), any())
        inOrder.verify(mViewBinder).showCard(eq(2), any(), any())
        //highlight only the second card
        inOrder.verify(mViewBinder).highlightCard(2, ViewBinder.HighlightType.NotMatch)

        inOrder.verify(mViewBinder).hideCard(0)
        inOrder.verify(mViewBinder).highlightCard(0, ViewBinder.HighlightType.None)
        inOrder.verify(mViewBinder).hideCard(2)
        inOrder.verify(mViewBinder).highlightCard(2, ViewBinder.HighlightType.None)

        inOrder.verify(mViewBinder).showCard(eq(2), any(), any())
        inOrder.verify(mViewBinder).showCard(eq(8), any(), any())
        //highlight only the second card
        inOrder.verify(mViewBinder).highlightCard(8, ViewBinder.HighlightType.Match)

        inOrder.verify(mViewBinder).showCard(eq(9), any(), any())
        inOrder.verify(mViewBinder).showCard(eq(1), any(), any())
        //highlight only the second card
        inOrder.verify(mViewBinder).highlightCard(1, ViewBinder.HighlightType.NotMatch)

        inOrder.verify(mViewBinder).hideCard(9)
        inOrder.verify(mViewBinder).highlightCard(9, ViewBinder.HighlightType.None)
        inOrder.verify(mViewBinder).hideCard(1)
        inOrder.verify(mViewBinder).highlightCard(1, ViewBinder.HighlightType.None)

        inOrder.verify(mViewBinder).showCard(eq(4), any(), any())
        inOrder.verify(mViewBinder).showCard(eq(3), any(), any())
        //highlight only the second card
        inOrder.verify(mViewBinder).highlightCard(3, ViewBinder.HighlightType.NotMatch)

        inOrder.verify(mViewBinder).hideCard(4)
        inOrder.verify(mViewBinder).highlightCard(4, ViewBinder.HighlightType.None)
        inOrder.verify(mViewBinder).hideCard(3)
        inOrder.verify(mViewBinder).highlightCard(3, ViewBinder.HighlightType.None)

        inOrder.verifyNoMoreInteractions()
    }

    @Test
    fun testWinGame() {
        val viewBinder = mock<ViewBinder>()
        mDealer = mock {
            on { cards } doReturn listOf(generateCard("A"), generateCard("A"), generateCard("C"), generateCard("C"))
        }

        val dealerUnderTest = Game(mDealer, viewBinder)
        dealerUnderTest.start()

        dealerUnderTest.onPlayerClicked(0)
        dealerUnderTest.onPlayerClicked(1)
        dealerUnderTest.onPlayerClicked(2)
        dealerUnderTest.onPlayerClicked(3)

        val inOrder = inOrder(viewBinder)
        inOrder.verify(viewBinder).keepScreenAwake(true)
        inOrder.verify(viewBinder).renderCards(any())

        inOrder.verify(viewBinder).showCard(eq(0), any(), any())
        inOrder.verify(viewBinder).showCard(eq(1), any(), any())
        inOrder.verify(viewBinder).highlightCard(1, ViewBinder.HighlightType.Match)

        inOrder.verify(viewBinder).showCard(eq(2), any(), any())
        inOrder.verify(viewBinder).showCard(eq(3), any(), any())

        inOrder.verify(viewBinder).highlightCard(3, ViewBinder.HighlightType.Match)

        inOrder.verify(viewBinder).showWinMessage()
        inOrder.verify(viewBinder).keepScreenAwake(false)

        inOrder.verifyNoMoreInteractions()
    }

    @Test
    fun testRestartGame() {
        val viewBinder = mock<ViewBinder>()
        mDealer = mock {
            on { cards } doReturn listOf(generateCard("A"), generateCard("A"), generateCard("C"), generateCard("C"))
        }

        val dealerUnderTest = Game(mDealer, viewBinder)
        dealerUnderTest.start()

        dealerUnderTest.onPlayerClicked(0)
        dealerUnderTest.start()
        dealerUnderTest.onPlayerClicked(2)
        dealerUnderTest.onPlayerClicked(3)

        val inOrder = inOrder(viewBinder)
        inOrder.verify(viewBinder).keepScreenAwake(true)
        inOrder.verify(viewBinder).renderCards(any())

        inOrder.verify(viewBinder).showCard(eq(0), any(), any())

        inOrder.verify(viewBinder).keepScreenAwake(true)
        inOrder.verify(viewBinder).renderCards(any())

        inOrder.verify(viewBinder).showCard(eq(2), any(), any())
        inOrder.verify(viewBinder).showCard(eq(3), any(), any())

        inOrder.verify(viewBinder).highlightCard(3, ViewBinder.HighlightType.Match)

        inOrder.verifyNoMoreInteractions()
    }

    private fun generateCard(letter: String) = MemoryCard(letter, letter.hashCode())
}