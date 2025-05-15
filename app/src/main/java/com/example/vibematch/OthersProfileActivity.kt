package com.example.vibematch

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class OthersProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_others_profile)

        val eventId = intent.getIntExtra("event_id", -1)
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.btnBack).setOnClickListener {
            val intent = Intent(this, EventActivity::class.java)
            if (eventId != -1) intent.putExtra("event_id", eventId)
            startActivity(intent)
            finish()
        }
    }
} 