package com.example.kotlinmessengerclone.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinmessengerclone.AdapterClasses.UserAdapter
import com.example.kotlinmessengerclone.ModelClasses.User

import com.example.kotlinmessengerclone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_searchs.*

/**
 * A simple [Fragment] subclass.
 */
class SearchsFragment : Fragment() {

    private var userAdapter: UserAdapter? = null

    private var mUsers: List<User>? = null

    private var recyclerView: RecyclerView? = null

    private var searchEditText : EditText? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View =  inflater.inflate(R.layout.fragment_searchs, container, false)

        recyclerView = view.findViewById(R.id.searhUsersList)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)

        searchEditText = view.findViewById(R.id.searchUsersET)

        mUsers = ArrayList()

        retrieveAllUsers()

        searchEditText!!.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(cs: CharSequence?, start: Int, before: Int, count: Int) {
                searchForUsers(cs.toString().toLowerCase())
            }

        })

        return view
    }

    private fun retrieveAllUsers() {
        val firebaseUserUID = FirebaseAuth.getInstance().currentUser!!.uid

        val refUsers = FirebaseDatabase.getInstance().reference.child("Users")

        refUsers.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (mUsers as ArrayList<User>).clear()
                if(searchEditText!!.text.toString()==""){
                    for(snap in snapshot.children){
                        val user: User? = snap.getValue(User::class.java)
                        if(!(user!!.getUid()).equals(firebaseUserUID)){
                            (mUsers as ArrayList<User>).add(user)
                        }
                    }
                    userAdapter = UserAdapter(context!!, mUsers!!, false)
                    recyclerView!!.adapter = userAdapter
                }
            }

        })
    }

    private fun searchForUsers(str: String){
        val firebaseUserUID = FirebaseAuth.getInstance().currentUser!!.uid

        val queryUsers = FirebaseDatabase.getInstance().reference.child("Users").orderByChild("search")
            .startAt(str).endAt(str + "\uf8ff")

        queryUsers.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (mUsers as ArrayList<User>).clear()
                for(snap in snapshot.children){
                    val user: User? = snap.getValue(User::class.java)
                    if(!(user!!.getUid()).equals(firebaseUserUID)){
                        (mUsers as ArrayList<User>).add(user)
                    }
                }
                userAdapter = UserAdapter(context!!, mUsers!!, false)
                recyclerView!!.adapter = userAdapter
            }

        })
    }

}
