package com.yemreak.yoursnake.oldsnake

import android.view.View
import com.yemreak.yoursnake.R
import kotlinx.android.synthetic.main.activity_snake_with_buttons.*

class SnakeActivityWithButtons : SnakeActivity() {

    override val layoutId: Int
        get() = R.layout.activity_snake_with_buttons

    override val surfaceViewId: Int
        get() = R.id.svGameField

    override fun bindEvents() {
        super.bindEvents() // Eklenmek zorunda

        svGameField.setOnTouchListener { _, event -> pauseSnakeEngine(event) }

        ibLeft.setOnClickListener { v: View -> changeSnakeDirection(v) }
        ibUp.setOnClickListener { v: View -> changeSnakeDirection(v) }
        ibDown.setOnClickListener { v: View -> changeSnakeDirection(v) }
        ibRight.setOnClickListener { v: View -> changeSnakeDirection(v) }
    }

    private fun changeSnakeDirection(v: View) {
        when (v.id) {
            R.id.ibLeft -> Snake.newDirection = Snake.Directions.LEFT
            R.id.ibRight -> Snake.newDirection = Snake.Directions.RIGHT
            R.id.ibUp -> Snake.newDirection = Snake.Directions.UP
            R.id.ibDown -> Snake.newDirection = Snake.Directions.DOWN
        }

        resumeSnakeEngine() // Oyun durduysa başlatmak
    }
}
