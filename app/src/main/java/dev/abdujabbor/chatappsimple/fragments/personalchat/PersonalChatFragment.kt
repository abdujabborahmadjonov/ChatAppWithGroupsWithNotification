package dev.abdujabbor.chatappsimple.fragments.personalchat

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import dev.abdujabbor.chatappsimple.R
import dev.abdujabbor.chatappsimple.adapters.RvClick
import dev.abdujabbor.chatappsimple.adapters.UsersAdapter
import dev.abdujabbor.chatappsimple.databinding.FragmentPersonalChatBinding
import dev.abdujabbor.chatappsimple.databinding.HeaderLayoutBinding
import dev.abdujabbor.chatappsimple.models.MyPerson
import dev.abdujabbor.chatappsimple.utils.MyData

class PersonalChatFragment : Fragment(),RvClick {
    private lateinit var database: FirebaseDatabase
    private lateinit var rvAdapter: UsersAdapter
    lateinit var userlist: ArrayList<MyPerson>
    private lateinit var reference: DatabaseReference
    lateinit var auth: FirebaseAuth
    val binding by lazy { FragmentPersonalChatBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        userlist = ArrayList()
        auth= FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("akkauntlar")
        loadData()
        add()

        return binding.root
    }

    fun loadData() {
        rvAdapter = UsersAdapter(userlist, requireContext(),this)
        binding.recyclerview.adapter = rvAdapter
        reference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                userlist = java.util.ArrayList<MyPerson>()
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(MyPerson::class.java)
                    if (value != null) {
                        userlist.add(value)
                    }
                    for ( i in userlist){
                        if (i.uid==auth.uid){
                            i.displayName="Saved Messeges"
                            i.photoUrl="https://static10.tgstat.ru/channels/_0/31/31324acfc838ed6e6add64460e46148d.jpg"
                        }
                    }
                }

                rvAdapter.list.clear()
                rvAdapter.list.addAll(userlist)
                rvAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }




    fun add() {
        val name = auth.currentUser?.displayName.toString()
        val photourl = auth.currentUser?.photoUrl.toString()
        val uid = auth.uid
        val key = reference.push().key
        for (i in userlist) {
            if (i.uid != uid&&photourl!="null") {
                val person = MyPerson(uid, photourl, name,"last seen online")
                reference.child(key!!).setValue(person)
                break
            }
        }


    }

    override fun click(moview: MyPerson, position: Int) {
        MyData.username = auth.currentUser?.displayName.toString()
        MyData.recieveruid =moview.uid!!
        MyData.usernameiu =moview.uid!!+auth.uid
        MyData.iuusernAme =auth.uid+moview.uid
        MyData.userall=moview
        MyData.photoother = moview.photoUrl!!
        findNavController().navigate(R.id.chatFragment)

    }
}
