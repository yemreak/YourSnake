package com.yemreak.yoursnake.oldsnake

import android.graphics.*
import com.yemreak.yoursnake.R
import com.yemreak.yoursnake.oldsnake.Snake.Directions.*
import com.yemreak.yoursnake.oldsnake.Snake.Length.*

class Snake {

    /**
     * Yılanın Uzunluğu
     *
     * Not: Değişikliklerin aşağıdaki yerlere de uygulanması gerekmektedir;
     * @see R.layout.dialog_ex_settings android:progress sayısı ile aynı sayıda olmalılar. (6 ise progress = 5 çünkü [0,5])
     * @see R.string "Yılan Uzunluğu Metinleri"
     * @see setAllLengths Aynı sayıda olmalılar.
     */
    enum class Length {
        VERY_SHORT,
        SHORT,
        NORMAL,
        LONG,
        VERY_LONG,
        EXTREMELY_LONG;

        companion object {
            /**
             * İndeksle örnek almak
             */
            fun get(index: Int): Length {
                return when (index) {
                    0 -> VERY_SHORT
                    1 -> SHORT
                    3 -> LONG
                    4 -> VERY_LONG
                    5 -> EXTREMELY_LONG
                    else -> NORMAL // Farklı değerler ve 2 için normal gelsin
                }
            }

            /**
             * Örneğin indexini almak
             */
            fun getIndex(size: Length): Int {
                return when (size) {
                    VERY_SHORT -> 0
                    SHORT -> 1
                    NORMAL -> 2
                    LONG -> 3
                    VERY_LONG -> 4
                    EXTREMELY_LONG -> 5
                }
            }

            /**
             * İndekse göre metin id'si almak
             */
            fun getTextId(index: Int): Int {
                return when (index) {
                    0 -> R.string.txt_snakeLength_veryShort
                    1 -> R.string.txt_snakeLength_short
                    3 -> R.string.txt_snakeLength_long
                    4 -> R.string.txt_snakeLength_veryLong
                    5 -> R.string.txt_snakeLength_extremelyLong
                    else -> R.string.txt_block_normal // Farklı değerler ve 2 için normal gelsin
                }
            }

            /**
             * Örneğe göre metin id'si almak.
             */
            fun getTextId(size: Length): Int {
                return when (size) {
                    VERY_SHORT -> R.string.txt_snakeLength_veryShort
                    SHORT -> R.string.txt_snakeLength_short
                    LONG -> R.string.txt_snakeLength_long
                    VERY_LONG -> R.string.txt_snakeLength_veryLong
                    EXTREMELY_LONG -> R.string.txt_snakeLength_extremelyLong
                    else -> R.string.txt_block_normal // Farklı değerler ve 2 için normal gelsin
                }
            }
        }
    }

    /**
     * Yön belirteci classı
     */
    enum class Directions {
        LEFT, UP, RIGHT, DOWN
    }

    companion object {

        var lengthMode = NORMAL

        /**
         * Başlangıç yönü
         */
        var direction = RIGHT
            private set(value) {
                field = value
            }

        /**
         * Yeni başlangıç yönü
         * Hatırlatma: Yılanın yönü bir adımda birden fazla değiştirilmesin diye yapıldı. Sağ'a giden bir yılana *yukarı - sol* verileri verince (yukarı dönmeden sol verisi veriliyor) ölmüş sayılıyordu engellendi.
         * @see SnakeActivityWithButtons.onButtonClick
         * @see SnakeActivityWithSwipe.svFullGameFieldOnTouch
         * @see refreshDireciton
         */
        var newDirection = RIGHT
    }

    /**
     * Yılanın başının merkez noktaları
     */
    var centerHead: Point = Point()
        private set(value) {
            field = value
        }

    /**
     * Yılanın kuyruğunun merkez noktaları
     */
    val centerTails = ArrayList<Point>()

    /**
     * Yılanın varsayılan uzunluğu
     * Not: Modsuz haldeki uzunluktur.
     */
    var defaultLength = 5
        private set(value) {
            field = value
        }

