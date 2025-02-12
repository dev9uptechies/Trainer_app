package com.example

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.trainerapp.ApiClass.CategoriesData
import com.example.trainerapp.R
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class EquipmentAdapter(
    private var splist: ArrayList<CategoriesData.Categoty>?,
    var context: Context,
    val listener: OnItemClickListener.OnItemClickCallback,
    var selectedRangeId: List<Int>? = null
) :
    RecyclerView.Adapter<EquipmentAdapter.MyViewHolder>() {
    lateinit var view: View
    var selectedItemPos = -1
    var lastItemSelectedPos = -1
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EquipmentAdapter.MyViewHolder {
        view = LayoutInflater.from(parent.context)
            .inflate(R.layout.equipment_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: EquipmentAdapter.MyViewHolder, position: Int) {
        val movie = splist!![position]
        holder.tvFname.text = movie.name
        holder.card_click.setOnClickListener(
            OnItemClickListener(
                position,
                listener,
                movie.id!!.toLong(),
                movie.name!!
            )
        )
        val image = "https://4trainersapp.com" + movie.image
        val thread = Thread {
            try {
                val urlConnection = URL(image)
                val connection: HttpURLConnection = urlConnection
                    .openConnection() as HttpURLConnection
                connection.setDoInput(true)
                connection.connect()
                val input: InputStream = connection.inputStream
                val myBitmap = BitmapFactory.decodeStream(input)
                holder.equipment_image.setImageBitmap(myBitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (!selectedRangeId.isNullOrEmpty()) {
            for (value in selectedRangeId!!) {
                if (movie.id == value) {
                    holder.equipment_image.setColorFilter(context.resources.getColor(R.color.white))
                    holder.tvFname.setTextColor(context.resources.getColor(R.color.white))
                }
            }
        }

        thread.start()

    }

    override fun getItemCount(): Int {
        return splist!!.size
    }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvFname: TextView = view.findViewById<View>(R.id.name_equipment) as TextView
        var equipment_image: ImageView = view.findViewById<View>(R.id.equipment_image) as ImageView
        var card_click: ConstraintLayout =
            view.findViewById<View>(R.id.card_click) as ConstraintLayout

//        fun checkValuesInner(value: Int) {
//            for (i in splist!!) {
//                Log.d("Response value:- ", "${i.id} $value")
//                if (i.id == value) {
//                    Log.d("Response value 1:- ", "${i.id} $value")
//                    equipment_image.setColorFilter(context.resources.getColor(R.color.white))
//                    tvFname.setTextColor(context.resources.getColor(R.color.white))
//                }
//            }
//        }
    }
}
