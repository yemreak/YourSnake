package com.yemreak.yoursnake.oldsnake

import android.content.Context
import com.yemreak.yoursnake.R

class Theme(private var ctx: Context) {
    var colorBg: Int = 0
    var colorField: Int = 0
    var colorScore: Int = 0
    var colorSnake: Int = 0
    var colorSnakeEyes: Int = 0
    var colorFeed: Int = 0


    private fun create(resId: Int): Theme {
        val colors: IntArray = ctx.resources.getIntArray(resId)
        colorBg = colors[0]
        colorField = colors[1]
        colorScore = colors[2]
        colorSnake = colors[3]
        colorSnakeEyes = colors[4]
        colorFeed = colors[5]

        return this
    }

    companion object {
        /**
         * Teme moduna göre tema oluşturma
         * @param ctx Context
         * @param mode Tema modu
         * @return Tema
         */
        fun getTheme(ctx: Context, mode: SnakeEngineOld.ThemeModes): Theme {
            val resId = when(mode) {
                SnakeEngineOld.ThemeModes.DEFAULT -> R.array.theme_default
                SnakeEngineOld.ThemeModes.DARKNESS -> R.array.theme_darkness
                SnakeEngineOld.ThemeModes.LIGHT -> R.array.theme_light
                SnakeEngineOld.ThemeModes.AQUA -> R.array.theme_aqua
                SnakeEngineOld.ThemeModes.PURPLE -> R.array.theme_purple
                SnakeEngineOld.ThemeModes.HORROR_THEME -> R.array.theme_horror
            }

            return Theme(ctx).create(resId)
        }
    }
}