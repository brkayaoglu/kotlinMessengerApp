package com.example.kotlinmessengerclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth

    private lateinit var refUsers: DatabaseReference

    private var firebaseUserID : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_login)

        setSupportActionBar(toolbar)

        supportActionBar!!.title = "Login"

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@LoginActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()

        login_btn.setOnClickListener {
            loginUser()
        }

    }

    private fun loginUser() {
        val emailFromTextView : String = email_login.text.toString()
        val passwordFromTextView : String = password_login.text.toString()

        if(emailFromTextView == ""){
            Toast.makeText(this, "Please write email!", Toast.LENGTH_LONG).show()
        }else if(passwordFromTextView == ""){
            Toast.makeText(this, "Please write password!", Toast.LENGTH_LONG).show()
        }else{
            mAuth.signInWithEmailAndPassword(emailFromTextView, passwordFromTextView).addOnCompleteListener {task ->
                if(task.isSuccessful){
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this, "Error Message: " + task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
