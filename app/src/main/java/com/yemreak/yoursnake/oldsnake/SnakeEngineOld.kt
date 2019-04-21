package com.yemreak.yoursnake.oldsnake

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Handler
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.SurfaceHolder
import com.yemreak.yoursnake.R
import com.yemreak.yoursnake.oldsnake.SnakeEngineOld.GameSpeed.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class SnakeEngineOld(private val context: Context, private val holder: SurfaceHolder) : SurfaceHolder.Callback, Runnable {

    /**
     * Oyunun Hızı
     *
     * Not: Değişikliklerin aşağıdaki yerlere de uygulanması gerekmektedir;
     * @see R.layout.dialog_ex_settings android:progress sayısı ile aynı sayıda olmalılar. (6 ise progress = 5 çünkü [0,5])
     * @see R.string "Yılan Uzunluğu Metinleri"
     * @see initModeVariables Aynı sayıda olmalılar.
     */
    enum class GameSpeed {
        VERY_SLOW,
        SLOW,
        NORMAL,
        FAST,
        VERY_FAST,
        EXTREMELY_FAST;

        companion object {
            /**
             * İndeksle örnek almak
             */
            fun get(index: Int): GameSpeed {
                return when (index) {
                    0 -> VERY_SLOW
                    1 -> SLOW
                    3 -> FAST
                    4 -> VERY_FAST
                    5 -> EXTREMELY_FAST
                    else -> NORMAL // Farklı değerler ve 2 için normal gelsin
                }
            }

            /**
             * Örneğin indexini almak
             */
            fun getIndex(size: GameSpeed): Int {
                return when (size) {
                    VERY_SLOW -> 0
                    SLOW -> 1
                    NORMAL -> 2
                    FAST -> 3
                    VERY_FAST -> 4
                    EXTREMELY_FAST -> 5
                }
            }

            /**
             * İndekse göre metin id'si almak
             */
            fun getTextId(index: Int): Int {
                return when (index) {
                    0 -> R.string.txt_gameSpeed_verySlow
                    1 -> R.string.txt_gameSpeed_slow
                    3 -> R.string.txt_gameSpeed_fast
                    4 -> R.string.txt_gameSpeed_veryFast
                    5 -> R.string.txt_gameSpeed_extremelyFast
                    else -> R.string.txt_gameSpeed_normal // Farklı değerler ve 2 için normal gelsin
                }
            }

            /**
             * Örneğe göre metin id'si almak.
             */
            fun getTextId(size: GameSpeed): Int {
                return when (size) {
                    VERY_SLOW -> R.string.txt_gameSpeed_verySlow
                    SLOW -> R.string.txt_gameSpeed_slow
                    FAST -> R.string.txt_gameSpeed_fast
                    VERY_FAST -> R.string.txt_gameSpeed_veryFast
                    EXTREMELY_FAST -> R.string.txt_gameSpeed_extremelyFast
                    else -> R.string.txt_gameSpeed_normal // Farklı değerler ve 2 için normal gelsin
                }
            }
        }
    }

    /**
     * Ses modu çeşitleri
     * Not: Veriler değiştirilirse alttakilerin de değişmesi lazımdır.
     * @see R.layout.dialog_sounds Aynı sırada olmak zorundalar
     * @see setSoundsMode Aynı sırada ve aynı sayıda objeye sahip olmak zorundalar.
     * @see soundModeIndex Aynı sırada ve aynı sayıda objeye sahip olmak zorundalar.
     * @see CustomDialogInfos.toggleButtonIds Aynı sırada ve sayıda olmak zorundalar.
     * @see soundIds Aynı çeşitlilikte olmalılar.
     */
    enum class SoundsModes {
        NO_SOUNDS,
        CLASSIC_SOUNDS,
        RETRO_SOUNDS,
        GUN_SOUNDS,
        HORROR_SOUNDS
    }

    /**
     * Muzik modu çeşitleri
     * Not: Veriler değiştirilirse alttakilerin de değişmesi lazımdır.
     * @see R.layout.dialog_musics Aynı sırada olmak zorundalar
     * @see setMusicMode Aynı sırada ve aynı sayıda objeye sahip olmak zorundalar.
     * @see musicModeIndex Aynı sırada ve aynı sayıda objeye sahip olmak zorundalar.
     * @see CustomDialogInfos.toggleButtonIds Aynı sırada ve sayıda olmak zorundalar.
     * @see musicId Aynı sayıda olmalılar.
     */
    enum class MusicModes {
        NO_MUSICS,
        FERAMBIE,
        HORROR_MUSIC
    }

    /**
     * Control modu çeşitleri
     * Kullanıldığı Alan: Kontrol moduna göre farklı aktivite başlatılmaktadır.
     * @see MainActivity.onNewGameClicked
     *
     * Not: Veriler değiştirilirse alttakilerin de güncellenmesi lazımdır.
     * @see R.layout.dialog_controls Aynı sırada olmak zorundalar
     * @see setControlMode Aynı sırada ve aynı sayıda objeye sahip olmak zorundalar.
     * @see controlModeIndex Aynı sırada ve aynı sayıda objeye sahip olmak zorundalar.
     * @see CustomDialogInfos.toggleButtonIds Aynı sırada ve sayıda olmak zorundalar.
     */
    enum class ControlModes {
        CONTROL_WITH_BUTTONS,
        CONTROL_WITH_SWIPE
    }

    /**
     * Tema modu çeşitleri
     * Not: Veriler değiştirilirse alttakilerin de değişmesi lazımdır.
     * @see Theme
     * @see R.layout.dialog_themes Aynı sırada olmak zorundalar
     * @see setThemeMode Aynı sırada ve aynı sayıda objeye sahip olmak zorundalar.
     * @see themeModeIndex Aynı sırada ve aynı sayıda objeye sahip olmak zorundalar.
     * @see CustomDialogInfos.toggleButtonIds Aynı sırada ve sayıda olmak zorundalar.
     */
    enum class ThemeModes {
        DEFAULT,
        DARKNESS,
        LIGHT,
        AQUA,
        PURPLE,
        HORROR_THEME
    }

    /**
     * Oyun modu çeşitleri
     * Not: Veriler değiştirilirse alttakilerin de değişmesi lazımdır.
     * @see R.layout.dialog_classic_modes Aynı sırada olmak zorundalar
     * @see setGameMode Aynı sırada ve aynı sayıda objeye sahip olmak zorundalar.
     * @see gameModeIndexes Aynı sırada ve aynı sayıda objeye sahip olmak zorundalar.
     * @see CustomDialogInfos.toggleButtonIds Aynı sırada ve sayıda olmak zorundalar.
     *
     * @sample FRAME Oyun dışındaki çerçeveyi kaldırır. Kenarlara çarpılmaz.
     * @see surfaceCreated
     * @see run
     * @see hasFrame
     *
     * @sample MORE_FEED_TYPES Birden fazla yem tipleri olur. 1, 2, 5, 10, 25 puanlık yemler.
     * @see Feed.spawnActTo
     *
     * @sample MORE_FEED Birden fazla yem olur.
     * @see initFeeds
     *
     * @sample SHRINK_SNAKE Yılan yem yedikçe büyümek yerine küçülür.
     * @see Snake.eatFeedActTo
     * @see Snake.create
     *
     */
    enum class GameModes {
        FRAME,
        MORE_FEED_TYPES,
        MORE_FEED,
        SHRINK_SNAKE
    }

    companion object {

        /**
         * Oyunun hız modu
         * @see GameSpeed
         * @see initModeVariables
         */
        var gameSpeedMode = NORMAL


        /**
         * Yılanın kontrol edilme modu
         * @see ControlModes
         */
        var controlMode = ControlModes.CONTROL_WITH_SWIPE
            private set(value) {
                field = value
            }

        /**
         * Oyun içindeki arka plan müziği modu
         * @see MusicModes
         */
        var musicMode = MusicModes.FERAMBIE
            private set(value) {
                field = value
            }

        /**
         * Oyundaki ses efektleri modu
         * @see SoundsModes
         */
        var soundsMode = SoundsModes.RETRO_SOUNDS
            private set(value) {
                field = value
            }

        /**
         * Oyun modları
         * @see GameModes
         */
        var gameModes = arrayListOf<GameModes>()
            private set(value) {
                field = value
            }

        /**
         * Tema modu
         * @see ThemeModes
         */
        var themeMode = ThemeModes.DEFAULT
            private set(value) {
                field = value
            }

        /**
         * Ayarlar diyalog menüsündeki buton indeksi döndürür.
         * @return Seçilmiş ayarın buton indexi
         *
         * Not: İçeriği ControlModes'a bağlıdır.
         * @see ControlModes
         */
        val controlModeIndex: Int
            get() = when (controlMode) {
                ControlModes.CONTROL_WITH_BUTTONS -> 0
                ControlModes.CONTROL_WITH_SWIPE -> 1
            }


        /**
         * Ayarlar diyalog menüsündeki buton indexlerine uygun modu ayarlar.
         * @param mode Buton indexi
         *
         * Not: İçeriği ControlModes'a bağlıdır.
         * @see ControlModes
         */
        fun setControlMode(mode: Int) {
            controlMode = when (mode) {
                0 -> ControlModes.CONTROL_WITH_BUTTONS
                1 -> ControlModes.CONTROL_WITH_SWIPE
                else -> {
                    Log.e(
                            "Kontrol hatası",
                            "Kontrol ayarlanamadı. Hatalı mod değeri girildi. Lütfen sıralamanın aynı olmasına dikkat et. " +
                                    "\"SnakeEngineOld.ControlMode, CustomDialogActivity.setMode \" (SnakeEngineOld.setControlMode)"
                    )

                    ControlModes.CONTROL_WITH_SWIPE
                }
            }
        }

        val musicId: Int?
            get() = when (musicMode) {
                MusicModes.NO_MUSICS -> null
                MusicModes.FERAMBIE -> R.raw.bg_music1
                MusicModes.HORROR_MUSIC -> R.raw.bg_horror
            }

        /**
         * Ayarlar diyalog menüsündeki buton indexlerini döndürür.
         * @return Seçilmiş ayarın buton indexi
         *
         * Not: İçeriği MusicModes'a bağlıdır.
         * @see MusicModes
         */
        val musicModeIndex: Int
            get() = when (musicMode) {
                MusicModes.NO_MUSICS -> 0
                MusicModes.FERAMBIE -> 1
                MusicModes.HORROR_MUSIC -> 2
            }


        /**
         * Ayarlar diyalog menüsündeki buton indexlerine uygun modu ayarlar.
         * @param mode Buton indexi
         *
         * Not: İçeriği MusicModes'a bağlıdır.
         * @see MusicModes
         */
        fun setMusicMode(mode: Int) {
            musicMode = when (mode) {
                0 -> MusicModes.NO_MUSICS
                1 -> MusicModes.FERAMBIE
                2 -> MusicModes.HORROR_MUSIC
                else -> {
                    Log.e(
                            "Müzik ayarlama hatası",
                            "Müzik modu ayarlanamadı. Hatalı mod değeri girildi. Lütfen sıralamanın aynı olmasına dikkat et. " +
                                    "\"SnakeEngineOld.MusicModes, OptionActivity.showMusicsDialog\" (SnakeEngineOld.setMusicMode)"
                    )

                    MusicModes.NO_MUSICS
                }
            }
        }

        /**
         * Ses moduna uygun ses dosyalarının id'si.
         * @return Ses dosyaları id'leri
         *
         * Not: Ses moduna uygun sesler döndürülmeli !
         */
        val soundIds: ArrayList<Int>
            get() {
                return when (soundsMode) {
                    SoundsModes.NO_SOUNDS -> arrayListOf() // Bu kısmı incele "return" sounds değişkenleri atlanmalı mı?
                    SoundsModes.CLASSIC_SOUNDS -> arrayListOf(
                            R.raw.classic_eating,
                            R.raw.classic_crash
                    )
                    SoundsModes.RETRO_SOUNDS -> arrayListOf(
                            R.raw.retro_eating,
                            R.raw.retro_crash
                    )
                    SoundsModes.GUN_SOUNDS -> arrayListOf(
                            R.raw.gun_eating,
                            R.raw.gun_crash
                    )
                    SoundsModes.HORROR_SOUNDS -> arrayListOf(
                            R.raw.scary_eating,
                            R.raw.scary_crash
                    )
                }
            }

        /**
         * Ayarlar diyalog menüsündeki buton indexlerini döndürür.
         * @return Seçilmiş ayarın buton indexi
         *
         * Not: İçeriği SoundsModes'a bağlıdır.
         * @see SoundsModes
         */
        val soundModeIndex: Int
            get() = when (soundsMode) {
                SoundsModes.NO_SOUNDS -> 0
                SoundsModes.CLASSIC_SOUNDS -> 1
                SoundsModes.RETRO_SOUNDS -> 2
                SoundsModes.GUN_SOUNDS -> 3
                SoundsModes.HORROR_SOUNDS -> 4
            }


        /**
         * Ayarlar diyalog menüsündeki buton indexlerine uygun modu ayarlar.
         * @param mode Buton indexi
         *
         * Not: İçeriği SoundsModes'a bağlıdır.
         * @see SoundsModes
         */
        fun setSoundsMode(mode: Int) {
            soundsMode = when (mode) {
                0 -> SoundsModes.NO_SOUNDS
                1 -> SoundsModes.CLASSIC_SOUNDS
                2 -> SoundsModes.RETRO_SOUNDS
                3 -> SoundsModes.GUN_SOUNDS
                4 -> SoundsModes.HORROR_SOUNDS
                else -> {
                    Log.e(
                            "Ses ayarlama hatası",
                            "Ses modu ayarlanamadı. Hatalı mod değeri girildi. Lütfen sıralamanın aynı olmasına dikkat et. " +
                                    "\"SnakeEngineOld.SoundsModes, OptionActivity.showSoundsDialogs\" (SnakeEngineOld.setSoundsMode)"
                    )

                    SoundsModes.CLASSIC_SOUNDS
                }
            }
        }

        /**
         * Ayarlar diyalog menüsündeki buton indexlerini döndürür.
         * @return Seçilmiş ayarın buton indexi
         *
         * Not: İçeriği GameModes'a bağlıdır.
         * @see GameModes
         */
        val gameModeIndexes: ArrayList<Int>
            get() {
                val indexes = arrayListOf<Int>()

                if (gameModes.contains(GameModes.FRAME)) indexes.add(0)
                if (gameModes.contains(GameModes.MORE_FEED_TYPES)) indexes.add(1)
                if (gameModes.contains(GameModes.MORE_FEED)) indexes.add(2)
                if (gameModes.contains(GameModes.SHRINK_SNAKE)) indexes.add(3)

                return indexes
            }

        /**
         * Ayarlar diyalog menüsündeki buton indexlerine uygun modu ayarlar.
         * @param mode Buton indexi
         *
         * Not: İçeriği GameModes'a bağlıdır.
         * @see GameModes
         */
        fun setGameMode(mode: Int) {
            when (mode) {
                0 -> changeGameMode(GameModes.FRAME)
                1 -> changeGameMode(GameModes.MORE_FEED_TYPES)
                2 -> changeGameMode(GameModes.MORE_FEED)
                3 -> changeGameMode(GameModes.SHRINK_SNAKE)
                else -> {
                    Log.e("Oyun Modu Ayarlanmadı", "Verilen index değeri tanımsız. Lütfen kontrol et; \" GameModeActivity.setMode \" . (SnakeEngineOld.setGameMode)")
                }
            }
        }

        /**
         * Oyun modunu değiştirme
         * Not: Aynı modun birden fazla eklenmesini önler.
         */
        private fun changeGameMode(mode: GameModes) {
            if (gameModes.contains(mode))
                gameModes.remove(mode)
            else
                gameModes.add(mode)
        }

        /**
         * Ayarlar diyalog menüsündeki buton indexlerini döndürür.
         * @return Seçilmiş ayarın buton indexi
         *
         * Not: İçeriği ThemeModes'a bağlıdır.
         * @see ThemeModes
         */
        val themeModeIndex: Int
            get() = when (themeMode) {
                ThemeModes.DEFAULT -> 0
                ThemeModes.DARKNESS -> 1
                ThemeModes.LIGHT -> 2
                ThemeModes.AQUA -> 3
                ThemeModes.PURPLE -> 4
                ThemeModes.HORROR_THEME -> 5
            }


        /**
         * Ayarlar diyalog menüsündeki buton indexlerine uygun modu ayarlar.
         * @param mode Buton indexi
         *
         * Not: İçeriği ThemeModes'a bağlıdır.
         * @see ThemeModes
         */
        fun setThemeMode(mode: Int) {
            themeMode = when (mode) {
                0 -> ThemeModes.DEFAULT
                1 -> ThemeModes.DARKNESS
                2 -> ThemeModes.LIGHT
                3 -> ThemeModes.AQUA
                4 -> ThemeModes.PURPLE
                5 -> ThemeModes.HORROR_THEME
                else -> {
                    Log.e(
                            "Tema hatası",
                            "Tema ayarlanamadı. Hatalı mod değeri girildi. Lütfen sıralamanın aynı olmasına dikkat et. " +
                                    "\"SnakeEngineOld.ThemeModes, OptionActivity.showThemeDialog\" (SnakeEngineOld.setThemeMode)"
                    )

                    ThemeModes.DEFAULT
                }
            }
        }

        /**
         * Oyun durdurulduğunda veya yılan çarptığında gerekli ekranların gösterilsin mi
         */
        var showScreen = true

    }

    /**
     * Oyunun teması
     */
    val theme = Theme.getTheme(context, themeMode)

    /**
     * Arka plan fon müziği
     * @see initSounds
     */
    private var mediaPlayer: MediaPlayer? = null

    /**
     * Ses efekti değişkenleri
     * @see initSounds
     */
    private var soundPool: SoundPool? = null
    /**
     * Yem yeme sesi
     * @see initSounds
     */
    private var soundEat: Int = -1
    /**
     * Çarpma sesi
     * @see initSounds
     */
    private var soundCrash: Int = -1


    /**
     * Oyun yüzeyine bağlı işlemlerin hatasız çalışması için değişken
     * @see surfaceCreated
     */
    var isCreated = false
        private set(value) {
            field = value
        }

    /**
     * Oyun motorunun durumuna bağlı işlemlerin hatasız çalışması için değişken
     * @see surfaceCreated
     */
    var isPaused = false
        private set(value) {
            field = value
        }

    /**
     * Değişkenlerin tanımlanmasına bağlı işlemlerin hatasız çalışması için değişken
     */
    var areVariablesDefined = false
        private set(value) {
            field = value
        }

    /**
     * Oyunun çerçevesinin olunabilirliği
     * @see initModeVariables
     */
    var hasFrame = false
        private set(value) {
            field = value
        }

    /**
     * Oyun ekranındaki yılan
     */
    var snake: Snake = Snake()
        private set(value) {
            field = value
        }

    /**
     * Oyun ekranındaki yemler
     */
    val feeds = ArrayList<Feed>()

    /**
     * Oyundaki skor
     */
    var score: Int = 0
        private set(value) {
            field = value
        }

    /**
     * Oyun motoru yenileyicisi
     * Not: Değişken olarak tanımlanma sebebi, durdurma gereksinimidir.
     * @see pause
     */
    var handler = Handler()

    /**
     * Saniyelik yenilenme sayısı
     * @see GameSpeed
     * @see initModeVariables
     */
    var gameSpeed = 15
        private set(value) {
            field = value
        }

    init {
        initModeVariables()
        initMusic()
        initSounds()
    }

    /**
     * Oyunun hızının moda göre ayarlanması
     * @see GameSpeed
     */
    private fun initModeVariables() {
        hasFrame = gameModes.contains(GameModes.FRAME)

        gameSpeed = when (gameSpeedMode) {
            VERY_SLOW -> 5
            SLOW -> 10
            NORMAL -> 15
            FAST -> 25
            VERY_FAST -> 50
            EXTREMELY_FAST -> 75
        }
    }

    /**
     * Müzik değişkenlerinin tanımlanması
     * @see MusicModes
     * @see musicId Tutarlı olmalılar.
     *
     */
    private fun initMusic() {
        if (musicId != null)
            mediaPlayer = MediaPlayer.create(context, musicId!!)
    }

    /**
     * Ses değişkenlerinin tanımlanması ve oluşturulması.
     * @see SoundsModes
     * @see soundIds Aynı sayıda ses değişkeni olmalı
     *
     * Not: Eğer ses id'leri yoksa çalışmaz
     * @see soundIds
     */
    private fun initSounds() {
        if (soundIds.size > 0) {
            soundPool = SoundPool.Builder()
                    .setMaxStreams(soundIds.size) // En fazla ses sayısı (şu an 2)
                    .setAudioAttributes( // Ses tipi
                            AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_GAME)
                                    .build()
                    )
                    .build()


            try {
                if (soundIds.size >= 2) {
                    soundEat = soundPool!!.load(context, soundIds[0], 1)
                    soundCrash = soundPool!!.load(context, soundIds[1], 1)
                }

            } catch (e: IOException) {
                Log.e("Ses Hatası", "Ses dosyaları bulunmamış olabilir kontrol et. Detaylar: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Oyun alanı çizildiğinde yapılacak oyun motoru işlemleri
     */
    override fun surfaceCreated(holder: SurfaceHolder) {
        // Yüzey kontrolu için
        isCreated = true

        if (isPaused) {
            // Ekrana çizme
            draw()
        } else {
            BlockField.initVariables(holder.surfaceFrame, hasFrame)
            prepare()
        }
    }

    /**
     * Oyun alanı değiştiğinde yapılacak oyun motoru işlemleri
     */
    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    /**
     * Oyun alanı silindiğinde yapılacak oyun motoru işlemleri
     */
    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        isCreated = false
    }

    /**
     * Oyunu hazır hale getirip, durgun halde bekletme
     * Not: Motor çalışırken kullanılamaz.
     * @see start
     * Not: Yüzey oluşturulmadan çalışmaz.
     * @see surfaceCreated
     */
    private fun prepare() {
        if (isCreated) {
            initVariables()
            newGame()

            isPaused = true
        } else {
            Log.e(
                    "Fonksiyon Çalışmadı",
                    "Zaten çalışan motoru hazır hale getiremeyiz. Yüzey alanı oluşturulmadan çalıştırılamaz, genelde SurfaceCreated içerisinden çağırılır." +
                            "\"surfaceCreated\" metodunu kontrol et. (SnakeEngineOld.prepare)"
            )
        }
    }

    /**
     * Değişkenlerin tanımlanması
     * Not: Yüzey oluşturulmadan çalışmaz.
     * @see surfaceCreated
     */
    private fun initVariables() {
        if (isCreated) {

            // Oyun moduna göre farklı tanımalama şekilleri olacak
            snake = Snake()

            initFeeds()

            areVariablesDefined = true

        } else {
            Log.e(
                    "Fonksiyon Çalışmadı",
                    "Yüzey alanı ve vision oluşturulmadan çalıştırılamaz, genelde SurfaceCreated'de  çağırılır." +
                            "\"prepare, surfaceCreated\" metodunu kontrol et. (SnakeEngineOld.initVariables)"
            )
        }
    }

    /**
     * Not: Sadece bir kere kullanılmalı
     */
    private fun initFeeds() {
        when { // Yemleri oluşturma
            gameModes.contains(GameModes.MORE_FEED) -> {
                for (i in 0..Random().nextInt(8)) // Düzenle
                    feeds.add(Feed())
            }
            else -> {
                feeds.add(Feed())
            }
        }
    }

    /**
     * Yeni oyunun oluşturup, çizilmesi
     * Not: Değişkenler tanımlanmadan çalışmaz.
     * Not: Oyun moduna göre şekillenir.
     * @see initVariables
     */
    private fun newGame() {
        if (areVariablesDefined) {
            snake.create(gameModes)

            spawnFeeds()

            score = 0

            // Oluşturulan oyunu ekrana çizmek için (Kaldırılabilir)
            draw()

        } else {
            Log.e(
                    "Fonksiyon Çalışmadı",
                    "Değişkenler tanımlanmadan çalıştırılamaz." +
                            "Metodu kullanmadan önce \"initVariables\" metodunu kullandığından emin ol. (SnakeEngineOld.newGame)"
            )
        }
    }

    /**
     * Tüm yemlerin ekrana yerleştirilmesi
     */
    private fun spawnFeeds() {
        feeds.forEach { feed -> feed.spawnActTo(snake, feeds, gameModes) }

    }

    /**
     * Verilerin yüzeye çizdirilmesi
     * Not: Değişkenler tanımlanmadan çalışmaz.
     * @see initVariables
     */
    private fun draw() {
        if (isCreated && areVariablesDefined) {
            if (holder.surface.isValid) {
                val canvas: Canvas = holder.lockCanvas()
                val paint = Paint()

                // canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR) // Canvası temizleme

                // Kenar boşluklarını çizme
                paint.color = Color.BLACK
                canvas.drawPaint(paint)

                // Eğer varsa çerçeveyi çizme
                if (hasFrame) {
                    paint.color = theme.colorBg
                    canvas.drawRect(BlockField.frame, paint)
                }

                // Oyun ekranını çiziyoruz
                paint.color = theme.colorField
                canvas.drawRect(BlockField.field, paint)

                // Score'u çiziyoruz
                paint.color = theme.colorScore
                paint.textSize = 64f
                canvas.drawText("Score: $score", 30f, 94f, paint)

                /*Dışa doğru çizilmeli içe dğeil
                paint.color = context.resources.getColor(R.color.ultra_violet)
                canvas.drawMyCircle(theBlockFrame(Snake.Directions.LEFT), paint)
                canvas.drawMyCircle(theBlockFrame(Snake.Directions.RIGHT), paint)
                canvas.drawMyCircle(theBlockFrame(Snake.Directions.DOWN), paint)
                canvas.drawMyCircle(theBlockFrame(Snake.Directions.UP), paint)
                */

                // Yılanı çiziyoruz
                paint.color = theme.colorSnake
                snake.drawHead(canvas, paint)
                snake.drawTails(canvas, paint)

                // Yılanın gözünü çizme
                paint.color = theme.colorSnakeEyes
                snake.drawEyes(canvas, paint)

                // Yemi çiziyoruz
                paint.color = theme.colorFeed
                feeds.forEach { feed -> feed.draw(context, canvas, paint) }

                holder.unlockCanvasAndPost(canvas)
            }
        } else {
            Log.e(
                    "Fonksiyon Çalışmadı",
                    "Değişkenler tanımlanmadan çalıştırılamaz." +
                            "Metodu kullanmadan önce \"initVariables\" metodunu kullandığından emin ol. (SnakeEngineOld.drawTails)"
            )
        }

    }

    /**
     * YılanMotor'unun çalışması
     * Not: Motor çalıştırılmadan çalışmaz.
     * @see start
     * Not: Motor durdulduysa çalışmaz.
     * @see pause
     * Not: Yüzey oluşturulmadan çalışmaz.
     * @see surfaceCreated
     * Not: Değişkenler tanımlanmadan çalışmaz.
     * @see initVariables
     */
    override fun run() {
        if (!isPaused && isCreated && areVariablesDefined) {
            snake.move(hasFrame)


            feeds.forEach { feed ->
                if (snake.isFeeding(feed.center)) {
                    eatFeed(feed)
                }
            }

            if (snake.isCrashed(hasFrame)) {
                soundPool?.play(soundCrash, 1f, 1f, 0, 0, 1f)
                gameOver()
                newGame()
            }

            draw()

            handler.postDelayed(this, (1000 / gameSpeed).toLong())
        } else {
            Log.e(
                    "Fonksiyon Çalışmadı",
                    "Motor başlatılmadan, yüzey oluşturulmadan, değişkenler tanımlanmadan çalıştırılamaz." +
                            "Metodu kullanmadan önce \"start, pause, surfaceCreated, initVariables\" metodlarını kullandığından emin ol. (SnakeEngineOld.run)"
            )
        }
    }

    /**
     * Oyun sonunda yapılacak işlemler.
     *
     * @see showScreen Oyun sonu ekranı gösterilsin mi
     */
    private fun gameOver() {
        if (showScreen) {
            val intent = Intent(context, SnakeEngineScreen::class.java)
            intent.putExtra("score", score)

            val option = ActivityOptionsCompat.makeCustomAnimation(context, R.anim.fade_in, R.anim.fade_out)
            ContextCompat.startActivity(context, intent, option.toBundle())
        }
    }

    /**
     * Yem yenildiğinde olacak işlemler
     * Not: Değişkenler tanımlanmadan çalışmaz.
     * Not: Oyun moduna göre şekillenir.
     * @see initVariables
     */
    private fun eatFeed(feed: Feed) {
        if (areVariablesDefined) {
            incScore(feed.feedType)

            soundPool?.play(soundEat, 1f, 1f, 0, 0, 1f)

            snake.eatFeedActTo(gameModes, feed.feedType)

            spawnFeeds()

        } else {
            Log.e(
                    "Fonksiyon Çalışmadı",
                    "Değişkenler tanımlanmadan çalıştırılamaz." +
                            "Metodu kullanmadan önce \"initVariables\" metodunun kullandığından emin ol. (SnakeEngineOld.eatFeed)"
            )
        }
    }

    /**
     * Yem tipine göre skor arttırma
     */
    private fun incScore(feedType: Feed.FeedTypes) {
        score += when (feedType) {
            Feed.FeedTypes.NORMAL -> 1
            Feed.FeedTypes.DOUBLE_FEED -> 2
            Feed.FeedTypes.X5_FEED -> 5
            Feed.FeedTypes.X10_FEED -> 10
            Feed.FeedTypes.X25_FEED -> 25
        }
    }

    /**
     * Oyunu belli bir gecikme ile devam ettirmek.
     * @param delayMs Gecikme süresi (ms)
     */
    fun resume(delayMs: Long) {
        Handler().postDelayed({
            if (isCreated && isPaused) {
                isPaused = false // Gecikme olduğunda, gecikme süresince basıldıkça oyun üst üste çalışır (bug çözüldü)
                start()
            } else {
                val msg = when {
                    !isCreated -> "Oyun motoru yüzeyi oluşturulmamış,"
                    else -> "Oyun motoru durgun değil,"
                }

                Log.e("Kontrol Hatası", "$msg lütfen kontrol edin \" SnakeEngineOld.prepare \" (SnakeEngineOld.resume)")
            }
        }, delayMs)

    }

    /**
     * Motorun başlatılması veya devam ettirilmesi
     * Not: Yüzey oluşturulmadan çalışmaz.
     * @see surfaceCreated
     */
    private fun start() {
        if (isCreated) {
            if (mediaPlayer != null) {
                mediaPlayer!!.setVolume(0.7f, 0.7f)
                mediaPlayer!!.isLooping = true
                mediaPlayer!!.start()
            }

            handler.postDelayed(this, (1000 / gameSpeed).toLong())
        } else {
            Log.e(
                    "Fonksiyon Çalışmadı",
                    "Yüzey oluşturulmadan çalıştırılamaz." +
                            "Metodu kullanmadan önce \"surfaceCreated\" metodunun kullandığından emin ol. (SnakeEngineOld.start)"
            )
        }
    }

    /**
     * Oyun motorunun durdurulması.
     * @param showScreen Durduruldu ekranı gösterilsin mi
     *
     * Not: Null ise varsayılan ayarı kullanır.
     */
    fun pause(showScreen: Boolean?) {
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying)
                mediaPlayer!!.pause()
        }

        handler.removeCallbacks(this)

        isPaused = true

        if ((showScreen == null && Companion.showScreen) || (showScreen != null && showScreen)) {
            isCreated = false // Aktivite değişeceği için hazır olmayacaktır.

            val option = ActivityOptionsCompat.makeCustomAnimation(context, R.anim.fade_in, R.anim.fade_out)
            ContextCompat.startActivity(context, Intent(context, SnakeEngineScreen::class.java), option.toBundle())
        }
    }
}



























