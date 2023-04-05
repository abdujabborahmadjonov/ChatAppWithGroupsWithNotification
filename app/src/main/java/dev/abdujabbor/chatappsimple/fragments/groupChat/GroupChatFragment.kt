package dev.abdujabbor.chatappsimple.fragments.groupChat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import dev.abdujabbor.chatappsimple.R
import dev.abdujabbor.chatappsimple.adapters.RvAction
import dev.abdujabbor.chatappsimple.adapters.GroupsAdapter
import dev.abdujabbor.chatappsimple.databinding.FragmentGroupChatBinding
import dev.abdujabbor.chatappsimple.models.modelOfGroups.GroupModel
import dev.abdujabbor.chatappsimple.utils.MyData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class GroupChatFragment : Fragment() ,RvAction{
    private lateinit var database: FirebaseDatabase
    private lateinit var rvAdapter: GroupsAdapter
    lateinit var groupList: ArrayList<GroupModel>
    private lateinit var reference: DatabaseReference
    lateinit var auth: FirebaseAuth
    val binding by lazy { FragmentGroupChatBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        groupList = ArrayList()
        auth= FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("groupsChatting")


        loadData()
        binding.createGroup.setOnClickListener {
            findNavController().navigate(R.id.addGroupFragment)
        }



        return binding.root
    }

    fun loadData() {
        rvAdapter = GroupsAdapter(groupList, requireContext(),this)
        binding.recyclerview.adapter = rvAdapter
        reference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                groupList = java.util.ArrayList<GroupModel>()
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(GroupModel::class.java)
                    if (value != null) {
                        groupList.add(value)
                    }
                }

                rvAdapter.list.clear()
                rvAdapter.list.addAll(groupList)
                rvAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun click(moview: GroupModel, position: Int) {
        MyData.group = moview
        findNavController().navigate(R.id.groupChattingFragment)
    }

}