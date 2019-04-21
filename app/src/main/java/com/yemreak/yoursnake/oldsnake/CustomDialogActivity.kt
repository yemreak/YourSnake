package com.yemreak.yoursnake.oldsnake

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ToggleButton
import com.yemreak.yoursnake.R
import com.yemreak.yoursnake.SubFullScreenActivity
import com.yemreak.yoursnake.oldsnake.CustomDialogInfos.Dialogs.*
import java.util.*

@SuppressLint("Registered")
abstract class CustomDialogActivity : SubFullScreenActivity() {

    /**
     * Gösterilecek layout'un id'si
     */
    abstract val layoutId: Int

    private lateinit var dialogInfos: CustomDialogInfos
    private lateinit var toggleButtons: ArrayList<ToggleButton>

    /**
     * Sadece tek bir buton seçilebilir olacak ise buna true değeri atanmalı
     * @see showDialog
     * @see resetCheckings
     */
    var setOnlyOneButtonCheckable = false

    /**
     * Aktivite oluşturulduğunda olacaklar.
     * Not: Ekrandaki öğelere tıklandığında yapılacak eylemleri eklemeyi unutma.
     * @see bindEvents
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)

        bindEvents()
    }

    /**
     * (Layouttaki) Ekrandaki öğelere tıklandığında yapılacak eylemler.
     * Not: Diyalog ekranı açacak olan butonlara *onButtonClick* onClickListener olarak tanımlanmalı
     * @see onButtonClick
     */
    abstract fun bindEvents()


    /**
     * Tıklandığında animasyonla ayrılıp diyalog ekranı açma
     * @see CustomDialogInfos
     */
    fun onButtonClick(dialogInfos: CustomDialogInfos, toggleButtonView: View) {
        this.dialogInfos = dialogInfos

        toggleButtonView.isClickable = false // Birden fazla basılmasın diye

        toggleButtonView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out))

        Handler().postDelayed({
            showDialog(toggleButtonView)
        }, delayMillis)

    }

    private fun showDialog(toggleButtonView: View) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(dialogInfos.layoutId, findViewById(dialogInfos.rootOfLayoutId), true)

        alertDialogBuilder.setView(view)
        val alertDialog = alertDialogBuilder.create()

        alertDialog.setOnDismissListener {
            toggleButtonView.isClickable = true // Kapandığı zaman, tekrar basılabilir olmasını sağlıyoruz
        }

        view.findViewById<ImageButton>(R.id.ibCancel).setOnClickListener { alertDialog.dismiss() } // Çıkış butonuna eylemini atıyoruz. (Ortak öğe olduğu için dialogInfos'tan almadım) (almalı mıyım?)

        toggleButtons = getToggleButtons(view)
        fixToggleButtonsChecking()
        refreshToggleButtons()

        for (i in 0 until toggleButtons.size) {
            toggleButtons[i].setOnClickListener { v ->
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out_fast))

                v.isClickable = false // Birden fazla basılmasın diye

                Handler().postDelayed({
                    onToggleButtonClicked(v, alertDialog, i)

                    v.isClickable = true // İşlemler bittikten sonra basılabilir yapıyoruz.
                }, delayMillisShort)
            }
        }

        /**
         * Diyaloglara animasyon ekleyip, görünürlük verir.
         * Not: Dialog'un animasyonla belirmesi için style'a oluşturuduğumuz değişkeni çağırıyoruz.
         */
        with(alertDialog) {
            window.attributes.windowAnimations = R.style.FadeDialog
            show()
        }
    }

    /**
     * Oluşturulan view içindeki toggle butonları almak.
     * @see CustomDialogInfos.toggleButtonIds
     */
    private fun getToggleButtons(view: View): ArrayList<ToggleButton> {
        val toggleButtons = arrayListOf<ToggleButton>()

        dialogInfos.toggleButtonIds.forEach { resId ->
            toggleButtons.add(view.findViewById(resId))
        }

        return toggleButtons
    }

    /**
     * Daha önceden seçilmiş butonları bulup, seçili hale getirir.
     */
    private fun fixToggleButtonsChecking() {
        val modeIds: ArrayList<Int> = when (dialogInfos.dialogType) {
            CLASSIC_MODES -> SnakeEngineOld.gameModeIndexes
            MUSICS -> arrayListOf(SnakeEngineOld.musicModeIndex)
            SOUNDS -> arrayListOf(SnakeEngineOld.soundModeIndex)
            CONTROLS -> arrayListOf(SnakeEngineOld.controlModeIndex)
            THEMES -> arrayListOf(SnakeEngineOld.themeModeIndex)
        }

        modeIds.forEach { index ->
            toggleButtons[index].isChecked = true
        }
    }

    /**
     * Butonların hepsini yeniler. (Seçili olan varsa güncellenmesi gerekir.)
     */
    private fun refreshToggleButtons() {
        for (i in 0 until toggleButtons.size)
            refreshToggleButton(i)
    }

    /**
     * Butonları  yeniler. (Seçili olan varsa güncellenmesi gerekir.)
     */
    private fun refreshToggleButton(index: Int) {
        toggleButtons[index].background = getBackgroundDrawble(index)
        toggleButtons[index].setTextColor(getToggleButtonTextColorId(index))
    }

    private fun onToggleButtonClicked(rootView: View, alertDialog: AlertDialog, index: Int) {
        if (setOnlyOneButtonCheckable) { // Eğer tek buton seçilebilir ise diğerlerini sıfırlayacağız
            if (!toggleButtons[index].isChecked) // Eğer buton zaten seçili ise işlem yapılmayacak. İlk önce seçili hale gelir sonra onCLick çalışır. Yani seçili ise seçili değil konumuna gelecektir.
                return
            resetCheckings(index)
        }

        setMode(index)
        onAnyButtonClicked(rootView, toggleButtons[index], alertDialog)
        refreshToggleButton(index)
    }

    private fun getBackgroundDrawble(index: Int): Drawable {
        return when (toggleButtons[index].isChecked) {
            true -> getDrawable(dialogInfos.drawableCheckedId)
            false -> getDrawable(dialogInfos.drawableId)
        }
    }

    private fun getToggleButtonTextColorId(index: Int): Int {
        return when (toggleButtons[index].isChecked) {
            true -> ContextCompat.getColor(this, dialogInfos.colorCheckedId)
            false -> ContextCompat.getColor(this, dialogInfos.colorId)
        }
    }

    private fun setMode(mode: Int) {
        when (dialogInfos.dialogType) {
            CLASSIC_MODES -> SnakeEngineOld.setGameMode(mode)
            MUSICS -> SnakeEngineOld.setMusicMode(mode)
            SOUNDS -> SnakeEngineOld.setSoundsMode(mode)
            CONTROLS -> SnakeEngineOld.setControlMode(mode)
            THEMES -> SnakeEngineOld.setThemeMode(mode)
        }
    }

    /**
     * Her bir butona tıklanındığında olacak eylemler.
     * @see showDialog
     * @param rootView Üzerine basılınca dialog açtıran butonun view'i
     * @param toggleButton Diyalogtaki butonlar
     * @param alertDialog Diyalogun kendisi
     */
    open fun onAnyButtonClicked(rootView: View, toggleButton: ToggleButton, alertDialog: AlertDialog) {}

    /**
     * Seçili olma durumunu kaldırma
     * @param index Seçilme durumu kaldırımlayacak olan buton indeksi
     */
    private fun resetCheckings(index: Int) {
        for (i in 0 until toggleButtons.size) {
            if (i == index)
                continue

            toggleButtons[i].isChecked = false
        }

        refreshToggleButtons()
    }

}