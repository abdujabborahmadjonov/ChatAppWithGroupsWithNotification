package dev.abdujabbor.chatappsimple.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import dev.abdujabbor.chatappsimple.databinding.ChatMessageItemBinding
import dev.abdujabbor.chatappsimple.databinding.ChatincomingmessegeBinding
import dev.abdujabbor.chatappsimple.models.ChatMessage
import dev.abdujabbor.chatappsimple.models.MyPerson

class ChatAdapter(
    private val messages: MutableList<ChatMessage>,
    private val currentUser: MyPerson,var context: Context,var rvClick: Rvclicks

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val MESSAGE_TYPE_SENT = 1
        private const val MESSAGE_TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.senderId == currentUser.uid) {
            MESSAGE_TYPE_SENT
        } else {
            MESSAGE_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == MESSAGE_TYPE_SENT) {
            SentMessageViewHolder(
                ChatincomingmessegeBinding.inflate(inflater, parent, false)
            )
        } else {
            ReceivedMessageViewHolder(
                ChatMessageItemBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message)
        }

    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class ReceivedMessageViewHolder(private val binding: ChatMessageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage) {
            binding.textViewMessage.text = message.message
            binding.textViewTime.text = message.senderName.toString()
            Glide.with(context).load(message.imageforchat).into(binding.imageviewmessege)
            Glide.with(context).load(message.imageLink).into(binding.userimage)
            binding.textViewMessage.setOnLongClickListener{
                rvClick.delete(message)
                true
            }
        }
    }

    inner class SentMessageViewHolder(private val binding: ChatincomingmessegeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage) {
            binding.text2messege.text = message.message
            binding.textViewTime.text = message.senderName.toString()
            val auth = FirebaseAuth.getInstance().currentUser?.photoUrl
            Glide.with(context).load(auth).into(binding.userimage)
            Glide.with(context).load(message.imageforchat).into(binding.imageviewmessege)
            binding.text2messege.setOnLongClickListener{
                rvClick.delete(message)
                true
            }
        }
    }
    interface Rvclicks{
        fun delete(messege:ChatMessage)
    }
}
