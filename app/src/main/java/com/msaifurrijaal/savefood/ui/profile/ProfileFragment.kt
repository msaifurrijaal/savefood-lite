package com.msaifurrijaal.savefood.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.data.Resource
import com.msaifurrijaal.savefood.data.model.User
import com.msaifurrijaal.savefood.databinding.FragmentProfileBinding
import com.msaifurrijaal.savefood.ui.edit_profile.EditProfileActivity
import com.msaifurrijaal.savefood.ui.edit_profile.EditProfileActivity.Companion.USER_ITEM_PROFILE
import com.msaifurrijaal.savefood.ui.login.LoginActivity
import com.msaifurrijaal.savefood.ui.myproduct.MyProductActivity
import com.msaifurrijaal.savefood.utils.showDialogLoading

class ProfileFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var profileViewModel: ProfileViewModel
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
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDataUser()
        onAction()

    }

    override fun onResume() {
        super.onResume()

        setDataUser()
    }

    private fun onAction() {
        binding.apply {
            btnLogout.setOnClickListener {
                firebaseAuth.signOut()
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finishAffinity()
            }

            btnEdit.setOnClickListener {
                startActivity(Intent(requireContext(), EditProfileActivity::class.java)
                    .putExtra(USER_ITEM_PROFILE, user))
            }

            btnMyProduct.setOnClickListener {
                startActivity(Intent(requireContext(), MyProductActivity::class.java))
            }
        }
    }

    private fun setDataUser() {
        profileViewModel.getCurrentUser().observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Error -> {
                    successResponse()
                    Toast.makeText(requireContext(), getString(R.string.loading_users_failed), Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    loadingResponse()
                }
                is Resource.Success -> {
                    Glide.with(requireContext())
                        .load(response.data?.avatarUser)
                        .into(binding.ivUser)

                    binding.tvUserFullName.text = response.data?.nameUser
                    binding.tvUserEmail.text = response.data?.emailUser
                    binding.tvUserPhoneNumber.text = response.data?.phoneNumber
                    binding.tvUserRole.text = response.data?.roleUser

                    successResponse()

                    user = response.data
                }
            }
        })
    }

    private fun successResponse() {
        binding.apply {
            pgUserProfile.visibility = View.GONE
        }
    }

    private fun loadingResponse() {
        binding.apply {
            pgUserProfile.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}