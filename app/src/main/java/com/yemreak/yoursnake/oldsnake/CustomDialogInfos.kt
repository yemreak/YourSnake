package com.yemreak.yoursnake.oldsnake

import com.yemreak.yoursnake.R
import com.yemreak.yoursnake.oldsnake.CustomDialogInfos.Dialogs.*

class CustomDialogInfos(val dialogType: Dialogs) {

    enum class Dialogs {
        CLASSIC_MODES,
        MUSICS,
        CONTROLS,
        SOUNDS,
        THEMES
    }

    val layoutId: Int
        get() {
            return when (dialogType) {
                CLASSIC_MODES -> R.layout.dialog_classic_modes
                MUSICS -> R.layout.dialog_musics
                CONTROLS -> R.layout.dialog_controls
                SOUNDS -> R.layout.dialog_sounds
                THEMES -> R.layout.dialog_themes
            }
        }

    val rootOfLayoutId: Int
        get() {
            return when (dialogType) {
                CLASSIC_MODES -> R.id.rlClassicModes
                MUSICS -> R.id.rlMusicsDialog
                CONTROLS -> R.id.rlSoundsDialog
                SOUNDS -> R.id.rlControlsDialog
                THEMES -> R.id.rlThemeDialog
            }
        }

    val toggleButtonIds: ArrayList<Int>
        get() {
            return when (dialogType) {
                CLASSIC_MODES -> arrayListOf( // Klasik Modlar
                        R.id.tbFrame,
                        R.id.tbMoreFeedTypes,
                        R.id.tbMoreFeed,
                        R.id.tbShrinkSnake
                )
                MUSICS -> arrayListOf( // Musics
                        R.id.tbNoMusic,
                        R.id.tbFerambie,
                        R.id.tbHorrorMusic
                )

                SOUNDS -> arrayListOf( // Sounds
                        R.id.tbNoSounds,
                        R.id.tbClassicalSounds,
                        R.id.tbRetroSounds,
                        R.id.tbGunSounds,
                        R.id.tbHorrorSounds
                )

                CONTROLS -> arrayListOf( // Controls
                        R.id.tbButtons,
                        R.id.ivSwipe
                )

                THEMES -> arrayListOf( // Themes
                        R.id.tbDefault,
                        R.id.tbDarkness,
                        R.id.tbLight,
                        R.id.tbAqua,
                        R.id.tbPurple,
                        R.id.tbHorrorTheme
                )
            }
        }

    val drawableId: Int
        get() {
            return when (dialogType) {
                CLASSIC_MODES -> R.drawable.bg_button_blue
                MUSICS, CONTROLS, SOUNDS, THEMES -> R.drawable.bg_button_purple
            }
        }

    val drawableCheckedId: Int
        get() {
            return when (dialogType) {
                CLASSIC_MODES -> R.drawable.bg_button_blue_checked
                MUSICS, CONTROLS, SOUNDS, THEMES -> R.drawable.bg_button_purple_checked
            }
        }

    val colorId: Int
        get() {
            return when (dialogType) {
                CLASSIC_MODES -> R.color.txt_blue_button
                MUSICS, CONTROLS, SOUNDS, THEMES -> R.color.txt_purple_button
            }
        }

    val colorCheckedId: Int
        get() {
            return when (dialogType) {
                CLASSIC_MODES -> R.color.txt_blue_button_checked
                MUSICS, CONTROLS, SOUNDS, THEMES -> R.color.txt_purple_button_checked
            }
        }

}