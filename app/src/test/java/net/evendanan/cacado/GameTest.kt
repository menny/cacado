package net.evendanan.cacado

import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test

class DealerTest {

    private lateinit var mCardsProvider: CardsProvider
    private lateinit var mViewBinder: ViewBinder
    private lateinit var mDealerUnderTest: Dealer
    private var mCards = listOf(
            MemoryCard("A"), MemoryCard("B"), MemoryCard("C"),
            MemoryCard("D"), MemoryCard("E"), MemoryCard("F"),
            MemoryCard("A"), MemoryCard("B"), MemoryCard("C"),
            MemoryCard("D"), MemoryCard("E"), MemoryCard("F"),
            MemoryCard("M"), MemoryCard("M"))

    @Before
    fun setUp() {
        mCardsProvider = mock {
            on { cards } doReturn mCards
        }

        mViewBinder = mock()
        mDealerUnderTest = Dealer(mCardsProvider, mViewBinder)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testStartVerifyPairsMissing() {
        mCardsProvider = mock {
            on { cards } doReturn listOf(MemoryCard("A"), MemoryCard("A"), MemoryCard("C"))
        }
        Dealer(mCardsProvider, mock()).start()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testStartVerifyNoPairs() {
        mCardsProvider = mock {
            on { cards } doReturn listOf<MemoryCard>()
        }
        Dealer(mCardsProvider, mock()).start()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testStartVerifyPairsExtra() {
        mCardsProvider = mock {
            on { cards } doReturn listOf(MemoryCard("A"), MemoryCard("A"), MemoryCard("C"), MemoryCard("C"), MemoryCard("C"))
        }

        Dealer(mCardsProvider, mock()).start()
    }

    @Test
    @SuppressWarnings("unchecked")
    fun testInitializeViewOnStart() {
        verifyZeroInteractions(mViewBinder)
        mDealerUnderTest.start()

        verify(mViewBinder).renderCards(same(mCards))
    }

    @Test
    fun testHappyPathGame() {
        mDealerUnderTest.start()

        mDealerUnderTest.onPlayerClicked(0)
        mDealerUnderTest.onPlayerClicked(2)
        //will hide both open cards. Will not open a new
        mDealerUnderTest.onPlayerClicked(2)

        mDealerUnderTest.onPlayerClicked(2)
        mDealerUnderTest.onPlayerClicked(8)
        //will keep 2&8 open, since they are a match

        mDealerUnderTest.onPlayerClicked(9)
        mDealerUnderTest.onPlayerClicked(1)
        //will hide both open cards. Will not open a new
        mDealerUnderTest.onPlayerClicked(1)

        //2 is already open, so nothing happens
        mDealerUnderTest.onPlayerClicked(2)

        mDealerUnderTest.onPlayerClicked(4)
        mDealerUnderTest.onPlayerClicked(4)
        mDealerUnderTest.onPlayerClicked(3)

        mDealerUnderTest.onPlayerClicked(5)

        val inOrder = inOrder(mViewBinder)
        inOrder.verify(mViewBinder).renderCards(any())

        inOrder.verify(mViewBinder).showCard(0)
        inOrder.verify(mViewBinder).showCard(2)
        inOrder.verify(mViewBinder).highlightCard(0, ViewBinder.HighlightType.NotMatch)
        inOrder.verify(mViewBinder).highlightCard(2, ViewBinder.HighlightType.NotMatch)

        inOrder.verify(mViewBinder).hideCard(0)
        inOrder.verify(mViewBinder).hideCard(2)

        inOrder.verify(mViewBinder).showCard(2)
        inOrder.verify(mViewBinder).showCard(8)
        inOrder.verify(mViewBinder).highlightCard(2, ViewBinder.HighlightType.Match)
        inOrder.verify(mViewBinder).highlightCard(8, ViewBinder.HighlightType.Match)

        inOrder.verify(mViewBinder).showCard(9)
        inOrder.verify(mViewBinder).showCard(1)
        inOrder.verify(mViewBinder).highlightCard(9, ViewBinder.HighlightType.NotMatch)
        inOrder.verify(mViewBinder).highlightCard(1, ViewBinder.HighlightType.NotMatch)

        inOrder.verify(mViewBinder).hideCard(9)
        inOrder.verify(mViewBinder).hideCard(1)

        inOrder.verify(mViewBinder).showCard(4)
        inOrder.verify(mViewBinder).showCard(3)
        inOrder.verify(mViewBinder).highlightCard(4, ViewBinder.HighlightType.NotMatch)
        inOrder.verify(mViewBinder).highlightCard(3, ViewBinder.HighlightType.NotMatch)

        inOrder.verify(mViewBinder).hideCard(4)
        inOrder.verify(mViewBinder).hideCard(3)

        inOrder.verifyNoMoreInteractions()
    }

    @Test
    fun testWinGame() {
        val viewBinder = mock<ViewBinder>()
        val dealerUnderTest = Dealer(listOf(MemoryCard("A"), MemoryCard("A"), MemoryCard("C"), MemoryCard("C")), viewBinder)
        dealerUnderTest.start()

        dealerUnderTest.onPlayerClicked(0)
        dealerUnderTest.onPlayerClicked(1)
        dealerUnderTest.onPlayerClicked(2)
        dealerUnderTest.onPlayerClicked(3)

        val inOrder = inOrder(viewBinder)
        inOrder.verify(viewBinder).renderCards(any())

        inOrder.verify(viewBinder).showCard(0)
        inOrder.verify(viewBinder).showCard(1)
        inOrder.verify(viewBinder).highlightCard(0, ViewBinder.HighlightType.Match)
        inOrder.verify(viewBinder).highlightCard(1, ViewBinder.HighlightType.Match)

        inOrder.verify(viewBinder).showCard(2)
        inOrder.verify(viewBinder).showCard(3)

        inOrder.verify(viewBinder).showWinMessage()

        inOrder.verifyNoMoreInteractions()
    }
}