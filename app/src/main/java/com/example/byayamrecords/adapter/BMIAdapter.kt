package com.example.byayamrecords.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.byayamrecords.R
import com.example.byayamrecords.model.BMIRecord

class BMIAdapter(
    private val context: Context,
    private var bmiList: ArrayList<BMIRecord>,
    private val onDeleteClick: (String) -> Unit,
    private val onEdit: (BMIRecord) -> Unit
) : RecyclerView.Adapter<BMIAdapter.BMIViewHolder>() {

    class BMIViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val txtBMI: TextView = itemView.findViewById(R.id.txtBMI)
        val txtStatus: TextView = itemView.findViewById(R.id.txtStatus)
        val txtWeight: TextView = itemView.findViewById(R.id.txtWeight)
        val txtWeightHeight: TextView = itemView.findViewById(R.id.txtHeight)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BMIViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_bmi_record, parent, false)
        return BMIViewHolder(view)
    }

    override fun onBindViewHolder(holder: BMIViewHolder, position: Int) {
        val bmiRecord = bmiList[position]

        holder.txtDate.text = bmiRecord.date
        holder.txtBMI.text = "BMI: ${bmiRecord.bmi}"
        holder.txtStatus.text = "Status: ${bmiRecord.status}"
        holder.txtWeight.text = "Weight: ${bmiRecord.weight} kg"
        holder.txtWeightHeight.text = "Height: ${"%.2f".format(bmiRecord.height)} m"
        holder.btnDelete.setOnClickListener {
            onDeleteClick(bmiRecord.id)
        }
        holder.itemView.setOnClickListener {
            onEdit(bmiRecord)
        }
    }

    override fun getItemCount(): Int {
        return bmiList.size
    }

    fun updateData(newList: List<BMIRecord>) {
        bmiList.clear()
        bmiList.addAll(newList)
        notifyDataSetChanged()
    }
}
