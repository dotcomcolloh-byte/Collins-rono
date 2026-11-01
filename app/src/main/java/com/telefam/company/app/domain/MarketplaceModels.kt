package com.telefam.company.app.domain

import androidx.annotation.StringRes
import com.example.R

/**
 * Top-level categories for Marketplace listings.
 */
enum class MarketplaceCategory(
    val categoryKey: String,
    @StringRes val titleRes: Int
) {
    ALL("all", R.string.marketplace_cat_all),
    VEHICLES("vehicles", R.string.marketplace_cat_vehicles),
    REAL_ESTATE("real_estate", R.string.marketplace_cat_real_estate),
    ELECTRONICS("electronics", R.string.marketplace_cat_electronics),
    FASHION("fashion", R.string.marketplace_cat_fashion),
    HOME_GARDEN("home_garden", R.string.marketplace_cat_home_garden),
    BEAUTY_HEALTH("beauty_health", R.string.marketplace_cat_beauty_health),
    SPORTS_OUTDOORS("sports_outdoors", R.string.marketplace_cat_sports_outdoors),
    JOBS_SERVICES("jobs_services", R.string.marketplace_cat_jobs_services),
    GENERAL("general", R.string.marketplace_cat_general);

    companion object {
        fun fromKey(key: String?): MarketplaceCategory {
            return entries.find { it.categoryKey.equals(key, ignoreCase = true) } ?: GENERAL
        }
    }
}

/**
 * Physical condition of the item being sold.
 */
enum class MarketplaceItemCondition(
    val conditionKey: String,
    @StringRes val labelRes: Int
) {
    BRAND_NEW("brand_new", R.string.marketplace_cond_brand_new),
    LIKE_NEW("like_new", R.string.marketplace_cond_like_new),
    USED_GOOD("used_good", R.string.marketplace_cond_used_good),
    USED_FAIR("used_fair", R.string.marketplace_cond_used_fair);

    companion object {
        fun fromKey(key: String?): MarketplaceItemCondition {
            return entries.find { it.conditionKey.equals(key, ignoreCase = true) } ?: USED_GOOD
        }
    }
}

/**
 * Delivery or collection arrangements supported by the seller.
 */
enum class MarketplaceDeliveryOption(
    val optionKey: String,
    @StringRes val labelRes: Int
) {
    LOCAL_PICKUP("pickup", R.string.marketplace_delivery_pickup),
    DOOR_DELIVERY("delivery", R.string.marketplace_delivery_door),
    COURIER_SHIPPING("courier", R.string.marketplace_delivery_courier);

    companion object {
        fun fromKey(key: String?): MarketplaceDeliveryOption? {
            return entries.find { it.optionKey.equals(key, ignoreCase = true) }
        }
    }
}

/**
 * Review outcome and lifecycle status of a listing.
 */
enum class MarketplaceReviewStatus(val statusKey: String) {
    ACTIVE("ACTIVE"),
    PENDING_REVIEW("PENDING_REVIEW"),
    REJECTED("REJECTED"),
    SOLD("SOLD"),
    EXPIRED("EXPIRED");

    companion object {
        fun fromKey(key: String?): MarketplaceReviewStatus {
            return entries.find { it.statusKey.equals(key, ignoreCase = true) } ?: ACTIVE
        }
    }
}

/**
 * Active tab filter on the Marketplace home screen.
 */
enum class MarketplaceScreenTab {
    EXPLORE,
    SAVED,
    MY_LISTINGS
}

/**
 * Sort orders for marketplace catalog.
 */
enum class MarketplaceSortOrder(
    val orderKey: String,
    @StringRes val labelRes: Int
) {
    NEWEST("newest", R.string.marketplace_sort_newest),
    PRICE_LOW_TO_HIGH("price_asc", R.string.marketplace_sort_price_asc),
    PRICE_HIGH_TO_LOW("price_desc", R.string.marketplace_sort_price_desc),
    MOST_VIEWED("most_viewed", R.string.marketplace_sort_popular)
}

