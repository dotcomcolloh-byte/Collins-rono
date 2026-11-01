package com.telefam.company.app.domain

enum class VirtualGiftCategory(val id: String, val title: String) {
    FOR_YOU("for_you", "For You"),
    TRENDING("trending", "Trending"),
    LOVE("love", "Love"),
    FUN("fun", "Fun"),
    LUXURY("luxury", "Luxury"),
    SUPPORT("support", "Support")
}

data class VirtualGiftItem(
    val id: String,
    val name: String,
    val coinCost: Long,
    val category: VirtualGiftCategory,
    val description: String = "",
    val accentHex: String = "#FFD700"
)

object VirtualGiftCatalog {

    // 20 gifts under Trending matching Image 1 exactly
    val trendingGifts = listOf(
        VirtualGiftItem("galaxy", "Galaxy", 30L, VirtualGiftCategory.TRENDING, "Cosmic swirl of stars", "#7B2CBF"),
        VirtualGiftItem("rocket", "Rocket", 50L, VirtualGiftCategory.TRENDING, "Blast off to the moon", "#FF5400"),
        VirtualGiftItem("music_vibes", "Music Vibes", 70L, VirtualGiftCategory.TRENDING, "Feel the sound waves", "#4361EE"),
        VirtualGiftItem("butterfly", "Butterfly", 80L, VirtualGiftCategory.TRENDING, "Fluttering purple wings", "#9D4EDD"),
        VirtualGiftItem("fireworks", "Fireworks", 100L, VirtualGiftCategory.TRENDING, "Sparkling night celebration", "#FF007F"),
        VirtualGiftItem("golden_lion", "Golden Lion", 200L, VirtualGiftCategory.TRENDING, "Regal king of the savanna", "#E5A910"),
        VirtualGiftItem("supercar", "Supercar", 500L, VirtualGiftCategory.TRENDING, "Neon hyper-speed thrill", "#9B5DE5"),
        VirtualGiftItem("treasure_chest", "Treasure Chest", 600L, VirtualGiftCategory.TRENDING, "Loaded with gold coins", "#D4AF37"),
        VirtualGiftItem("hot_air_balloon", "Hot Air Balloon", 80L, VirtualGiftCategory.TRENDING, "Colorful aerial voyage", "#F72585"),
        VirtualGiftItem("panda", "Panda", 60L, VirtualGiftCategory.TRENDING, "Cute bamboo nibbler", "#2EC4B6"),
        VirtualGiftItem("castle", "Castle", 300L, VirtualGiftCategory.TRENDING, "Enchanted royal towers", "#4895EF"),
        VirtualGiftItem("crystal_heart", "Crystal Heart", 150L, VirtualGiftCategory.TRENDING, "Shimmering faceted gemstone", "#FF4D6D"),
        VirtualGiftItem("magic_wand", "Magic Wand", 40L, VirtualGiftCategory.TRENDING, "Starlight sorcery burst", "#B5179E"),
        VirtualGiftItem("rose_box", "Rose Box", 120L, VirtualGiftCategory.TRENDING, "Velvet box of crimson roses", "#E63946"),
        VirtualGiftItem("crown", "Crown", 250L, VirtualGiftCategory.TRENDING, "Golden imperial majesty", "#FFB703"),
        VirtualGiftItem("eagle", "Eagle", 180L, VirtualGiftCategory.TRENDING, "Soaring golden predator", "#FB8500"),
        VirtualGiftItem("diamond_ring", "Diamond Ring", 300L, VirtualGiftCategory.TRENDING, "Sparkling platinum promise", "#00B4D8"),
        VirtualGiftItem("yacht", "Yacht", 1000L, VirtualGiftCategory.TRENDING, "Luxury ocean liner", "#0077B6"),
        VirtualGiftItem("private_jet", "Private Jet", 5000L, VirtualGiftCategory.TRENDING, "First-class VIP skies", "#023E8A"),
        VirtualGiftItem("golden_trophy", "Golden Trophy", 400L, VirtualGiftCategory.TRENDING, "Champion golden cup", "#F4A261")
    )

