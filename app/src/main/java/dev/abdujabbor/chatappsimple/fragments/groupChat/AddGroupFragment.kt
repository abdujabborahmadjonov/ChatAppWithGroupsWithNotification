package dev.abdujabbor.chatappsimple.fragments.groupChat

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dev.abdujabbor.chatappsimple.R
import dev.abdujabbor.chatappsimple.databinding.FragmentAddGroupBinding
import dev.abdujabbor.chatappsimple.models.modelOfGroups.GroupModel

class AddGroupFragment : Fragment() {
    var curfile: Uri? = null
    lateinit var imageUrl:String
    var has:Boolean = false
    lateinit var auth:FirebaseAuth
    lateinit var grouplist:ArrayList<GroupModel>
    lateinit var database:FirebaseDatabase
    lateinit var dataRef:DatabaseReference
    var imageRef = Firebase.storage.reference
val binding by lazy { FragmentAddGroupBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        grouplist=ArrayList()

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        dataRef = database.getReference("groupsChatting")
        loadData()
        binding.btnAdd.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, 0)
            }
        }
        binding.btnSignUp.setOnClickListener {
            for (i in grouplist) {
                if (i.creator == auth.currentUser?.displayName) {
                    has = true
                    val alertDialog = AlertDialog.Builder(requireContext()) //set icon
                        .setIcon(R.drawable.baseline_workspace_premium_24) //set title
                        .setTitle("Buying Premium version") //set message
                        .setMessage("If you have not premim version you won't open more one group ") //set positive button
                        .setPositiveButton("Buy premium ") { _, i -> //set what would happen when positive button is clicked
                            val number = Uri.parse("tel:+998916563726")
                            val callIntent = Intent(Intent.ACTION_DIAL, number)
                            startActivity(callIntent)
                        } //set negative button
                        .setNegativeButton("No") { dialogInterface, i -> //set what should happen when negative button is clicked
                            Snackbar.make(binding.root, "Not opening", 2000).show()
                        }
                        .show()
                    break

                }
            }
            if (!has) {
                if (imageUrl != null && binding.edtName.text.isNotEmpty() && binding.edtFamilya.text.isNotEmpty()) {
                    val id = dataRef.push().key
                    val modelofgroup = GroupModel(
                        id,
                        binding.edtName.text.toString(),
                        binding.edtFamilya.text.toString(),
                        imageUrl,
                        auth.currentUser?.displayName
                    )
                    dataRef.child(id!!).setValue(modelofgroup)
                }
            }
        }
        return binding.root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data

            binding.image.setImageURI(imageUri)
            val storageRef = Firebase.storage.reference
            val imagesRef = storageRef.child("images/${imageUri?.lastPathSegment}")

            val uploadTask = imageUri?.let { imagesRef.putFile(it) }

            uploadTask?.addOnSuccessListener {
                // Image uploaded successfully, get download URL
                imagesRef.downloadUrl.addOnSuccessListener { uri ->
                     imageUrl = uri.toString()
                    // Add the image URL to the Realtime Database
                }
            }?.addOnFailureListener {
                // Handle errors
            }
        }
    }

    fun loadData() {
        dataRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(GroupModel::class.java)
                    if (value != null) {
                        grouplist.add(value)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}