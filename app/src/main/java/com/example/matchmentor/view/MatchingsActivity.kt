package com.example.matchmentor.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.matchmentor.R
import com.example.matchmentor.model.Match
import com.example.matchmentor.repository.ProfileService
import com.example.matchmentor.repository.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MatchingsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var matchingsAdapter: MatchingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matchings)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        matchingsAdapter = MatchingsAdapter(emptyList())
        recyclerView.adapter = matchingsAdapter

        fetchMatchings()

        footerBarClickListeners()
    }

    private fun fetchMatchings() {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("USER_ID", -1)
        val userType = sharedPreferences.getString("USER_TYPE", "usuario") ?: "usuario"

        val service = RetrofitClient.instance.create(ProfileService::class.java)
        service.getMatchings(userId, userType).enqueue(object : Callback<List<Match>> {
            override fun onResponse(call: Call<List<Match>>, response: Response<List<Match>>) {
                if (response.isSuccessful) {
                    val matchings = response.body() ?: emptyList()
                    Log.d("Matchs", matchings.toString())
                    matchingsAdapter.updateMatchings(matchings)
                } else {
                    Toast.makeText(this@MatchingsActivity, "Erro ao carregar matchings", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Match>>, t: Throwable) {
                Toast.makeText(this@MatchingsActivity, "Erro de rede: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun footerBarClickListeners() {
        val logoIcon = findViewById<ImageView>(R.id.icon_academic)
        val searchIcon = findViewById<ImageView>(R.id.icon_search)
        val profileIcon = findViewById<ImageView>(R.id.icon_profile)

        logoIcon.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }

        searchIcon.setOnClickListener {
            val intent = Intent(this, SearchProfileActivity::class.java)
            startActivity(intent)
        }

        profileIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
