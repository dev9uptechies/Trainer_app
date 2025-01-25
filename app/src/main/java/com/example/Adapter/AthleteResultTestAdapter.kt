package com.example.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.R
import com.example.trainerapp.TestListData

class AthleteResultTestAdapter(
    private var splist: MutableList<TestListData.testData>?,
    var context: Context
) : RecyclerView.Adapter<AthleteResultTestAdapter.MyViewHolder>() {


    private val resultsList: MutableList<String?> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.performance_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val testItem = splist!![position]

        holder.mainContainer.removeAllViews()

        val athleteDataList = testItem.data ?: arrayListOf()

        Log.d("Adapter", "Test ID: ${testItem.id}, Athlete Count: ${athleteDataList.size}")

        // Ensure resultsList has enough elements, initialize with existing results
        while (resultsList.size < athleteDataList.size) {
            resultsList.add(null)
        }

        // Iterate through each athlete and their data
        athleteDataList.forEachIndexed { athleteIndex, athleteData ->
            val athleteView = LayoutInflater.from(context).inflate(R.layout.athlete_result_data, null)

            val athleteNameTextView = athleteView.findViewById<TextView>(R.id.athelete_name)
            val resultEditText = athleteView.findViewById<EditText>(R.id.Result_edt)

            athleteNameTextView.text = athleteData.athlete?.name ?: "Unknown Athlete"

            // Use the existing value if it's already set; otherwise, use the athlete's result
            if (resultsList[athleteIndex] == null) {
                resultsList[athleteIndex] = athleteData.result
            }

            resultEditText.setText(resultsList[athleteIndex] ?: "")

            Log.d("Adapter", "Setting EditText for athlete: ${athleteData.athlete?.name}, Value: ${resultEditText.text}")

            resultEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    charSequence: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {}

                override fun afterTextChanged(editable: Editable?) {
                    val currentResult = resultEditText.text.toString()

                    // Update resultsList with new input or keep the existing value
                    resultsList[athleteIndex] = if (currentResult.isNotEmpty()) currentResult else athleteData.result

                    Log.d("ResultsList", "Results size: ${resultsList.size}, Data: $resultsList")
                }
            })

            // Add margins (optional)
            val margin = context.resources.getDimensionPixelSize(R.dimen._3sdp)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, margin, 0, 0)
            }
            athleteView.layoutParams = layoutParams

            holder.mainContainer.addView(athleteView)
        }
    }

    override fun getItemCount(): Int {
        return splist!!.size
    }

    fun getTestResults(): List<String?> {
        Log.d("SKSKSKSK", "getTestResults: $resultsList")
        return resultsList
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mainContainer: LinearLayout = view.findViewById(R.id.mainContainer)
    }
}
