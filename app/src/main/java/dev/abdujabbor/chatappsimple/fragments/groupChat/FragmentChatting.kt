package dev.abdujabbor.chatappsimple.fragments.groupChat

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.storage.ktx.storage
import dev.abdujabbor.chatappsimple.adapters.ChatAdapter
import dev.abdujabbor.chatappsimple.databinding.FragmentChattingBinding
import dev.abdujabbor.chatappsimple.models.ChatMessage
import dev.abdujabbor.chatappsimple.models.MyPerson
import dev.abdujabbor.chatappsimple.models.notifiation.NotifiationData
import dev.abdujabbor.chatappsimple.models.notifiation.PushNotifiation
import dev.abdujabbor.chatappsimple.retrofit.RetrofitInstance
import dev.abdujabbor.chatappsimple.utils.MyData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class GroupChattingFragment : Fragment(), ChatAdapter.Rvclicks {
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    lateinit var dataRef: DatabaseReference
    lateinit var dialogDelete: AlertDialog
    var imageforchat = ""
    lateinit var refchild: DatabaseReference
    var imageRef = Firebase.storage.reference
    private lateinit var chatAdapter: ChatAdapter
    private var chatMessages = mutableListOf<ChatMessage>()

    val binding by lazy { FragmentChattingBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding.icBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.imageforchat.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, 120)
            }
        }
        Glide.with(requireActivity()).load(MyData.group?.phottorl).into(binding.userimage)
        binding.username.text = MyData.group?.groupName
        binding.tick.text = ""
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        chatAdapter = ChatAdapter(
            chatMessages,
            MyPerson(
                auth.uid,
                auth.currentUser?.photoUrl.toString(),
                auth.currentUser?.displayName,
                "online"
            ),
            requireContext(),
            this
        )
        binding.recyclerViewChat.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        binding.buttonSend.setOnClickListener {
            val message = binding.editTextMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message)
                binding.editTextMessage.text.clear()
                chatAdapter.notifyDataSetChanged()
                binding.recyclerViewChat.scrollToPosition(chatMessages.size - 1)
            } else {
                Toast.makeText(requireContext(), "Please enter a message", Toast.LENGTH_SHORT)
                    .show()
            }

        }
        listenForMessages()

        return binding.root
    }

    override fun delete(messege: ChatMessage) {

    }

    private fun sendMessage(message: String) {
        var id = database.push().key
        val chatMessage = ChatMessage(
            id!!,
            auth.currentUser?.displayName!!, auth.currentUser!!.photoUrl.toString(),
            auth.currentUser?.uid ?: "", MyData.recieveruid,
            message,
            SimpleDateFormat("hh:mm").format(Date()),
            imageforchat
        )
        Log.d("imageui", chatMessage.imageLink)
        val notificationData = mapOf("title" to "My Notification", "message" to "Hello, world!")
        FirebaseMessaging.getInstance().send(
            RemoteMessage.Builder(getString(dev.abdujabbor.chatappsimple.R.string.sender_id) + "@gcm.googleapis.com")
                .setMessageId(Integer.toString(Random().nextInt(100000)))
                .setData(notificationData)
                .build()
        )

        val title = auth.currentUser?.displayName!!
        val message = binding.editTextMessage.text.toString()
        if (title.isNotEmpty() && message.isNotEmpty()) {
            PushNotifiation(
                NotifiationData(title, message), "topics/" + chatMessage.receiverId
            ).also {
                sendNotification(it)
            }
        }
        database.child("GroupMessege").child(MyData.group?.id.toString() + MyData.group?.groupName)
            .push().setValue(chatMessage)
    }

    private fun listenForMessages() {
        val chatMessagesRef = database.child("GroupMessege")
            .child(MyData.group?.id.toString() + MyData.group?.groupName)
        chatMessagesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                chatMessage?.let {
                    Log.d("offhd", it.id)
                    chatMessages.add(it)
                    chatAdapter.notifyDataSetChanged()
                    binding.recyclerViewChat.scrollToPosition(chatMessages.size - 1)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // not used
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // not used
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // not used
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to read value.", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun sendNotification(notification: PushNotifiation) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
                    Log.d("notifyda", "Response: ${response.toString()}")

                } else {
                    Log.e("notifyda", response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.e("notifyda", e.toString())
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 120 && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data

            val storageRef = Firebase.storage.reference
            val imagesRef = storageRef.child("images/${imageUri?.lastPathSegment}")

            val uploadTask = imageUri?.let {
                imagesRef.putFile(it)
            }

            uploadTask?.addOnSuccessListener {
                // Image uploaded successfully, get download URL
                imagesRef.downloadUrl.addOnSuccessListener { uri ->
                    imageforchat = uri.toString()
                    var id = database.push().key
                    val chatMessage = ChatMessage(
                        id!!,
                        auth.currentUser?.displayName!!, auth.currentUser!!.photoUrl.toString(),
                        auth.currentUser?.uid ?: "", MyData.recieveruid,
                        "",
                        SimpleDateFormat("hh:mm").format(Date()),
                        uri.toString()
                    )
                    database.child("GroupMessege").child(MyData.group?.id.toString() + MyData.group?.groupName)
                        .push().setValue(chatMessage)
                }
            }?.addOnFailureListener {
                Log.d(TAG, "onActivityResult: error")
            }?.addOnProgressListener {
             Log.d("loadingpro",it.toString())
            }
        }
    }
}