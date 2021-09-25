package com.levp.bookplayer.util

import android.content.Context
import android.net.Uri
import java.io.Serializable
import kotlin.properties.Delegates

class TrackSupport{
    @kotlinx.serialization.Serializable
    class Track(var dataUri:String, var name : String,var album : String, var artist : String) : Serializable {
        //var duration by Delegates.notNull<Long>()


    }

}