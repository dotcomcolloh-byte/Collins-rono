package com.telefam.company.app.domain

import androidx.compose.runtime.Immutable

enum class CampaignScreenTab {
    CREATE,
    MANAGE
}

/**
 * 5 Core Objectives / Goals for Creator & Business Campaigns.
 */
enum class CampaignGoal(
    val id: String,
    val title: String,
    val shortDesc: String,
    val defaultCta: String
) {
    FOLLOWERS(
        id = "FOLLOWERS",
        title = "Gain Followers",
        shortDesc = "Grow your fan base and profile visits",
        defaultCta = "Follow"
    ),
    VIDEO_VIEWS(
        id = "VIDEO_VIEWS",
        title = "More Video Views",
        shortDesc = "Maximize impressions and engagement",
        defaultCta = "Watch More"
    ),
    WEBSITE_STORE(
        id = "WEBSITE_STORE",
        title = "Website & Store",
        shortDesc = "Direct buyers to your store or landing page",
        defaultCta = "Shop Now"
    ),
    DIRECT_MESSAGES(
        id = "DIRECT_MESSAGES",
        title = "Direct Messages",
        shortDesc = "Receive high-intent inquiries in your inbox",
        defaultCta = "Send Message"
    ),
    FORM_APPLICATION(
        id = "FORM_APPLICATION",
        title = "Form Submission",
        shortDesc = "Collect leads and applications directly in-app",
        defaultCta = "Apply Now"
    );

    companion object {
        fun fromId(id: String): CampaignGoal =
            entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: FOLLOWERS
    }
}

/**
 * 5-Tier Pricing Specification per Goal.
 * Daily prices are calculated per day per post; total budget = dailyPrice * durationDays.
 */
@Immutable
data class CampaignPricingTier(
    val tierId: String,
    val name: String,
    val minReach: Int,
    val maxReach: Int,
    val dailyPriceKES: Double,
    val dailyPriceUSD: Double,
    val isPopular: Boolean = false
) {
    fun getDailyPrice(currency: String): Double {
        return when (currency.uppercase()) {
            "KES" -> dailyPriceKES
            "USD" -> dailyPriceUSD
            "NGN" -> (dailyPriceUSD * 1550.0).coerceAtLeast(400.0)
            "GHS" -> (dailyPriceUSD * 15.5).coerceAtLeast(4.0)
            "ZAR" -> (dailyPriceUSD * 18.5).coerceAtLeast(5.0)
            "EUR" -> (dailyPriceUSD * 0.92)
            "GBP" -> (dailyPriceUSD * 0.78)
            else -> dailyPriceUSD
        }
    }

    fun formatReachRange(): String = "%,d – %,d".format(minReach, maxReach)
}

/**
 * Verified Catalog of 5 Tiers for each campaign goal with exact pricing matching user requirements.
 */
object CampaignPricingCatalog {

    val FOLLOWER_TIERS = listOf(
        CampaignPricingTier(
            tierId = "tier_followers_1",
            name = "Starter Reach",
            minReach = 500,
            maxReach = 1000,
            dailyPriceKES = 35.0,
            dailyPriceUSD = 0.27
        ),
        CampaignPricingTier(
            tierId = "tier_followers_2",
            name = "Growth Tier",
            minReach = 1500,
            maxReach = 2500,
            dailyPriceKES = 70.0, // Exact user requirement: 1500-2500 = kes70
            dailyPriceUSD = 0.54,
            isPopular = true
        ),
        CampaignPricingTier(
            tierId = "tier_followers_3",
            name = "Pro Expansion",
            minReach = 3000,
            maxReach = 5000,
            dailyPriceKES = 140.0,
            dailyPriceUSD = 1.08
        ),
        CampaignPricingTier(
            tierId = "tier_followers_4",
            name = "Viral Surge",
            minReach = 7500,
            maxReach = 12000,
            dailyPriceKES = 320.0,
            dailyPriceUSD = 2.46
        ),
        CampaignPricingTier(
            tierId = "tier_followers_5",
            name = "Maximum Scale",
            minReach = 15000,
            maxReach = 25000,
            dailyPriceKES = 650.0,
            dailyPriceUSD = 5.00
        )
    )

