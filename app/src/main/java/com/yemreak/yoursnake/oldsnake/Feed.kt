package com.yemreak.yoursnake.oldsnake

import android.content.Context
import android.graphics.*
import com.yemreak.yoursnake.R
import java.util.*


class Feed() {
    /**
     * Yem merkez koordinatları
     */
    var center = Point()

    var feedType = FeedTypes.NORMAL
        private set(value) {
            field = value
        }


    /**
     * Yem tipleri
     * Not: Yeni yem eklendiğinde alttaki metodları değiştirmen lazım;
     * @see draw
     * @see Snake.eatFeedActTo
     * @see SnakeEngineOld.incScore
     */
    enum class FeedTypes {
        NORMAL,
        DOUBLE_FEED,
        X5_FEED,
        X10_FEED,
        X25_FEED;

        companion object {
            fun random(): FeedTypes {
                return when (Random().nextInt(5)) {
                    1 -> DOUBLE_FEED
                    2 -> X5_FEED
                    3 -> X10_FEED
                    4 -> X25_FEED
                    else -> NORMAL
                }
            }
        }
    }


    constructor(feedType: FeedTypes) : this() {
        this.feedType = feedType
    }

    /**
     * Oyun moduna göre yem oluşturma
     * @param snake Yılan
     * @param feeds Ekrandaki tüm yemler
     * @param gameModes Oyun modları listesi
     */
    fun spawnActTo(snake: Snake, feeds: ArrayList<Feed>, gameModes: ArrayList<SnakeEngineOld.GameModes>) {
        // Oyun moduna göre şekilleniyor
        when {
            gameModes.contains(SnakeEngineOld.GameModes.MORE_FEED_TYPES) -> {
                feedType = FeedTypes.random()
                spawn(snake, feeds, feedType)
            }
            else -> spawn(snake, feeds)
        }
    }

    /**
     * İstenilen türde yemlerin oluşturulması
     * @param snake Yılan
     * @param feeds Ekrandaki tüm yemler
     */
    private fun spawn(snake: Snake, feeds: ArrayList<Feed>, type: FeedTypes) {
        feedType = type
        spawn(snake, feeds)
    }


    /**
     * Yemlerin *yılanla ve diğer yemlerle kesişmeyecek şekilde* rastgele oluşturulması
     * @param snake Yılan
     * @param feeds Ekrandaki tüm yemler
     */
    private fun spawn(snake: Snake, feeds: ArrayList<Feed>) {
        // Eğer yılan tüm ekranı kaplamıyorsa.

        var loop = true

        while (loop) {
            center = Point(BlockField.randomCenter())

            if (center.equals(snake.centerHead.x, snake.centerHead.y)) // Yılanın kafasında yem çıkmasına izin vermiyoruz.
                continue

            loop = false

            for (i in 0 until snake.centerTails.size) // Kuyrukla keşismesin.
                if (snake.centerTails[i].equals(center.x, center.y)) {
                    loop = true
                    break
                }

            for (i in 0 until feeds.size) // Diğer yemlerle keşismesin. (Kendisini atlıyoruz.)
                if ((feeds[i] != this) && feeds[i].center.equals(center.x, center.y)) {
                    loop = true
                    break
                }
        }
    }

    /**
     * Yemi ekrana çizme
     * Not: Yem tipine göre şekillenir
     * @see feedType
     */
    fun draw(context: Context, canvas: Canvas, paint: Paint) {
        when (feedType) {
            FeedTypes.NORMAL -> canvas.drawRect(center, paint)

            FeedTypes.DOUBLE_FEED -> {
                canvas.drawOval(center, paint) //
            }

            FeedTypes.X5_FEED -> {
                drawBitmapTo(context, canvas, paint, R.drawable.ic_x5_feed)
            }

            FeedTypes.X10_FEED -> {
                drawBitmapTo(context, canvas, paint, R.drawable.ic_x10_feed)
            }

            FeedTypes.X25_FEED -> {
                extDrawBitmapTo(context, canvas, paint, R.drawable.ic_x25_feed)
            }
        }
    }

    private fun Canvas.drawRect(center: Point, paint: Paint) {
        this.drawRect(BlockField.getBlock(center), paint)
    }

    private fun Canvas.drawOval(center: Point, paint: Paint) {
        this.drawOval(android.graphics.RectF(BlockField.getBlock(center)), paint)
    }

    private fun drawBitmapTo(context: Context, canvas: Canvas, paint: Paint, resId: Int) {
        val bitmap = BlockField.bitmapBlock(context.resources, resId)
        paint.colorFilter = PorterDuffColorFilter(paint.color, PorterDuff.Mode.SRC_IN) // Bitmapi hangi renkte çizeceksek filtre ayarlıyoruz.
        canvas.drawBitmap(
                bitmap,
                BlockField.getBlock(center).left.toFloat(),
                BlockField.getBlock(center).top.toFloat(),
                paint
        )
    }

    private fun extDrawBitmapTo(context: Context, canvas: Canvas, paint: Paint, resId: Int) {
        val bitmap = BlockField.bitmapBlock(context.resources, resId)
        paint.colorFilter = PorterDuffColorFilter(paint.color, PorterDuff.Mode.SRC_IN) // Bitmapi hangi renkte çizeceksek filtre ayarlıyoruz.
        canvas.drawBitmap(
                bitmap,
                BlockField.getBlock(center).left.toFloat(),
                BlockField.getBlock(center).top.toFloat(),
                paint
        )
    }


}