package com.example.matchmentor.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.matchmentor.R
import com.example.matchmentor.model.Match

class MatchingsAdapter(private var matchings: List<Match>) : RecyclerView.Adapter<MatchingsAdapter.MatchingsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchingsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_matching, parent, false)
        return MatchingsViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchingsViewHolder, position: Int) {
        val matching = matchings[position]
        holder.bind(matching)
    }

    override fun getItemCount(): Int = matchings.size

    fun updateMatchings(newMatchings: List<Match>) {
        matchings = newMatchings
        notifyDataSetChanged()
    }

    class MatchingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.name_text_view)
        private val dateTextView: TextView = itemView.findViewById(R.id.date_text_view)
        private val messageTextView: TextView = itemView.findViewById(R.id.message_text_view)
        private val profileImageView: ImageView = itemView.findViewById(R.id.profile_image_view)

        fun bind(match: Match) {
            val fullName = "${match.nome} ${match.sobrenome}"
            nameTextView.text = fullName
            dateTextView.text = match.match_date
            messageTextView.text = match.profissao

            // Carregar a imagem do perfil com Glide e aplicar transformação circular
            val imageUrl = "https://focus-clientes.com.br/MatchMentorBackEnd/icons-users/${match.foto}"
            Glide.with(itemView.context)
                .load(imageUrl)
                .transform(CircleCrop())
                .into(profileImageView)
        }
    }
}
