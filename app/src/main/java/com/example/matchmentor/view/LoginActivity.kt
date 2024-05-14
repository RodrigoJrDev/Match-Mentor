package com.example.matchmentor.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.matchmentor.R
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.matchmentor.model.UserLogin
import com.example.matchmentor.model.LoginResponse
import com.example.matchmentor.repository.AuthService
import com.example.matchmentor.repository.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var btnCadastar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        btnCadastar = findViewById(R.id.btnCadastar)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            loginUser(email, password)
        }

        btnCadastar.setOnClickListener {
            val intent = Intent(this@LoginActivity, ChooseProfileTypeActivity::class.java)
            startActivity(intent)

            finish()
        }

    }

    private fun loginUser(email: String, password: String) {
        val service = RetrofitClient.instance.create(AuthService::class.java)
        service.loginUser(UserLogin(email, password)).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.message == "Login bem-sucedido") {
                    Toast.makeText(this@LoginActivity, "Login bem-sucedido. ID: ${response.body()?.user_id}", Toast.LENGTH_LONG).show()


                    val intent = Intent(this@LoginActivity, CreateUserActivity::class.java)
                    intent.putExtra("USER_ID", response.body()?.user_id)
                    startActivity(intent)

                    // Finaliza a LoginActivity se não precisar mais dela
                    finish()

                } else {
                    Toast.makeText(this@LoginActivity, "Erro no login: ${response.body()?.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Erro de rede: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
