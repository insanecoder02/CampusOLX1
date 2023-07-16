package com.example.campusolx.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.campusolx.activites.ForgotPassActivity
import com.example.campusolx.activities.ProfileEditActivity
import com.example.campusolx.databinding.FragmentAccountBinding
import com.example.campusolx.dataclass.Account


class AccountFragment : Fragment() {
    private lateinit var binding: FragmentAccountBinding

    // Simulated account data
    private val account: Account by lazy { getAccountData() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the account information in the views
        binding.nameTvAcc.text = account.name
        binding.rollNoTvAcc.text = account.enrollmentNo
        binding.semesterTvAcc.text = account.semester.toString()
        binding.BranchTvAcc.text = account.branch
        binding.ContactTvAcc.text = account.contact
        binding.EmailTvAcc.text = account.email

        // Set profile picture if available
        account.profilePictureUrl?.let { profilePictureUrl ->
            // Load the image using your preferred image loading library
            // For example:
            Glide.with(requireContext())
                .load(profilePictureUrl)
                .into(binding.profileTvAcc)
        }

        // Set click listeners for edit profile and change password
        binding.editProfileCvAcc.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileEditActivity::class.java))
        }
        binding.changePasswordCvAcc.setOnClickListener {
            startActivity(Intent(requireContext(), ForgotPassActivity::class.java))
        }
    }

    // Simulated account data retrieval
    private fun getAccountData(): Account {
        val sharedPreferences = requireContext().getSharedPreferences("Account_Details", Context.MODE_PRIVATE)
        return Account(
            sharedPreferences.getString("name", ""),
            sharedPreferences.getString("enrollmentNo", ""),
            sharedPreferences.getInt("semester", 0),
            sharedPreferences.getString("branch", ""),
            sharedPreferences.getString("email", ""),
            sharedPreferences.getString("contact", ""),
            sharedPreferences.getString("profilePictureUrl", "")
        )
    }
}