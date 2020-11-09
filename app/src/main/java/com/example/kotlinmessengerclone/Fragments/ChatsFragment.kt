package com.example.kotlinmessengerclone.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinmessengerclone.AdapterClasses.UserAdapter
import com.example.kotlinmessengerclone.ModelClasses.Chat
import com.example.kotlinmessengerclone.ModelClasses.ChatList
import com.example.kotlinmessengerclone.ModelClasses.User
import com.example.kotlinmessengerclone.Notifications.Token

import com.example.kotlinmessengerclone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.fragment_chats.*

/**
 * A simple [Fragment] subclass.
 */
class ChatsFragment : Fragment() {

    private var userAdapter: UserAdapter? = null

    private var mUsers: List<User>? = null

    private var usersChatList : List<ChatList>? = null

    lateinit var recycler_view_chats_list : RecyclerView

    private var firebaseUser : FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chats, container, false)

        recycler_view_chats_list = view.findViewById(R.id.recycler_view_chat_list)

        recycler_view_chats_list.setHasFixedSize(true)

        recycler_view_chats_list.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        usersChatList = ArrayList()

        val refChats = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)

        refChats!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (usersChatList as ArrayList).clear()
                for(snap in snapshot.children){
                    val chatList = snap.getValue(ChatList::class.java)

                    (usersChatList as ArrayList).add(chatList!!)
                }
                retrieveChatList()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        updateToken(FirebaseInstanceId.getInstance().token)

        return view
    }

    private fun updateToken(token: String?) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }

    private fun retrieveChatList(){
        mUsers = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("Users")

        ref!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (mUsers as ArrayList<User>).clear()

                for(snap in snapshot.children){
                    val user : User? = snap.getValue(User::class.java)

                    for(eachChatList in usersChatList!!){
                        if(user!!.getUid().equals(eachChatList.getId())){
                            (mUsers as ArrayList).add(user!!)
                        }
                    }
                    userAdapter = UserAdapter(context!!, (mUsers as ArrayList<User>), true)
                    recycler_view_chats_list.adapter = userAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}