    val forYouGifts = listOf(
        VirtualGiftItem("rose", "Rose", 1L, VirtualGiftCategory.FOR_YOU, "A delicate crimson rose", "#E63946"),
        VirtualGiftItem("ice_cream", "Ice Cream", 5L, VirtualGiftCategory.FOR_YOU, "Sweet dessert treat", "#FFB4A2"),
        VirtualGiftItem("sparkles", "Sparkles", 10L, VirtualGiftCategory.FOR_YOU, "Glint of inspiration", "#FFE66D"),
        VirtualGiftItem("coffee", "Coffee", 15L, VirtualGiftCategory.FOR_YOU, "Fresh morning brew", "#6F4E37"),
        VirtualGiftItem("fire", "Fire", 20L, VirtualGiftCategory.FOR_YOU, "Blazing heat energy", "#FF5400"),
        VirtualGiftItem("galaxy", "Galaxy", 30L, VirtualGiftCategory.FOR_YOU, "Cosmic swirl of stars", "#7B2CBF"),
        VirtualGiftItem("magic_wand", "Magic Wand", 40L, VirtualGiftCategory.FOR_YOU, "Starlight sorcery burst", "#B5179E"),
        VirtualGiftItem("rocket", "Rocket", 50L, VirtualGiftCategory.FOR_YOU, "Blast off to the moon", "#FF5400")
    )

    val loveGifts = listOf(
        VirtualGiftItem("crystal_heart", "Crystal Heart", 150L, VirtualGiftCategory.LOVE, "Shimmering faceted gemstone", "#FF4D6D"),
        VirtualGiftItem("rose_box", "Rose Box", 120L, VirtualGiftCategory.LOVE, "Velvet box of crimson roses", "#E63946"),
        VirtualGiftItem("diamond_ring", "Diamond Ring", 300L, VirtualGiftCategory.LOVE, "Sparkling platinum promise", "#00B4D8"),
        VirtualGiftItem("love_letter", "Love Letter", 25L, VirtualGiftCategory.LOVE, "Sealed wax romantic note", "#FF758F"),
        VirtualGiftItem("teddy_bear", "Teddy Bear", 65L, VirtualGiftCategory.LOVE, "Cuddly fluffy companion", "#A37081"),
        VirtualGiftItem("cupid_arrow", "Cupid Arrow", 90L, VirtualGiftCategory.LOVE, "Golden heart pierce", "#C9184A"),
        VirtualGiftItem("heart_balloon", "Heart Balloon", 45L, VirtualGiftCategory.LOVE, "Floating pink helium heart", "#FF4D6D"),
        VirtualGiftItem("kiss_lips", "Kiss Lips", 35L, VirtualGiftCategory.LOVE, "Warm sweet kiss", "#D90429")
    )

    val funGifts = listOf(
        VirtualGiftItem("music_vibes", "Music Vibes", 70L, VirtualGiftCategory.FUN, "Feel the sound waves", "#4361EE"),
        VirtualGiftItem("butterfly", "Butterfly", 80L, VirtualGiftCategory.FUN, "Fluttering purple wings", "#9D4EDD"),
        VirtualGiftItem("hot_air_balloon", "Hot Air Balloon", 80L, VirtualGiftCategory.FUN, "Colorful aerial voyage", "#F72585"),
        VirtualGiftItem("panda", "Panda", 60L, VirtualGiftCategory.FUN, "Cute bamboo nibbler", "#2EC4B6"),
        VirtualGiftItem("party_popper", "Party Popper", 30L, VirtualGiftCategory.FUN, "Confetti celebration blast", "#FFBE0B"),
        VirtualGiftItem("ferris_wheel", "Ferris Wheel", 180L, VirtualGiftCategory.FUN, "Amusement park ride", "#3A86FF"),
        VirtualGiftItem("rollercoaster", "Rollercoaster", 220L, VirtualGiftCategory.FUN, "Adrenaline thrill loop", "#8338EC"),
        VirtualGiftItem("arcade", "Arcade Game", 140L, VirtualGiftCategory.FUN, "Retro 80s arcade cabinet", "#06D6A0")
    )

