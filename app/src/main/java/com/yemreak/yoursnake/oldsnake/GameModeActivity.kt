package com.yemreak.yoursnake.oldsnake

import android.view.View
import com.yemreak.yoursnake.R
import kotlinx.android.synthetic.main.activity_game_mode.*
import com.yemreak.yoursnake.oldsnake.CustomDialogInfos.Dialogs.*


class GameModeActivity : CustomDialogActivity() {

    override val layoutId: Int
        get() = R.layout.activity_game_mode


    override fun bindEvents() {
        ibCancel.setOnClickListener { onBackPressed() }
        btnClassicModes.setOnClickListener { v: View -> onButtonClick(CustomDialogInfos(CLASSIC_MODES), v) }
    }

}
