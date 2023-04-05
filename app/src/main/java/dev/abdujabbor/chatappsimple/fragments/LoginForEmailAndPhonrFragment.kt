package dev.abdujabbor.chatappsimple.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dev.abdujabbor.chatappsimple.R
import dev.abdujabbor.chatappsimple.databinding.FragmentLoginForEmailAndPhonrBinding
import dev.abdujabbor.chatappsimple.models.MyPerson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class LoginForEmailAndPhonrFragment : Fragment() {

    var curFile: Uri? = null
    private val REQUEST_CODE_IMAGE_PICK = 0
    lateinit var auth: FirebaseAuth
    lateinit var userlist: ArrayList<MyPerson>
    val imageRef = Firebase.storage.reference
    lateinit var database: FirebaseDatabase
    lateinit var dataRef: DatabaseReference
    val bining by lazy { FragmentLoginForEmailAndPhonrBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        database = FirebaseDatabase.getInstance()
        dataRef = database.getReference("akkauntlar")
        auth = FirebaseAuth.getInstance()
        userlist = ArrayList()
        bining.ivimage.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, REQUEST_CODE_IMAGE_PICK)
            }
            downloadImageFireStorage("images/auth.uid!!")
        }
        bining.save.setOnClickListener {
            add()
            findNavController().navigate(R.id.allUsersFragment)
        }
        return bining.root
    }

    fun add() {
        val name = bining.name.text.toString()
        val photourl = curFile.toString()
        val uid = auth.uid
        val key = dataRef.push().key
        for (i in userlist) {
            if (i.displayName != name && photourl != "null") {
                val person = MyPerson(uid, photourl, name,"online")
                dataRef.child(key!!).setValue(person)
                break
            }
        }
    }

    private fun uploadImageToStorage(filename: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            curFile?.let {
                imageRef.child("images/$filename").putFile(it).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(), "Successfully uploaded image", Toast.LENGTH_LONG
                    ).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE_PICK) {
            data?.data?.let {
                curFile = it
                bining.ivimage.setImageURI(it)
                uploadImageToStorage("auth.uid!!")
            }
        }
    }

    private fun downloadImageFireStorage(fileName: String) = CoroutineScope(Dispatchers.IO).launch {

        try {
            val maxdowloadSize = 8L * 1024 * 1024
            val bytes = imageRef.child("images/$fileName").getBytes(maxdowloadSize).await()
            val bpm = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), bpm.toString(), Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}