package com.example.matchmentor.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.matchmentor.R
import com.example.matchmentor.model.MentorProfile
import com.example.matchmentor.repository.MentorProfileService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


class CreateMentorActivity : AppCompatActivity() {

    private lateinit var emailMentor: EditText
    private lateinit var nomeMentor: EditText
    private lateinit var senhaMentor: EditText
    private lateinit var idadeMentor: EditText
    private lateinit var cidadeMentor: EditText
    private lateinit var profissaoMentor: EditText
    private lateinit var descricaoPessoalMentor: EditText
    private lateinit var descricaoProfissaoMentor: EditText
    private lateinit var areaDeAtuacao: EditText
    private lateinit var imageViewUserPhoto: ImageView
    private lateinit var btnChoosePhoto: Button
    private lateinit var btnCadastrarMentor: Button

    private val IMAGE_PICK_CODE = 1000
    private val PERMISSION_CODE = 1001

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://focus-clientes.com.br/MatchMentorBackEnd/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val MentorProfileService = retrofit.create(MentorProfileService::class.java)
    private var uploadedImageName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_mentor)
        supportActionBar?.hide()

        emailMentor = findViewById(R.id.textEmailMentor)
        nomeMentor = findViewById(R.id.textNomeMentor)
        senhaMentor = findViewById(R.id.textSenhaMentor)
        idadeMentor = findViewById(R.id.textIdadeMentor)
        cidadeMentor = findViewById(R.id.textCidadeMentor)
        profissaoMentor = findViewById(R.id.textProfissaoMentor)
        descricaoPessoalMentor = findViewById(R.id.textDescricaoPessoalMentor)
        descricaoProfissaoMentor = findViewById(R.id.textDescricaoProfissaoMentor)
        areaDeAtuacao = findViewById(R.id.textAreaDeAtuacao)
        imageViewUserPhoto = findViewById(R.id.imageViewUserPhoto)
        btnChoosePhoto = findViewById(R.id.buttonChoosePhoto)
        btnCadastrarMentor = findViewById(R.id.btnCadastrarMentor)

        btnChoosePhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openGallery()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_CODE
                )
            }
        }


        btnCadastrarMentor.setOnClickListener {
            val missingField = validateFields()
            if (missingField.isEmpty()) {
                createMentor()
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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val imageUri = data?.data
            imageViewUserPhoto.setImageURI(imageUri)

            val fileName = generateRandomFileName() + ".jpg"
            val filePath = getRealPathFromURI(imageUri!!)
            uploadImage(filePath, fileName)
        }
    }

    private fun generateRandomFileName(): String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..10)
            .map { charset.random() }
            .joinToString("")
    }

    private fun uploadImage(filePath: String, fileName: String) {
        val file = File(filePath)
        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("file", fileName, requestFile)

        val call = MentorProfileService.uploadImage(body)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    uploadedImageName = fileName
                    Toast.makeText(this@CreateMentorActivity, "Imagem enviada com sucesso", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@CreateMentorActivity, "Erro ao enviar imagem", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@CreateMentorActivity, "Falha ao enviar imagem: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.d("ErrorImage", t.message.toString())
            }
        })
    }

    private fun getRealPathFromURI(uri: Uri): String {
        var path = ""
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.let {
            it.moveToFirst()
            val index = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            path = it.getString(index)
            it.close()
        }
        return path
    }

    private fun validateFields(): String {
        if (nomeMentor.text.toString().trim().isEmpty()) return "nome"
        if (emailMentor.text.toString().trim().isEmpty()) return "email"
        if (senhaMentor.text.toString().trim().isEmpty()) return "senha"
        if (idadeMentor.text.toString().trim().isEmpty()) return "idade"
        if (cidadeMentor.text.toString().trim().isEmpty()) return "cidade"
        if (profissaoMentor.text.toString().trim().isEmpty()) return "Profissão"
        if (descricaoPessoalMentor.text.toString().trim().isEmpty()) return "descrição pessoal"
        if (descricaoProfissaoMentor.text.toString().trim().isEmpty()) return "descrição da profissão"
        if (areaDeAtuacao.text.toString().trim().isEmpty()) return "área de atuação"
        return ""
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun createMentor() {
        val nome = nomeMentor.text.toString().trim()
        val sobrenome = "Sobrenome" // Ajustar conforme necessário
        val email = emailMentor.text.toString().trim()
        val senha = senhaMentor.text.toString().trim()
        val idade = idadeMentor.text.toString().trim().toIntOrNull()
        val cidade = cidadeMentor.text.toString().trim()
        val profissao = profissaoMentor.text.toString().trim()
        val descricaoPessoalMentor = descricaoPessoalMentor.text.toString().trim()
        val descricaoProfissaoMentor = descricaoProfissaoMentor.text.toString().trim()
        val areaDeAtuacao = areaDeAtuacao.text.toString().trim()
        val foto = uploadedImageName ?: ""

        val mentorProfile = MentorProfile(nome, sobrenome, idade, cidade, descricaoPessoalMentor, profissao, areaDeAtuacao, descricaoProfissaoMentor, foto, email, senha)

        MentorProfileService.createMentor(mentorProfile).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CreateMentorActivity, "Conta criada com sucesso.", Toast.LENGTH_LONG).show()

                    val intent = Intent(this@CreateMentorActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorMessage = response.errorBody()?.string()?.let {
                        // Tente extrair a mensagem de erro do JSON
                        try {
                            val jsonObj = JSONObject(it)
                            jsonObj.getString("message")
                        } catch (e: JSONException) {
                            "Erro ao criar conta: ${response.code()} ${response.message()}"
                        }
                    } ?: "Erro ao criar conta: ${response.code()} ${response.message()}"

                    Toast.makeText(this@CreateMentorActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@CreateMentorActivity, "Falha ao criar conta: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })

    }
}
