package com.example.matchmentor.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.matchmentor.R
import com.example.matchmentor.model.Profile

class ProfileAdapter(private var profiles: List<Profile>) : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_profile, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val profile = profiles[position]
        holder.bind(profile)
    }

    override fun getItemCount(): Int = profiles.size

    fun updateProfiles(newProfiles: List<Profile>) {
        profiles = newProfiles
        notifyDataSetChanged()
    }

    class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.name_text_view)
        private val ageTextView: TextView = itemView.findViewById(R.id.age_text_view)
        private val infoTextView: TextView = itemView.findViewById(R.id.info_text_view)
        private val photoImageView: ImageView = itemView.findViewById(R.id.photo_image_view)

        fun bind(profile: Profile) {
            val fullName = "${profile.nome} ${profile.sobrenome}"
            nameTextView.text = fullName
            ageTextView.text = profile.idade?.toString() ?: "N/A"

            val info = StringBuilder()
            profile.cidade?.let { info.append("Cidade: $it\n") }
            profile.area_de_interesse?.let { info.append("Interesse: $it\n") }
            profile.profissao?.let { info.append("Profissão: $it\n") }
            profile.area_de_atuacao?.let { info.append("Área de Atuação: $it\n") }
            profile.descricao_pessoal?.let { info.append("Descrição: $it\n") }

            infoTextView.text = info.toString().trim()

            if (!profile.foto.isNullOrEmpty()) {
                val imageUrl = "https://focus-clientes.com.br/MatchMentorBackEnd/icons-users/${profile.foto}"
                Glide.with(itemView.context).load(imageUrl).into(photoImageView)
            } else {
                photoImageView.setImageResource(R.drawable.image1)
            }
        }
    }
}
