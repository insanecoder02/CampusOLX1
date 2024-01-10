package com.example.campusolx.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.campusolx.R
import com.example.campusolx.activites.ForgotPassActivity
import com.example.campusolx.activites.ProfileEditActivity
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

        binding.nameTvAcc.text = account.name
        binding.rollNoTvAcc.text = account.enrollmentNo
        binding.semesterTvAcc.text = account.semester.toString()
        binding.BranchTvAcc.text = account.branch
        binding.ContactTvAcc.text = account.contact
        binding.EmailTvAcc.text = account.email

        binding.delteaccontImageView.setImageResource(R.drawable.fi_user_x)
        binding.verfiyAccountIMageVidwe.setImageResource(R.drawable.fi_unlock)
        binding.changePasswordImageView.setImageResource(R.drawable.fi_edit_3)
        binding.editProfileImageView.setImageResource(R.drawable.fi_user)
        binding.logoutImageView.setImageResource(R.drawable.logout_svgrepo_com)

        account.profilePictureUrl?.let { profilePictureUrl ->
            Glide.with(requireContext())
                .load(profilePictureUrl)
                .placeholder(R.drawable.i2)
                .into(binding.profileTvAcc)
        }

//         Set click listeners for edit profile and change password
        binding.editProfileCvAcc.setOnClickListener {
            val intent = Intent(requireContext(), ProfileEditActivity::class.java)
            intent.putExtra("name", account.name)
            intent.putExtra("enrollmentNo", account.enrollmentNo)
            intent.putExtra("email", account.email)
            intent.putExtra("contact", account.contact)
            intent.putExtra("profilePictureUrl", account.profilePictureUrl)
            startActivity(intent)
        }

        binding.changePasswordCvAcc.setOnClickListener {
            startActivity(Intent(requireContext(), ForgotPassActivity::class.java))
        }
    }

    // Simulated account data retrieval
    private fun getAccountData(): Account {
        val sharedPreferences = requireContext().getSharedPreferences("Account_Details", Context.MODE_PRIVATE)
        return Account(
            sharedPreferences.getString("_id", null),
            sharedPreferences.getString("name", null),
            sharedPreferences.getString("enrollmentNo", null),
            sharedPreferences.getInt("semester", 0),
            sharedPreferences.getString("branch", null),
            sharedPreferences.getString("email", null),
            sharedPreferences.getString("contact", null),
            sharedPreferences.getString("profilePictureUrl", null)
        )
    }
}