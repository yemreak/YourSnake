package com.yemreak.yoursnake.oldsnake

import android.graphics.Point
import android.view.MotionEvent
import com.yemreak.yoursnake.R
import kotlinx.android.synthetic.main.activity_snake_with_swipe.*

class SnakeActivityWithSwipe : SnakeActivity() {

    override val layoutId: Int
        get() = R.layout.activity_snake_with_swipe

    override val surfaceViewId: Int
        get() = R.id.svFullGameField


    // Fark değeri tutacağız
    private val p = Point(0, 0)

    override fun bindEvents() {
        super.bindEvents() // Eklenmek zorunda

        svFullGameField.setOnTouchListener { _, event -> onTouch(event) }
    }

    /**
     * Not: Action_DOWN dokununca
     * Not: Action_UP elini kaldırınca
     */
    private fun onTouch(e: MotionEvent): Boolean {
        multiTouchEvent(e, { changeSnakeDirection(e) }, { pauseSnakeEngine(e) })

        return true
    }

    private fun changeSnakeDirection(e: MotionEvent) {
        when (e.action) {
        // Dokunulduğunda "pressed"
            MotionEvent.ACTION_DOWN -> {
                // İlk konumu alıyoruz
                p.x = e.x.toInt()
                p.y = e.y.toInt()

            }
        // Dokunma bırakıldığında "released
            MotionEvent.ACTION_UP -> {
                // Son konumdan ilk konumu çıkarığ fark değerine atıyoruz.
                p.x = e.x.toInt() - p.x
                p.y = e.y.toInt() - p.y

                val horizontal = when {
                    Math.abs(p.x) > Math.abs(p.y) -> true
                    else -> false
                }

                // Ufak kaydırmalar sayılmasın.
                val sensibility = 40

                when (horizontal) {
                    true -> when {
                        p.x > sensibility -> Snake.newDirection = Snake.Directions.RIGHT
                        p.x < -sensibility -> Snake.newDirection = Snake.Directions.LEFT
                    }
                    false -> when {
                        p.y > sensibility -> Snake.newDirection = Snake.Directions.DOWN
                        p.y < -sensibility -> Snake.newDirection = Snake.Directions.UP
                    }
                }

                resumeSnakeEngine() // Kaldırılma Sebebi: 2 parmakla dokunulduğunda en son ACTION_UP çalışıyor, durduruma ekranında iken arkadan tekrardan run ediyoruz.
            }
        }
    }
}
