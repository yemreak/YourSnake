package com.yemreak.yoursnake.newsnake.controller

import android.graphics.*
import android.view.MotionEvent

class JoystickController(private val joystickCallBack: JoystickListener) {

    /**
     * Joystick merkez noktaları
     * [onTouch]'ın içerisinden [createJoystick] ile noktaların değerleri oluşturulur.
     */
    private val jsCenter = PointF()

    /**
     * Joystick yarıçapı
     */
    private val sizeJS = 108f

    /**
     * Joystick işaretçisinin yarı çapı
     */
    private val sizeJSPointer = 72f

    /**
     * Joystick işaretçisinin merken noktaları
     * [onTouch]'ın içerisinden [setJoyStickPointer] ile noktaların değerleri oluşturulur.
     */
    private val jsPointerCenter = PointF()

    /**
     * Joystick'in görünürlük kontrolü için kullanılır.
     * [createJoystick] ile görünür hale gelir ve [draw] ile ekrana çizilir.
     */
    private var showJoystick = false

    /**
     * Joystick işaretçisinin merkezle yaptığı açı
     */
    private var degree: Double = 0.0

    /**
     * Joystick işaretçisinin merkeze olan uzaklık oranı
     * @see setJoyStickPointer
     */
    private var ratio = 0.0


    fun onTouch(e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                showJoystick = true
                createJoystick(e.x, e.y)
                joystickCallBack.onJoystickCreated(jsCenter, jsPointerCenter, degree, ratio)
            }
            MotionEvent.ACTION_MOVE -> {
                setJoyStickPointer(e.x, e.y)
                joystickCallBack.onJoystickChanged(jsCenter, jsPointerCenter, degree, ratio)
            }
            MotionEvent.ACTION_UP -> {
                removeJoystick()
                joystickCallBack.onJoystickReleased(jsCenter, jsPointerCenter, degree, ratio)
            }
        }

        return true
    }

    interface JoystickListener {
        /**
         * Joystick oluşturulduğunda çalışan metod
         *
         * @param center Joystick Merkez Noktaları
         * @param pointerCenter Joystick İşaretçisi Merkez Noktaları
         * @param degree Joystick işaretçisi ile merkezinin arasındaki açının derecesi [0 - 360)
         * @param ratio Joystick işaretçisinin merkeze uzaklığının joystick yarıçapına oranı [0 - 1]
         *
         */
        fun onJoystickCreated(center: PointF, pointerCenter: PointF, degree: Double, ratio: Double)

        /**
         * Joystick işaretçisi hareket ettikçe çalışan metod
         *
         * @param center Joystick Merkez Noktaları
         * @param pointerCenter Joystick İşaretçisi Merkez Noktaları
         * @param degree Joystick işaretçisi ile merkezinin arasındaki açının derecesi [0 - 360)
         * @param ratio Joystick işaretçisinin merkeze uzaklığının joystick yarıçapına oranı [0 - 1]
         *
         */
        fun onJoystickChanged(center: PointF, pointerCenter: PointF, degree: Double, ratio: Double)

        /**
         * Joystick kaldırıldığında çalışan metod
         *
         * @param center Joystick Merkez Noktaları
         * @param pointerCenter Joystick İşaretçisi Merkez Noktaları
         * @param degree Joystick işaretçisi ile merkezinin arasındaki açının derecesi [0 - 360)
         * @param ratio Joystick işaretçisinin merkeze uzaklığının joystick yarıçapına oranı [0 - 1]
         *
         */
        fun onJoystickReleased(center: PointF, pointerCenter: PointF, degree: Double, ratio: Double)
    }

    /**
     * Ekrana joystick çizilmesini sağlar
     * [showJoystick] true olmadan çalışmaz.
     * @see showJoystick
     */
    fun drawJoystick(canvas: Canvas) {
        if (showJoystick) {
            val paint = Paint()
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
            paint.color = Color.WHITE
            canvas.drawCircle(jsCenter.x, jsCenter.y, sizeJS, paint)
            paint.color = Color.RED
            canvas.drawCircle(jsPointerCenter.x, jsPointerCenter.y, sizeJSPointer, paint)
        }
    }

    /**
     * Joystick'in çizileceği noktaları ayarlar.
     */
    private fun createJoystick(x: Float, y: Float) {
        jsCenter.x = x
        jsCenter.y = y

        setJoyStickPointer(x, y)
        showJoystick = true
    }

    /**
     * Joystick işaretçisinin çizileceği noktaları ayarlar.
     */
    private fun setJoyStickPointer(x: Float, y: Float) {
        // Dokunduğumuz alanın merkez noktasına uzaklığını hesaplıyoruz.
        var displacement =
                Math.sqrt(
                        Math.pow((x - jsCenter.x).toDouble(), 2.0) +
                                Math.pow((y - jsCenter.y).toDouble(), 2.0)
                )

        // İki nokta arasındaki açıyı hesaplıyoruz.
        if (x != jsCenter.x)
            degree = Math.toDegrees(Math.acos((x - jsCenter.x) / displacement))

        // Açı değerlerinin eş değerleri olduğu için son bir düzeltme yapıyoruz. (cos90 = cos(360 - 90))
        if (y - jsCenter.y < 0) {
            degree = 360 - degree
        }

        // Eğer uzunluk bizim joystick uzunluğundan büyük ise ona eşitliyoruz.
        if (displacement > sizeJS)
            displacement = sizeJS.toDouble()

        ratio = displacement / sizeJS

        // İşaretçi konumlarını atıyoruz.
        jsPointerCenter.x = jsCenter.x + (Math.cos(Math.toRadians(degree)) * displacement).toFloat()
        jsPointerCenter.y = jsCenter.y + (Math.sin(Math.toRadians(degree)) * displacement).toFloat()
    }


    /**
     * Joystick'i [drawJoystick] ile ekrana çizilmesini engeller.
     *
     * @see drawJoystick
     */
    private fun removeJoystick() {
        showJoystick = false
    }

}