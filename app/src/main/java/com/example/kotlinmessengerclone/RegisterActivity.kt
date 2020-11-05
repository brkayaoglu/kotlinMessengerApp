package com.example.kotlinmessengerclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth

    private lateinit var refUsers: DatabaseReference

    private var firebaseUserID : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_register)

        setSupportActionBar(toolbar)

        supportActionBar!!.title = "Register"

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@RegisterActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
            finish()
        }

        mAuth = FirebaseAuth.getInstance()

        register_btn.setOnClickListener {
            registerUser()
        }


    }

    private fun registerUser() {
        val usernameFromTextView : String = username_register.text.toString()
        val emailFromTextView : String = email_register.text.toString()
        val passwordFromTextView : String = password_register.text.toString()

        if(usernameFromTextView == ""){
            Toast.makeText(this, "Please write username!", Toast.LENGTH_LONG).show()
        }else if(emailFromTextView == ""){
            Toast.makeText(this, "Please write email!", Toast.LENGTH_LONG).show()
        }else if(passwordFromTextView == ""){
            Toast.makeText(this, "Please write password!", Toast.LENGTH_LONG).show()
        }else{
            mAuth.createUserWithEmailAndPassword(emailFromTextView, passwordFromTextView).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    firebaseUserID = mAuth.currentUser!!.uid
                    refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)

                    val userHashMap = HashMap<String, Any>()
                    userHashMap["uid"] = firebaseUserID
                    userHashMap["username"] = usernameFromTextView
                    userHashMap["profile"] = "https://firebasestorage.googleapis.com/v0/b/kotlinmessengerclone-8d725.appspot.com/o/profile.png?alt=media&token=a7e7ab75-205a-43dd-8a53-745898a408ff"
                    userHashMap["cover"] = "https://firebasestorage.googleapis.com/v0/b/kotlinmessengerclone-8d725.appspot.com/o/cover.jpg?alt=media&token=51c915a4-97d4-47da-95a8-462f666ef37c"
                    userHashMap["status"] = "offline"
                    userHashMap["search"] = usernameFromTextView.toLowerCase()
                    userHashMap["facebook"] = "https://m.facebook.com"
                    userHashMap["instagram"] = "https://m.instagram.com"
                    userHashMap["website"] = "https://www.google.com"

                    refUsers.updateChildren(userHashMap).addOnCompleteListener{task ->
                        if(task.isSuccessful){
                            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }else{

                        }
                    }
                }else{
                    Toast.makeText(this, "Error Message: " + task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
