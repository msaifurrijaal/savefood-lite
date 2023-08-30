package com.msaifurrijaal.savefood.adapter

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.ui.listproduct.ListProductFragment

class TabsPagerAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
//        return when (position) {
//            0 -> ListProductFragment()
//            1 -> ListProductFragment()
//            2 -> ListProductFragment()
//            else -> throw IllegalStateException("Invalid tab position")
//        }
        val fragment = ListProductFragment()
        val args = Bundle()
        args.putInt("slidePosition", position)
        fragment.arguments = args
        return fragment
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.all)
            1 -> context.getString(R.string.donation)
            2 -> context.getString(R.string.sell)
            else -> null
        }
    }
}