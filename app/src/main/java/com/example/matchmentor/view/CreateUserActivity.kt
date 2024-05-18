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
import com.example.matchmentor.model.UserProfile
import com.example.matchmentor.repository.UserProfileService
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
import kotlin.random.Random

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
    private var uploadedImageName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        supportActionBar?.hide()

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

        val call = userProfileService.uploadImage(body)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    uploadedImageName = fileName
                    Toast.makeText(this@CreateUserActivity, "Imagem enviada com sucesso", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@CreateUserActivity, "Erro ao enviar imagem", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@CreateUserActivity, "Falha ao enviar imagem: ${t.message}", Toast.LENGTH_SHORT).show()
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
        val foto = uploadedImageName ?: ""

        val userProfile = UserProfile(nome, sobrenome, idade, cidade, areaInteresse, profissao, descricao, foto, email, senha)

        userProfileService.createProfile(userProfile).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CreateUserActivity, "Conta criada com sucesso.", Toast.LENGTH_LONG).show()

                    val intent = Intent(this@CreateUserActivity, LoginActivity::class.java)
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

                    Toast.makeText(this@CreateUserActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@CreateUserActivity, "Falha ao criar conta: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}