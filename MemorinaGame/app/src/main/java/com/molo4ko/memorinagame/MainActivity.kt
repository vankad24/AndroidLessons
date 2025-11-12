package com.molo4ko.memorinagame


import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val cardsImages = mutableListOf(
        R.drawable.img1, R.drawable.img2,
        R.drawable.img3, R.drawable.img4,
        R.drawable.img5, R.drawable.img6,
        R.drawable.img7, R.drawable.img8,
    )
    val closedCard = R.drawable.back
    private val cardsImagesIndices = (1..8).flatMap { listOf(it, it) }.toTypedArray()
    private var clickedSecond: ImageView? = null
    private var clickedFirst: ImageView? = null
    private var firstPlayerTurn = false
    private var pairsFound: Int = 0
    private lateinit var firstScore: TextView
    private lateinit var secondScore: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(applicationContext)
        layout.orientation = LinearLayout.VERTICAL

        val imageParams = LinearLayout.LayoutParams(
            0, // 0 - обязательно для работы веса
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            weight = 1.0f
        }

        cardsImagesIndices.shuffle()
        val catViews = ArrayList<ImageView>()
        for (i in 1..16) {
            catViews.add(
                ImageView(applicationContext).apply {
                    setImageResource(closedCard)
                    adjustViewBounds = true
                    layoutParams = imageParams
                    tag = "image${cardsImagesIndices[i - 1]}"
                    setOnClickListener(colorListener)
                })
        }


        val scoreRow = LinearLayout(applicationContext)
        firstScore = TextView(applicationContext)
        secondScore = TextView(applicationContext)

        val font = 72f
        firstScore.textSize = font
        secondScore.textSize = font

        val params2 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params2.setMargins(200, 0, 200, 0)

        firstScore.layoutParams = params2
        firstScore.setText("0")
        secondScore.setText("0")

        scoreRow.addView(firstScore)
        scoreRow.addView(secondScore)
        layout.addView(scoreRow)


        // Создание горизонтальных строк
        val rowSize = 4
        val rows = Array(rowSize) {
            LinearLayout(applicationContext).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
        }

        var count = 0
        for (view in catViews) {
            val rowIndex: Int = count / rows.size
            rows[rowIndex].addView(view)
            count++
        }

        // Добавление строк в главный макет
        for (row in rows) {
            layout.addView(row)
        }

        setContentView(layout)
    }

    suspend fun setBackgroundWithDelay(v: ImageView) {
        val clickedIndex = v.tag.toString().filter { it.isDigit() }.toInt() - 1
        v.setImageResource(cardsImages[clickedIndex])

        delay(1000)

        if (clickedFirst == null) {
            clickedFirst = v
        } else if (v != clickedFirst) {
            clickedSecond = v
        }

        if (clickedFirst != null && clickedSecond != null) {
                if (clickedFirst!!.tag == clickedSecond!!.tag) {
                    clickedFirst!!.visibility = View.INVISIBLE
                    clickedSecond!!.visibility = View.INVISIBLE
                    clickedFirst = null
                    clickedSecond = null
                    pairsFound += 1

                    val score = if (firstPlayerTurn) firstScore  else secondScore
                    score.setText((score.text.toString().toInt()+1).toString())

                    if (pairsFound == 8) {
                        Toast.makeText(this, (if (firstPlayerTurn) "First" else "Second")+" player won!", Toast.LENGTH_LONG).show()
                    }
                } else {
                    clickedFirst!!.setImageResource(closedCard)
                    clickedSecond!!.setImageResource(closedCard)
                    clickedFirst = null
                    clickedSecond = null
                    firstPlayerTurn= !firstPlayerTurn
                }

        }

    }

    // обработчик нажатия на кнопку
    val colorListener = View.OnClickListener() {
        // запуск функции в фоновом потоке
        GlobalScope.launch(Dispatchers.Main)
        { setBackgroundWithDelay(it as ImageView) }
    }
}