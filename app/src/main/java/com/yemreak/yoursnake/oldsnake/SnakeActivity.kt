package com.yemreak.yoursnake.oldsnake

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceView
import com.yemreak.yoursnake.FullScreenActivity
import com.yemreak.yoursnake.newsnake.SnakeEngine

@SuppressLint("Registered")
abstract class SnakeActivity : FullScreenActivity() {

    /**
     * Gösterilecek olan layout Id'si
     */
    abstract val layoutId: Int

    /**
     * Oyun motorunun gösterileceğin alanın id'si
     */
    abstract val surfaceViewId: Int

    private lateinit var surfaceView: SurfaceView
    private lateinit var snakeEngineOld: SnakeEngineOld

    private val startDelay: Long = 1000 // SnakeEngineOld.surfaceCreated çalıştıktan sonra çalışması için gecikme vermemiz lazım

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(layoutId)

        surfaceView = findViewById(surfaceViewId)

        snakeEngineOld = SnakeEngineOld(this, surfaceView.holder)

        bindEvents()
    }

    /**
     * Ekrandaki bütün eylemlerin burada tanımlanması gerekmektedir.
     * @see onCreate
     */
    protected open fun bindEvents() {
        // Eğer surface değişirse haberimiz olacak
        surfaceView.holder.addCallback(snakeEngineOld)
    }

    /**
     * Yılan motorunu belirli bir gecikme ile devam ettirme.
     * @see onResume Oyun durdurulduktan sonra gelindiğinde yılan otomatik başlar.
     *
     * Not: onResume'de olmazsa yılanı durdurup durdurup çok güvenli şekilde hareket edilebilir ve ölünmez (bug çözüldü.)
     */
    fun resumeSnakeEngine() {
        snakeEngineOld.resume(startDelay)
    }


    /**
     * Güvenli bir şekilde yılan motorunu durdurma ve durma aktivitesine geçme
     * @param e Dokunam verisi || Kesinlikle durdurulmak isteniyorsa "null"
     *
     * Not: Dokunma durumu 2 aşamadan oluşuyor, basma ve çekme. Bu yüzden 2 kez çalışıyor.
     */
    fun pauseSnakeEngine(e: MotionEvent?): Boolean {
        when {
            e == null ||
                    e.action == MotionEvent.ACTION_DOWN ||
                    e.actionMasked == MotionEvent.ACTION_POINTER_DOWN -> {
                snakeEngineOld.pause(null)
            }
        }

        return true
    }

    /**
     * Aktivite devam ettiğinde yapılacak işlemler.
     *
     * Not: Aktivite açıldığında aktif olur. (create'den hemen sonra)
     */
    override fun onResume() {
        super.onResume()

        resumeSnakeEngine()
    }

    override fun onBackPressed() {
        when (SnakeEngineOld.showScreen) {
            true -> pauseSnakeEngine(null)
            false -> super.onBackPressed()
        }

    }

    /**
     * Not: Bu varsayılan işlemdir, eğer burada da "durduruldu ekranı" gösterirsek 2 tane oluşacaktır.
     * @see SnakeEngineOld.pause
     */
    override fun onPause() {
        super.onPause()
        snakeEngineOld.pause(false)
    }
}