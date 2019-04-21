package com.yemreak.yoursnake.newsnake

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.yemreak.yoursnake.newsnake.SnakeEngine.ControlType.*
import com.yemreak.yoursnake.newsnake.controller.JoystickController
import com.yemreak.yoursnake.newsnake.elements.Feed
import com.yemreak.yoursnake.newsnake.elements.Snake

@SuppressLint("ViewConstructor") // Diğer surfaceView constructer'larını kullanılmaz halde getirdik (?)
class SnakeEngine(context: Context, val point: Point) : SurfaceView(context), SurfaceHolder.Callback, Runnable, JoystickController.JoystickListener {

    /**
     * Kontrol değişkenlerinin türlerini belirlemek için kullanılır.
     *
     * Not: Verilerin değişmesi durumunda [check] güncellenmelidir.
     */
    enum class ControlType {
        CREATED,
        READY,
        PAUSED
    }

    /**
     * Hata günlüklerini aktif etmek için kullanılır.
     */
    private val debugMode = true

    /**
     * Oyun motorunun oluşturulduğunu kontrol etmek için kullanılıyor.
     * [surfaceCreated] ve [surfaceDestroyed] ile güncellenir.
     */
    var isCreated = false
        private set

    /**
     * Oyun motorunun durdurulduğunu kontrol etmek için kullanılıyor.
     * [surfaceCreated] içinde kullanılmaktadır.
     */
    var isPaused = false
        private set

    /**
     * Oyun motorunun değişkenlerinin hazır olduğunu kontrol etmek için kullanılıyor.
     * [initVariables] içerisinde kullanlmaktadır.
     */
    var isReady = false
        private set

    private val snakes = arrayListOf<Snake>()
    private val feeds = arrayListOf<Feed>()

    var score: Int = 0
        private set

    private val joyStickController = JoystickController(this)

    /**
     * En fazla hız değeri
     * [onJoystickChanged] ile yılana belli bir oranda aktarılır.
     */
    val maxSpeed = 30f


    init {
        holder.addCallback(this)
        setOnTouchListener { _, event -> onTouch(event) }
    }


    /**
     * [debugMode]'a göre hata günlükleriyle değişkenleri kontrol eder.
     *
     * @return Kontrol sonucu
     */
    private fun check(boolId: ControlType, funcName: String): Boolean {
        return when (boolId) {
            CREATED -> {
                if (debugMode && !isCreated)
                    Log.e("Düzgün Çalışmadı", "Yüzey oluşturulmadan kullanılmamalı. [surfaceCreated] ile yüzeyin oluşturduğundan emin ol. SnakeEngine.$funcName")
                isCreated
            }
            READY -> {
                if (debugMode && !isReady)
                    Log.e("Düzgün Çalışmadı", "Değişkenler oluşturulmadan kullanılmamalı. [initVariables] ile değişkenlerin oluşturduğundan emin ol. SnakeEngine.$funcName")
                isReady
            }
            PAUSED -> {
                isPaused
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        isCreated = false // Yüzey kaldırıldığı için güncelliyoruz.
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        isCreated = true // Yüzey oluşturulduğu için güncelliyoruz.

        when (isPaused) {
            true -> {
            }
            false -> {
                Camera.initialize(holder.surfaceFrame)
                prepare()
            }
        }
    }


    override fun onJoystickChanged(center: PointF, pointerCenter: PointF, degree: Double, ratio: Double) {
        with(snakes[0]) {
            nextDegree = degree
            nextSpeed = (maxSpeed * ratio).toFloat()
        }

        with(snakes[1]) {
            nextDegree = -degree
            nextSpeed = (maxSpeed * ratio).toFloat()
        }
    }

    override fun onJoystickCreated(center: PointF, pointerCenter: PointF, degree: Double, ratio: Double) {

    }

    override fun onJoystickReleased(center: PointF, pointerCenter: PointF, degree: Double, ratio: Double) {
        with(snakes[0]) {
            nextDegree = this.degree
        }

        with(snakes[1]) {
            nextDegree = this.degree
        }
    }

    /**
     * Oyun alanı oluşturulduktan sonra oyun alanını hazır hale getirip durgun halde bekletmek için kullanılır.
     *
     * Not: [surfaceCreated] metodu çalıştıktan sonra çalışması gerekmektedir.
     */
    private fun prepare() {
        if (check(CREATED, "prepare")) {
            initVariables()
            newGame()
        }
    }

    /**
     * Oyun alanı içerisindeki değişkenleri oluştumak için kullanılır.
     *
     * Not: [surfaceCreated] metodu çalıştıktan sonra çalışması gerekir.
     */
    private fun initVariables() {
        if (check(CREATED, "initVariables")) {
            initSnakes()
            initFeeds()

            isReady = true
        }
    }

    private fun initSnakes() {
        snakes.add(Snake())
        snakes.add(Snake())
        //...
    }

    private fun initFeeds() {
        for (i in 0..2)
            feeds.add(Feed())
        // ...
    }

    private fun newGame() {
        if (check(READY, "newGame")) {
            // Yılanı oluşturuyoruz
            snakes[0].create()
            snakes[1].create(PointF(20f, 20f))

            // Yemleri yeniden oluşturuyoruz.
            feeds.forEach { feed -> feed.spawn(snakes, feeds) }

            // Skoru sıfırlıyoruz
            score = 0

            draw() // Kaldırılabilir. (?)

            android.os.Handler().postDelayed(this, 24) // (?)
        }
    }

    private fun draw() {
        if (check(CREATED, "draw") && check(READY, "draw")) {
            if (holder.surface.isValid) {
                val canvas = holder.lockCanvas()
                val paint = Paint()

                // Arkaplanı temizleme
                canvas.drawColor(0, PorterDuff.Mode.CLEAR)

                // Arka planı çizme
                paint.color = Color.BLACK
                canvas.drawPaint(paint)

                // Score'u çiziyoruz
                paint.color = Color.WHITE
                paint.textSize = 64f
                canvas.drawText("Score: $score", 30f, 94f, paint)

                feeds.forEach { feed -> feed.drawConditionally(context, canvas) }

                snakes.forEach { snake -> snake.draw(context, canvas) }

                // Eğer joystick aktif ise ekrana çiziyoruz.
                joyStickController.drawJoystick(canvas)

                holder.unlockCanvasAndPost(canvas)
            }
        }
    }

    /**
     * Oyun ekranına dokunulduğuna olacak işlemler.
     */
    private fun onTouch(e: MotionEvent): Boolean {
        return joyStickController.onTouch(e)
    }

    /**
     * Tekrarlanacak işlemlerin bulunduğu metot
     */
    override fun run() {
        if (check(CREATED, "run") && check(READY, "run")) {

            snakes.forEach { snake -> snake.move() }

            Camera.follow(snakes[0]) // Düzenlenecek

            /* feeds.forEach { feed ->
                if (snakes[0].isFeeding(feed)){

                }
            } */

            draw()

            Handler().postDelayed(this, 40) // Kasarak hareket ediyor ayarlamam lazım. ! Internetten oyun grid sistemini araştırmalıyım.
        }
    }

    // onPause de durdurmamız
    // onResume da devam ettirmemiz lazım
}




























