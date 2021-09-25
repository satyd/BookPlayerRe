package com.levp.bookplayer.activity

import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil.setContentView
import com.levp.bookplayer.R
import com.levp.bookplayer.databinding.ActivityPlayerBinding
import java.util.concurrent.TimeUnit

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var mp: MediaPlayer
    private var totalTime = 0
    //main controls
    private lateinit var prevBtn: ImageButton
    private lateinit var playBtn: ImageButton
    private lateinit var nextBtn: ImageButton

    private lateinit var btnPause: ImageButton //to get resource
    private lateinit var btnPlay: ImageButton //to get res
    //sub controls
    private lateinit var posBar: SeekBar
    private lateinit var elapsedTimeLabel: TextView
    private lateinit var totalTimeLabel: TextView
    private lateinit var addToPlaylist: ImageButton
    private lateinit var addToPlayFavorite: ImageButton

    private var sTime: Int = 0
    private var eTime: Int = 0

    private val handler: Handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentView(this, R.layout.activity_player)
        initializeIds()
        setSupportActionBar(binding.toolbar as Toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        mp = MediaPlayer.create(this, R.raw.the_rat)
        mp.isLooping = true
        mp.setVolume(0.5f,0.5f)
        totalTime = mp.duration
        initializeListeners()

    }
    private fun initializeListeners(){
        playBtn.setOnClickListener(){
            playBtnClick()
        }
        posBar.setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            mp.seekTo(progress)
                        }
                    }
                    override fun onStartTrackingTouch(p0: SeekBar?) {
                    }
                    override fun onStopTrackingTouch(p0: SeekBar?) {
                    }
                }
        )

        mp.setOnCompletionListener { mp ->
            try {
                mp!!.start()
                posBar.progress = sTime
                mp.isLooping = true
            } catch (e: Exception) {
                Toast.makeText(this@PlayerActivity, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun initializeIds(){

        prevBtn = findViewById(R.id.prev_btn)
        playBtn = findViewById(R.id.play_btn)
        nextBtn = findViewById(R.id.next_btn)
        btnPause = findViewById(R.id.btn_pause)
        btnPlay = findViewById(R.id.btn_play)
        posBar = findViewById(R.id.seek_bar_play)
        elapsedTimeLabel = findViewById(R.id.time_elapsed)
        totalTimeLabel = findViewById(R.id.time_length)
    }
    private val updateSongTime: Runnable = object : Runnable {
        override fun run() {
            sTime = mp.currentPosition
            elapsedTimeLabel.text = setTimeLabel(sTime)
            posBar.progress = sTime
            handler.postDelayed(this, 100)
        }
    }

    private fun onStartPlaying(){

        eTime = mp.duration.toLong().toInt()
        sTime = mp.currentPosition.toLong().toInt()

        totalTimeLabel.text = setTimeLabel(eTime)
        elapsedTimeLabel.text = setTimeLabel(sTime)

        posBar.progress = 0
        posBar.max = mp.duration

        handler.postDelayed(updateSongTime,100)
    }
    private fun setTimeLabel(time:Int): String {
        val seconds = TimeUnit.MILLISECONDS.toSeconds(time.toLong()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time.toLong()))
        if(seconds>=10)
            return String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(time.toLong()), seconds)
        else
            return String.format("%d:0%d", TimeUnit.MILLISECONDS.toMinutes(time.toLong()), seconds)
    }

    private fun playBtnClick() {
        if(mp.isPlaying){

            mp.pause()
            val res: Drawable = btnPlay.drawable
            playBtn.setImageDrawable(res)
        } else{
            mp.start()
            onStartPlaying()//TODO fix seekbar bug!!
            val res: Drawable = btnPause.drawable
            playBtn.setImageDrawable(res)
        }

    }
}