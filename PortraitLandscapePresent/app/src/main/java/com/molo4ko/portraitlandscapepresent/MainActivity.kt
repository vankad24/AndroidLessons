package com.molo4ko.portraitlandscapepresent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var adapter: ArrayAdapter<CharSequence>
    private lateinit var imageView: ImageView
    private lateinit var spinner: Spinner

    private val carImages = intArrayOf(
        R.drawable.car1,
        R.drawable.car2,
        R.drawable.car3
    )

    // Переменные для сохранения состояния
    private var currentImageIndex: Int = 0
    private var selectedSpinnerPosition: Int = 0
    private val IMAGE_INDEX_KEY = "current_image_index"
    private val SPINNER_POSITION_KEY = "selected_spinner_position"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.picture)
        spinner = findViewById(R.id.pictures_list)

        // Восстановление состояния при повороте экрана
        if (savedInstanceState != null) {
            currentImageIndex = savedInstanceState.getInt(IMAGE_INDEX_KEY, 0)
            selectedSpinnerPosition = savedInstanceState.getInt(SPINNER_POSITION_KEY, 0)
            imageView.setImageResource(carImages[currentImageIndex])
        }

        adapter = ArrayAdapter.createFromResource(
            this,
            R.array.pictures,
            R.layout.item
        )
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this

        spinner.setSelection(selectedSpinnerPosition)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Сохранение текущего индекса картинки и выбранного элемента спиннера
        outState.putInt(IMAGE_INDEX_KEY, currentImageIndex)
        outState.putInt(SPINNER_POSITION_KEY, selectedSpinnerPosition)
    }


    fun onChangePictureClick(v: View) {
        currentImageIndex = (currentImageIndex + 1) % carImages.size

        imageView.setImageResource(carImages[currentImageIndex])

        spinner.setSelection(currentImageIndex)
        selectedSpinnerPosition = currentImageIndex
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Toast.makeText(this, "Выбран: ${parent?.getItemAtPosition(position)}", Toast.LENGTH_SHORT).show()

        if (position in 0 until carImages.size) {
            currentImageIndex = position
            selectedSpinnerPosition = position
            imageView.setImageResource(carImages[currentImageIndex])
        } else {
            // Если не найдён
            imageView.setImageResource(R.drawable.squarecat)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(this, "Элемент не выбран", Toast.LENGTH_SHORT).show()
    }
}