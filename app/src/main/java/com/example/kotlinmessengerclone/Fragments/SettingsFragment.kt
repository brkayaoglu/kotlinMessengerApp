package com.example.kotlinmessengerclone.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.kotlinmessengerclone.ModelClasses.User

import com.example.kotlinmessengerclone.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.view.*

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {

    var usersRef : DatabaseReference? = null

    var firebaseUser : FirebaseUser? = null

    private val RequestCode = 438

    private var imageUri : Uri? = null

    private var storageRef : StorageReference? = null

    private var coverChecker : String? = ""

    private var socialChecker : String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View =  inflater.inflate(R.layout.fragment_settings, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        usersRef = FirebaseDatabase.getInstance().reference.child("Users")
            .child(firebaseUser!!.uid)

        storageRef = FirebaseStorage.getInstance().reference.child("User Images")

        usersRef!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user : User? = snapshot.getValue(User::class.java)

                    if(context != null){
                        view.username_set.text = user!!.getUsername()

                        Picasso.get().load(user!!.getProfile()).into(view.profile_image_settings)
                        Picasso.get().load(user!!.getCover()).into(view.cover_image)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        view.profile_image_settings.setOnClickListener {
            pickImageFromGallery()
        }

        view.cover_image.setOnClickListener {
            coverChecker = "cover"
            pickImageFromGallery()
        }

        view.set_fb.setOnClickListener {
            socialChecker = "fb"
            setSocialLinks()
        }

        view.set_instagram.setOnClickListener {
            socialChecker = "ig"
            setSocialLinks()
        }

        view.set_website.setOnClickListener {
            socialChecker = "wb"
            setSocialLinks()
        }

        return view
    }

    private fun setSocialLinks() {
        var builder : AlertDialog.Builder = AlertDialog.Builder(context, R.style.Theme_AppCompat_DayNight_Dialog_Alert)


        val editText = EditText(context)

        if(socialChecker == "wb"){
            builder.setTitle("Write URL:")
            editText.hint = "e.g www.google.com"
        }else{
            builder.setTitle("Write username")
            editText.hint = "e.g berkayrkayaoglu"
        }

        builder.setView(editText)

        builder.setPositiveButton("Create", DialogInterface.OnClickListener{
            dialog, which ->
            val str = editText.text.toString()

            if(str == ""){
                Toast.makeText(context, "Write something..", Toast.LENGTH_LONG).show()
            }else{
                createSocialLink(str)
            }
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener{
            dialog, which ->
            dialog.cancel()
        })

        builder.show()
    }

    private fun createSocialLink(str: String) {
        val mapSocial = HashMap<String, Any>()

        when(socialChecker){
            "fb" -> {
                mapSocial["facebook"] = "https://m.facebook.com/$str"
            }
            "ig" -> {
                mapSocial["instagram"] = "https://m.instagram.com/$str"
            }
            "wb" -> {
                mapSocial["website"] = "https://$str"
            }
        }

        usersRef!!.updateChildren(mapSocial).addOnCompleteListener {
            task ->
            if(task.isSuccessful){
                Toast.makeText(context, "Link updated successfully!",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RequestCode && resultCode == Activity.RESULT_OK && data!!.data != null){
            imageUri = data.data
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(context)

        progressBar.setMessage("Image is uploading.. Please wait!")

        progressBar.show()

        if(imageUri != null){
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")
            val uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWith { task ->
                if(!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
                fileRef.downloadUrl
            }.addOnCompleteListener { task ->
                if(task.isSuccessful){
                    task.result!!.addOnSuccessListener{task ->
                        val url = task.toString()

                        if(coverChecker == "cover"){
                            val mapCoverImage = HashMap<String, Any>()

                            mapCoverImage["cover"] = url

                            usersRef!!.updateChildren(mapCoverImage)

                            coverChecker = ""
                        }else{
                            val mapProfileImage = HashMap<String, Any>()

                            mapProfileImage["profile"] = url

                            usersRef!!.updateChildren(mapProfileImage)

                            coverChecker = ""
                        }
                        progressBar.dismiss()
                    }
                }
            }
        }
    }

}