    val VIEW_TIERS = listOf(
        CampaignPricingTier(
            tierId = "tier_views_1",
            name = "Starter Views",
            minReach = 500,
            maxReach = 1000,
            dailyPriceKES = 38.0,
            dailyPriceUSD = 0.29
        ),
        CampaignPricingTier(
            tierId = "tier_views_2",
            name = "Buzz Creator",
            minReach = 1500,
            maxReach = 2500,
            dailyPriceKES = 75.0, // Exact user requirement: 1500-2500 = kes75
            dailyPriceUSD = 0.58,
            isPopular = true
        ),
        CampaignPricingTier(
            tierId = "tier_views_3",
            name = "Trending Push",
            minReach = 3500,
            maxReach = 6000,
            dailyPriceKES = 160.0,
            dailyPriceUSD = 1.23
        ),
        CampaignPricingTier(
            tierId = "tier_views_4",
            name = "High Velocity",
            minReach = 8000,
            maxReach = 15000,
            dailyPriceKES = 350.0,
            dailyPriceUSD = 2.69
        ),
        CampaignPricingTier(
            tierId = "tier_views_5",
            name = "Viral Takeover",
            minReach = 20000,
            maxReach = 35000,
            dailyPriceKES = 780.0,
            dailyPriceUSD = 6.00
        )
    )

    val WEBSITE_STORE_TIERS = listOf(
        CampaignPricingTier(
            tierId = "tier_store_1",
            name = "Store Kickoff",
            minReach = 500,
            maxReach = 1000,
            dailyPriceKES = 75.0,
            dailyPriceUSD = 0.58
        ),
        CampaignPricingTier(
            tierId = "tier_store_2",
            name = "Traffic Booster",
            minReach = 1500,
            maxReach = 2500,
            dailyPriceKES = 150.0, // Exact user requirement: 1500-2500 reach 150
            dailyPriceUSD = 1.15,
            isPopular = true
        ),
        CampaignPricingTier(
            tierId = "tier_store_3",
            name = "Conversion Pro",
            minReach = 3500,
            maxReach = 5500,
            dailyPriceKES = 310.0,
            dailyPriceUSD = 2.38
        ),
        CampaignPricingTier(
            tierId = "tier_store_4",
            name = "Sales Magnet",
            minReach = 7000,
            maxReach = 12000,
            dailyPriceKES = 650.0,
            dailyPriceUSD = 5.00
        ),
        CampaignPricingTier(
            tierId = "tier_store_5",
            name = "Commerce Scale",
            minReach = 15000,
            maxReach = 25000,
            dailyPriceKES = 1300.0,
            dailyPriceUSD = 10.00
        )
    )

    // Exact user requirement: "direct message the same also"
    val DIRECT_MESSAGE_TIERS = listOf(
        CampaignPricingTier(
            tierId = "tier_dm_1",
            name = "Starter Inquiries",
            minReach = 500,
            maxReach = 1000,
            dailyPriceKES = 75.0,
            dailyPriceUSD = 0.58
        ),
        CampaignPricingTier(
            tierId = "tier_dm_2",
            name = "Client Connect",
            minReach = 1500,
            maxReach = 2500,
            dailyPriceKES = 150.0,
            dailyPriceUSD = 1.15,
            isPopular = true
        ),
        CampaignPricingTier(
            tierId = "tier_dm_3",
            name = "Deal Closer",
            minReach = 3500,
            maxReach = 5500,
            dailyPriceKES = 310.0,
            dailyPriceUSD = 2.38
        ),
        CampaignPricingTier(
            tierId = "tier_dm_4",
            name = "Lead Wave",
            minReach = 7000,
            maxReach = 12000,
            dailyPriceKES = 650.0,
            dailyPriceUSD = 5.00
        ),
        CampaignPricingTier(
            tierId = "tier_dm_5",
            name = "Inbox Surge",
            minReach = 15000,
            maxReach = 25000,
            dailyPriceKES = 1300.0,
            dailyPriceUSD = 10.00
        )
    )

