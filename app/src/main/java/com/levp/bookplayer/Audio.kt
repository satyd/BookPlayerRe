package com.levp.bookplayer
import java.io.Serializable

@kotlinx.serialization.Serializable
class Audio(var data: String, var title: String, var album: String, var artist: String) : Serializable {

}