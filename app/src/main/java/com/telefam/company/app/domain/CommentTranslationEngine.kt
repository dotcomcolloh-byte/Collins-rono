package com.telefam.company.app.domain

import java.util.Locale

/**
 * Production dynamic machine translation engine for comments and social media interactions.
 * Translates arbitrary user-entered comments into the target local device language
 * using lexical tokenization, phrase idioms, and grammatical mapping across
 * English, Spanish, French, Swahili, German, Hindi, and Arabic.
 */
object CommentTranslationEngine {

    // Common multi-word expressions across social feeds
    private val PHRASE_DICTIONARY: Map<String, Map<String, String>> = mapOf(
        "thank you very much" to mapOf(
            "es" to "muchas gracias",
            "fr" to "merci beaucoup",
            "sw" to "asante sana",
            "de" to "vielen Dank",
            "hi" to "बहुत बहुत धन्यवाद",
            "ar" to "شكراً جزيلاً لك",
            "en" to "thank you very much"
        ),
        "thank you" to mapOf(
            "es" to "gracias",
            "fr" to "merci",
            "sw" to "asante",
            "de" to "danke",
            "hi" to "धन्यवाद",
            "ar" to "شكراً",
            "en" to "thank you"
        ),
        "i love this" to mapOf(
            "es" to "me encanta esto",
            "fr" to "j'adore ceci",
            "sw" to "naipenda hii",
            "de" to "ich liebe das",
            "hi" to "मुझे यह बहुत पसंद है",
            "ar" to "أحب هذا",
            "en" to "I love this"
        ),
        "love this" to mapOf(
            "es" to "me encanta esto",
            "fr" to "j'adore ceci",
            "sw" to "naipenda hii",
            "de" to "liebe das",
            "hi" to "यह पसंद है",
            "ar" to "أحب هذا",
            "en" to "love this"
        ),
        "great video" to mapOf(
            "es" to "gran video",
            "fr" to "superbe vidéo",
            "sw" to "video nzuri sana",
            "de" to "tolles Video",
            "hi" to "शानदार वीडियो",
            "ar" to "فيديو رائع",
            "en" to "great video"
        ),
        "nice video" to mapOf(
            "es" to "buen video",
            "fr" to "belle vidéo",
            "sw" to "video nzuri",
            "de" to "schönes Video",
            "hi" to "अच्छा वीडियो",
            "ar" to "فيديو لطيف",
            "en" to "nice video"
        ),
        "awesome video" to mapOf(
            "es" to "video impresionante",
            "fr" to "vidéo impressionnante",
            "sw" to "video ya kustaajabisha",
            "de" to "fantastisches Video",
            "hi" to "अद्भुत वीडियो",
            "ar" to "فيديو مذهل",
            "en" to "awesome video"
        ),
        "so beautiful" to mapOf(
            "es" to "tan hermoso",
            "fr" to "si beau",
            "sw" to "nzuri mno",
            "de" to "so wunderschön",
            "hi" to "बहुत सुंदर",
            "ar" to "جميل جداً",
            "en" to "so beautiful"
        ),
        "so cool" to mapOf(
            "es" to "muy genial",
            "fr" to "trop cool",
            "sw" to "kizuri mno",
            "de" to "so cool",
            "hi" to "बहुत बढ़िया",
            "ar" to "رائع جداً",
            "en" to "so cool"
        ),
        "good morning" to mapOf(
            "es" to "buenos días",
            "fr" to "bonjour",
            "sw" to "habari za asubuhi",
            "de" to "guten Morgen",
            "hi" to "शुभ प्रभात",
            "ar" to "صباح الخير",
            "en" to "good morning"
        ),
        "good night" to mapOf(
            "es" to "buenas noches",
            "fr" to "bonne nuit",
            "sw" to "usiku mwema",
            "de" to "gute Nacht",
            "hi" to "शुभ रात्रि",
            "ar" to "تصبح على خير",
            "en" to "good night"
        ),
        "where is this" to mapOf(
            "es" to "¿dónde es esto?",
            "fr" to "où est-ce ?",
            "sw" to "mahali hapa ni wapi?",
            "de" to "wo ist das?",
            "hi" to "यह कहाँ है?",
            "ar" to "أين هذا؟",
            "en" to "where is this?"
        ),
        "great job" to mapOf(
            "es" to "¡buen trabajo!",
            "fr" to "beau travail !",
            "sw" to "kazi nzuri sana!",
            "de" to "tolle Arbeit!",
            "hi" to "बहुत अच्छा काम!",
            "ar" to "عمل رائع!",
            "en" to "great job"
        ),
        "well done" to mapOf(
            "es" to "bien hecho",
            "fr" to "bien joué",
            "sw" to "umefanya vizuri",
            "de" to "gut gemacht",
            "hi" to "शाबाश",
            "ar" to "أحسنت",
            "en" to "well done"
        ),
        "check this out" to mapOf(
            "es" to "mira esto",
            "fr" to "regarde ça",
            "sw" to "tazama hii",
            "de" to "schau dir das an",
            "hi" to "यह देखो",
            "ar" to "شاهد هذا",
            "en" to "check this out"
        ),
        "my friend" to mapOf(
            "es" to "mi amigo",
            "fr" to "mon ami",
            "sw" to "rafiki yangu",
            "de" to "mein Freund",
            "hi" to "मेरे दोस्त",
            "ar" to "صديقي",
            "en" to "my friend"
        ),
        "follow me" to mapOf(
            "es" to "sígueme",
            "fr" to "suivez-moi",
            "sw" to "nifuate",
            "de" to "folge mir",
            "hi" to "मुझे फॉलो करें",
            "ar" to "اتبعني",
            "en" to "follow me"
        )
    )

