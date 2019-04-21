package com.yemreak.yoursnake

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View

@SuppressLint("Registered")
abstract class FullScreenActivity : AppCompatActivity() {

    val delayMillisLong: Long = 1000
    val delayMillis: Long = 400
    val delayMillisShort: Long = 250


    /**
     * İki parmak dokunma olayı için gereken en fazla süre
     * @see fastClickEvent
     */
    private val delayFastClickMillis: Long = 300

    /**
     * Daha önceden tıklanmış mı kontrolü
     *
     * @see fastClickEvent İki kez hızlıca tıklanma kontrolü için kullanılır
     */
    private var justClicked = false

    private var doubleTouchConfirmed = false
    private var singleTouchConfirmed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        immersiveMode()
    }

    private fun immersiveMode() {
        // Navigation Bar'ı kaldırma
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        window.decorView.setOnSystemUiVisibilityChangeListener { hideSystemUi() }
    }

    private fun hideSystemUi() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    // Bu kısım olmazsa tıklandığında navigation bar yine gelir.
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            hideSystemUi()
        }
    }

    /**
     * Ard arda hızlı tıklama olayında istenen işlemleri yapma
     *
     * @param onOnceClick Bir kez tıklandığında yapılacak işlemler
     * @param onTwiceClicks İki kez *hızlıca* tıklandığında yapılacak işlemler
     */
    protected fun fastClickEvent(e: MotionEvent, onOnceClick: () -> Unit, onTwiceClicks: () -> Unit): Boolean {
        if (e.action == MotionEvent.ACTION_DOWN) // Dokunma eylemi ise bu işlemler yapılacak, dokunduktan sonrası bizi ilgilendirmiyor.
            when (justClicked) {
                false -> {
                    justClicked = true

                    Handler().postDelayed({
                        if (justClicked) {
                            onOnceClick()
                        }
                        justClicked = false
                    }, delayFastClickMillis)
                }
                true -> {
                    onTwiceClicks()
                    justClicked = false
                }
            }

        return true
    }

    /**
     * 2 parmak ile dokunma olayında istenen işlemleri yapma
     *
     * @param onSingleTouch Bir kez dokunulduğunda yapılacak işlemler
     * @param onDoubleTouch 2 kez dokunulduğunda  yapılacak işlemler
     *
     * Not: İlk dokunma işlemi hep çalışıyor :/ [MotionEvent.ACTION_DOWN]
     */
    protected fun multiTouchEvent(e: MotionEvent, onSingleTouch: () -> Unit, onDoubleTouch: () -> Unit): Boolean {
        if (e.action == MotionEvent.ACTION_DOWN) // İlk aksiyonu belirlemek oldukça karmaşık, tek parmak diye ele alıyoruz.
            onSingleTouch()
        else {
            when (singleTouchConfirmed) {
                true -> {
                    singleTouchConfirmed = e.action != MotionEvent.ACTION_UP // Son aksiyonda değeri sıfırlıyoruz.
                    onSingleTouch()
                }
                false -> {
                    when (doubleTouchConfirmed) {
                        false -> {
                            singleTouchConfirmed = e.pointerCount == 1
                            doubleTouchConfirmed = e.pointerCount == 2

                            if (singleTouchConfirmed)
                                onSingleTouch()
                            else
                                onDoubleTouch()
                        }
                        true -> {
                            doubleTouchConfirmed = e.action != MotionEvent.ACTION_UP // Son aksiyonda değeri sıfırlıyoruz.

                            if (e.pointerCount == 2) // Sadece iki parmak işlemleri için metot çalışmalı, diğer durumlarda çalışmamalı. (diğer durumlar da var)
                                onDoubleTouch()
                        }
                    }
                }
            }
        }

        return true
    }


    override fun onResume() {
        super.onResume()
        hideSystemUi()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Animasyonlu geçiş için.
        overridePendingTransition(R.anim.fade_in_fast, R.anim.fade_out_fast)

    }


}