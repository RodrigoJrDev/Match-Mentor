package com.example.matchmentor.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.matchmentor.R

class ChooseProfileTypeActivity : AppCompatActivity() {

    private lateinit var btnAluno: Button
    private lateinit var btnMentor: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_choose_profile_type)

        supportActionBar?.hide()

        btnAluno = findViewById(R.id.btnAluno)
        btnMentor = findViewById(R.id.btnMentor)

        btnAluno.setOnClickListener {
            val intent = Intent(this@ChooseProfileTypeActivity, CreateUserActivity::class.java)
            startActivity(intent)

            finish()
        }

        btnMentor.setOnClickListener {
            val intent = Intent(this@ChooseProfileTypeActivity, CreateMentorActivity::class.java)
            startActivity(intent)

            finish()
        }


    }
}