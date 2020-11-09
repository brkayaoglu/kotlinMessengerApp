package com.example.kotlinmessengerclone

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinmessengerclone.AdapterClasses.ChatsAdapter
import com.example.kotlinmessengerclone.Fragments.APIService
import com.example.kotlinmessengerclone.ModelClasses.Chat
import com.example.kotlinmessengerclone.ModelClasses.User
import com.example.kotlinmessengerclone.Notifications.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message_chat.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessageChatActivity : AppCompatActivity() {

    var userIdVisit : String = ""

    var firebaseUser : FirebaseUser? = null

    var chatsAdapter : ChatsAdapter? = null

    var mChatList: List<Chat>? = null

    var reference : DatabaseReference? = null

    lateinit var recycler_view_chats: RecyclerView

    var notify = false

    var apiService : APIService? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_message_chat)

        setSupportActionBar(toolbar)

        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{
            val intent = Intent(this@MessageChatActivity, WelcomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        apiService = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)

        intent = intent
        userIdVisit = intent.getStringExtra("visit_id")

        firebaseUser = FirebaseAuth.getInstance().currentUser

        recycler_view_chats = findViewById(R.id.recycler_view_m_chat)
        recycler_view_chats.setHasFixedSize(true)

        var layoutManager = LinearLayoutManager(applicationContext)

        layoutManager.stackFromEnd = true

        recycler_view_chats.layoutManager = layoutManager

        reference = FirebaseDatabase.getInstance().reference.child("Users").child(userIdVisit)

        reference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user : User? = snapshot.getValue(User::class.java)

                username_message_chat.text = user!!.getUsername()
                Picasso.get().load(user!!.getProfile()).into(profile_image_message_chat)

                retrieveMessages(firebaseUser!!.uid, userIdVisit, user.getProfile())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        send_message_m_chat.setOnClickListener {
            notify = true
            val message = text_message.text.toString()
            if(message == ""){
                Toast.makeText(this, "Please write message..", Toast.LENGTH_SHORT).show()
            }else{
                sendMessageToUser(firebaseUser!!.uid, userIdVisit, message)
            }
            text_message.setText("")
        }

        attach_image_file_m_chat.setOnClickListener {
            notify = true
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent,"Pick Image"), 438)
        }

        seenMessage(userIdVisit)
    }
    private fun sendMessageToUser(sender_id: String, receiver_id: String?, message: String) {
        val reference = FirebaseDatabase.getInstance().reference

        val messageKey = reference.push().key

        val messageHashMap = HashMap<String, Any?>()

        messageHashMap["sender"] = sender_id
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiver_id
        messageHashMap["isSeen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey

        reference.child("Chats").child(messageKey!!).setValue(messageHashMap).addOnCompleteListener {
            task ->
            if (task.isSuccessful){
                val chatsListReference = FirebaseDatabase.getInstance().reference.child("ChatList")
                    .child(firebaseUser!!.uid).child(userIdVisit)

                chatsListReference.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()){
                            chatsListReference.child("id").setValue(userIdVisit)
                        }
                        val chatsListReceiverReference = FirebaseDatabase.getInstance().reference.child("ChatList")
                            .child(userIdVisit).child(firebaseUser!!.uid)

                        chatsListReceiverReference.child("id").setValue(firebaseUser!!.uid)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

                //push notification
            }
        }
        val userReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        userReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)

                if(notify){
                    sendNotification(receiver_id, user!!.getUsername(), message)
                }
                notify = false
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun sendNotification(receiverId: String?, username: String, message: String) {

        var ref = FirebaseDatabase.getInstance().reference.child("Tokens")

        val query = ref.orderByKey().equalTo(receiverId)

        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(snap in snapshot.children){
                    val token : Token? = snap.getValue(Token::class.java)

                    val data = Data(firebaseUser!!.uid, R.mipmap.ic_launcher,"$username: $message","New message",userIdVisit)

                    val sender = Sender(data!!,token!!.getToken().toString())

                    apiService!!.sendNotification(sender).enqueue(object : Callback<MyResponse>{
                        override fun onResponse(
                            call: Call<MyResponse>,
                            response: Response<MyResponse>
                        ) {
                            if(response.code() == 200){
                                if(response.body()!!.success !== 1){
                                    Toast.makeText(this@MessageChatActivity, "Failed. Nothing happen..",Toast.LENGTH_LONG).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                            TODO("Not yet implemented")
                        }

                    })

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun retrieveMessages(sender_id: String, receiver_id: String?, receiver_profile_image_url: String) {
        mChatList = ArrayList()

        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (mChatList as ArrayList<Chat>).clear()

                for (snap in snapshot.children){
                    val chat : Chat? = snap.getValue(Chat::class.java)

                    if((chat!!.getReceiver().equals(receiver_id) && chat!!.getSender().equals(sender_id))|| (chat!!.getReceiver().equals(sender_id) && chat!!.getSender().equals(receiver_id))){
                        (mChatList as ArrayList<Chat>).add(chat)
                    }
                    chatsAdapter = ChatsAdapter(this@MessageChatActivity, (mChatList as ArrayList<Chat>), receiver_profile_image_url)
                    recycler_view_chats.adapter = chatsAdapter
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 438 && resultCode == Activity.RESULT_OK && data!=null && data!!.data != null){
            val loadingBar = ProgressDialog(this)
            loadingBar.setMessage("Please wait, image is sending...")
            loadingBar.show()

            val fileUri = data.data

            val storageRef = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference

            val messageId = ref.push().key

            val filePath = storageRef.child("$messageId.jpg")

            val uploadTask = filePath.putFile(fileUri!!)

            uploadTask.continueWith { task ->
                if(!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
                filePath.downloadUrl
            }.addOnCompleteListener {task->
                if(task.isSuccessful){
                    task.result!!.addOnSuccessListener{task ->
                        val url = task.toString()

                        val messageHashMap = HashMap<String, Any?>()

                        messageHashMap["sender"] = firebaseUser!!.uid
                        messageHashMap["message"] = "sent you an image."
                        messageHashMap["receiver"] = userIdVisit
                        messageHashMap["isSeen"] = false
                        messageHashMap["url"] = url
                        messageHashMap["messageId"] = messageId

                        ref.child("Chats").child(messageId!!).setValue(messageHashMap)
                            .addOnCompleteListener {
                                task ->
                                if (task.isSuccessful){
                                    val reference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

                                    reference.addValueEventListener(object : ValueEventListener{
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val user = snapshot.getValue(User::class.java)

                                            if(notify){
                                                sendNotification(userIdVisit, user!!.getUsername(), "sent you an image.")
                                            }
                                            notify = false
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }

                                    })
                                }
                            }


                        loadingBar.dismiss()
                    }
                }
            }
        }
    }

    var seenListener : ValueEventListener? = null

    private fun seenMessage(userId : String){
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        seenListener = reference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(snap in snapshot.children){
                    val chat = snap.getValue(Chat::class.java)

                    if((chat!!.getReceiver().equals(firebaseUser!!.uid)) && (chat!!.getSender().equals(userId))){
                        val hashMap = HashMap<String, Any?>()

                        hashMap["isSeen"] = true

                        snap.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onPause() {
        super.onPause()
        reference!!.removeEventListener(seenListener!!)
    }

}