/**
 * Progression states during listing creation and review:
 * Uploading, Uploaded, Under review, Published.
 */
enum class MarketplaceUploadProgress {
    IDLE,
    UPLOADING,
    UPLOADED,
    UNDER_REVIEW,
    PUBLISHED,
    FAILED
}

/**
 * Result of layered moderation (deterministic + Gemini AI).
 */
data class MarketplaceModerationResult(
    val isApproved: Boolean,
    val policyViolations: List<String> = emptyList(),
    val riskScore: Double = 0.0,
    val requiresSuspension: Boolean = false,
    val suspensionReason: String = ""
)

/**
 * Full domain representation of a product listing.
 */
data class MarketplaceProduct(
    val id: String,
    val sellerUserId: String,
    val sellerFullName: String,
    val sellerUsername: String,
    val sellerAvatarUrl: String?,
    val sellerCountry: String,
    val sellerCountryIso: String,
    val sellerLocation: String,
    val sellerResponseRate: String,
    val sellerVerified: Boolean,
    val title: String,
    val description: String,
    val price: Double,
    val currency: String,
    val category: MarketplaceCategory,
    val condition: MarketplaceItemCondition,
    val photoPaths: List<String>,
    val specs: Map<String, String>,
    val deliveryOptions: Set<MarketplaceDeliveryOption>,
    val showExactLocation: Boolean,
    val reviewStatus: MarketplaceReviewStatus,
    val riskScore: Double,
    val isSold: Boolean,
    val isExpired: Boolean,
    val expiresAtMs: Long,
    val createdAtMs: Long,
    val updatedAtMs: Long,
    val viewCount: Int,
    val favoriteCount: Int,
    val isFavoritedByMe: Boolean = false
) {
    val isExpiredOrNear: Boolean
        get() = isExpired || (expiresAtMs > 0 && expiresAtMs <= System.currentTimeMillis())

    fun toSnapshot(): MarketplaceProductSnapshot = MarketplaceProductSnapshot(
        productId = id,
        title = title,
        price = price,
        currency = currency,
        thumbnailPath = photoPaths.firstOrNull(),
        sellerLocation = sellerLocation
    )
}

object MarketplaceJsonHelpers {
    fun parseStringList(json: String?): List<String> {
        if (json.isNullOrBlank()) return emptyList()
        return runCatching {
            val array = org.json.JSONArray(json)
            val list = mutableListOf<String>()
            for (i in 0 until array.length()) {
                list.add(array.getString(i))
            }
            list
        }.getOrDefault(emptyList())
    }

    fun serializeStringList(list: List<String>): String {
        val array = org.json.JSONArray()
        list.forEach { array.put(it) }
        return array.toString()
    }

