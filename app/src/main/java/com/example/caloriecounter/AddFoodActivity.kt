package com.example.caloriecounter
import android.app.Activity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AddFoodActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)

        val proteinInput = findViewById<EditText>(R.id.proteinInput)
        val fatInput = findViewById<EditText>(R.id.fatInput)
        val carbInput = findViewById<EditText>(R.id.carbInput)
        val calorieInput = findViewById<EditText>(R.id.calorieInput)
        val mealSpinner = findViewById<Spinner>(R.id.mealSpinner)
        val saveButton = findViewById<Button>(R.id.saveButton)

        val mealTypes = listOf("Завтрак", "Второй завтрак", "Обед", "Полдник", "Ужин")
        mealSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, mealTypes)

        saveButton.setOnClickListener {
            val protein = proteinInput.text.toString().toDoubleOrNull() ?: 0.0
            val fat = fatInput.text.toString().toDoubleOrNull() ?: 0.0
            val carbs = carbInput.text.toString().toDoubleOrNull() ?: 0.0
            val calories = calorieInput.text.toString().toDoubleOrNull() ?: 0.0
            val mealTime = mealSpinner.selectedItem.toString()

            val data = "$mealTime: Б: $protein Ж: $fat У: $carbs Ккал: $calories"

            val intent = intent
            intent.putExtra("foodEntry", data)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}
