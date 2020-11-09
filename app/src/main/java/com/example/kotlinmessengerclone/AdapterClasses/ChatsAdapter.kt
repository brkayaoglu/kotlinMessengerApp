package com.example.kotlinmessengerclone.AdapterClasses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinmessengerclone.ModelClasses.Chat
import com.example.kotlinmessengerclone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.message_item_left.view.*
import org.w3c.dom.Text

class ChatsAdapter(private val mContext: Context, private val mChatList: List<Chat>, private val imageUrl: String) : RecyclerView.Adapter<ChatsAdapter.ViewHolder?>() {

    var firebaseUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser!!

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var profileImage : CircleImageView? = null

        var show_text_message: TextView? = null

        var left_image_view : ImageView? = null
        var right_image_view : ImageView? = null

        var text_seen : TextView? = null

        init {
            profileImage = itemView.findViewById(R.id.profile_image_near_message)
            show_text_message = itemView.findViewById(R.id.text_message_show)
            left_image_view = itemView.findViewById(R.id.left_image_message)
            right_image_view = itemView.findViewById(R.id.right_image_message)
            text_seen = itemView.findViewById(R.id.text_seen)
        }
    }

    override fun getItemViewType(position: Int): Int {

        return if(mChatList[position].getSender().equals(firebaseUser!!.uid)){
            1
        }else{
            0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == 1){
            val view : View = LayoutInflater.from(mContext).inflate(R.layout.message_item_right, parent, false)
            ViewHolder(view)
        }else{
            val view : View = LayoutInflater.from(mContext).inflate(R.layout.message_item_left, parent, false)
            ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat : Chat = mChatList[position]

        Picasso.get().load(imageUrl).into(holder.profileImage)

        //image message
        if(chat.getMessage().equals("sent you an image.") && chat.getUrl() != ""){
            //image message by sender
            if(chat.getSender().equals(firebaseUser!!.uid)){
                holder.show_text_message!!.visibility = View.GONE
                holder.right_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.right_image_view)
            }
            //image message by receiver
            else if(!chat.getSender().equals(firebaseUser!!.uid)){
                holder.show_text_message!!.visibility = View.GONE
                holder.left_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.left_image_view)
            }
        }
        //text message
        else{
            holder.show_text_message!!.text = chat.getMessage()
        }

        //sent and seen message

        if (position == mChatList.size - 1){
            if(chat.getIsSeen()){
                holder.text_seen!!.text = "Seen"
                if(chat.getMessage().equals("sent you an image.") && chat.getUrl() != ""){
                    val lp: RelativeLayout.LayoutParams? = holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams
                    lp!!.setMargins(0,248,0,0)
                    holder.text_seen!!.layoutParams = lp
                }
            }else{
                holder.text_seen!!.text = "Sent"
                if(chat.getMessage().equals("sent you an image.") && chat.getUrl() != ""){
                    val lp: RelativeLayout.LayoutParams? = holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams
                    lp!!.setMargins(0,248,0,0)
                    holder.text_seen!!.layoutParams = lp
                }
            }
        }else{
            holder.text_seen!!.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return mChatList.size
    }
}