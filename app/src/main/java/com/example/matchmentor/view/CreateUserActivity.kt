package com.example.matchmentor.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.matchmentor.R
import com.example.matchmentor.model.UserProfile
import com.example.matchmentor.repository.UserProfileService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random

class CreateUserActivity : AppCompatActivity() {

    private lateinit var emailAluno: EditText
    private lateinit var nomeAluno: EditText
    private lateinit var sobreNomeAluno: EditText
    private lateinit var senhaAluno: EditText
    private lateinit var idadeAluno: EditText
    private lateinit var cidadeAluno: EditText
    private lateinit var profissaoAluno: EditText
    private lateinit var descricaoAluno: EditText
    private lateinit var areaDeInteresse: EditText
    private lateinit var imageViewUserPhoto: ImageView
    private lateinit var btnChoosePhoto: Button
    private lateinit var btnCadastrarAluno: Button

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://focus-clientes.com.br/MatchMentorBackEnd/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val userProfileService = retrofit.create(UserProfileService::class.java)
    private var uploadedImageName: String? = null

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var takePhotoLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        supportActionBar?.hide()

        emailAluno = findViewById(R.id.textEmailAluno)
        nomeAluno = findViewById(R.id.textNomeAluno)
        sobreNomeAluno = findViewById(R.id.textSobrenomeAluno)
        senhaAluno = findViewById(R.id.textSenhaAluno)
        idadeAluno = findViewById(R.id.textIdadeAluno)
        cidadeAluno = findViewById(R.id.textCidadeAluno)
        profissaoAluno = findViewById(R.id.textProfissaoAluno)
        descricaoAluno = findViewById(R.id.textDescricaoAluno)
        areaDeInteresse = findViewById(R.id.textAreaDeInterresse)
        imageViewUserPhoto = findViewById(R.id.imageViewUserPhoto)
        btnChoosePhoto = findViewById(R.id.buttonChoosePhoto)
        btnCadastrarAluno = findViewById(R.id.btnCadastrarAluno)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            showImageSourceDialog()
        }

        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                imageViewUserPhoto.setImageURI(imageUri)
                val fileName = generateRandomFileName() + ".jpg"
                val filePath = getRealPathFromURI(imageUri!!)
                uploadImage(filePath, fileName)
            }
        }

        takePhotoLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                imageViewUserPhoto.setImageBitmap(imageBitmap)
                val fileName = generateRandomFileName() + ".jpg"
                val file = createImageFile()
                val outStream = FileOutputStream(file)
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                outStream.flush()
                outStream.close()
                uploadImage(file.absolutePath, fileName)
            }
        }

        btnChoosePhoto.setOnClickListener {
            checkAndRequestPermissions()
        }

        btnCadastrarAluno.setOnClickListener {
            val missingField = validateFields()
            if (uploadedImageName == null) {
                Toast.makeText(this, "Por favor, suba uma imagem.", Toast.LENGTH_LONG).show()
            } else if (missingField.isEmpty()) {
                btnCadastrarAluno.isEnabled = false
                btnCadastrarAluno.text = "Criando conta..."
                createUser()
                hideKeyboard()
            } else {
                Toast.makeText(this, "Por favor, preencha o campo $missingField.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val readImagesPermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        val writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val listPermissionsNeeded = mutableListOf<String>()

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (readImagesPermission != PackageManager.PERMISSION_GRANTED) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                listPermissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (listPermissionsNeeded.isNotEmpty()) {
            requestPermissionLauncher.launch(listPermissionsNeeded.toTypedArray())
        } else {
            showImageSourceDialog()
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Galeria", "Câmera")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Escolha a fonte da imagem")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openGallery()
                1 -> openCamera()
            }
        }
        builder.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePhotoLauncher.launch(cameraIntent)
    }

    private fun createImageFile(): File {
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(generateRandomFileName(), ".jpg", storageDir)
    }

    private fun generateRandomFileName(): String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..10)
            .map { charset.random() }
            .joinToString("")
    }

    private fun uploadImage(filePath: String, fileName: String) {
        val file = File(filePath)
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", fileName, requestFile)

        val call = userProfileService.uploadImage(body)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    uploadedImageName = fileName
                    Toast.makeText(this@CreateUserActivity, "Imagem enviada com sucesso", Toast.LENGTH_SHORT).show()
                    btnCadastrarAluno.isEnabled = true
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
        if (sobreNomeAluno.text.toString().trim().isEmpty()) return "Sobrenome"
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
        val sobrenome = sobreNomeAluno.text.toString().trim()
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
                    btnCadastrarAluno.isEnabled = true
                    btnCadastrarAluno.text = "Cadastrar conta"
                    val errorMessage = response.errorBody()?.string()?.let {
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