    fun parseStringMap(json: String?): Map<String, String> {
        if (json.isNullOrBlank()) return emptyMap()
        return runCatching {
            val obj = org.json.JSONObject(json)
            val map = mutableMapOf<String, String>()
            val keys = obj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                map[key] = obj.optString(key, "")
            }
            map
        }.getOrDefault(emptyMap())
    }

    fun serializeStringMap(map: Map<String, String>): String {
        val obj = org.json.JSONObject()
        map.forEach { (k, v) -> obj.put(k, v) }
        return obj.toString()
    }

    fun parseDeliveryOptions(json: String?): Set<MarketplaceDeliveryOption> {
        val keys = parseStringList(json)
        return keys.mapNotNull { MarketplaceDeliveryOption.fromKey(it) }.toSet()
    }

    fun serializeDeliveryOptions(options: Set<MarketplaceDeliveryOption>): String {
        return serializeStringList(options.map { it.optionKey })
    }

    fun parseProductSnapshot(tokenOrUrl: String): MarketplaceProductSnapshot? {
        val tokenPrefix = "[telefam_marketplace:"
        val tokenSuffix = "]"
        val raw = if (tokenOrUrl.contains(tokenPrefix)) {
            tokenOrUrl.substringAfter(tokenPrefix).substringBefore(tokenSuffix)
        } else null

        if (raw != null) {
            val parts = raw.split('|')
            if (parts.size >= 4) {
                return MarketplaceProductSnapshot(
                    productId = parts[0],
                    title = parts[1],
                    price = parts[2].toDoubleOrNull() ?: 0.0,
                    currency = parts[3],
                    thumbnailPath = parts.getOrNull(4)?.takeIf { it.isNotBlank() },
                    sellerLocation = parts.getOrNull(5) ?: ""
                )
            }
        }

        // Deep-link URL fallback e.g. telefam.company/marketplace/item/{id}
        val urlPattern = Regex("""marketplace/item/([a-zA-Z0-9_\-]+)""")
        val match = urlPattern.find(tokenOrUrl)
        if (match != null) {
            val id = match.groupValues[1]
            return MarketplaceProductSnapshot(
                productId = id,
                title = "Marketplace Listing",
                price = 0.0,
                currency = "",
                thumbnailPath = null,
                sellerLocation = ""
            )
        }
        return null
    }

    fun serializeProductSnapshotToken(snapshot: MarketplaceProductSnapshot): String {
        return "[telefam_marketplace:${snapshot.productId}|${snapshot.title}|${snapshot.price}|${snapshot.currency}|${snapshot.thumbnailPath.orEmpty()}|${snapshot.sellerLocation}]"
    }

    fun stripProductSnapshotToken(text: String): String {
        val regex = Regex("""\[telefam_marketplace:[^\]]+\]\s*""")
        return text.replace(regex, "").trim()
    }
}

/**
 * World Currency model representing standard ISO 4217 currency.
 */
data class MarketplaceCurrency(
    val code: String,
    val name: String,
    val symbol: String
)

/**
 * Lightweight product snapshot payload for chat attachments and interactive previews.
 */
data class MarketplaceProductSnapshot(
    val productId: String,
    val title: String,
    val price: Double,
    val currency: String,
    val thumbnailPath: String?,
    val sellerLocation: String
)

/**
 * Comprehensive registry of all world currencies (ISO 4217 standard).
 */
object MarketplaceCurrencies {

