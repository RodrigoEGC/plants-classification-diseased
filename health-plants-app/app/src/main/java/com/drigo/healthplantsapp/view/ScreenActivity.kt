package com.drigo.healthplantsapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import com.drigo.healthplantsapp.MainActivity
import com.drigo.healthplantsapp.R
import com.drigo.healthplantsapp.databinding.ActivityScreenBinding

class ScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScreenBinding
    private val splashTimeOut: Long = 6000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_screen)
        binding.executePendingBindings()

        binding.cardViewDialog.animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        binding.cardViewSlogan.animation = AnimationUtils.loadAnimation(this, R.anim.bounce)

        binding.textViewWelcome.animation = AnimationUtils.loadAnimation(this, R.anim.sequential)
        binding.textViewPrimary.animation = AnimationUtils.loadAnimation(this, R.anim.open_next)
        binding.textViewSecond.animation = AnimationUtils.loadAnimation(this, R.anim.close_next)

        binding.imageView.animation = AnimationUtils.loadAnimation(this, R.anim.bounce)
        binding.textDialog.animation = AnimationUtils.loadAnimation(this, R.anim.open_next)

        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, splashTimeOut)
    }
}