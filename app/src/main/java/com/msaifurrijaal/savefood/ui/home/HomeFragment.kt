package com.msaifurrijaal.savefood.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.databinding.FragmentHomeBinding
import com.msaifurrijaal.savefood.ui.additem.AddItemActivity
import com.msaifurrijaal.savefood.utils.showDialogError
import com.msaifurrijaal.savefood.utils.showDialogLoading
import com.msaifurrijaal.savefood.utils.showDialogSuccess


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialogLoading: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        dialogLoading = showDialogLoading(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onAction()
    }

    private fun onAction() {
        binding.apply {
            fabAddItem.setOnClickListener {
                startActivity(Intent(requireContext(), AddItemActivity::class.java))
            }
        }
    }

    private fun setAllFoodsV() {
        binding.rvFoods.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}