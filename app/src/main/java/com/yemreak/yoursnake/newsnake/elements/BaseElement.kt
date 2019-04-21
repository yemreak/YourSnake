package com.yemreak.yoursnake.newsnake.elements

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.icu.lang.UCharacter
import com.yemreak.yoursnake.newsnake.Camera


abstract class BaseElement {

    companion object {
        /**
         * Oyundaki objelerin genel büyüklüğü
         */
        const val radius = 100f

        /**
         * Verilen merkez noktasına ve blok genişliğine göre dörtgen oluşturur.
         * @see radius
         *
         * @param center Dörtgenin merkez noktaları
         * @param width Dörtgenin uzunluğu
         * @param height Dörtgenin yüksekliği
         * @return Merkezi verilen dörtgen
         */
        fun getRectF(center: PointF, width: Float, height: Float): RectF {
            return RectF(
                    center.x - width / 2,
                    center.y - height / 2,
                    center.x + width / 2,
                    center.y + height / 2
            )
        }
    }

    /**
     * Merkez noktaları bilgisi
     */
    val center = PointF()

    /**
     * Hız değeri
     */
    var speed = 0f

    /**
     * Merkezin yatay eksen ile yaptığı açının derecesi [0 - 360)
     */
    var degree = 0.0
        set(value) {
            field = (value + 360) % 360
        }

    /**
     * Genel obje boyutuna göre boyut oranı
     * Genelde çizimlerde ölçekleme için kullanılır.
     */
    abstract val ratio: Float

    /**
     * Noktanın eşdeğerini döndürür.
     */
    fun PointF.clone(): PointF {
        return PointF(x, y)
    }

    operator fun PointF.plusAssign(pointF: PointF) {
        x += pointF.x
        y += pointF.y
    }

    /**
     * Verilen merkez noktasına ve blok genişliğine göre dörtgen oluşturur.
     * @see radius
     *
     * @param center Dörtgenin merkez noktaları
     * @return Merkezi verilen dörtgen
     */
    fun getRectF(center: PointF): RectF {
        return RectF(
                center.x - radius / 2,
                center.y - radius / 2,
                center.x + radius / 2,
                center.y + radius / 2
        )
    }

    /**
     * Verilen eleman ile kesişme bilgisini hesaplar
     * @param baseElement Eleman
     * @return Kesişme varsa 'true'
     */
    fun intersects(baseElement: BaseElement): Boolean {
        return RectF.intersects(getRectF(center), getRectF(baseElement.center))
    }

    /**
     * Verilen [canvas]'a [pointF] noktası verilen [resId]'yi [ratio] kat büyüklükte çizmek.
     */
    fun drawBitmapTo(context: Context, canvas: Canvas, resId: Int, pointF: PointF = Camera.center, ratio: Float = 1f) {
        val bitmap = bitmapBlock(context.resources, resId, (radius * ratio).toInt())
        val paint = Paint()

        //paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OVER) // Çizimi en üste yapıyoruz.
        canvas.drawBitmap(
                bitmap,
                getRectF(pointF).left - Camera.offset.x,
                getRectF(pointF).top - Camera.offset.y,
                paint
        )
    }

    /**
     * Verilen [resId]'yi istenen [size]'da bitmap objesine çevirir.
     */
    private fun bitmapBlock(res: Resources, resId: Int, size: Int): Bitmap {
        return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, resId), size, size, true) // Resim kaynağını bitmap'e dönüştürüyoruz, boyutlandırıp ortalıyoruz.
    }

}