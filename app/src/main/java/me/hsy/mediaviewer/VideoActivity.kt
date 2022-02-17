package me.hsy.mediaviewer

import android.graphics.PixelFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.View.*
import android.widget.*

class VideoActivity : AppCompatActivity() {
    private var playBtn: ImageView? = null
    private var seekBar: SeekBar? = null
    private var replayBtn: Button? = null
    private var videoView: VideoView? = null
    private var status = PAUSE
    private var loadingInterface: RelativeLayout? = null
    private var duration: Int? = null
    private var durationMin: String? = null
    private var position: TextView? = null
    //private var replayPanel: RelativeLayout? = null

    private val handler = Handler(Looper.getMainLooper())
    // for seekBar position update
    private inner class Runner: Runnable{
        override fun run() {
            if (status == PLAY) {
                val current: Int = videoView!!.currentPosition

                seekBar?.progress = (current.toFloat() / duration!!.toFloat() * 100).toInt()
                //Log.d("@=>", "currentPos = $current ${current.toFloat() / duration!!.toFloat() * 100}")
                position?.text = "${msec2min(current)}/$durationMin"
            }

            handler.postDelayed(this, 1000)
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        title = "Video Viewer"

        playBtn = findViewById(R.id.pause_btn)
        seekBar = findViewById(R.id.seek_bar)
        replayBtn = findViewById(R.id.replay_btn)
        videoView = findViewById(R.id.videoView)
        loadingInterface = findViewById(R.id.loading_interface)
        position = findViewById(R.id.tv_time)
        //replayPanel = findViewById(R.id.replay_interface)

        playBtn?.setOnClickListener{
            if (status == PLAY) {
                pauseVideo()
            }
            else {
                playVideo()
            }
        }
        replayBtn?.setOnClickListener{
            replayVideo()
        }
//        findViewById<ImageView>(R.id.replay_icon).setOnClickListener{
//            replayVideo()
//        }
        seekBar?.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    Log.d("@=>", progress.toString())
                    val msec = (progress * duration!! / 100)
                    videoView!!.seekTo(msec)
                    position?.text = "${msec2min(msec)}:$durationMin"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {//刚开始触摸的时候
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {//结束触摸
            }
        })

        videoView!!.holder.setFormat(PixelFormat.TRANSPARENT)
        videoView!!.setZOrderOnTop(true)
        videoView!!.setVideoPath("android.resource://" + this.packageName + "/" + R.raw.big_buck_bunny)
        videoView!!.setOnPreparedListener {
            loadingInterface!!.visibility = GONE // set loading interface invisible
            duration = videoView!!.duration // length of the video in msec
            durationMin = msec2min(duration!!)
            position?.text = "00:00/$durationMin"
            //Log.d("@=>", durationMin!!)
            playVideo()
        }

        videoView!!.setOnCompletionListener {
            stopVideo()
            //replayPanel!!.visibility = VISIBLE
        }
        // UPDATE SEEK BAR
        handler.post(Runner())

    }

    private fun playVideo(){
        videoView!!.start()
        playBtn!!.setImageResource(R.drawable.pause_icon)
        status = PLAY
        //replayPanel!!.visibility = INVISIBLE
    }

    private fun pauseVideo(){
        videoView!!.pause()
        playBtn!!.setImageResource(R.drawable.play_icon)
        status = PAUSE
    }

    private fun replayVideo(){
        videoView!!.resume()
        playBtn!!.setImageResource(R.drawable.pause_icon)
        status = PLAY
        position?.text = "00:00/$durationMin"
        //replayPanel!!.visibility = INVISIBLE
    }

    private fun stopVideo(){
        videoView!!.stopPlayback()
        playBtn!!.setImageResource(R.drawable.play_icon)
        seekBar!!.progress = 0
        status = STOP
        position?.text = "00:00/$durationMin"
    }

    private fun msec2min(msec: Int): String {
        val totalSec: Int = msec / 1000
        val min: String = if((totalSec / 60).toInt() < 10) {
            "0" + (totalSec / 60).toInt().toString()
         } else {
            (totalSec / 60).toInt().toString()
         }

        val sec: String = if((totalSec % 60).toInt() < 10) {
            "0" + (totalSec % 60).toInt().toString()
        } else {
            (totalSec % 60).toInt().toString()
        }
        return "$min:$sec"
    }

    companion object{
        const val PLAY = 0
        const val PAUSE = 1
        const val STOP = 2
    }
}
