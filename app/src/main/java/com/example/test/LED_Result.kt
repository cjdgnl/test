package com.example.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.widget.EditText
import android.widget.TextView
import android.animation.ObjectAnimator

class LED_Result:  ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.led_result)

        // 입력값 가져오기
        val userInput = intent.getStringExtra("userInput")

        // 텍스트뷰에 입력값 설정
        val displayText = findViewById<TextView>(R.id.displayText)
        displayText.text = userInput

        // 아래에서 위로 애니메이션 적용
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val animation = ObjectAnimator.ofFloat(displayText, "translationY", screenWidth, -screenWidth)
        animation.duration = 6000  // 지속 시간 (6초)
        animation.start()
    }
}