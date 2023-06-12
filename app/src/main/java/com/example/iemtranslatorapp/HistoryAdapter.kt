package com.example.iemtranslatorapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.iemtranslatorapp.model.History

class HistoryAdapter(private val historyList: List<History>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val translationTextView: TextView = itemView.findViewById(R.id.textView5)
        val sourceTextView: TextView = itemView.findViewById(R.id.textView4)
        val timestampTextView: TextView=itemView.findViewById(R.id.textView7)
        val translationCard= itemView.findViewById<CardView>(R.id.translationCard)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {

        val translation = historyList[position]

        holder.translationTextView.text = translation.translation
        holder.sourceTextView.text = translation.source
        holder.timestampTextView.text=translation.timestamp
        if( position %2==1){
        holder.translationCard.setBackgroundResource(R.drawable.background_border_1)
        }
        else {
            holder.translationCard.setBackgroundResource(R.drawable.background_border)
        }
    }



    override fun getItemCount(): Int {
        return historyList.size
    }
}