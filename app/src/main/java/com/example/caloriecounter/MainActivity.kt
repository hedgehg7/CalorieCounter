package com.example.caloriecounter

import android.app.AlertDialog
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val dailyData = mutableMapOf<String, DayData>()
    private lateinit var dateSpinner: Spinner
    private lateinit var proteinText: TextView
    private lateinit var fatText: TextView
    private lateinit var carbText: TextView
    private lateinit var caloriesText: TextView
    private lateinit var historyLayout: LinearLayout
    private lateinit var addFoodButton: Button
    private lateinit var addBurnedButton: Button

    data class DayData(
        var protein: Float = 0f,
        var fat: Float = 0f,
        var carbs: Float = 0f,
        var calories: Float = 0f,
        val history: MutableList<HistoryItem> = mutableListOf()
    )

    data class HistoryItem(
        val type: String, // "food" или "burned"
        val time: String,
        val name: String,
        val protein: Float,
        val fat: Float,
        val carbs: Float,
        val calories: Float
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupDateSpinner()
        setupButtons()
    }

    private fun initViews() {
        dateSpinner = findViewById(R.id.dateSpinner)
        proteinText = findViewById(R.id.proteinText)
        fatText = findViewById(R.id.fatText)
        carbText = findViewById(R.id.carbText)
        caloriesText = findViewById(R.id.caloriesText)
        historyLayout = findViewById(R.id.historyLayout)
        addFoodButton = findViewById(R.id.addFoodButton)
        addBurnedButton = findViewById(R.id.addBurnedButton)
    }

    private fun setupDateSpinner() {
        val dates = getLast7Days()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dates)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dateSpinner.adapter = adapter

        dateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateTotals()
                updateHistory()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupButtons() {
        addFoodButton.setOnClickListener { showAddFoodDialog() }
        addBurnedButton.setOnClickListener { showAddBurnedDialog() }
    }

    private fun getLast7Days(): List<String> {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val dates = mutableListOf<String>()
        repeat(7) {
            dates.add(dateFormat.format(calendar.time))
            calendar.add(Calendar.DATE, -1)
        }
        return dates
    }

    private fun createNumericInput(hint: String): EditText {
        return EditText(this).apply {
            this.hint = hint
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
                if (source.matches(Regex("[0-9.]*"))) source else ""
            })
        }
    }

    private fun showAddFoodDialog() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val nameInput = EditText(this).apply { hint = "Название еды" }
        val proteinInput = createNumericInput("Белки")
        val fatInput = createNumericInput("Жиры")
        val carbInput = createNumericInput("Углеводы")
        val calorieInput = createNumericInput("Калории")

        val timeSpinner = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_spinner_item,
                listOf("Завтрак", "Обед", "Ужин", "Перекус")
            ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        }

        layout.apply {
            addView(nameInput)
            addView(proteinInput)
            addView(fatInput)
            addView(carbInput)
            addView(calorieInput)
            addView(timeSpinner)
        }

        AlertDialog.Builder(this)
            .setTitle("Добавить еду")
            .setView(layout)
            .setPositiveButton("OK") { _, _ ->
                val currentDate = dateSpinner.selectedItem.toString()
                val dayData = dailyData.getOrPut(currentDate) { DayData() }

                val newEntry = HistoryItem(
                    type = "food",
                    time = timeSpinner.selectedItem.toString(),
                    name = nameInput.text.toString(),
                    protein = proteinInput.text.toString().toFloatOrNull() ?: 0f,
                    fat = fatInput.text.toString().toFloatOrNull() ?: 0f,
                    carbs = carbInput.text.toString().toFloatOrNull() ?: 0f,
                    calories = calorieInput.text.toString().toFloatOrNull() ?: 0f
                )

                with(dayData) {
                    protein += newEntry.protein
                    fat += newEntry.fat
                    carbs += newEntry.carbs
                    calories += newEntry.calories
                    history.add(newEntry)
                }

                updateTotals()
                updateHistory()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showAddBurnedDialog() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val activitySpinner = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_spinner_item,
                listOf("Бег", "Ходьба", "Тренировка", "Плавание")
            ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        }

        val calInput = createNumericInput("Сожжено калорий")

        layout.apply {
            addView(activitySpinner)
            addView(calInput)
        }

        AlertDialog.Builder(this)
            .setTitle("Добавить активность")
            .setView(layout)
            .setPositiveButton("OK") { _, _ ->
                val currentDate = dateSpinner.selectedItem.toString()
                val dayData = dailyData.getOrPut(currentDate) { DayData() }

                val newEntry = HistoryItem(
                    type = "burned",
                    time = "Активность",
                    name = activitySpinner.selectedItem.toString(),
                    protein = 0f,
                    fat = 0f,
                    carbs = 0f,
                    calories = -(calInput.text.toString().toFloatOrNull() ?: 0f)
                )

                dayData.apply {
                    calories += newEntry.calories
                    history.add(newEntry)
                }

                updateTotals()
                updateHistory()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun updateTotals() {
        val currentDate = dateSpinner.selectedItem.toString()
        val dayData = dailyData[currentDate] ?: DayData()

        proteinText.text = "Б: %.1f".format(dayData.protein)
        fatText.text = "Ж: %.1f".format(dayData.fat)
        carbText.text = "У: %.1f".format(dayData.carbs)
        caloriesText.text = "Ккал: %.1f".format(dayData.calories)
    }

    private fun updateHistory() {
        historyLayout.removeAllViews()
        val currentDate = dateSpinner.selectedItem.toString()
        val dayData = dailyData[currentDate] ?: return

        dayData.history
            .groupBy { it.time }
            .forEach { (time, items) ->
                historyLayout.addView(TextView(this).apply {
                    text = time
                    setTypeface(null, Typeface.BOLD)
                    textSize = 18f
                    setPadding(0, 16, 0, 8)
                })

                items.forEach { item ->
                    historyLayout.addView(TextView(this).apply {
                        text = when (item.type) {
                            "food" -> "• ${item.name}: ${item.calories} ккал (Б:${item.protein} Ж:${item.fat} У:${item.carbs})"
                            else -> "• ${item.name}: -${-item.calories} ккал"
                        }
                        setPadding(32, 8, 0, 8)
                    })
                }
            }
    }
}