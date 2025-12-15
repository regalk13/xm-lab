package com.example.xmlab


data class ConsoleBand(
    val id: Int,
    val label: String,
    var gain: Float
)

enum class EqPreset(val title: String, val gains: List<Float>) {
    Flat("Flat", listOf(0f, 0f, 0f, 0f, 0f, 0f)),
    BassBoost("Bass Boosted", listOf(8f, 5f, 0f, 0f, -2f, 5f)),
    Vibrant("Vibrant", listOf(4f, -2f, -4f, 3f, 6f, 8f)),
    Vocal("Voice / Spoken", listOf(-5f, -2f, 5f, 4f, -3f, 2f))
}