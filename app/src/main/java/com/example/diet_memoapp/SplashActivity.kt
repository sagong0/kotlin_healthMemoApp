package com.example.diet_memoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // ...
        // Initialize Firebase Auth
        auth = Firebase.auth

        try{
            Log.d("SPLASH",auth.currentUser!!.uid)
            Toast.makeText(this,"이미 비회원 로그인 완료", Toast.LENGTH_LONG).show()
            Handler().postDelayed({
                startActivity(Intent(this,MainActivity::class.java))
            }, 3000)

        }catch (e :Exception){
            Log.d("SPLASH","회원가입 시켜줘야됨")

            auth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(this,"비회원 로그인 완료", Toast.LENGTH_LONG).show()

                        Handler().postDelayed({
                            startActivity(Intent(this,MainActivity::class.java))
                        }, 3000)

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this,"비회원 로그인 실패", Toast.LENGTH_LONG).show()
                    }
                }
        }


    }
}