    /**
     * Shrink game modundaki yılanın başlangıç uzunluğu
     * @see SnakeEngineOld.GameModes
     */
    var longLength = 100
        private set(value) {
            field = value
        }

    init {
        setAllLengths()
    }

    /**
     * Moda göre yılan uzunluğunun ayarlanması
     * @see Length
     */
    private fun setAllLengths() {
        defaultLength = when (lengthMode) {
            VERY_SHORT -> 1
            SHORT -> 3
            NORMAL -> 5
            LONG -> 15
            VERY_LONG -> 25
            EXTREMELY_LONG -> 50
        }

        longLength = when (lengthMode) {
            VERY_SHORT -> 25
            SHORT -> 50
            NORMAL -> 100
            LONG -> 150
            VERY_LONG -> 200
            EXTREMELY_LONG -> 250
        }
    }

    /**
     * Yılanı oyun alanında oluşturmak
     */
    fun create(gameModes: ArrayList<SnakeEngineOld.GameModes>) {
        // Yönü sıfırlıyoruz
        direction = RIGHT

        // Kuyruğu temizliyoruz
        centerTails.clear()

        // Başı merkezde oluşturuyoruz. *Kopyasını* atıyoruz.
        centerHead = Point(BlockField.centerPoint())

        // Her bir kuyruğu oluşturuyoruz.

        for (i in 1 until when {
            gameModes.contains(SnakeEngineOld.GameModes.SHRINK_SNAKE) -> longLength
            else -> defaultLength
        }) {
            centerTails.add(BlockField.point(centerHead, -i, 0))
        }
    }

    /**
     * Yılanın hareket etmesi
     */
    fun move(hasFrame: Boolean) {
        for (i in centerTails.lastIndex downTo 0) {
            if (i == 0) {
                centerTails[i] = Point(centerHead) // Not: Direk atama işlemi yaparsak adresi kopyalanır.
                continue
            }
            centerTails[i] = Point(centerTails[i - 1]) // Not: Direk atama işlemi yaparsak adresi kopyalanır.
        }

        refreshDireciton() // Yeni hareket için yönümüzü güncelliyoruz
        centerHead = BlockField.besidePoint(centerHead, hasFrame, direction)
    }

    /**
     * Yılanın yönünü yeni yön istediğine göre günceller.
     * @see newDirection
     */
    private fun refreshDireciton() {
        when (direction) {
            LEFT ->
                if (newDirection != RIGHT)
                    direction = newDirection
            UP ->
                if (newDirection != DOWN)
                    direction = newDirection
            RIGHT ->
                if (newDirection != LEFT)
                    direction = newDirection
            DOWN ->
                if (newDirection != UP)
                    direction = newDirection
        }
    }

    /**
     * Yılanın yem yemesi
     */
    fun isFeeding(feed: Point): Boolean {
        return feed.equals(centerHead.x, centerHead.y)
    }

    /**
     * Oyun moduna göre yılanın yem yeme durumunda yapılan işlemleri yapar.
     * @param gameModes Oyun Modları Listesi
     * @param feedType Yinelen yemin tipi
     */
    fun eatFeedActTo(gameModes: ArrayList<SnakeEngineOld.GameModes>, feedType: Feed.FeedTypes) {
        // Oyun moduna göre değişecek kısım
        if (gameModes.contains(SnakeEngineOld.GameModes.SHRINK_SNAKE)) {
            when (feedType) {
                Feed.FeedTypes.NORMAL -> shrinkDown()
                Feed.FeedTypes.DOUBLE_FEED -> shrinkDown(2)
                Feed.FeedTypes.X5_FEED -> shrinkDown(5)
                Feed.FeedTypes.X10_FEED -> shrinkDown(10)
                Feed.FeedTypes.X25_FEED -> shrinkDown(25)
            }
        } else {
            when (feedType) {
                Feed.FeedTypes.NORMAL -> growUp()
                Feed.FeedTypes.DOUBLE_FEED -> growUp(2)
                Feed.FeedTypes.X5_FEED -> growUp(5)
                Feed.FeedTypes.X10_FEED -> growUp(10)
                Feed.FeedTypes.X25_FEED -> growUp(25)
            }
        }
    }

