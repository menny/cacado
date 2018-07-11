package net.evendanan.cacado.prefs

import android.content.Context
import android.preference.PreferenceManager

class GameSettings(context: Context) {
    private val res = context.resources
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    var language: Language
        get() = Language.valueOf(prefs.getString("GameSettings_language", Language.Hebrew.name))
        set(value) = prefs.edit().putString("GameSettings_language", value.name).apply()

    var lettersCount: LettersCount
        get() = LettersCount.valueOf(prefs.getString("GameSettings_lettersCount", LettersCount.Few.name))
        set(value) = prefs.edit().putString("GameSettings_lettersCount", value.name).apply()
    
    enum class Language {
        Hebrew, English
    }

    enum class LettersCount(val letters: Int) {
        Few(6), Some(10), More(18), All(100)
    }
}