package com.example.matchmentor.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.matchmentor.R
import com.example.matchmentor.model.Item

class CardStackAdapter(
    private var items: List<Item>
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getItem(position: Int): Item {
        return items[position]
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.item_image)
        private val titleTextView: TextView = view.findViewById(R.id.item_title)
        private val subtitleTextView: TextView = view.findViewById(R.id.item_subtitle)

        fun bind(item: Item) {
            val imageUrl = "https://focus-clientes.com.br/MatchMentorBackEnd/icons-users/${item.foto}"
            Glide.with(imageView.context)
                .load(imageUrl)
                .into(imageView)
            titleTextView.text = item.nome
            subtitleTextView.text = item.descricao
        }
    }

    fun setItems(items: List<Item>) {
        this.items = items
        notifyDataSetChanged()
    }
}