    val FORM_APPLICATION_TIERS = listOf(
        CampaignPricingTier(
            tierId = "tier_leads_1",
            name = "Application Starter",
            minReach = 500,
            maxReach = 1000,
            dailyPriceKES = 80.0,
            dailyPriceUSD = 0.62
        ),
        CampaignPricingTier(
            tierId = "tier_leads_2",
            name = "Qualified Inflow",
            minReach = 1500,
            maxReach = 2500,
            dailyPriceKES = 160.0,
            dailyPriceUSD = 1.23,
            isPopular = true
        ),
        CampaignPricingTier(
            tierId = "tier_leads_3",
            name = "Recruitment & Sales",
            minReach = 3500,
            maxReach = 5500,
            dailyPriceKES = 330.0,
            dailyPriceUSD = 2.54
        ),
        CampaignPricingTier(
            tierId = "tier_leads_4",
            name = "High Volume Intake",
            minReach = 7000,
            maxReach = 12000,
            dailyPriceKES = 700.0,
            dailyPriceUSD = 5.38
        ),
        CampaignPricingTier(
            tierId = "tier_leads_5",
            name = "Enterprise Acquisition",
            minReach = 15000,
            maxReach = 25000,
            dailyPriceKES = 1450.0,
            dailyPriceUSD = 11.15
        )
    )

    fun getTiersForGoal(goal: CampaignGoal): List<CampaignPricingTier> {
        return when (goal) {
            CampaignGoal.FOLLOWERS -> FOLLOWER_TIERS
            CampaignGoal.VIDEO_VIEWS -> VIEW_TIERS
            CampaignGoal.WEBSITE_STORE -> WEBSITE_STORE_TIERS
            CampaignGoal.DIRECT_MESSAGES -> DIRECT_MESSAGE_TIERS
            CampaignGoal.FORM_APPLICATION -> FORM_APPLICATION_TIERS
        }
    }
}

/**
 * Available duration options (in days).
 */
val CAMPAIGN_DURATION_OPTIONS = listOf(1, 2, 3, 5, 7, 14, 30)

/**
 * Standard Call-To-Action (CTA) Button Options.
 */
val CAMPAIGN_CTA_BUTTONS = listOf(
    "Shop Now",
    "Learn More",
    "Apply Now",
    "Send Message",
    "Contact Us",
    "Sign Up",
    "Visit Profile",
    "Book Now",
    "Watch More"
)

/**
 * Audience Interest Categories for Targeting.
 */
val CAMPAIGN_INTEREST_CATEGORIES = listOf(
    "Comedy & Entertainment",
    "Tech & Innovation",
    "Business & Finance",
    "Fashion & Style",
    "Music & Dance",
    "Fitness & Wellness",
    "Food & Cooking",
    "Gaming & Esports",
    "Education & Career",
    "Travel & Adventure"
)

/**
 * Payment Gateways based on geographic availability.
 */
enum class CampaignPaymentGateway(val id: String, val displayName: String) {
    PAYSTACK(id = "PAYSTACK", displayName = "Paystack (M-Pesa, Cards, Bank)"),
    PAYPAL(id = "PAYPAL", displayName = "PayPal (Global Cards & PayPal)")
}

/**
 * Determines Payment Gateway and Currency based on the country ISO code.
 * Paystack supported countries: Kenya (KE), Nigeria (NG), Ghana (GH), South Africa (ZA).
 * Other countries default to PayPal and USD.
 */
object CampaignBillingResolver {
    private val PAYSTACK_SUPPORTED_ISOS = setOf("KE", "NG", "GH", "ZA")

    fun resolveGateway(countryIsoCode: String?): CampaignPaymentGateway {
        val cleanIso = countryIsoCode?.trim()?.uppercase() ?: ""
        return if (cleanIso in PAYSTACK_SUPPORTED_ISOS) {
            CampaignPaymentGateway.PAYSTACK
        } else {
            CampaignPaymentGateway.PAYPAL
        }
    }

    fun resolveCurrency(countryIsoCode: String?): String {
        return when (countryIsoCode?.trim()?.uppercase()) {
            "KE" -> "KES"
            "NG" -> "NGN"
            "GH" -> "GHS"
            "ZA" -> "ZAR"
            "GB", "UK" -> "GBP"
            "DE", "FR", "ES", "IT" -> "EUR"
            else -> "USD" // Fallback to USD if unsure
        }
    }

    fun formatPrice(amount: Double, currency: String): String {
        return when (currency.uppercase()) {
            "KES" -> "KES %,.0f".format(amount)
            "NGN" -> "NGN %,.0f".format(amount)
            "GHS" -> "GH₵ %,.2f".format(amount)
            "ZAR" -> "R %,.2f".format(amount)
            "EUR" -> "€%,.2f".format(amount)
            "GBP" -> "£%,.2f".format(amount)
            else -> "$%,.2f".format(amount)
        }
    }
}