    val luxuryGifts = listOf(
        VirtualGiftItem("supercar", "Supercar", 500L, VirtualGiftCategory.LUXURY, "Neon hyper-speed thrill", "#9B5DE5"),
        VirtualGiftItem("treasure_chest", "Treasure Chest", 600L, VirtualGiftCategory.LUXURY, "Loaded with gold coins", "#D4AF37"),
        VirtualGiftItem("castle", "Castle", 300L, VirtualGiftCategory.LUXURY, "Enchanted royal towers", "#4895EF"),
        VirtualGiftItem("crown", "Crown", 250L, VirtualGiftCategory.LUXURY, "Golden imperial majesty", "#FFB703"),
        VirtualGiftItem("yacht", "Yacht", 1000L, VirtualGiftCategory.LUXURY, "Luxury ocean liner", "#0077B6"),
        VirtualGiftItem("private_jet", "Private Jet", 5000L, VirtualGiftCategory.LUXURY, "First-class VIP skies", "#023E8A"),
        VirtualGiftItem("gold_bar", "Gold Bar", 800L, VirtualGiftCategory.LUXURY, "Pure 24k solid ingot", "#FFD700"),
        VirtualGiftItem("sports_car", "Sports Car", 1500L, VirtualGiftCategory.LUXURY, "Exotic speedster convertible", "#E63946")
    )

    val supportGifts = listOf(
        VirtualGiftItem("golden_lion", "Golden Lion", 200L, VirtualGiftCategory.SUPPORT, "Regal king of the savanna", "#E5A910"),
        VirtualGiftItem("eagle", "Eagle", 180L, VirtualGiftCategory.SUPPORT, "Soaring golden predator", "#FB8500"),
        VirtualGiftItem("golden_trophy", "Golden Trophy", 400L, VirtualGiftCategory.SUPPORT, "Champion golden cup", "#F4A261"),
        VirtualGiftItem("fireworks", "Fireworks", 100L, VirtualGiftCategory.SUPPORT, "Sparkling night celebration", "#FF007F"),
        VirtualGiftItem("helping_hands", "Helping Hands", 50L, VirtualGiftCategory.SUPPORT, "Care and solidarity", "#588157"),
        VirtualGiftItem("laurel_wreath", "Laurel Wreath", 120L, VirtualGiftCategory.SUPPORT, "Symbol of triumph and honor", "#3A5A40"),
        VirtualGiftItem("megaphone", "Megaphone", 85L, VirtualGiftCategory.SUPPORT, "Amplify creator voice", "#D00000"),
        VirtualGiftItem("super_star", "Super Star", 350L, VirtualGiftCategory.SUPPORT, "Glittering star of support", "#FFBA08")
    )

    fun getGiftsForCategory(category: VirtualGiftCategory): List<VirtualGiftItem> {
        return when (category) {
            VirtualGiftCategory.TRENDING -> trendingGifts
            VirtualGiftCategory.FOR_YOU -> forYouGifts
            VirtualGiftCategory.LOVE -> loveGifts
            VirtualGiftCategory.FUN -> funGifts
            VirtualGiftCategory.LUXURY -> luxuryGifts
            VirtualGiftCategory.SUPPORT -> supportGifts
        }
    }

    val allGifts: List<VirtualGiftItem> by lazy {
        (trendingGifts + forYouGifts + loveGifts + funGifts + luxuryGifts + supportGifts).distinctBy { it.id }
    }

    fun findGiftById(id: String): VirtualGiftItem? {
        return allGifts.firstOrNull { it.id.equals(id, ignoreCase = true) }
    }
}
