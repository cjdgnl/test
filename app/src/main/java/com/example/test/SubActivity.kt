package com.example.test

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import okhttp3.*
import java.io.IOException


class SubActivity : ComponentActivity() {
    // 위치 권한 요청 코드
    private val LOCATION_PERMISSION_REQUEST = 1
    // 서버 엔드포인트 URL
    private val serverUrl = "3.37.242.54:3000/getAPI"

    // 선택된 버스 번호와 위치 정보를 저장하는 변수
    private var selectedBusNumber: String = ""
    private var locationData: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub) // activity_sub.xml에 정의된 레이아웃을 설정

        val editText = findViewById<EditText>(R.id.sub_2) // 입력된 버스 번호 값
        val button = findViewById<Button>(R.id.sub_1)

        button.setOnClickListener {
            val inputText = editText.text.toString()
            selectedBusNumber = inputText

            // 위치 정보 가져오고 데이터를 서버로 전송
            requestLocationAndSendData()

//            val intent = Intent(this, SubActivity2::class.java)
//            startActivity(intent)
        }
    }
    private fun requestLocationAndSendData() {
        // 위치 권한이 승인되었는지 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            location?.let {
                // 위치 정보 가져오기
                val latitude = it.latitude
                val longitude = it.longitude

                // 위치 정보 가져오기
                val xCoordinate = latitude
                val yCoordinate = longitude

                // 서버로 데이터 전송
                sendDataToServer(xCoordinate, yCoordinate)
            }
        } else {
            // 권한이 없는 경우 권한 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        }
    }
    private fun sendDataToServer(xCoordinate: Double, yCoordinate: Double) {
        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("busNumber", selectedBusNumber)
            .add("xCoordinate", xCoordinate.toString()) // x 좌표를 문자열로 변환하여 전송
            .add("yCoordinate", yCoordinate.toString()) // y 좌표를 문자열로 변환하여 전송

        val request = Request.Builder()
            .url(serverUrl)
            .post(requestBody.build())
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 네트워크 오류 처리
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    // 응답 데이터를 다음 화면으로 전달
                    val intent = Intent(this@SubActivity, SubActivity2::class.java)
                    intent.putExtra("responseData", responseData)
                    startActivity(intent)
                } else {
                    // 응답이 실패한 경우 처리
                }
            }
        })
    }
}
