package com.example.matchmentor.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import com.example.matchmentor.R
import com.example.matchmentor.model.CheckSessionRequest
import com.example.matchmentor.model.Item
import com.example.matchmentor.model.SessionResponse
import com.example.matchmentor.repository.AuthService
import com.example.matchmentor.repository.RetrofitClient
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.StackFrom
import com.yuyakaido.android.cardstackview.SwipeableMethod
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

class HomePageActivity: AppCompatActivity(), CardStackListener {

    private lateinit var cardStackView: CardStackView
    private lateinit var manager: CardStackLayoutManager
    private lateinit var adapter: CardStackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        checkSession()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cardStackView = findViewById(R.id.card_stack_view)
        manager = CardStackLayoutManager(this, this)
        adapter = CardStackAdapter(createItems())

        setupCardStackView()
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

    private fun createItems(): List<Item> {
        return listOf(
            Item(R.drawable.image1, "Fushimi Inari Shrine", "Kyoto"),
            Item(R.drawable.image2, "Kiyomizu-dera", "Kyoto"),
            Item(R.drawable.image3, "Kinkaku-ji", "Kyoto"),
        )
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {
        // Do something
    }

    override fun onCardSwiped(direction: Direction?) {
        // Do something
    }

    override fun onCardRewound() {
        // Do something
    }

    override fun onCardCanceled() {
        // Do something
    }

    override fun onCardAppeared(view: View?, position: Int) {
        // Do something
    }

    override fun onCardDisappeared(view: View?, position: Int) {
        // Do something
    }
}