    val ALL_CURRENCIES: List<MarketplaceCurrency> = listOf(
        // Africa
        MarketplaceCurrency("KES", "Kenyan Shilling", "KSh"),
        MarketplaceCurrency("TZS", "Tanzanian Shilling", "TSh"),
        MarketplaceCurrency("UGX", "Ugandan Shilling", "USh"),
        MarketplaceCurrency("RWF", "Rwandan Franc", "FRw"),
        MarketplaceCurrency("ETB", "Ethiopian Birr", "Br"),
        MarketplaceCurrency("NGN", "Nigerian Naira", "₦"),
        MarketplaceCurrency("GHS", "Ghanaian Cedi", "GH₵"),
        MarketplaceCurrency("ZAR", "South African Rand", "R"),
        MarketplaceCurrency("EGP", "Egyptian Pound", "E£"),
        MarketplaceCurrency("MAD", "Moroccan Dirham", "DH"),
        MarketplaceCurrency("DZD", "Algerian Dinar", "DA"),
        MarketplaceCurrency("XOF", "West African CFA Franc", "CFA"),
        MarketplaceCurrency("XAF", "Central African CFA Franc", "FCFA"),
        MarketplaceCurrency("BWP", "Botswana Pula", "P"),
        MarketplaceCurrency("MWK", "Malawian Kwacha", "MK"),
        MarketplaceCurrency("ZMW", "Zambian Kwacha", "ZK"),
        MarketplaceCurrency("MZN", "Mozambican Metical", "MT"),
        MarketplaceCurrency("AOA", "Angolan Kwanza", "Kz"),
        MarketplaceCurrency("MUR", "Mauritian Rupee", "₨"),
        MarketplaceCurrency("SCR", "Seychellois Rupee", "SR"),
        MarketplaceCurrency("SOS", "Somali Shilling", "S"),
        MarketplaceCurrency("DJF", "Djiboutian Franc", "Fdj"),
        MarketplaceCurrency("KMF", "Comorian Franc", "CF"),
        MarketplaceCurrency("BIF", "Burundian Franc", "FBu"),
        MarketplaceCurrency("ERN", "Eritrean Nakfa", "Nfk"),
        MarketplaceCurrency("LSL", "Lesotho Loti", "L"),
        MarketplaceCurrency("NAD", "Namibian Dollar", "N$"),
        MarketplaceCurrency("SZL", "Swazi Lilangeni", "L"),
        MarketplaceCurrency("SSP", "South Sudanese Pound", "£"),
        MarketplaceCurrency("SDG", "Sudanese Pound", "SD"),
        MarketplaceCurrency("LYD", "Libyan Dinar", "LD"),
        MarketplaceCurrency("TND", "Tunisian Dinar", "DT"),
        MarketplaceCurrency("GMD", "Gambian Dalasi", "D"),
        MarketplaceCurrency("GNF", "Guinean Franc", "FG"),
        MarketplaceCurrency("LRD", "Liberian Dollar", "L$"),
        MarketplaceCurrency("SLE", "Sierra Leonean Leone", "Le"),
        MarketplaceCurrency("CVE", "Cape Verdean Escudo", "Esc"),
        MarketplaceCurrency("STN", "São Tomé Dobra", "Db"),
        MarketplaceCurrency("MGA", "Malagasy Ariary", "Ar"),

        // Americas
        MarketplaceCurrency("USD", "United States Dollar", "$"),
        MarketplaceCurrency("CAD", "Canadian Dollar", "CA$"),
        MarketplaceCurrency("MXN", "Mexican Peso", "Mex$"),
        MarketplaceCurrency("BRL", "Brazilian Real", "R$"),
        MarketplaceCurrency("ARS", "Argentine Peso", "$"),
        MarketplaceCurrency("CLP", "Chilean Peso", "$"),
        MarketplaceCurrency("COP", "Colombian Peso", "Col$"),
        MarketplaceCurrency("PEN", "Peruvian Sol", "S/"),
        MarketplaceCurrency("UYU", "Uruguayan Peso", "\$U"),
        MarketplaceCurrency("PYG", "Paraguayan Guaraní", "₲"),
        MarketplaceCurrency("BOB", "Bolivian Boliviano", "Bs"),
        MarketplaceCurrency("VES", "Venezuelan Bolívar", "Bs"),
        MarketplaceCurrency("CRC", "Costa Rican Colón", "₡"),
        MarketplaceCurrency("DOP", "Dominican Peso", "RD$"),
        MarketplaceCurrency("GTQ", "Guatemalan Quetzal", "Q"),
        MarketplaceCurrency("HNL", "Honduran Lempira", "L"),
        MarketplaceCurrency("NIO", "Nicaraguan Córdoba", "C$"),
        MarketplaceCurrency("PAB", "Panamanian Balboa", "B/."),
        MarketplaceCurrency("BSD", "Bahamian Dollar", "B$"),
        MarketplaceCurrency("BBD", "Barbadian Dollar", "Bds$"),
        MarketplaceCurrency("BZD", "Belize Dollar", "BZ$"),
        MarketplaceCurrency("JMD", "Jamaican Dollar", "J$"),
        MarketplaceCurrency("TTD", "Trinidad and Tobago Dollar", "TT$"),
        MarketplaceCurrency("XCD", "East Caribbean Dollar", "EC$"),
        MarketplaceCurrency("HTG", "Haitian Gourde", "G"),
        MarketplaceCurrency("GYD", "Guyanese Dollar", "G$"),
        MarketplaceCurrency("SRD", "Surinamese Dollar", "Sr$"),
        MarketplaceCurrency("AWG", "Aruban Florin", "Afl"),
        MarketplaceCurrency("ANG", "Netherlands Antillean Guilder", "NAƒ"),
        MarketplaceCurrency("KYD", "Cayman Islands Dollar", "CI$"),
        MarketplaceCurrency("BMD", "Bermudian Dollar", "BD$"),

        // Europe
        MarketplaceCurrency("EUR", "Euro", "€"),
        MarketplaceCurrency("GBP", "British Pound Sterling", "£"),
        MarketplaceCurrency("CHF", "Swiss Franc", "CHF"),
        MarketplaceCurrency("SEK", "Swedish Krona", "kr"),
        MarketplaceCurrency("NOK", "Norwegian Krone", "kr"),
        MarketplaceCurrency("DKK", "Danish Krone", "kr"),
        MarketplaceCurrency("PLN", "Polish Złoty", "zł"),
        MarketplaceCurrency("CZK", "Czech Koruna", "Kč"),
        MarketplaceCurrency("HUF", "Hungarian Forint", "Ft"),
        MarketplaceCurrency("RON", "Romanian Leu", "lei"),
        MarketplaceCurrency("BGN", "Bulgarian Lev", "лв"),
        MarketplaceCurrency("ISK", "Icelandic Króna", "kr"),
        MarketplaceCurrency("RSD", "Serbian Dinar", "дин"),
        MarketplaceCurrency("BAM", "Bosnia Convertible Mark", "KM"),
        MarketplaceCurrency("ALL", "Albanian Lek", "L"),
        MarketplaceCurrency("MKD", "Macedonian Denar", "ден"),
        MarketplaceCurrency("GEL", "Georgian Lari", "₾"),
        MarketplaceCurrency("AMD", "Armenian Dram", "֏"),
        MarketplaceCurrency("AZN", "Azerbaijani Manat", "₼"),
        MarketplaceCurrency("BYN", "Belarusian Ruble", "Br"),
        MarketplaceCurrency("MDL", "Moldovan Leu", "L"),
        MarketplaceCurrency("UAH", "Ukrainian Hryvnia", "₴"),
        MarketplaceCurrency("TRY", "Turkish Lira", "₺"),
        MarketplaceCurrency("RUB", "Russian Ruble", "₽"),

        // Middle East
        MarketplaceCurrency("AED", "United Arab Emirates Dirham", "AED"),
        MarketplaceCurrency("SAR", "Saudi Riyal", "SAR"),
        MarketplaceCurrency("QAR", "Qatari Riyal", "QR"),
        MarketplaceCurrency("KWD", "Kuwaiti Dinar", "KD"),
        MarketplaceCurrency("BHD", "Bahraini Dinar", "BD"),
        MarketplaceCurrency("OMR", "Omani Rial", "OMR"),
        MarketplaceCurrency("ILS", "Israeli New Shekel", "₪"),
        MarketplaceCurrency("JOD", "Jordanian Dinar", "JD"),
        MarketplaceCurrency("LBP", "Lebanese Pound", "L£"),
        MarketplaceCurrency("IQD", "Iraqi Dinar", "ID"),
        MarketplaceCurrency("IRR", "Iranian Rial", "IR"),
        MarketplaceCurrency("SYP", "Syrian Pound", "S£"),
        MarketplaceCurrency("YER", "Yemeni Rial", "YR"),

        // Asia & Oceania
        MarketplaceCurrency("JPY", "Japanese Yen", "¥"),
        MarketplaceCurrency("CNY", "Chinese Yuan", "¥"),
        MarketplaceCurrency("INR", "Indian Rupee", "₹"),
        MarketplaceCurrency("KRW", "South Korean Won", "₩"),
        MarketplaceCurrency("SGD", "Singapore Dollar", "S$"),
        MarketplaceCurrency("HKD", "Hong Kong Dollar", "HK$"),
        MarketplaceCurrency("AUD", "Australian Dollar", "A$"),
        MarketplaceCurrency("NZD", "New Zealand Dollar", "NZ$"),
        MarketplaceCurrency("TWD", "New Taiwan Dollar", "NT$"),
        MarketplaceCurrency("THB", "Thai Baht", "฿"),
        MarketplaceCurrency("IDR", "Indonesian Rupiah", "Rp"),
        MarketplaceCurrency("MYR", "Malaysian Ringgit", "RM"),
        MarketplaceCurrency("PHP", "Philippine Peso", "₱"),
        MarketplaceCurrency("VND", "Vietnamese Dong", "₫"),
        MarketplaceCurrency("PKR", "Pakistani Rupee", "₨"),
        MarketplaceCurrency("BDT", "Bangladeshi Taka", "৳"),
        MarketplaceCurrency("LKR", "Sri Lankan Rupee", "Rs"),
        MarketplaceCurrency("NPR", "Nepalese Rupee", "Rs"),
        MarketplaceCurrency("MMK", "Myanmar Kyat", "K"),
        MarketplaceCurrency("KHR", "Cambodian Riel", "៛"),
        MarketplaceCurrency("LAK", "Lao Kip", "₭"),
        MarketplaceCurrency("MNT", "Mongolian Tögrög", "₮"),
        MarketplaceCurrency("BND", "Brunei Dollar", "B$"),
        MarketplaceCurrency("MVR", "Maldivian Rufiyaa", "Rf"),
        MarketplaceCurrency("BTN", "Bhutanese Ngultrum", "Nu"),
        MarketplaceCurrency("AFN", "Afghan Afghani", "؋"),
        MarketplaceCurrency("KZT", "Kazakhstani Tenge", "₸"),
        MarketplaceCurrency("UZS", "Uzbekistani Som", "so'm"),
        MarketplaceCurrency("KGS", "Kyrgyzstani Som", "som"),
        MarketplaceCurrency("TJS", "Tajikistani Somoni", "SM"),
        MarketplaceCurrency("TMT", "Turkmenistan Manat", "T"),
        MarketplaceCurrency("FJD", "Fijian Dollar", "FJ$"),
        MarketplaceCurrency("PGK", "Papua New Guinean Kina", "K"),
        MarketplaceCurrency("SBD", "Solomon Islands Dollar", "SI$"),
        MarketplaceCurrency("VUV", "Vanuatu Vatu", "VT"),
        MarketplaceCurrency("WST", "Samoan Tala", "WS$"),
        MarketplaceCurrency("TOP", "Tongan Paʻanga", "T$")
    )

