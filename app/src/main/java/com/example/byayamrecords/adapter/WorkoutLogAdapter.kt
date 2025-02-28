package com.example.byayamrecords.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.byayamrecords.R
import com.example.byayamrecords.model.WorkoutLog
import com.example.byayamrecords.ui.activity.updateLogActivity

class WorkoutLogAdapter(
    var context: Context,
    var data: ArrayList<WorkoutLog>
) : RecyclerView.Adapter<WorkoutLogAdapter.WorkoutViewHolder>() {

    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val edit: TextView = itemView.findViewById(R.id.lblEdit)
        val wName: TextView = itemView.findViewById(R.id.displayName)
        val wSets: TextView = itemView.findViewById(R.id.displaySets)
        val wWeight: TextView = itemView.findViewById(R.id.displayWeight)
        val pDesc: TextView = itemView.findViewById(R.id.displayDesc)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val itemView: View = LayoutInflater.from(context)
            .inflate(R.layout.sample_workout, parent, false)
        return WorkoutViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = data[position]

        holder.wName.text = workout.WorkoutName
        holder.wSets.text = workout.WorkoutSets.toString()
        holder.wWeight.text = workout.WorkoutWeight
        holder.pDesc.text = workout.WorkoutDesc


        holder.progressBar.visibility = View.GONE

        holder.edit.setOnClickListener {
            val intent = Intent(context, updateLogActivity::class.java)
            intent.putExtra("productId", workout.LogId)
            context.startActivity(intent)
        }
    }

    fun updateData(products: List<WorkoutLog>) {
        data.clear()
        data.addAll(products)
        notifyDataSetChanged()
    }

    fun getProductId(position: Int): String {
        return data[position].LogId
    }
}
