package com.yemreak.yoursnake

import android.annotation.SuppressLint
import android.os.Handler
import android.view.MotionEvent

/**
 * Maindeki ayarlar butonlarının extend ettiği class
 */
@SuppressLint("Registered")
abstract class SubFullScreenActivity: FullScreenActivity() {

    override fun onPause() {
        super.onPause()

        if(MainActivity.mediaPlayer.isPlaying)
            MainActivity.mediaPlayer.pause()
    }

    override fun onResume() {
        super.onResume()

        if(!MainActivity.mediaPlayer.isPlaying)
            MainActivity.mediaPlayer.start()
    }
}