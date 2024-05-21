package com.example.matchmentor.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import com.example.matchmentor.R
import com.example.matchmentor.model.CheckSessionRequest
import com.example.matchmentor.model.Item
import com.example.matchmentor.model.SessionResponse
import com.example.matchmentor.model.MatchRequest
import com.example.matchmentor.model.MatchResponse
import com.example.matchmentor.repository.AuthService
import com.example.matchmentor.repository.MatchmakingService
import com.example.matchmentor.repository.ProfileService
import com.example.matchmentor.repository.RetrofitClient
import com.example.matchmentor.viewmodel.NotificationHandler
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.StackFrom
import com.yuyakaido.android.cardstackview.SwipeableMethod
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomePageActivity : AppCompatActivity(), CardStackListener {

    private lateinit var cardStackView: CardStackView
    private lateinit var manager: CardStackLayoutManager
    private lateinit var adapter: CardStackAdapter
    private lateinit var notificationHandler: NotificationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cardStackView = findViewById(R.id.card_stack_view)
        manager = CardStackLayoutManager(this, this)
        adapter = CardStackAdapter(emptyList(), this::onLikeClick, this::onDislikeClick)
        notificationHandler = NotificationHandler(this)

        setupCardStackView()
        checkSession()
        loadProfiles()

        // Footer Bar
        footerBarClickListeners()
    }

    private fun onLikeClick(item: Item) {
        swipeCard(Direction.Right)
    }

    private fun onDislikeClick(item: Item) {
        swipeCard(Direction.Left)
    }

    private fun swipeCard(direction: Direction) {
        if (manager.topPosition < adapter.itemCount) {
            when (direction) {
                Direction.Right -> {
                    manager.setSwipeAnimationSetting(
                        com.yuyakaido.android.cardstackview.SwipeAnimationSetting.Builder()
                            .setDirection(Direction.Right)
                            .setDuration(200)
                            .setInterpolator(LinearInterpolator())
                            .build()
                    )
                    cardStackView.swipe()
                }
                Direction.Left -> {
                    manager.setSwipeAnimationSetting(
                        com.yuyakaido.android.cardstackview.SwipeAnimationSetting.Builder()
                            .setDirection(Direction.Left)
                            .setDuration(200)
                            .setInterpolator(LinearInterpolator())
                            .build()
                    )
                    cardStackView.swipe()
                }
                else -> { /* Do nothing */ }
            }
        }
    }

    private fun footerBarClickListeners() {
        val searchIcon = findViewById<ImageView>(R.id.icon_search)
        searchIcon.setOnClickListener {
            val intent = Intent(this, SearchProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkSession() {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("USER_ID", -1)
        val userEmail = sharedPreferences.getString("USER_EMAIL", null)
        val userType = sharedPreferences.getString("USER_TYPE", null)
        if (userId == -1 || userEmail == null || userType == null) {
            Toast.makeText(this, "Sessão inválida", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val service = RetrofitClient.instance.create(AuthService::class.java)
        val request = CheckSessionRequest(userId, userEmail, userType)
        service.checkSession(request).enqueue(object : Callback<SessionResponse> {
            override fun onResponse(call: Call<SessionResponse>, response: Response<SessionResponse>) {
                if (response.isSuccessful && response.body()?.logged_in == true) {
                    setupCardStackView()
                } else {
                    Toast.makeText(this@HomePageActivity, "Sessão inválida", Toast.LENGTH_LONG).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<SessionResponse>, t: Throwable) {
                Toast.makeText(this@HomePageActivity, "Erro de rede: ${t.message}", Toast.LENGTH_LONG).show()
                finish()
            }
        })
    }

    private fun setupCardStackView() {
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(false)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())

        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator = DefaultItemAnimator()
    }

    private fun loadProfiles() {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("USER_ID", -1)
        val userType = sharedPreferences.getString("USER_TYPE", null)

        if (userId == -1 || userType == null) {
            Toast.makeText(this, "Sessão inválida", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val service = RetrofitClient.instance.create(ProfileService::class.java)
        val call = if (userType == "mentor") {
            service.getProfilesForMentor(userId, "mentor")
        } else {
            service.getProfilesForUser(userId, "usuario")
        }
        call.enqueue(object : Callback<List<Item>> {
            override fun onResponse(call: Call<List<Item>>, response: Response<List<Item>>) {
                if (response.isSuccessful) {
                    Log.d("Itens Card", response.body().toString())
                    adapter.setItems(response.body() ?: emptyList())
                } else {
                    Toast.makeText(this@HomePageActivity, "Erro ao carregar perfis", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Item>>, t: Throwable) {
                Toast.makeText(this@HomePageActivity, "Erro de rede: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
        // Implement as needed
    }

    override fun onCardSwiped(direction: Direction?) {
        val position = manager.topPosition - 1
        val item = adapter.getItem(position)
        if (direction == Direction.Right) {
            updateMatchmaking(item.id, true)
        } else if (direction == Direction.Left) {
            updateMatchmaking(item.id, false)
        }
    }

    private fun updateMatchmaking(profileId: Int, match: Boolean) {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("USER_ID", -1)
        val userType = sharedPreferences.getString("USER_TYPE", "usuario") ?: "usuario"

        val service = RetrofitClient.instance.create(MatchmakingService::class.java)
        val request = MatchRequest(userId, profileId, userType, match)
        service.updateMatch(request).enqueue(object : Callback<MatchResponse> {
            override fun onResponse(call: Call<MatchResponse>, response: Response<MatchResponse>) {
                Log.d("Resposta de Match", response.body().toString())
                if (response.isSuccessful) {
                    if (response.body()?.isMatch == true) {
                        notificationHandler.showSimpleNotification()
                        Toast.makeText(this@HomePageActivity, "Match encontrado!", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@HomePageActivity, "Erro ao atualizar match", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<MatchResponse>, t: Throwable) {
                Toast.makeText(this@HomePageActivity, "Erro de rede: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onCardRewound() {
        // Implement as needed
    }

    override fun onCardCanceled() {
        // Implement as needed
    }

    override fun onCardAppeared(view: View?, position: Int) {
        // Implement as needed
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        // Implement as needed
    }
}

