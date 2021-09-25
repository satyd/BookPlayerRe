package com.levp.bookplayer.activity

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.provider.MediaStore.Audio.*
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.levp.bookplayer.*
import com.levp.bookplayer.util.StorageUtil
import com.levp.bookplayer.util.TrackSupport
import kotlinx.android.synthetic.main.activity_tracklist.*
import java.io.File


class TracklistActivity : AppCompatActivity() {


    lateinit var trackList: ArrayList<TrackSupport.Track>

    //lateinit var trackList: ArrayList<Audio>
    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 9834

    lateinit var prefs: SharedPreferences
    var trackAmount:Int = 0

    private var player: MediaPlayerService? = null
    var serviceBound = false

    lateinit var adapter: TracklistAdapter

    private val allShownAudioPath: ArrayList<File> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracklist)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)
                ) {
                    // Explain to the user why we need to read the contacts
                }

                requestPermissions(
                        arrayOf(READ_EXTERNAL_STORAGE),
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                )
                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant that should be quite unique
                return;
            }
            loadAudio()
        }

        val tracklistAdapter = TracklistAdapter(trackList)

        tracklist_holder.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            // set adapter
            adapter = tracklistAdapter

            // Touch handling
            tracklist_holder.addOnItemTouchListener(RecyclerTouchListener(
                    applicationContext,
                    tracklist_holder,
                    object : RecyclerTouchListener.ClickListener {
                        override fun onClick(view: View?, position: Int) {
                            val path = trackList.get(position).dataUri
                            //Log.e("picked index : ", position.toString())

                            playAudio(position)


                        }

                        override fun onLongClick(
                                view: View,
                                recyclerView: RecyclerView,
                                position: Int
                        ) {


                        }
                    }
            ))

        }


    }

    private fun loadAudio() {
        trackList = ArrayList()
        val contentResolver = contentResolver
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        val cursor: Cursor? = contentResolver.query(uri, null, selection, null, sortOrder)
        trackAmount = cursor?.count ?: 0
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putInt(APP_TOTAL_AUDIO, trackAmount)
        editor.apply()


        if (cursor != null && cursor.count > 0) {

            trackList = ArrayList(0)

            while (cursor.moveToNext()) {
                val _id = cursor.getLong(cursor.getColumnIndexOrThrow(Media._ID))
                //val volumeName = cursor.getString(cursor.getColumnIndex(Media.VOLUME_NAME))

                val title: String = cursor.getString(cursor.getColumnIndex(Media.TITLE))
                val album: String = cursor.getString(cursor.getColumnIndex(Media.ALBUM))
                val artist: String = cursor.getString(cursor.getColumnIndex(Media.ARTIST))
                val duration = cursor.getLong(cursor.getColumnIndex(Media.DURATION))
                //val art = cursor.getString(cursor.getColumnIndex())

                val albumId = cursor.getString(cursor.getColumnIndex(Media.ALBUM_ID))
                val ImageUrl: Uri = getAlbumUri(applicationContext, _id.toString())!!

                lateinit var track: TrackSupport.Track
                //ёval dataUri = Media.getContentUri(volumeName)
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    val data = cursor.getString(cursor.getColumnIndex(Media.DATA))
                    //val albumArt: String = cursor.getString(cursor.getColumnIndex(Albums.ALBUM_ART))
                    track = TrackSupport.Track(data, title, album, artist)
                    //track.imageUri = ImageUrl
                } else {

                }
                // Save to trackList
                //trackList.add(Audio(data, title, album, artist))


                //track.duration = duration

                trackList.add(track)

            }
        }

        //Log.e("loaded _list : ",trackList.size.toString())
        cursor?.close()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putBoolean("ServiceState", serviceBound)
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        serviceBound = savedInstanceState.getBoolean("ServiceState")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (serviceBound) {
            unbindService(serviceConnection)
            //service is active
            player!!.stopSelf()
        }
    }

    private fun gotoPlayer(switch: Boolean) {
        intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra("switch", switch)
        startActivity(intent)
    }

    private fun gotoTracklist() {
        intent = Intent(this, TracklistActivity::class.java)
        startActivity(intent)
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as MediaPlayerService.LocalBinder
            player = binder.getService()
            serviceBound = true
            Toast.makeText(this@TracklistActivity, "Service Bound", Toast.LENGTH_SHORT).show()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            serviceBound = false
        }
    }

    override fun onPause() {
        super.onPause()

    }

    private fun playAudio(audioIndex: Int) {
        //Check is service is active
        //Check is service is active
        if (!serviceBound) {
            //Store Serializable trackList to SharedPreferences
            val storage = StorageUtil(applicationContext)
            //Log.e("_list storing : ", trackList.size.toString())
            storage.storeAudio(trackList)
            storage.storeAudioIndex(audioIndex)
            val playerIntent = Intent(this, MediaPlayerService::class.java)
            startService(playerIntent)
            bindService(playerIntent, serviceConnection, BIND_AUTO_CREATE)
        } else {
            //Store the new audioIndex to SharedPreferences
            val storage = StorageUtil(applicationContext)
            storage.storeAudioIndex(audioIndex)

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            val broadcastIntent = Intent(Broadcast_PLAY_NEW_AUDIO)
            sendBroadcast(broadcastIntent)
        }
    }

    fun getAlbumUri(mContext: Context, album_id: String): Uri? {
        if (mContext != null) {
            val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
            return Uri.withAppendedPath(sArtworkUri, java.lang.String.valueOf(album_id))
        }
        return null
    }

    companion object {
        const val Broadcast_PLAY_NEW_AUDIO = "com.levp.bookplayer.audioplayer.PlayNewAudio"
        const val APP_PREFERENCES = "data"
        const val APP_TOTAL_AUDIO = "total audios"
    }
}
