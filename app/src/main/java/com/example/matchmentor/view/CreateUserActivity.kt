package com.example.matchmentor.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.matchmentor.R
import com.example.matchmentor.model.UserProfile
import com.example.matchmentor.repository.UserProfileService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class CreateUserActivity : AppCompatActivity() {

    private lateinit var emailAluno: EditText
    private lateinit var nomeAluno: EditText
    private lateinit var senhaAluno: EditText
    private lateinit var idadeAluno: EditText
    private lateinit var cidadeAluno: EditText
    private lateinit var profissaoAluno: EditText
    private lateinit var descricaoAluno: EditText
    private lateinit var areaDeInteresse: EditText
    private lateinit var imageViewUserPhoto: ImageView
    private lateinit var btnChoosePhoto: Button
    private lateinit var btnCadastrarAluno: Button

    private val IMAGE_PICK_CODE = 1000
    private val PERMISSION_CODE = 1001

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://focus-clientes.com.br/MatchMentorBackEnd/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val userProfileService = retrofit.create(UserProfileService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        emailAluno = findViewById(R.id.textEmailAluno)
        nomeAluno = findViewById(R.id.textNomeAluno)
        senhaAluno = findViewById(R.id.textSenhaAluno)
        idadeAluno = findViewById(R.id.textIdadeAluno)
        cidadeAluno = findViewById(R.id.textCidadeAluno)
        profissaoAluno = findViewById(R.id.textProfissaoAluno)
        descricaoAluno = findViewById(R.id.textDescricaoAluno)
        areaDeInteresse = findViewById(R.id.textAreaDeInterresse)
        imageViewUserPhoto = findViewById(R.id.imageViewUserPhoto)
        btnChoosePhoto = findViewById(R.id.buttonChoosePhoto)
        btnCadastrarAluno = findViewById(R.id.btnCadastrarAluno)

        btnChoosePhoto.setOnClickListener {
            openGallery()
        }

        btnCadastrarAluno.setOnClickListener {
            val missingField = validateFields()
            if (missingField.isEmpty()) {
                createUser()
                hideKeyboard()
            } else {
                Toast.makeText(this, "Por favor, preencha o campo $missingField.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            imageViewUserPhoto.setImageURI(data?.data)  // Mostrar a imagem selecionada
        }
    }

    private fun validateFields(): String {
        if (nomeAluno.text.toString().trim().isEmpty()) return "nome"
        if (emailAluno.text.toString().trim().isEmpty()) return "email"
        if (senhaAluno.text.toString().trim().isEmpty()) return "senha"
        if (idadeAluno.text.toString().trim().isEmpty()) return "idade"
        if (cidadeAluno.text.toString().trim().isEmpty()) return "cidade"
        if (profissaoAluno.text.toString().trim().isEmpty()) return "profissão"
        if (descricaoAluno.text.toString().trim().isEmpty()) return "descrição"
        if (areaDeInteresse.text.toString().trim().isEmpty()) return "área de interesse"
        return ""
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun createUser() {
        val nome = nomeAluno.text.toString().trim()
        val sobrenome = "Sobrenome" // Ajustar conforme necessário
        val email = emailAluno.text.toString().trim()
        val senha = senhaAluno.text.toString().trim()
        val idade = idadeAluno.text.toString().trim().toIntOrNull()
        val cidade = cidadeAluno.text.toString().trim()
        val areaInteresse = areaDeInteresse.text.toString().trim()
        val profissao = profissaoAluno.text.toString().trim()
        val descricao = descricaoAluno.text.toString().trim()
        val foto = "URL_da_Foto" // Ajustar conforme necessário

        val userProfile = UserProfile(nome, sobrenome, idade, cidade, areaInteresse, profissao, descricao, foto, email, senha)

        userProfileService.createProfile(userProfile).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CreateUserActivity, "Conta criada com sucesso.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@CreateUserActivity, "Erro ao criar conta: ${response.code()} ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@CreateUserActivity, "Falha ao criar conta: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
