package com.example.caloriecounter

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast

class AddBurnActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_burn)

        val burnedCaloriesInput = findViewById<EditText>(R.id.burnedCaloriesInput)
        val activitySpinner = findViewById<Spinner>(R.id.activitySpinner)
        val saveBurnedButton = findViewById<Button>(R.id.saveBurnedButton)

        val activityTypes = listOf("Тренировка", "Бег", "Ходьба", "Плавание", "Другое")
        activitySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, activityTypes)

        saveBurnedButton.setOnClickListener {
            val calories = burnedCaloriesInput.text.toString().toFloatOrNull()
            val type = activitySpinner.selectedItem.toString()

            if (calories != null) {
                val result = "$type: -$calories ккал"
                setResult(Activity.RESULT_OK, intent.putExtra("burnedEntry", result))
                finish()
            } else {
                Toast.makeText(this, "Введите число", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