    /**
     * Yılanın birden fazla büyümesi
     */
    private fun growUp(num: Int) {
        for (i in 0 until num)
            growUp()
    }

    /**
     * Yılanın büyümesi
     */
    private fun growUp() {
        when (centerTails.size) {
            0 -> centerTails.add(BlockField.behindPoint(centerHead, direction))
            else -> centerTails.add(centerTails.last())
        }
    }

    /**
     * Yılanın birden fazla küçülmesi
     */
    private fun shrinkDown(num: Int) {
        for (i in 0 until num)
            shrinkDown()
    }

    /**
     * Yılanın küçülmesi
     */
    private fun shrinkDown() {
        when (centerTails.size) {
            0 -> return
            else -> centerTails.remove(centerTails.last())
        }
    }

    /**
     * Yılanın kuyruğunun ekrana çizilmesi
     */
    fun drawTails(canvas: Canvas, paint: Paint) {
        centerTails.forEach { tail ->
            canvas.drawRect(tail, paint)
        }
    }

    /**
     * Merkez noktalarına göre ekrana dörtgen çizme
     */
    private fun Canvas.drawRect(center: Point, paint: Paint) {
        this.drawRect(BlockField.getBlock(center), paint)
    }

    /**
     * Yılanın başının ekrana çizilmesi
     */
    fun drawHead(canvas: Canvas, paint: Paint) {
        canvas.drawRect(centerHead, paint)
    }

    /**
     * Yılanın gözlerinin *yöne uygun şekilde* ekrana çizilmesi
     */
    fun drawEyes(canvas: Canvas, paint: Paint) {
        val angle: Float = when (direction) {
            LEFT -> 180f
            UP -> 270f
            RIGHT -> 0f
            DOWN -> 90f
        }

        canvas.save()
        canvas.rotate(angle, BlockField.getBlock(centerHead).exactCenterX(), BlockField.getBlock(centerHead).exactCenterY())

        val leftEyes = RectF(
                BlockField.getBlock(centerHead).right - 3 * BlockField.blockSize / 7f,
                BlockField.getBlock(centerHead).top + BlockField.blockSize / 7f,
                BlockField.getBlock(centerHead).right - BlockField.blockSize / 7f,
                BlockField.getBlock(centerHead).top + 3 * BlockField.blockSize / 7f
        )

        val rightEyes = RectF(
                BlockField.getBlock(centerHead).right - 3 * BlockField.blockSize / 7f,
                BlockField.getBlock(centerHead).bottom - 3 * BlockField.blockSize / 7f,
                BlockField.getBlock(centerHead).right - BlockField.blockSize / 7f,
                BlockField.getBlock(centerHead).bottom - BlockField.blockSize / 7f
        )

        canvas.drawRect(leftEyes, paint)
        canvas.drawRect(rightEyes, paint)

        canvas.restore()
    }

    /**
     * Çerçeve durumuna göre yılanın çarpışma durumunu bulma
     * @param hasFrame Çerçeve varsa true, yoksa false
     * @return Çarpışma varsa true, yoksa false
     */
    fun isCrashed(hasFrame: Boolean): Boolean {
        return when (hasFrame) {
            true -> when {
                centerHead.x < 0 -> true
                centerHead.x >= BlockField.blockNum.x -> true
                centerHead.y < 0 -> true
                centerHead.y >= BlockField.blockNum.y -> true
                isTail() -> true
                else -> false
            }
            false -> when {
                isTail() -> true
                else -> false
            }

        }
    }

    /**
     * Yılan kuyruğuna mı deyiyor kontrolü
     */
    private fun isTail(): Boolean {
        return centerTails.contains(centerHead)
    }


}