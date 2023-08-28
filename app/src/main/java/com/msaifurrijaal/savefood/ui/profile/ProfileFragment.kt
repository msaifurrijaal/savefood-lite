package com.msaifurrijaal.savefood.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.data.model.User
import com.msaifurrijaal.savefood.databinding.FragmentProfileBinding
import com.msaifurrijaal.savefood.ui.login.LoginActivity
import com.msaifurrijaal.savefood.utils.showDialogLoading

class ProfileFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialogLoading: AlertDialog
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        dialogLoading = showDialogLoading(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finishAffinity()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}