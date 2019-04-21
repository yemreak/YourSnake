package com.yemreak.yoursnake.oldsnake

import android.graphics.*
import android.view.MotionEvent
import com.yemreak.yoursnake.R
import kotlinx.android.synthetic.main.activity_snake_with_joy_stick.*

class SnakeActivityWithJoyStick : SnakeActivity() {

    /**
     * JoyStick yarı çapı
     * @see showJoyStick
     */
    private val jsRadius = 99f
    private val jsBorderSize = 7f
    private val jspRadius = 2.6f

    private lateinit var jsPoint: PointF

    override val layoutId: Int
        get() = R.layout.activity_snake_with_joy_stick
    override val surfaceViewId: Int
        get() = R.id.svFullGameField


    override fun bindEvents() {
        svFullGameField.setOnTouchListener { _, event -> showJoyStick(event)}
    }

    private fun showJoyStick(e: MotionEvent): Boolean {
        if (svFullGameField.holder.surface.isValid) { // Surface oluşturulduysa

            val canvas = svFullGameField.holder.lockCanvas()

            when (e.action) {
                MotionEvent.ACTION_DOWN -> setJoyStick(e.x, e.y)
                MotionEvent.ACTION_MOVE -> setJoyStick(e.x,e.y)
                MotionEvent.ACTION_UP -> {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                }
            }

            svFullGameField.holder.unlockCanvasAndPost(canvas) // Canvas'ı surface'ye çizmek
        }

        return true
    }

    private fun setJoyStick(x: Float, y: Float) {
        jsPoint = PointF(x, y)
    }
}
