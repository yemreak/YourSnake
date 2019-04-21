package com.yemreak.yoursnake.newsnake.elements

import android.content.Context
import android.graphics.Canvas
import com.yemreak.yoursnake.R
import com.yemreak.yoursnake.newsnake.Camera

class Feed : BaseElement() {

    /**
     * Boyut oranı
     * @see draw
     */
    override var ratio = 0.25f

    fun spawn(snakes: ArrayList<Snake>, feeds: ArrayList<Feed>) {

        var intersect = true

        while (intersect) {
            // Kesişmenin olmadığını varsayıp başlıyoruz.
            intersect = false

            center.set(Camera.randomPoint) // Rastgele konum atama

            // Yılanlarla kesişiyorsa yeni koordinatlar atıyacağız
            snakes.forEach { snake ->
                if (intersects(snake)) {
                    intersect = true
                    return
                }
            }
            // Kesişim varsa diğerler kontrolleri yapmaya gerek yok.
            if (intersect)
                continue

            // Diğer yemlerle kesişiyorsa yeni koordinatlar atıyacağız
            feeds.forEach { feed ->
                if ((feed != this) && intersects(feed)) {
                    intersect = true
                    return
                }
            }
        }
    }

    /**
     * Sadece kameranın görüş açısında [Camera.canSee] iken çizim işlemini gerçekleştirir.
     *
     * @param context Palet'in kaynağı
     * @param canvas Palet
     */
    fun drawConditionally(context: Context, canvas: Canvas) {
        if (Camera.canSee(this)) {
            draw(context, canvas)
        }

    }

    /**
     * Palet'e çizme işlemi
     * @param context Palet'in kaynağı
     * @param canvas Palet
     */
    private fun draw(context: Context, canvas: Canvas) {
        drawBitmapTo(context, canvas, R.drawable.ic_snake_body, center, ratio)
    }
}