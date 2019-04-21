package com.yemreak.yoursnake.oldsnake

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import android.util.Log
import com.yemreak.yoursnake.R
import java.util.*

abstract class BlockField {

    /**
     * Ekranın büyklüğü
     *
     * Not: Değişikliklerin aşağıdaki yerlere de uygulanması gerekmektedir;
     * @see R.layout.dialog_ex_settings android:progress sayısı ile aynı sayıda olmalılar. (6 ise progress = 5 çünkü [0,5])
     * @see R.string "Oyun Büyüklüğü Metinleri"
     * @see setBlockSize Aynı sayıda olmalılar.
     */
    enum class Sizes {
        VERY_SMALL,
        SMALL,
        NORMAL,
        LARGE,
        VERY_LARGE,
        EXTREMELY_LARGE;

        companion object {
            /**
             * İndeksle örnek almak
             */
            fun get(index: Int): Sizes {
                return when (index) {
                    0 -> VERY_SMALL
                    1 -> SMALL
                    3 -> LARGE
                    4 -> VERY_LARGE
                    5 -> EXTREMELY_LARGE
                    else -> NORMAL // Farklı değerler ve 2 için normal gelsin
                }
            }

            /**
             * Örneğin indexini almak
             */
            fun getIndex(size: Sizes): Int {
                return when (size) {
                    VERY_SMALL -> 0
                    SMALL -> 1
                    NORMAL -> 2
                    LARGE -> 3
                    VERY_LARGE -> 4
                    EXTREMELY_LARGE -> 5
                }
            }

            /**
             * İndekse göre metin id'si almak
             */
            fun getTextId(index: Int): Int {
                return when (index) {
                    0 -> R.string.txt_block_verySmall
                    1 -> R.string.txt_block_small
                    3 -> R.string.txt_block_large
                    4 -> R.string.txt_block_veryLarge
                    5 -> R.string.txt_block_extremelyLarge
                    else -> R.string.txt_block_normal // Farklı değerler ve 2 için normal gelsin
                }
            }

            /**
             * Örneğe göre metin id'si almak.
             */
            fun getTextId(size: Sizes): Int {
                return when (size) {
                    VERY_SMALL -> R.string.txt_block_verySmall
                    SMALL -> R.string.txt_block_small
                    LARGE -> R.string.txt_block_large
                    VERY_LARGE -> R.string.txt_block_veryLarge
                    EXTREMELY_LARGE -> R.string.txt_block_extremelyLarge
                    else -> R.string.txt_block_normal // Farklı değerler ve 2 için normal gelsin
                }
            }
        }
    }

