package dev.abdujabbor.chatappsimple.adapters

import com.bumptech.glide.Glide
import dev.abdujabbor.chatappsimple.databinding.ItemRvUserBinding
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.abdujabbor.chatappsimple.models.modelOfGroups.GroupModel

class GroupsAdapter(var list: ArrayList<GroupModel>, var context:Context,var rvClick: RvAction) :
    RecyclerView.Adapter<GroupsAdapter.VH>() {
    inner class VH(var itemViewBinding: ItemRvUserBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(list: GroupModel, position: Int) {
            Glide.with(context).load(list.phottorl).into(itemViewBinding.imageofusers)
            itemViewBinding.textofuser.text = list.groupName
            itemViewBinding.root.setOnClickListener {
                rvClick.click(list,position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {

        return VH(ItemRvUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        return holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}

interface RvAction {
    fun click(moview: GroupModel, position: Int)
}