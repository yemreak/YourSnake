package com.yemreak.yoursnake.oldsnake

import android.content.Intent
import android.os.Bundle
import com.yemreak.yoursnake.FullScreenActivity
import com.yemreak.yoursnake.MainActivity
import com.yemreak.yoursnake.R
import kotlinx.android.synthetic.main.activity_snake_engine_screen.*


class SnakeEngineScreen : FullScreenActivity() {

    /**
     * Durdurma işlemi olduğunuzu anlamamızı sağlayan değer. Score değeri değildir demek.
     * @see setProperties
     */
    private val nullScore = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snake_engine_screen)

        setProperties()
        bindEvents()
    }

    private fun setProperties() {
        val score = intent.getIntExtra("score", nullScore)

        if (score != nullScore) {
            tvScreenTop1.setText(R.string.txt_toMenu1_screen)
            tvScreenTop2.setText(R.string.txt_toMenu2_screen)
            tvScreenTop3.setText(R.string.txt_toMenu3_screen)
            tvTitleScreen.setText(R.string.txtTitle_gameOver_screen)
            tvScreenMiddle.text = getString(R.string.txt_score_screen, score)
            tvScreenMiddle.textSize = 26f
            tvScreenBottom.setText(R.string.txt_toRestart_screen)
        }
    }

    private fun bindEvents() {
        rlScreenPaused.setOnTouchListener { _, event -> fastClickEvent(event, { onBackPressed() }, { startMainActivity() }) }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    private fun startMainActivity() {

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Arkasındaki tüm işlemleri bitirme.
        finish() // İşlemi sonlandırma
        startActivity(intent)
    }


}