    companion object {

        var blockSizeMode = Sizes.NORMAL

        /**
         * Ekrandaki block geniliği
         * Not: Dışarıdan değiştirmek için, modu değiştirin.
         * @see blockSizeMode
         *
         */
        var blockSize: Int = 0
            private set(value) {
                field = value
            }

        /**
         * Çerçevelerin ve boşluğun çıkartıldığı yüzeyin adresi
         * Not: Yüzey oluşturulup, çerçeve çıkarılınca değerini alır.
         * @see SnakeEngineOld.surfaceCreated
         */
        var field = Rect()
            private set(value) {
                field = value
            }

        /**
         * Çerçevenin dörtgensel koordinatları
         * Not: Eğer çerçeve yoksa eşdeğeri 0 'dır.
         */
        var frame = Rect()
            private set(value) {
                field = value
            }

        /**
         * Çerçevenin noktasal büyüklüğü
         * Not: Çerçeve yoksa sıfırlanması gerekir. Aksi halde işleyiş yanlış olacaktır.
         *
         * Kullanıldığı kritik yerler;
         * @see getBlock Çerçeveye göre blok verisi verir.
         * @see initVariables Eğer çerçeve yoksa sıfırlanma işlemi burada yapılmaılıdır.
         */
        var frameSize = Point()
            private set(value) {
                field = value
            }

        /**
         * Kenar boşluklarının noktasal uzunluğu (toplam boşluk *değildir*.)
         */
        var gap: Point = Point()
            private set(value) {
                field = value
            }

        /**
         * Ekrandaki blok sayısı
         */
        var blockNum: Point = Point(0, 0)
            private set(value) {
                field = value
            }

        const val FRAME_POINT_SIZE = 1

        /**
         * Sık kullanılan değerlerin oluşturulması
         * Not: Surface tanımlanmadan kullanılmamalı
         * @see SnakeEngineOld.surfaceCreated
         */
        fun initVariables(surfaceField: Rect, hasFrame: Boolean) {
            setBlockSize() // Blok büyüklüğünü oluşturuyoruz

            if (surfaceField.right != 0 && surfaceField.bottom != 0) { // FRAME_POINT_SIZE kadar çerçeveye gidiyor. (Tam bölünmesi lazım)
                gap.x = (surfaceField.right % blockSize) / 2
                gap.y = (surfaceField.bottom % blockSize) / 2

                field = Rect(surfaceField)
                field.inset(gap.x, gap.y)

                if (hasFrame) {
                    frame = Rect(field)
                    frameSize = Point((FRAME_POINT_SIZE * blockSize), (FRAME_POINT_SIZE * blockSize))
                    field.inset(frameSize.x, frameSize.y)
                } else { // Sıfırlanmazsa bazı metodlar düzgün çalışmayacaktır.
                    frame = Rect()
                    frameSize = Point(0, 0)
                }

                blockNum.x = numBlockOf(field.width())
                blockNum.y = numBlockOf(field.height())


            } else {
                Log.e("Oyun alanı mevcut değil", "Lütfen oyun alanının oluşturulduğundan emin ol (BlockField.initVariables)")
            }
        }

        /**
         * Moda göre block büyüklüğü ayarlanması
         * @see Sizes
         */
        private fun setBlockSize() {
            blockSize = when (blockSizeMode) {
                Sizes.VERY_SMALL -> 20
                Sizes.SMALL -> 30
                Sizes.NORMAL -> 40
                Sizes.LARGE -> 60
                Sizes.VERY_LARGE -> 80
                Sizes.EXTREMELY_LARGE -> 100
            }
        }

        /**
         * Verilen değerin blok sayısı karşılığı
         */
        fun numBlockOf(x: Int): Int {
            return x / blockSize
        }

        /**
         * Ortadaki noktayı döndürür,
         * Not: ortası yoksa *soldakini* döndürür.
         */
        fun centerPoint(): Point {
            return Point(
                    blockNum.x / 2 + blockNum.x % 2 - 1,
                    blockNum.y / 2 + blockNum.y % 2 - 1
            )

        }

        /**
         * Ekrana göre kaydırılmış blok döndürür.
         * Not: Eğer en sağda iken bir sağını istiyorsan, ekran *dışında* nokta değeri verir.
         */
        fun point(point: Point, offsetX: Int, offsetY: Int): Point {
            return Point(
                    point.x + offsetX,
                    point.y + offsetY
            )
        }

        /**
         * ALınan dörtgenin 1 blok yanındaki dörtgeni döndürür. Eğer ekran dışında olursa
         * ters doğrultudan verir. (mod işlemi gibi, en sağa gitmek en soldan çıkmaktır.)
         * @param direction Yön bilgisi
         * @param center Dörtgenin merkez noktası
         */
        fun besidePoint(center: Point, hasFrame: Boolean, direction: Snake.Directions): Point {
            return when (direction) {
                Snake.Directions.UP -> point(center, hasFrame, 0, -1)
                Snake.Directions.DOWN -> point(center, hasFrame, 0, 1)
                Snake.Directions.RIGHT -> point(center, hasFrame, 1, 0)
                Snake.Directions.LEFT -> point(center, hasFrame, -1, 0)
            }
        }

        /**
         * Ekrana göre kaydırılmış blok döndürür.
         * Not: Eğer en sağda iken bir sağını istiyorsan, soldan verir. (Mod işlemi)
         * Not: Negatif (-) olma durumun kaldırmak için mod değerini ekliyoruz.
         */
        fun point(point: Point, hasFrame: Boolean, offsetX: Int, offsetY: Int): Point {
            return when (hasFrame) {
                true -> Point(
                        point.x + offsetX,
                        point.y + offsetY
                )
                false -> Point(
                        (point.x + offsetX + blockNum.x) % blockNum.x,
                        (point.y + offsetY + blockNum.y) % blockNum.y
                )
            }
        }

        /**
         * ALınan dörtgenin 1 blok arkasındaki dörtgeni döndürür
         * @param direction Yön bilgisi
         * @param center Dörtgenin merkez noktası
         */
        fun behindPoint(center: Point, direction: Snake.Directions): Point {
            return when (direction) {
                Snake.Directions.UP -> point(center, 0, 1)
                Snake.Directions.DOWN -> point(center, 0, -1)
                Snake.Directions.RIGHT -> point(center, -1, 0)
                Snake.Directions.LEFT -> point(center, 1, 0)
            }
        }

        /**
         * ALınan dörtgenin 1 blok arkasındaki (arkası yoksa en öndeki) dörtgeni döndürür
         * @param center Dörtgenin merkez noktası
         * @param hasFrame Çerçeve varsa true, yoksa false
         * @param direction Yön bilgisi
         */
        fun behindPoint(center: Point, hasFrame: Boolean, direction: Snake.Directions): Point {
            return when (direction) {
                Snake.Directions.UP -> point(center, hasFrame, 0, 1)
                Snake.Directions.DOWN -> point(center, hasFrame, 0, -1)
                Snake.Directions.RIGHT -> point(center, hasFrame, -1, 0)
                Snake.Directions.LEFT -> point(center, hasFrame, 1, 0)
            }
        }

        /**
         * Merkez verileri verilen bloğu oluşturma
         * @param center Merkez koordinatları
         * @return Merkezi verilen blok
         *
         * Not: Çerçeve yok iken çalıştırıldığında "frameSize" 0 olmalıdır.
         * @see frameSize
         */
        fun getBlock(center: Point): Rect {
            return Rect( // Px = x.s + s / 2
                    center.x * blockSize + gap.x + frameSize.x,
                    center.y * blockSize + gap.y + frameSize.y,
                    (center.x + 1) * blockSize + gap.x + frameSize.x,
                    (center.y + 1) * blockSize + gap.y + frameSize.y
            )
        }

        /**
         * Oyun alanı içerisinden rastgele merkez noktası döndürür.
         * @return Rastgele merkez noktası
         */
        fun randomCenter(): Point {
            return Point(
                    Random().nextInt(blockNum.x),
                    Random().nextInt(blockNum.y)
            )
        }

        fun bitmapBlock(res: Resources, resId: Int): Bitmap {
            return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, resId), blockSize, blockSize, true) // Resim kaynağını bitmap'e dönüştürüyoruz, boyutlandırıp ortalıyoruz.

        }
    }
}