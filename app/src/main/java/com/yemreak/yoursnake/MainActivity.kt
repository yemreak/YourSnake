package com.yemreak.yoursnake

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView
import com.yemreak.yoursnake.newsnake.SnakeEngine
import com.yemreak.yoursnake.oldsnake.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_ex_settings.*

class MainActivity : FullScreenActivity() { // BİRDEN FAZLA DİALOG AÇILMASIN KONTROL EKLE - Yatay dönmeyi kapat.

    private lateinit var snakeEngine: SnakeEngine

    companion object {
        lateinit var mediaPlayer: MediaPlayer
        var mustMediaPLayerPaused = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        snakeEngine = SnakeEngine(this, size)
        setContentView(snakeEngine)

        /*
        setContentView(R.layout.activity_main)


        createAndStartSounds()
        bindAnims()
        bindEvents()
        */
    }


    private fun createAndStartSounds() {
        mediaPlayer = MediaPlayer.create(this, R.raw.bg_main)
        mediaPlayer.setVolume(1f, 1f)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }


    private fun bindAnims() {
        cl_main.animation = AnimationUtils.loadAnimation(this, R.anim.fade_in_slow)

        ibExOptions.animation = AnimationUtils.loadAnimation(this, R.anim.fade_in_slow)

        btnNewGame.animation = AnimationUtils.loadAnimation(this, R.anim.fade_in_slow)
        btnGameMode.animation = AnimationUtils.loadAnimation(this, R.anim.fade_in_slow)
        btnOptions.animation = AnimationUtils.loadAnimation(this, R.anim.fade_in_slow)
    }

    private fun bindEvents() {
        btnNewGame.setOnClickListener { v: View -> onNewGameClicked(v) }
        btnGameMode.setOnClickListener { v: View -> onGameModeClicked(v) }
        ibExOptions.setOnClickListener { v: View -> onExOptionClicked(v) }
        btnOptions.setOnClickListener { v: View -> onOptionsClicked(v) }
    }

