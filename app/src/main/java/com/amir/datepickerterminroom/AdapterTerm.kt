package com.amir.datepickerterminroom

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.amir.datepickerterminroom.databinding.RecordsRowBinding
import com.amir.datepickerterminroom.room.EntityTerm

class AdapterTerm(
    private val termList: MutableList<EntityTerm>,
    private val updateListener: (id: Int) -> Unit
) :
    RecyclerView.Adapter<AdapterTerm.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            RecordsRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = termList[position]

        holder.tvRowTerm.text = appointment.term
        holder.tvTowDate.text = appointment.date



        if (position % 2 == 0) {
            holder.cvMain.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.lightBlue
                )
            )
        }
        holder.cvMain.setOnClickListener {
            updateListener.invoke(appointment.id)
        }

    }

    override fun getItemCount(): Int {
        return termList.size
    }


    inner class ViewHolder(itemBinding: RecordsRowBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        val cvMain = itemBinding.cvMain
        val tvRowTerm = itemBinding.tvRowTerm
        val tvTowDate = itemBinding.tvRowDate
    }


}