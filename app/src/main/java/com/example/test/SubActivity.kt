package com.example.test

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
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
    // private val serverUrl = "http://localhost:3000/getAPI" //"3.37.242.54:3000/getAPI"

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
                val xCoordinate = 127.081101 //latitude
                val yCoordinate = 37.798489 //longitude

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
        var serverUrl = "http://3.37.242.54:3000/getAPI" //"3.37.242.54:3000/getAPI"
        serverUrl += "?busNumber="+selectedBusNumber+"&xCoordinate="+xCoordinate+"&yCoordinate="+yCoordinate
//        val requestBody = FormBody.Builder()
//            .add("busNumber", selectedBusNumber)
//            .add("xCoordinate", xCoordinate.toString()) // x 좌표를 문자열로 변환하여 전송
//            .add("yCoordinate", yCoordinate.toString()) // y 좌표를 문자열로 변환하여 전송
//            .build()

        val request = Request.Builder()
            .url(serverUrl)
            .get()
            //.post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("SubActivity","오류")
                // 네트워크 오류 처리
                // IOException의 메시지를 가져옴
                val errorMessage = e.message

                // 오류 메시지를 확인하여 오류 유형 식별
                if (errorMessage != null) {
                    if (errorMessage.contains("Timeout", ignoreCase = true)) {
                        Log.e("SubActivity", "네트워크 타임아웃 오류")
                    } else if (errorMessage.contains("Connection refused", ignoreCase = true)) {
                        Log.e("SubActivity", "서버 연결 거부 오류")
                    } else {
                        Log.e("SubActivity", "알 수 없는 네트워크 오류: $errorMessage")
                    }
                } else {
                    Log.e("SubActivity", "알 수 없는 네트워크 오류 발생")
                }
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    Log.e("responseData","$responseData")
                    // 응답 데이터를 다음 화면으로 전달
                    val intent = Intent(this@SubActivity, SubActivity2::class.java)
                    intent.putExtra("responseData", responseData)
                    startActivity(intent)
                    Log.d("SubActivity", "성공")
                } else {
                    // 응답이 실패한 경우 처리
                    Log.e("SubActivity", "실패")
                }
            }
        })
    }
}
