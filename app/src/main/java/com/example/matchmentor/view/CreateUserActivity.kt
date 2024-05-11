package com.example.matchmentor.view

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.matchmentor.R
import com.example.matchmentor.repository.UserProfile
import com.example.matchmentor.repository.UserProfileService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CreateUserActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var experienceEditText: EditText
    private lateinit var skillsEditText: EditText
    private lateinit var createUserButton: Button

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://focus-clientes.com.br/MatchMentorBackEnd/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val userProfileService = retrofit.create(UserProfileService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user_2)

        nameEditText = findViewById(R.id.editTextName)
        experienceEditText = findViewById(R.id.editTextExperience)
        skillsEditText = findViewById(R.id.editTextSkills)
        createUserButton = findViewById(R.id.buttonCreateUser)

        createUserButton.setOnClickListener {
            createUser()
        }
    }

    private fun createUser() {
        val name = nameEditText.text.toString().trim()
        val experience = experienceEditText.text.toString().trim()

        val userProfile = UserProfile(name, experience)

        userProfileService.createProfile(userProfile).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.i("CreateUser", "Profile created successfully")
                } else {
                    Log.e("CreateUser", "Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("CreateUser", "Failure: ${t.message}")
            }
        })
    }

}
