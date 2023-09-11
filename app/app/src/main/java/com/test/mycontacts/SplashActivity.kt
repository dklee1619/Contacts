package com.test.mycontacts

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.anim_splash)

        val messageImage = findViewById<ImageView>(R.id.message_image)
        val contactText = findViewById<TextView>(R.id.contact_text)

        // 메시지 이미지에 통통 튀는 애니메이션 적용
        val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
        val linearAnimation = AnimationUtils.loadAnimation(this, R.anim.linear)

        // 애니메이션 시작 시 이미지와 텍스트를 보이도록 설정
        bounceAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                messageImage.visibility = ImageView.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation) {
                contactText.startAnimation(linearAnimation)
            }

            override fun onAnimationRepeat(animation: Animation?) {
                // 애니메이션 반복 시 필요한 처리 (무조건 구현해야 함)
            }
        })

        linearAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                contactText.visibility = TextView.VISIBLE
            }


            override fun onAnimationEnd(animation: Animation) {
                Handler().postDelayed({
                    val intent = Intent(this@MessageActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 1500) // 1초 대기
            }

            override fun onAnimationRepeat(animation: Animation?) {
                // 애니메이션 반복 시 필요한 처리 (무조건 구현해야 함)
            }
        })

        messageImage.startAnimation(bounceAnimation)
    }
}