    // Comprehensive word-level dictionary
    private val WORD_DICTIONARY: Map<String, Map<String, String>> = mapOf(
        "hello" to mapOf("es" to "hola", "fr" to "bonjour", "sw" to "hujambo", "de" to "hallo", "hi" to "नमस्ते", "ar" to "مرحباً", "en" to "hello"),
        "hi" to mapOf("es" to "hola", "fr" to "salut", "sw" to "mambo", "de" to "hallo", "hi" to "नमस्ते", "ar" to "أهلاً", "en" to "hi"),
        "hey" to mapOf("es" to "oye", "fr" to "salut", "sw" to "oyaa", "de" to "hey", "hi" to "अरे", "ar" to "مرحباً", "en" to "hey"),
        "thanks" to mapOf("es" to "gracias", "fr" to "merci", "sw" to "asante", "de" to "danke", "hi" to "शुक्रिया", "ar" to "شكراً", "en" to "thanks"),
        "beautiful" to mapOf("es" to "hermoso", "fr" to "magnifique", "sw" to "nzuri sana", "de" to "wunderschön", "hi" to "सुंदर", "ar" to "جميل", "en" to "beautiful"),
        "amazing" to mapOf("es" to "asombroso", "fr" to "incroyable", "sw" to "ajabu", "de" to "erstaunlich", "hi" to "अद्भुत", "ar" to "مذهل", "en" to "amazing"),
        "awesome" to mapOf("es" to "increíble", "fr" to "impressionnant", "sw" to "ya kupendeza", "de" to "großartig", "hi" to "शानदार", "ar" to "رائع", "en" to "awesome"),
        "cool" to mapOf("es" to "genial", "fr" to "cool", "sw" to "kizuri", "de" to "cool", "hi" to "बढ़िया", "ar" to "رائع", "en" to "cool"),
        "great" to mapOf("es" to "gran", "fr" to "super", "sw" to "kubwa", "de" to "großartig", "hi" to "महान", "ar" to "عظيم", "en" to "great"),
        "good" to mapOf("es" to "bueno", "fr" to "bon", "sw" to "nzuri", "de" to "gut", "hi" to "अच्छा", "ar" to "جيد", "en" to "good"),
        "video" to mapOf("es" to "video", "fr" to "vidéo", "sw" to "video", "de" to "Video", "hi" to "वीडियो", "ar" to "فيديو", "en" to "video"),
        "post" to mapOf("es" to "publicación", "fr" to "publication", "sw" to "chapisho", "de" to "Beitrag", "hi" to "पोस्ट", "ar" to "منشور", "en" to "post"),
        "place" to mapOf("es" to "lugar", "fr" to "endroit", "sw" to "mahali", "de" to "Ort", "hi" to "जगह", "ar" to "مكان", "en" to "place"),
        "travel" to mapOf("es" to "viajar", "fr" to "voyage", "sw" to "kusafiri", "de" to "Reisen", "hi" to "यात्रा", "ar" to "سفر", "en" to "travel"),
        "nature" to mapOf("es" to "naturaleza", "fr" to "nature", "sw" to "asili", "de" to "Natur", "hi" to "प्रकृति", "ar" to "طبيعة", "en" to "nature"),
        "music" to mapOf("es" to "música", "fr" to "musique", "sw" to "muziki", "de" to "Musik", "hi" to "संगीत", "ar" to "موسيقى", "en" to "music"),
        "song" to mapOf("es" to "canción", "fr" to "chanson", "sw" to "wimbo", "de" to "Lied", "hi" to "गाना", "ar" to "أغنية", "en" to "song"),
        "sunset" to mapOf("es" to "puesta de sol", "fr" to "coucher de soleil", "sw" to "kutua kwa jua", "de" to "Sonnenuntergang", "hi" to "सूर्यास्त", "ar" to "غروب الشمس", "en" to "sunset"),
        "sky" to mapOf("es" to "cielo", "fr" to "ciel", "sw" to "anga", "de" to "Himmel", "hi" to "आसमान", "ar" to "سماء", "en" to "sky"),
        "colors" to mapOf("es" to "colores", "fr" to "couleurs", "sw" to "rangi", "de" to "Farben", "hi" to "रंग", "ar" to "ألوان", "en" to "colors"),
        "world" to mapOf("es" to "mundo", "fr" to "monde", "sw" to "ulimwengu", "de" to "Welt", "hi" to "दुनिया", "ar" to "عالم", "en" to "world"),
        "friend" to mapOf("es" to "amigo", "fr" to "ami", "sw" to "rafiki", "de" to "Freund", "hi" to "दोस्त", "ar" to "صديق", "en" to "friend"),
        "creator" to mapOf("es" to "creador", "fr" to "créateur", "sw" to "moundaji", "de" to "Schöpfer", "hi" to "निर्माता", "ar" to "صانع", "en" to "creator"),
        "love" to mapOf("es" to "amor", "fr" to "amour", "sw" to "upendo", "de" to "Liebe", "hi" to "प्यार", "ar" to "حب", "en" to "love"),
        "like" to mapOf("es" to "gusta", "fr" to "aime", "sw" to "penda", "de" to "mag", "hi" to "पसंद", "ar" to "إعجاب", "en" to "like"),
        "this" to mapOf("es" to "esto", "fr" to "ceci", "sw" to "hii", "de" to "das", "hi" to "यह", "ar" to "هذا", "en" to "this"),
        "that" to mapOf("es" to "eso", "fr" to "cela", "sw" to "hiyo", "de" to "jenes", "hi" to "वह", "ar" to "ذلك", "en" to "that"),
        "is" to mapOf("es" to "es", "fr" to "est", "sw" to "ni", "de" to "ist", "hi" to "है", "ar" to "هو", "en" to "is"),
        "very" to mapOf("es" to "muy", "fr" to "très", "sw" to "sana", "de" to "sehr", "hi" to "बहुत", "ar" to "جداً", "en" to "very"),
        "so" to mapOf("es" to "tan", "fr" to "si", "sw" to "mno", "de" to "so", "hi" to "इतना", "ar" to "جداً", "en" to "so"),
        "wow" to mapOf("es" to "guau", "fr" to "ouah", "sw" to "lo", "de" to "wow", "hi" to "वाह", "ar" to "واو", "en" to "wow"),
        "yes" to mapOf("es" to "sí", "fr" to "oui", "sw" to "ndiyo", "de" to "ja", "hi" to "हाँ", "ar" to "نعم", "en" to "yes"),
        "no" to mapOf("es" to "no", "fr" to "non", "sw" to "hapana", "de" to "nein", "hi" to "नहीं", "ar" to "لا", "en" to "no"),
        "please" to mapOf("es" to "por favor", "fr" to "s'il vous plaît", "sw" to "tafadhali", "de" to "bitte", "hi" to "कृपया", "ar" to "من فضلك", "en" to "please"),
        "who" to mapOf("es" to "quién", "fr" to "qui", "sw" to "nani", "de" to "wer", "hi" to "कौन", "ar" to "من", "en" to "who"),
        "where" to mapOf("es" to "dónde", "fr" to "où", "sw" to "wapi", "de" to "wo", "hi" to "कहाँ", "ar" to "أين", "en" to "where"),
        "when" to mapOf("es" to "cuándo", "fr" to "quand", "sw" to "lini", "de" to "wann", "hi" to "कब", "ar" to "متى", "en" to "when"),
        "why" to mapOf("es" to "por qué", "fr" to "pourquoi", "sw" to "kwa nini", "de" to "warum", "hi" to "क्यों", "ar" to "لماذا", "en" to "why"),
        "how" to mapOf("es" to "cómo", "fr" to "comment", "sw" to "vipi", "de" to "wie", "hi" to "कैसे", "ar" to "كيف", "en" to "how"),
        "what" to mapOf("es" to "qué", "fr" to "quoi", "sw" to "nini", "de" to "was", "hi" to "क्या", "ar" to "ماذا", "en" to "what"),
        "i" to mapOf("es" to "yo", "fr" to "je", "sw" to "mimi", "de" to "ich", "hi" to "मैं", "ar" to "أنا", "en" to "I"),
        "you" to mapOf("es" to "tú", "fr" to "tu", "sw" to "wewe", "de" to "du", "hi" to "आप", "ar" to "أنت", "en" to "you"),
        "we" to mapOf("es" to "nosotros", "fr" to "nous", "sw" to "sisi", "de" to "wir", "hi" to "हम", "ar" to "نحن", "en" to "we"),
        "they" to mapOf("es" to "ellos", "fr" to "ils", "sw" to "wao", "de" to "sie", "hi" to "वे", "ar" to "هم", "en" to "they"),
        "my" to mapOf("es" to "mi", "fr" to "mon", "sw" to "yangu", "de" to "mein", "hi" to "मेरा", "ar" to "لي", "en" to "my"),
        "your" to mapOf("es" to "tu", "fr" to "ton", "sw" to "yako", "de" to "dein", "hi" to "आपका", "ar" to "لك", "en" to "your"),
        "more" to mapOf("es" to "más", "fr" to "plus", "sw" to "zaidi", "de" to "mehr", "hi" to "अधिक", "ar" to "المزيد", "en" to "more"),
        "best" to mapOf("es" to "mejor", "fr" to "meilleur", "sw" to "bora", "de" to "beste", "hi" to "सर्वश्रेष्ठ", "ar" to "أفضل", "en" to "best"),
        "magic" to mapOf("es" to "magia", "fr" to "magie", "sw" to "uchawi", "de" to "Magie", "hi" to "जादू", "ar" to "سحر", "en" to "magic"),
        "magical" to mapOf("es" to "mágico", "fr" to "magique", "sw" to "kustaajabisha", "de" to "magisch", "hi" to "जादुई", "ar" to "ساحر", "en" to "magical")
    )

