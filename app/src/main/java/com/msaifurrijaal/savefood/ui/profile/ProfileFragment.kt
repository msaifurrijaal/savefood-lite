package com.msaifurrijaal.savefood.ui.profile

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.msaifurrijaal.savefood.databinding.LayoutDialogDeleteAccountBinding
import com.msaifurrijaal.savefood.ui.edit_profile.EditProfileActivity
import com.msaifurrijaal.savefood.ui.edit_profile.EditProfileActivity.Companion.USER_ITEM_PROFILE
import com.msaifurrijaal.savefood.ui.login.LoginActivity
import com.msaifurrijaal.savefood.ui.myproduct.MyProductActivity
import com.msaifurrijaal.savefood.utils.showDialogError
import com.msaifurrijaal.savefood.utils.showDialogLoading
import com.msaifurrijaal.savefood.utils.showDialogSuccess

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
            tvUrlPrivacyPolicy.setOnClickListener {
                var linkPrivacy =
                    "https://doc-hosting.flycricket.io/savefood-privacy-policy/7bbf5d7e-e926-40f5-9123-4091607c85d0/privacy"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkPrivacy))
                startActivity(intent)
            }

            btnLogout.setOnClickListener {
                firebaseAuth.signOut()
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finishAffinity()
            }

            btnEdit.setOnClickListener {
                startActivity(Intent(requireContext(), EditProfileActivity::class.java)
                    .putExtra(USER_ITEM_PROFILE, user))
            }

            btnDeleteAccount.setOnClickListener {
                user?.let {
                    val bindingAlert = LayoutDialogDeleteAccountBinding.inflate(LayoutInflater.from(requireContext()))
                    var alertDialog = AlertDialog
                        .Builder(requireContext())
                        .setView(bindingAlert.root)
                        .setCancelable(false)
                        .create()

                    alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    bindingAlert.btnDelete.setOnClickListener {
                        alertDialog.dismiss()
                        profileViewModel.deleteUserData(user?.uidUser!!).observe(viewLifecycleOwner, Observer { response ->
                            when (response) {
                                is Resource.Error -> {
                                    dialogLoading.dismiss()
                                    showDialogError(
                                        requireContext(),
                                        response.message.toString()
                                    )
                                }
                                is Resource.Loading -> {
                                    dialogLoading.show()
                                }
                                is Resource.Success -> {
                                    dialogLoading.dismiss()
                                    deleteUserData()
                                }
                            }
                        }
                        )
                    }

                    bindingAlert.btnCancel.setOnClickListener {
                        alertDialog.dismiss()
                    }

                    alertDialog.show()
                }
            }

            btnMyProduct.setOnClickListener {
                startActivity(Intent(requireContext(), MyProductActivity::class.java))
            }
        }
    }

    private fun deleteUserData() {
        profileViewModel.deleteUser().observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Error -> {
                    dialogLoading.dismiss()
                    showDialogError(
                        requireContext(),
                        response.message.toString()
                    )
                }
                is Resource.Loading -> {
                    dialogLoading.show()
                }
                is Resource.Success -> {
                    dialogLoading.dismiss()
                    val dialogSuccess = showDialogSuccess(
                        requireContext(),
                        getString(R.string.selamat_akun_anda_berhasil_dihapus),
                    )
                    dialogSuccess.show()

                    Handler(Looper.getMainLooper())
                        .postDelayed({
                            dialogSuccess.dismiss()
                            startActivity(
                                Intent(requireActivity(), LoginActivity::class.java)
                            )
                            requireActivity().finishAffinity()
                        }, 1500)
                }
            }
        })
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