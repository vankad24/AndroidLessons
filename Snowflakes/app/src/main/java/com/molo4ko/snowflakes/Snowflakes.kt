package com.molo4ko.snowflakes

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.AsyncTask
import android.view.MotionEvent
import android.view.View
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.sin
import kotlin.random.Random


data class Snowflake(
    var x: Float,
    var y: Float,
    val velocity: Float,
    val radius: Float,
    val color: Int,
    val initialX: Float
)


lateinit var snow: Array<Snowflake>
val paint = Paint()
var h = 1000 // Высота экрана
var w = 1000 // Ширина экрана
val r = Random(System.currentTimeMillis())


open class Snowflakes(ctx: Context) : View(ctx) {
    lateinit var moveTask: MoveTask
    // Счетчик кадров
    private var frameCount = 0

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(Color.rgb(15, 164, 245))

        if (::snow.isInitialized) {
            for (s in snow) {
                paint.color = s.color
                canvas.drawCircle(s.x, s.y, s.radius, paint)
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        h = bottom - top
        w = right - left

        val numSnowflakes = 50

        snow = Array(numSnowflakes) {
            val radius = 8f + 8f * r.nextFloat() // Радиус 8-16

            // Базовая скорость, которая будет замедляться
            val baseVelocity = 3f + 6f * r.nextFloat()
            val initialX = r.nextFloat() * w

            // Снежинки разных оттенков
            val red = (200 + r.nextInt(56))
            val green = (200 + r.nextInt(56))
            val blue = (220 + r.nextInt(36))
            val color = Color.rgb(red, green, blue)

            Snowflake(
                x = initialX,
                y = r.nextFloat() * h,
                velocity = baseVelocity,
                radius = radius,
                color = color,
                initialX = initialX
            )
        }
    }


    fun moveSnowflakes() {
        frameCount++

        // Константы для настройки движения
        val WOBBLE_STRENGTH = 17.0f // Амплитуда горизонтального качания
        val WOBBLE_SPEED = 0.02f  // Частота качания

        for (snowflake in snow) {
            // Коэфициент замедления: 1.0 (на y=0) -> 0.0 (на y=h)
            var slowFactor = 1.0f - (snowflake.y / h)
            slowFactor = max(0.4f, slowFactor)// Задаем минимальное значение, чтобы снежинки не останавливались полностью

            // Движение вниз (по оси Y)
            snowflake.y += snowflake.velocity * slowFactor

            // Горизонтальное качание (по оси X)
            // Используем sin(snowflake.y/ h) для получения периодического значения от -1 до 1.
            // Применяем это значение к initialX, чтобы создать "качание" вокруг начальной позиции.
            val offsetX = sin(WOBBLE_SPEED.toDouble()*snowflake.y + snowflake.initialX).toFloat() * WOBBLE_STRENGTH
            snowflake.x = snowflake.initialX + offsetX

            // Если снежинка вышла за пределы нижнего края, возвращаем её наверх
            if (snowflake.y > h + snowflake.radius) {
                snowflake.y = -snowflake.radius
                 snowflake.x = w * r.nextFloat()
            }
        }

        // используется для безопасного обновления UI из фонового потока
        postInvalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // Запускаем задачу движения при первом касании
        if (event?.action == MotionEvent.ACTION_DOWN) {
            // Проверяем, что MoveTask не запущен или уже завершен
            if (!::moveTask.isInitialized || moveTask.status == AsyncTask.Status.FINISHED) {
                moveTask = MoveTask(this)
                // Задержка 50 мс дает 33 кадров в секунду
                moveTask.execute(30)
            }
        }
        return true // Возвращаем true, чтобы потребить событие
    }


    class MoveTask(val s: Snowflakes) : AsyncTask<Int, Int, Int>() {
        override fun doInBackground(vararg params: Int?): Int {
            val delay = params[0] ?: 30 // Задерка по умолчанию 30 мс
            while (isCancelled == false) {
                try {
                    Thread.sleep(delay.toLong())
                    s.moveSnowflakes()
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    break // Выход из цикла при прерывании
                }
            }
            return 0
        }
    }
}