package com.example.namethattune.models
import kotlinx.serialization.Serializable
@Serializable
data class Track(
    val trackName: String,
    val artist: String,
    val preview_url: String,
    val albumArt: String
)
