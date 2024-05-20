package com.example.matchmentor.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.matchmentor.R
import com.example.matchmentor.model.Profile
import com.example.matchmentor.repository.ProfileService
import com.example.matchmentor.repository.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchProfileActivity : AppCompatActivity() {

    private lateinit var searchInput: AutoCompleteTextView
    private lateinit var searchButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var profileAdapter: ProfileAdapter
    private lateinit var noResultsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_profile)

        searchInput = findViewById(R.id.search_input)
        searchButton = findViewById(R.id.search_button)
        recyclerView = findViewById(R.id.recycler_view)
        noResultsTextView = findViewById(R.id.no_results_text_view)
        profileAdapter = ProfileAdapter(emptyList())

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = profileAdapter

        searchButton.setOnClickListener {
            val searchQuery = searchInput.text.toString().trim()
            if (searchQuery.isNotEmpty()) {
                performSearch(searchQuery)
            } else {
                Toast.makeText(this, "Por favor, digite uma área de interesse", Toast.LENGTH_SHORT).show()
            }
        }

        searchInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchInput.text.isEmpty()) {
                loadSuggestions()
            }
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    loadSuggestions()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        loadSuggestions()
    }

    private fun loadSuggestions() {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userType = sharedPreferences.getString("USER_TYPE", "usuario") ?: "usuario"

        val service = RetrofitClient.instance.create(ProfileService::class.java)
        service.getSuggestions(userType).enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val suggestions = response.body() ?: emptyList()
                    val adapter = ArrayAdapter(this@SearchProfileActivity, android.R.layout.simple_dropdown_item_1line, suggestions)
                    searchInput.setAdapter(adapter)
                    searchInput.showDropDown()
                } else {
                    Toast.makeText(this@SearchProfileActivity, "Erro ao carregar sugestões", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Toast.makeText(this@SearchProfileActivity, "Erro de rede: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun performSearch(query: String) {
        searchButton.isEnabled = false
        searchButton.text = "Pesquisando..."

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("USER_ID", -1)
        val userType = sharedPreferences.getString("USER_TYPE", "usuario") ?: "usuario"

        val service = RetrofitClient.instance.create(ProfileService::class.java)
        service.getProfiles(userId, userType, query).enqueue(object : Callback<List<Profile>> {
            override fun onResponse(call: Call<List<Profile>>, response: Response<List<Profile>>) {
                searchButton.isEnabled = true
                searchButton.text = "Pesquisar"

                if (response.isSuccessful) {
                    val profiles = response.body() ?: emptyList()
                    if (profiles.isEmpty()) {
                        profileAdapter.updateProfiles(emptyList())
                        noResultsTextView.visibility = View.VISIBLE
                    } else {
                        noResultsTextView.visibility = View.GONE
                        profileAdapter.updateProfiles(profiles)
                    }
                } else {
                    Toast.makeText(this@SearchProfileActivity, "Erro ao carregar perfis", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Profile>>, t: Throwable) {
                searchButton.isEnabled = true
                searchButton.text = "Pesquisar"
                Toast.makeText(this@SearchProfileActivity, "Erro de rede: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
