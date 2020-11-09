package com.example.kotlinmessengerclone.AdapterClasses

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinmessengerclone.MessageChatActivity
import com.example.kotlinmessengerclone.ModelClasses.User
import com.example.kotlinmessengerclone.R
import com.example.kotlinmessengerclone.WelcomeActivity
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.user_search_item_layout.view.*

class UserAdapter(mContext: Context, mUsers: List<User>, isChatCheck: Boolean ): RecyclerView.Adapter<UserAdapter.ViewHolder?>()
{
    private val mContext : Context
    private val mUsers: List<User>
    private val isChatCheck: Boolean

    init {
        this.mContext = mContext
        this.mUsers = mUsers
        this.isChatCheck = isChatCheck
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var userNameTxt: TextView
        var profileImageView: CircleImageView
        var onlineImageView: CircleImageView
        var offlineImageView: CircleImageView
        var lastMessageTxt: TextView

        init {
            userNameTxt = itemView.findViewById(R.id.username)
            profileImageView = itemView.findViewById((R.id.profile_image))
            onlineImageView = itemView.findViewById((R.id.image_online))
            offlineImageView = itemView.findViewById((R.id.image_offline))
            lastMessageTxt = itemView.findViewById((R.id.message_last))
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = LayoutInflater.from(mContext).inflate(R.layout.user_search_item_layout, parent, false)
        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: User? = mUsers[position]
        holder.userNameTxt.text = user!!.getUsername()
        Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile).into(holder.profileImageView)

        holder.itemView.findViewById<LinearLayout>(R.id.user_line).setOnClickListener {
            val intent = Intent(mContext, MessageChatActivity::class.java)
            intent.putExtra("visit_id", user.getUid())
            mContext.startActivity(intent)
        }

        holder.itemView.findViewById<ImageView>(R.id.profile_image).setOnClickListener {

        }
    }


}