    private fun onNewGameClicked(v: View) {
        mustMediaPLayerPaused = true // Müziğin kapanmasını sağlıyoruz.

        v.isClickable = false // Tekrardan basılmasın diye

        v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out))

        Handler().postDelayed({
            if (mediaPlayer.isPlaying)
                mediaPlayer.stop()

            val option = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
            /*
            val intent: Intent = when (SnakeEngineOld.controlMode) {
                SnakeEngineOld.ControlModes.CONTROL_WITH_BUTTONS -> Intent(this, SnakeActivityWithButtons::class.java)
                SnakeEngineOld.ControlModes.CONTROL_WITH_SWIPE -> Intent(this, SnakeActivityWithSwipe::class.java)
            }
            */

            startActivity(intent, option.toBundle())
        }, delayMillis)

        Handler().postDelayed({
            // Süresi daha uzun oluyor ki arada açık olup tekrar basılmasın.
            v.isClickable = true // İşlem sonrası tekrar basılabilir hale alıyoruz
        }, delayMillisLong)
    }

    private fun onGameModeClicked(v: View) {
        mustMediaPLayerPaused = false // müziği devamını sağlıyoruz.

        v.isClickable = false // Tekrardan basılmasın diye

        v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out))

        Handler().postDelayed({
            val option = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
            val intent = Intent(this, GameModeActivity::class.java)

            startActivity(intent, option.toBundle())
        }, delayMillis)

        Handler().postDelayed({
            // Süresi daha uzun oluyor ki arada açık olup tekrar basılmasın.
            v.isClickable = true // İşlem sonrası tekrar basılabilir hale alıyoruz
        }, delayMillisLong)
    }


    private fun onExOptionClicked(v: View) {
        v.isClickable = false // Tekrardan basılmasın diye

        v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotata_indefinitely))

        showExOptionsDialog()

        Handler().postDelayed({
            // Süresi daha uzun oluyor ki arada açık olup tekrar basılmasın.
            v.isClickable = true // İşlem sonrası tekrar basılabilir hale alıyoruz
        }, delayMillisLong)
    }

    /**
     * Ekstra ayarlar ekranının diyaloğunu gösterme
     *
     * Not: Her eklenen ek ayarı "setNegativeButton"da sıfırlamamız lazım.
     */
    private fun showExOptionsDialog() { // DÜZENLE BURAYI; ÇOK AZ, AZ ... ÇOK FAZLA
        val settingsDialogBuilder = AlertDialog.Builder(this)
        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val settingsView = layoutInflater.inflate(R.layout.dialog_ex_settings, rl_ex_options_dialog, true)

        with(settingsDialogBuilder) {
            setTitle("Ayarlar")
            setIcon(R.drawable.ic_ex_options)
            setView(settingsView)
            setCancelable(false)
            setOnDismissListener { _ -> ibExOptions.clearAnimation() }
            setPositiveButton(R.string.txtButton_saveAndExit,null)
            setNegativeButton(R.string.txtButton_resetAndExit) { _, _ ->
                Snake.lengthMode = Snake.Length.NORMAL
                BlockField.blockSizeMode = BlockField.Sizes.NORMAL
                SnakeEngineOld.gameSpeedMode = SnakeEngineOld.GameSpeed.NORMAL
                SnakeEngineOld.showScreen = true
            }
        }

        val settingsDialog = settingsDialogBuilder.create()

        // settingsView.findViewById<Button>(R.id.btnSaveAndExit).setOnClickListener { settingsDialog.dismiss() } // Butona basıldığında diyaloğu kapatıyoruz.

        val tvLength = settingsView.findViewById<TextView>(R.id.tv_snakeLength)
        tvLength.text = getString(R.string.txt_lengthOfSnake_exOp, getString(Snake.Length.getTextId(Snake.lengthMode))) // Metnin ayarlanması

        val sbLength = settingsView.findViewById<SeekBar>(R.id.sb_length)
        sbLength.progress = Snake.Length.getIndex(Snake.lengthMode) // Seek Bar'ın doğru değeri göstermesini sağlıyoruz.

        sbLength.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvLength.text = getString(R.string.txt_lengthOfSnake_exOp, getString(Snake.Length.getTextId(progress))) // Metnin ayarlanması
                Snake.lengthMode = Snake.Length.get(progress) // Yeni modun ayarlanması

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        val tvBlockSize = settingsView.findViewById<TextView>(R.id.tv_blockSize)
        tvBlockSize.text = getString(R.string.txt_sizeOfBlock_exOp, getString(BlockField.Sizes.getTextId(BlockField.blockSizeMode))) // Metnin ayarlanması

        val sbBlockSize = settingsView.findViewById<SeekBar>(R.id.sb_blockSize)
        sbBlockSize.progress = BlockField.Sizes.getIndex(BlockField.blockSizeMode) // Seek Bar'ın doğru değeri göstermesini sağlıyoruz.

        sbBlockSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvBlockSize.text = getString(R.string.txt_sizeOfBlock_exOp, getString(BlockField.Sizes.getTextId(progress))) // Metnin ayarlanması
                BlockField.blockSizeMode = BlockField.Sizes.get(progress) // Yeni modun ayarlanması
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        val tvFps = settingsView.findViewById<TextView>(R.id.tv_fps)
        tvFps.text = getString(R.string.txt_speedOfGame_exOp, getString(SnakeEngineOld.GameSpeed.getTextId(SnakeEngineOld.gameSpeedMode)))

        val sbFps = settingsView.findViewById<SeekBar>(R.id.sb_fps)
        sbFps.progress = SnakeEngineOld.GameSpeed.getIndex(SnakeEngineOld.gameSpeedMode)

        sbFps.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvFps.text = getString(R.string.txt_speedOfGame_exOp, getString(SnakeEngineOld.GameSpeed.getTextId(progress)))
                SnakeEngineOld.gameSpeedMode = SnakeEngineOld.GameSpeed.get(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        val cbNoScreen = settingsView.findViewById<CheckBox>(R.id.cbNoScreen)

        cbNoScreen.isChecked = !SnakeEngineOld.showScreen
        cbNoScreen.setOnClickListener {
            SnakeEngineOld.showScreen = !cbNoScreen.isChecked
        }


        with(settingsDialog) {
            window.attributes.windowAnimations = R.style.FadeDialog
            show()
        }

    }


    private fun onOptionsClicked(v: View) {
        mustMediaPLayerPaused = false // müziği devamını sağlıyoruz.

        v.isClickable = false // Tekrardan basılmasın diye

        v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out))

        Handler().postDelayed({
            val option = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
            val intent = Intent(this, OptionsActivity::class.java)

            startActivity(intent, option.toBundle())
        }, delayMillis)

        Handler().postDelayed({
            // Süresi daha uzun oluyor ki arada açık olup tekrar basılmasın.
            v.isClickable = true // İşlem sonrası tekrar basılabilir hale alıyoruz
        }, delayMillisLong)
    }

    /*
    override fun onDestroy() {
        super.onDestroy()

        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun onResume() {
        super.onResume()

        mustMediaPLayerPaused = true

        if (!mediaPlayer.isPlaying)
            mediaPlayer.start()
    }

    override fun onPause() {
        super.onPause()

        if (mustMediaPLayerPaused && mediaPlayer.isPlaying)
            mediaPlayer.pause()
    }
*/
}