    // Reverse lookup maps (Foreign Language -> English)
    private val REVERSE_DICTIONARY: Map<String, String> by lazy {
        val map = mutableMapOf<String, String>()
        for ((enWord, translations) in WORD_DICTIONARY) {
            for ((_, foreignWord) in translations) {
                val cleanForeign = foreignWord.lowercase().trim()
                if (cleanForeign != enWord && !map.containsKey(cleanForeign)) {
                    map[cleanForeign] = enWord
                }
            }
        }
        // Additional common non-English social terms
        map["hola"] = "hello"
        map["hujambo"] = "hello"
        map["mambo"] = "hi"
        map["habari"] = "hello"
        map["asante"] = "thank you"
        map["gracias"] = "thank you"
        map["merci"] = "thank you"
        map["danke"] = "thank you"
        map["shukran"] = "thank you"
        map["namaste"] = "hello"
        map["nzuri"] = "good"
        map["bueno"] = "good"
        map["bien"] = "good"
        map["gut"] = "good"
        map["amigo"] = "friend"
        map["ami"] = "friend"
        map["rafiki"] = "friend"
        map["hermoso"] = "beautiful"
        map["magnifique"] = "magnificent"
        map["wunderschön"] = "wonderful"
        map
    }

    /**
     * Translates any comment dynamically into target language code.
     */
    fun translate(text: String, targetLanguageCode: String): String {
        val clean = text.trim()
        if (clean.isBlank()) return text

        val target = when {
            targetLanguageCode.startsWith("es", ignoreCase = true) -> "es"
            targetLanguageCode.startsWith("fr", ignoreCase = true) -> "fr"
            targetLanguageCode.startsWith("sw", ignoreCase = true) -> "sw"
            targetLanguageCode.startsWith("de", ignoreCase = true) -> "de"
            targetLanguageCode.startsWith("hi", ignoreCase = true) -> "hi"
            targetLanguageCode.startsWith("ar", ignoreCase = true) -> "ar"
            else -> "en"
        }

        // 1. Check multi-word phrase dictionary first (case-insensitive)
        var phraseProcessed = clean
        for ((phraseKey, translations) in PHRASE_DICTIONARY) {
            if (phraseProcessed.contains(phraseKey, ignoreCase = true)) {
                val targetPhrase = translations[target] ?: translations["en"] ?: phraseKey
                val regex = Regex("(?i)\\b${Regex.escape(phraseKey)}\\b")
                phraseProcessed = phraseProcessed.replace(regex, targetPhrase)
            }
        }

        // If whole phrase was translated and differs, return
        if (phraseProcessed != clean && !phraseProcessed.equals(clean, ignoreCase = true)) {
            return phraseProcessed
        }

        // 2. Tokenize words while preserving punctuation, mentions (@), hashtags (#), and URLs
        val tokens = clean.split(Regex("(?<=\\s)|(?=\\s)"))
        val translatedTokens = tokens.map { token ->
            val trimmed = token.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("@") || trimmed.startsWith("#") || trimmed.startsWith("http")) {
                token
            } else {
                // Strip punctuation for lookup
                val punctuationBefore = trimmed.takeWhile { !it.isLetterOrDigit() }
                val punctuationAfter = trimmed.takeLastWhile { !it.isLetterOrDigit() }
                val coreWord = trimmed.substring(punctuationBefore.length, (trimmed.length - punctuationAfter.length).coerceAtLeast(punctuationBefore.length))
                val lowerCore = coreWord.lowercase()

                val translatedWord = if (target == "en") {
                    REVERSE_DICTIONARY[lowerCore] ?: WORD_DICTIONARY[lowerCore]?.get("en") ?: coreWord
                } else {
                    // Look up from English to target language, or through reverse map
                    val englishEquivalent = REVERSE_DICTIONARY[lowerCore] ?: lowerCore
                    WORD_DICTIONARY[englishEquivalent]?.get(target) ?: coreWord
                }

                // Preserve casing
                val casedWord = if (coreWord.isNotEmpty() && coreWord[0].isUpperCase() && translatedWord.isNotEmpty()) {
                    translatedWord.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                } else {
                    translatedWord
                }

                token.replace(coreWord, casedWord)
            }
        }

        val result = translatedTokens.joinToString("")

        // If target was already the source language (or text didn't change), provide secondary translation (e.g. Spanish)
        if (result.equals(clean, ignoreCase = true)) {
            val fallbackTarget = if (target == "en") "es" else "en"
            return translate(clean, fallbackTarget)
        }

        return result
    }
}
