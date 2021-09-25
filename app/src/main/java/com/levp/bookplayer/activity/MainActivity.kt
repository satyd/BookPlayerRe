package com.levp.bookplayer.activity

import android.Manifest
import android.app.WallpaperManager
import android.content.*
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import com.levp.bookplayer.*
import com.levp.bookplayer.MediaPlayerService.LocalBinder
import com.levp.bookplayer.activity.TracklistActivity.Companion.APP_TOTAL_AUDIO
import com.levp.bookplayer.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 9834

    private var player: MediaPlayerService? = null
    var serviceBound = false
    lateinit var audioList:ArrayList<Audio>

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                ) {
                    // Explain to the user why we need to read the contacts
                }

                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                )
                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant that should be quite unique
                return;
            }

        }
        binding.gotoPlayer.setOnClickListener {
            gotoPlayer(false)
        }
        binding.gotoTracklist.setOnClickListener {
            gotoTracklist()
        }
        val wallpaperManager: WallpaperManager = WallpaperManager.getInstance(this)
        val wallpaperDrawable : Drawable = wallpaperManager.drawable
        val blurredBackground = blur(this, wallpaperDrawable.toBitmap())
        val drawableBackground: Drawable = BitmapDrawable(resources, blurredBackground)

        goto_player.background = drawableBackground

        //playAudio("https://upload.wikimedia.org/wikipedia/commons/6/6c/Grieg_Lyric_Pieces_Kobold.ogg");
    }

    override fun onResume() {
        super.onResume()
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val track_count = prefs.getInt(APP_TOTAL_AUDIO,0)
        if (track_count > 0)
            trackAmountView.text = "Amount: $track_count"


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
    private fun gotoPlayer(switch: Boolean)
    {
        intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra("switch", switch)
        startActivity(intent)
    }
    private fun gotoTracklist()
    {
        intent = Intent(this, TracklistActivity::class.java)
        startActivity(intent)
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as LocalBinder
            player = binder.getService()
            serviceBound = true
            Toast.makeText(this@MainActivity, "Service Bound", Toast.LENGTH_SHORT).show()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            serviceBound = false
        }
    }
    private fun playAudio(media: String) {
        //Check is service is active
        if (!serviceBound) {
            val playerIntent = Intent(this, MediaPlayerService::class.java)
            playerIntent.putExtra("media", media)
            startService(playerIntent)
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        } else {
            //TODO ы
            //Service is active
            //Send media with BroadcastReceiver
        }
    }

}