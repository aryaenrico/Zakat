package com.example.zakat.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zakat.Detal_History_Activity
import com.example.zakat.R
import com.example.zakat.model.DaftarTransfer
import com.example.zakat.model.HistoryResponse
import java.text.NumberFormat
import java.util.Locale
import java.util.StringTokenizer

class historyAdapter (private val listData: ArrayList<DaftarTransfer>): RecyclerView.Adapter<historyAdapter.HistoryTrsanferAdapater>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): historyAdapter.HistoryTrsanferAdapater {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryTrsanferAdapater(view)
    }

    override fun onBindViewHolder(holder: historyAdapter.HistoryTrsanferAdapater, position: Int) {
        Glide.with(holder.itemView.context)
            .load("https://indrasela.net/mobilezakat/foto/${listData[position].foto}")
            .into(holder.imgPhoto)
        holder.tvName.text = "Tanggal : "+listData[position].tgl_penyerahan
        holder.tvTotal.text = token(currency(listData[position].total_pembayaran.toInt()))
        holder.tvTipeZakat.text ="Status: "+listData[position].status
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context,Detal_History_Activity::class.java)
            intent.putExtra("transfer",listData[position])
            holder.itemView.context.startActivity(intent)
        }
    }

    private fun currency(data:Int):String{
        val locale = Locale("IND", "ID")
        val formatter = NumberFormat.getCurrencyInstance(locale)
        return formatter.format(data)
    }

    private fun token(data: String): String {
        val tokenizer = StringTokenizer(data, ",")
        return tokenizer.nextToken()
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    class HistoryTrsanferAdapater(itemView:View): RecyclerView.ViewHolder(itemView) {
        var imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        var tvName: TextView = itemView.findViewById(R.id.tv_item_name)
        var tvTotal: TextView = itemView.findViewById(R.id.tv_total_transfer)
        var tvTipeZakat: TextView = itemView.findViewById(R.id.status)
    }

}


