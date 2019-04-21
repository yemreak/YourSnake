package com.yemreak.yoursnake.newsnake

import android.graphics.*
import com.yemreak.yoursnake.newsnake.elements.BaseElement
import java.util.*

abstract class Camera {

    companion object {

        /**
         * Maksimum oyun alanı. Oyundaki objelerin bu alan dışına çıkması engellenir.
         */
        private val max = PointF(3000f, 3000f)

        /**
         * Görüş alanı dörtgeni özellikleri
         * [max] değerler içerisinde görüş olacağından değer atama işlemi koşullu gerçekleşir.
         * @see max
         */
        var vision = RectF()
            private set(value) {
                if (value.left >= 0 && value.left <= max.x - field.width()) {
                    field.left = value.left
                    field.right = value.right
                }


                if (value.top >= 0 && value.top <= max.x - field.height()) {
                    field.top = value.top
                    field.bottom = value.bottom
                }

            }

        /**
         * Oyun alanının merkez noktası
         */
        val center: PointF
            get() {
                return with(this.vision) {
                    PointF(centerX(), centerY())
                }
            }

        /**
         * Oyun alanında rastgele nokta
         */
        val randomPoint: PointF
            get() {
                return with(this.vision) {
                    PointF(
                            Random().nextFloat() * max.x,
                            Random().nextFloat() * max.y
                    )
                }
            }

        /**
         * Kameranın yer değiştirme değerleri
         * Yılan sağa 1 birim giderse, bütün veriler 1 birim sola çizilmelidir. Bu sebeple [BaseElement.drawBitmapTo]'da bunu çıkarıyoruz.
         *
         */
        var offset: PointF = PointF()

        /**
         * Oyun alanını hazırlamak için kullanılır.
         * [SnakeEngine.surfaceCreated] içerisinden kullanılmak için tasarlanmıştır.
         */
        fun initialize(field: Rect) {
            this.vision = RectF(field)
        }

        /**
         * Gösterilen oyun olanını güncelleme
         */
        fun follow(baseElement: BaseElement) {
            vision = BaseElement.getRectF(baseElement.center, vision.width(), vision.height())

            offset.x = vision.left // Başta 0 olduğundan, sapma bunun değeri kadardır.
            offset.y = vision.top // Başta 0 olduğundan, sapma bunun değeri kadardır.
        }

        fun canSee(baseElement: BaseElement): Boolean {
            return RectF.intersects(vision, baseElement.getRectF(baseElement.center))
        }


    }
}