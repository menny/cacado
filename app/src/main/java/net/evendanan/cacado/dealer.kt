package net.evendanan.cacado

interface Dealer {
    val cards: List<MemoryCard>
}

abstract class CardsDealer(private val maxLetters: Int, private val allLetters: () -> List<MemoryCard>) : Dealer {
    override val cards: List<MemoryCard>
        get() = allLetters().run {
            val subList = this.shuffled().subList(0, Math.min(maxLetters, this.size))
            val letters = subList.toMutableList()
            letters.addAll(subList)

            letters.shuffle()

            return letters
        }
}

private fun cards(letters: String, audios: List<Int>): List<MemoryCard> {
    if (letters.length != audios.size) throw IllegalArgumentException("letters and audios count are incompatible. ${letters.length} vs ${audios.size}.")

    val cards = ArrayList<MemoryCard>()
    letters.forEachIndexed { index, character -> cards.add(MemoryCard(character.toString(), audios[index])) }

    return cards
}

class HebrewCards(maxLetters: Int) : CardsDealer(maxLetters, {
    cards(
            "אבגדהוזחטיכלמנסעפצקרשת",
            listOf(R.raw.hebrew_alef, R.raw.hebrew_bet, R.raw.hebrew_gimel, R.raw.hebrew_dalet, R.raw.hebrew_hey,
                    R.raw.hebrew_vav, R.raw.hebrew_zain, R.raw.hebrew_khet, R.raw.hebrew_tet, R.raw.hebrew_yud,
                    R.raw.hebrew_kaf, R.raw.hebrew_lamed, R.raw.hebrew_mem, R.raw.hebrew_noon, R.raw.hebrew_samekh,
                    R.raw.hebrew_ain, R.raw.hebrew_pei, R.raw.hebrew_tzadik, R.raw.hebrew_kuf, R.raw.hebrew_resh,
                    R.raw.hebrew_shin, R.raw.hebrew_taff))
})

class EnglishCards(maxLetters: Int) : CardsDealer(maxLetters, {
    cards(
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
            listOf(R.raw.english_a, R.raw.english_b, R.raw.english_c, R.raw.english_d, R.raw.english_e,
                    R.raw.english_f, R.raw.english_g, R.raw.english_h, R.raw.english_i, R.raw.english_j,
                    R.raw.english_k, R.raw.english_l, R.raw.english_m, R.raw.english_n, R.raw.english_o,
                    R.raw.english_p, R.raw.english_q, R.raw.english_r, R.raw.english_s, R.raw.english_t,
                    R.raw.english_u, R.raw.english_v, R.raw.english_w, R.raw.english_x, R.raw.english_y,
                    R.raw.english_z))
})