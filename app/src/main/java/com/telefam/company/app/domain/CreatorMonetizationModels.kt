package com.telefam.company.app.domain

enum class CreatorMonetizationStatus {
    NOT_APPLIED,
    SUBMITTED,
    APPROVED,
    NOT_APPROVED;

    companion object {
        fun fromWire(wire: String?): CreatorMonetizationStatus {
            return when (wire?.uppercase()) {
                "SUBMITTED" -> SUBMITTED
                "APPROVED" -> APPROVED
                "NOT_APPROVED" -> NOT_APPROVED
                else -> NOT_APPLIED
            }
        }
    }
}

data class CreatorQualificationSnapshot(
    val followersCount: Long = 0L,
    val targetFollowers: Long = 1000L,
    val watchTimeMinutes: Long = 0L,
    val targetWatchTimeMinutes: Long = 4000L,
    val publicVideosCount: Long = 0L,
    val targetPublicVideosCount: Long = 10L,
    val communityGuidelinesMet: Boolean = true,
    val accountStatusMet: Boolean = true,
    val originalContentMet: Boolean = true,
    val isEngagementMet: Boolean = false
) {
    val isFollowersMet: Boolean get() = followersCount >= targetFollowers
    val isWatchTimeMet: Boolean get() = watchTimeMinutes >= targetWatchTimeMinutes
    val isContentMet: Boolean get() = publicVideosCount >= targetPublicVideosCount
    val isAllMandatoryMet: Boolean get() = isFollowersMet && isWatchTimeMet && isContentMet && communityGuidelinesMet && accountStatusMet
}

data class WhyNotApprovedItem(
    val id: String,
    val title: String,
    val description: String,
    val iconType: String, // "ELIGIBILITY", "GUIDELINES", "HISTORY"
    val detailedAdvice: String
)

data class ApprovedMonetizationFeature(
    val id: String,
    val title: String,
    val isEnabled: Boolean = true,
    val accentHex: String
)

data class VideoMonetizationItem(
    val videoId: String,
    val title: String,
    val coverUrl: String? = null,
    val isMonetized: Boolean = true,
    val instreamAdsEnabled: Boolean = true,
    val videoGiftsEnabled: Boolean = true,
    val creatorRewardsEligible: Boolean = true,
    val monetizedViews: Long = 0L,
    val adImpressions: Long = 0L,
    val adEarningsUsd: Double = 0.0,
    val rewardEarningsUsd: Double = 0.0,
    val giftEarningsUsd: Double = 0.0,
    val rpmUsd: Double = 2.45
) {
    val totalEarningsUsd: Double get() = adEarningsUsd + rewardEarningsUsd + giftEarningsUsd
}