    fun findByCode(code: String): MarketplaceCurrency {
        val upper = code.trim().uppercase()
        return ALL_CURRENCIES.find { it.code == upper }
            ?: MarketplaceCurrency(upper, upper, upper)
    }

    fun getDefaultCurrencyForCountry(countryIso: String): String {
        return when (countryIso.trim().uppercase()) {
            "KE" -> "KES"
            "TZ" -> "TZS"
            "UG" -> "UGX"
            "RW" -> "RWF"
            "ET" -> "ETB"
            "NG" -> "NGN"
            "GH" -> "GHS"
            "ZA" -> "ZAR"
            "EG" -> "EGP"
            "MA" -> "MAD"
            "DZ" -> "DZD"
            "US" -> "USD"
            "GB", "UK" -> "GBP"
            "CA" -> "CAD"
            "AU" -> "AUD"
            "NZ" -> "NZD"
            "DE", "FR", "IT", "ES", "NL", "BE", "AT", "PT", "IE", "FI", "GR" -> "EUR"
            "CH" -> "CHF"
            "SE" -> "SEK"
            "NO" -> "NOK"
            "DK" -> "DKK"
            "PL" -> "PLN"
            "IN" -> "INR"
            "CN" -> "CNY"
            "JP" -> "JPY"
            "KR" -> "KRW"
            "SG" -> "SGD"
            "HK" -> "HKD"
            "AE" -> "AED"
            "SA" -> "SAR"
            "QA" -> "QAR"
            "BR" -> "BRL"
            "MX" -> "MXN"
            "AR" -> "ARS"
            "CO" -> "COP"
            "CL" -> "CLP"
            "TR" -> "TRY"
            "RU" -> "RUB"
            else -> "USD"
        }
    }
}

