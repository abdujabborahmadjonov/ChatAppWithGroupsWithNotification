package dev.abdujabbor.chatappsimple.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.os.Bundle

import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import dev.abdujabbor.chatappsimple.adapters.ChatAdapter
import dev.abdujabbor.chatappsimple.databinding.DialogForDeleteBinding
import dev.abdujabbor.chatappsimple.databinding.FragmentChatBinding
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
import kotlin.collections.HashMap


class ChatFragment : Fragment() ,ChatAdapter.Rvclicks{
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    lateinit var dataRef:DatabaseReference
lateinit var dialogDelete:AlertDialog
lateinit var refchild :DatabaseReference
    private lateinit var chatAdapter: ChatAdapter
    lateinit var imageforChat:String
    var imageRef = Firebase.storage.reference
    private var chatMessages = mutableListOf<ChatMessage>()

    val binding by lazy { FragmentChatBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding.icBack.setOnClickListener {
            findNavController().popBackStack()
        }
        Glide.with(requireActivity()).load(MyData.userall.photoUrl).into(binding.userimage)
        binding.username.text=MyData.userall.displayName
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        dataRef = FirebaseDatabase.getInstance().getReference("akkauntlar")
        refchild = dataRef.child(MyData.recieveruid).child("tick")

            dataRef.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val chatMessage = snapshot.getValue(MyPerson::class.java)
                    chatMessage?.let {
                        if (chatMessage.uid == MyData.userall.uid) {
                            if (chatMessage.tick=="online"){
                                binding.tickOnline.setImageResource(dev.abdujabbor.chatappsimple.R.drawable.online_tick)
                            }else{
                                binding.tickOnline.setImageResource(dev.abdujabbor.chatappsimple.R.drawable.offline_tick)
                            }
                            binding.tick.setText(chatMessage.tick)

                        }
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val chatMessage = snapshot.getValue(MyPerson::class.java)
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

        listenForMessages()


        chatAdapter = ChatAdapter(chatMessages, MyPerson(auth.uid,auth.currentUser?.photoUrl.toString(),auth.currentUser?.displayName,"online"),requireContext(),this)
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
                binding.recyclerViewChat.scrollToPosition(chatMessages.size-1)
            } else {
                Toast.makeText(requireContext(), "Please enter a message", Toast.LENGTH_SHORT)
                    .show()
            }

        }

        return binding.root
    }

    private fun listenForMessages() {
        val chatMessagesRef = database.child("messege").child(MyData.usernameiu)
        chatMessagesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                chatMessage?.let {
                    Log.d("offhd",it.id)

                    chatMessages.add(it)
                    chatAdapter.notifyDataSetChanged()
                    binding.recyclerViewChat.scrollToPosition(chatMessages.size-1   )
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

    private fun sendMessage(message: String) {
        var id= database.push().key
        val chatMessage = ChatMessage(
            id!!,
            auth.currentUser?.displayName!!,auth.currentUser!!.photoUrl.toString(),
            auth.currentUser?.uid ?: "",MyData.recieveruid,
            message,
            SimpleDateFormat("hh:mm").format(Date()),
        ""
        )
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
                NotifiationData(title, message),"topics/"+chatMessage.receiverId
            ).also {
                sendNotification(it)
            }
        }
        database.child("messege").child(MyData.usernameiu).push().setValue(chatMessage)
        database.child("messege").child(MyData.iuusernAme).push().setValue(chatMessage)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun delete(messege: ChatMessage) {
val alertDialog=AlertDialog.Builder(activity,dev.abdujabbor.chatappsimple.R.style.MyMenuDialogTheme)
     val bindingDelete =DialogForDeleteBinding.inflate(layoutInflater)

        alertDialog.setView(bindingDelete.root)
        bindingDelete.tvDelete.setOnClickListener {
            if (bindingDelete.checkBox.isChecked){
                database.child("messege").child(MyData.usernameiu).removeValue()
                database.child("messege").child(MyData.iuusernAme).removeValue()
            }else{
                database.child("messege").child(MyData.usernameiu).removeValue()
            }
            dialogDelete.cancel()
            chatAdapter.notifyDataSetChanged()


        }
        bindingDelete.tvCancel.setOnClickListener {
            dialogDelete.cancel()
        }
        dialogDelete=alertDialog.create()

        dialogDelete.window!!.attributes.windowAnimations = dev.abdujabbor.chatappsimple.R.style.MyAnimation
        dialogDelete.show()

    }

    private fun sendNotification(notification: PushNotifiation) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d("notifyda", "Response: ${response.toString()}")

            } else {
                Log.e("notifyda", response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e("notifyda", e.toString())
        }
    }
}