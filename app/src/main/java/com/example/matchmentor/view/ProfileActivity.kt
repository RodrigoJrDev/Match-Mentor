package com.example.matchmentor.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.matchmentor.R
import com.example.matchmentor.model.Profile
import com.example.matchmentor.repository.ProfileService
import com.example.matchmentor.repository.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.hide()

        val profileImage = findViewById<ImageView>(R.id.profile_image)
        val profileName = findViewById<TextView>(R.id.profile_name)
        val profileType = findViewById<TextView>(R.id.profile_type)
        val areaDeAtuacaoInteresse = findViewById<EditText>(R.id.area_de_atuacao_interesse)
        val descricaoPessoal = findViewById<EditText>(R.id.descricao_pessoal)
        val editButton = findViewById<Button>(R.id.edit_button)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("USER_ID", -1)
        val userType = sharedPreferences.getString("USER_TYPE", "") ?: ""

        val service = RetrofitClient.instance.create(ProfileService::class.java)
        service.getProfile(userId, userType).enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                if (response.isSuccessful) {
                    val profile = response.body()
                    if (profile != null) {
                        val fullName = "${profile.nome} ${profile.sobrenome}"
                        profileName.text = fullName
                        profileType.text = if (userType == "mentor") "Mentor" else "Aluno"
                        areaDeAtuacaoInteresse.setText(if (userType == "mentor") profile.area_de_atuacao else profile.area_de_interesse)
                        descricaoPessoal.setText(profile.descricao_pessoal)

                        val imageUrl = "https://focus-clientes.com.br/MatchMentorBackEnd/icons-users/${profile.foto}"
                        Glide.with(this@ProfileActivity).load(imageUrl).circleCrop().into(profileImage)
                    }
                } else {
                    Toast.makeText(this@ProfileActivity, "Erro ao carregar o perfil", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                Toast.makeText(this@ProfileActivity, "Erro de rede: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })

        // Footer Bar
        footerBarClickListeners()
    }

    private fun footerBarClickListeners() {
        val logoIcon = findViewById<ImageView>(R.id.icon_academic)
        val starIcon = findViewById<ImageView>(R.id.icon_star)

        logoIcon.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }

        starIcon.setOnClickListener {
            val intent = Intent(this, MatchingsActivity::class.java)
            startActivity(intent)
        }

    }
}
