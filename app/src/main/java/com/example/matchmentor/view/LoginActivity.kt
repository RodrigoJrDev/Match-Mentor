package com.example.matchmentor.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.matchmentor.R
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

            loginButton.isEnabled = false
            loginButton.text = "Logando..."

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
                    val userId = response.body()?.user_id
                    val userEmail = response.body()?.email_user
                    val userType = response.body()?.type_user

                    val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    if (userId != null) {
                        editor.putInt("USER_ID", userId)
                    }
                    editor.putString("USER_EMAIL", userEmail)
                    editor.putString("USER_TYPE", userType)
                    editor.apply()

                    val intent = Intent(this@LoginActivity, HomePageActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Erro no login: ${response.body()?.message}", Toast.LENGTH_LONG).show()
                }

                loginButton.isEnabled = true
                loginButton.text = "Login"
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Erro de rede: ${t.message}", Toast.LENGTH_LONG).show()
                loginButton.isEnabled = true
                loginButton.text = "Login"
            }
        })
    }
}
