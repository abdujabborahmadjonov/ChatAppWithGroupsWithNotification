package dev.abdujabbor.chatappsimple.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.abdujabbor.chatappsimple.fragments.groupChat.GroupChatFragment
import dev.abdujabbor.chatappsimple.fragments.personalchat.PersonalChatFragment

class ViewPagerAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PersonalChatFragment()
            1 -> GroupChatFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}