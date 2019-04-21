package com.yemreak.yoursnake.newsnake.elements

import android.content.Context
import android.graphics.*
import com.yemreak.yoursnake.R
import com.yemreak.yoursnake.newsnake.Camera

class Snake : BaseElement() {

    /**
     * Boyut oranı
     * @see draw
     */
    override var ratio = 1.0f

    /**
     * Yılanın kuyruklarının merkez konumu
     */
    val centerTails = arrayListOf<PointF>()

    /**
     * Yılanın uzunluğu
     * Not: Her kuyruk 10 can'ı temsil edeceğinden 10 kuyruğu olacak. (Kafa can olarak sayılmayacak.)
     */
    val length = 25

    /**
     * Joystick ile alınan yeni açı değeri
     * @see com.yemreak.yoursnake.newsnake.SnakeEngine.onJoystickChanged
     */
    var nextDegree = 0.0

    /**
     * Yılanın tek seferde yapabileceği en fazla dönüş açısı
     */
    private val maxDegreeOffset = 15

    /**
     * Joystick ile alınan yeni hız değeri
     * @see com.yemreak.yoursnake.newsnake.SnakeEngine.onJoystickChanged
     */
    var nextSpeed = 0f

    /**
     * Yılanın merkezinin yapmış olduğu toplam yer değiştime.
     * Genelde kamerayı ortalamak için kullanılır
     */
    var totalOffset = PointF()

    fun create(center: PointF = Camera.center) {
        // Merkezde yılanı oluşturuyoruz.
        this.center.set(center)

        // Kuyruk verilerini temizliyoruz.
        centerTails.clear()

        for (i in 0 until length) {
            centerTails.add(this.center.clone())
        }
    }

    fun move() { // Prev speed, speed 'i burada yap (?)
        for (i in centerTails.lastIndex downTo 0) {
            if (i == 0) {
                centerTails[i].set(center) // Head'ın hızı kadar ona doğru gitmeli
                continue
            }
            centerTails[i].set(centerTails[i - 1])
        }

        moveConditionally()
    }

    /**
     * Koşullu olarak parçacık hareketi
     * @see nextDegree
     * @see nextSpeed
     *
     * @param nextSpeed Bir sonraki hız (eğer değer verilmezse varsayılanı alır)
     * @param nextDegree Bir sonraki açı (eğer değer verilmezse varsayılanı alır)
     */
    private fun moveConditionally(nextSpeed: Float = this.nextSpeed, nextDegree: Double = this.nextDegree) {
        speed = nextSpeed

        degree = when (Math.abs(nextDegree - degree) < maxDegreeOffset) {
            true -> nextDegree
            false -> when ((nextDegree - degree + 360) % 360 > 180) {
                true -> degree - maxDegreeOffset // Sola dönüş
                false -> degree + maxDegreeOffset // Sağa dönüş
            }
        }

        val offset = PointF()
        offset.x = (Math.cos(Math.toRadians(degree)) * speed).toFloat()
        offset.y = (Math.sin(Math.toRadians(degree)) * speed).toFloat()

        totalOffset += offset
        center += offset
    }

    fun isFeeding(feed: Feed): Boolean {
        return intersects(feed)
    }

    fun draw(context: Context, canvas: Canvas) {
        drawTails(context, canvas)
        drawHead(context, canvas)
    }

    private fun drawHead(context: Context, canvas: Canvas) {
        drawBitmapTo(context, canvas, R.drawable.ic_snake_head, center, 1f)
    }

    /**
     * Yılanın kuyruğunu çizme.
     * Not: Terstten çiziyoruz ki en son kuyruk en altta kalsın.
     */
    private fun drawTails(context: Context, canvas: Canvas) {
        for (i in centerTails.lastIndex downTo 0)
            drawBitmapTo(context, canvas, R.drawable.ic_snake_body, centerTails[i], ratio)
    }

}