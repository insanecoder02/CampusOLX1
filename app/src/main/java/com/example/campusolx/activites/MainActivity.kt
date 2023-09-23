package com.example.campusolx.activites

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import androidx.core.content.ContextCompat
import com.example.campusolx.fragments.AccountFragment
import com.example.campusolx.fragments.HomeFragment
import com.example.campusolx.fragments.MyAdsFragment
import com.example.campusolx.R
import com.example.campusolx.fragments.SettingsFragment
import com.example.campusolx.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


// Define the MainActivity class
class MainActivity : AppCompatActivity() {
    // Declare the view binding variable
    private lateinit var binding: ActivityMainBinding
    private var selectedTab : Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the view using the layout defined in ActivityMainBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide the action bar if it is present
        supportActionBar?.hide()

        // Set flags to display the activity in full-screen mode
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Set click listener for bottom navigation items
//        binding.bottomNavigationView.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.menu_home -> {
//                    showHomeFragment()
//                    true
//                }
//                R.id.menu_account -> {
//                    showAccountFragment()
//                    true
//                }
//                R.id.menu_my_ads -> {
//                    showMyAdsFragment()
//                    true
//                }
//                R.id.menu_settings -> {
//                    showSettingsFragment()
//                    true
//                }
//                R.id.menu_sell -> {
//                    // Start the AdCreateActivity when "Sell" is selected
//                    startActivity(Intent(this, AdCreateActivity::class.java))
//                    true
//                }
//                else -> false
//            }
//        }


        binding.homee.setOnClickListener{


            if(selectedTab!=1){
                showHomeFragment()
                binding.settingstext.visibility= View.GONE
//                binding.plustext.visibility = View.GONE
                binding.myaddstext.visibility=View.GONE
                binding.profiletext.visibility=View.GONE

                binding.settingimage.setImageResource(R.drawable.settingnormal)
//                binding.plusimage.setImageResource(R.drawable.normalplus)
                binding.myadsimage.setImageResource(R.drawable.myaddnormal)
                binding.profileimage.setImageResource(R.drawable.person)

                binding.settting.setBackgroundColor(ContextCompat.getColor(this, R.color.white_transparent))
                binding.pluslayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white_transparent))
                binding.myadsss.setBackgroundColor(ContextCompat.getColor(this, R.color.white_transparent))
                binding.profilelayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white_transparent))

                binding.hometext.visibility = View.VISIBLE
                binding.homeImage.setImageResource(R.drawable.ic_home)
                val customDrawable = ContextCompat.getDrawable(this, R.drawable.round_back_home)
                binding.homee.background = customDrawable
                val scaleXAnimation = ScaleAnimation(
                    0.8f, // fromX
                    1.0f, // toX
                    1.0f, // fromY
                    1.0f, // toY
                    Animation.RELATIVE_TO_SELF, // pivotXType
                    0.5f, // pivotXValue (center X)
                    Animation.RELATIVE_TO_SELF, // pivotYType
                    0.5f // pivotYValue (center Y)
                )
                scaleXAnimation.duration = 200
                scaleXAnimation.fillAfter = true
                val animationSet = AnimationSet(true)
                val alphaAnimation = AlphaAnimation(1.0f, 1.0f)
                alphaAnimation.duration = 200
                animationSet.addAnimation(scaleXAnimation)
                animationSet.addAnimation(alphaAnimation)
                binding.homee.startAnimation(animationSet)
                selectedTab =1
            }
        }

        binding.settting.setOnClickListener{


            if(selectedTab!=2){
                showSettingsFragment()
                binding.hometext.visibility= View.GONE
//                binding.plustext.visibility = View.GONE
                binding.myaddstext.visibility=View.GONE
                binding.profiletext.visibility=View.GONE

                binding.homeImage.setImageResource(R.drawable.ic_home_normal)
//                binding.plusimage.setImageResource(R.drawable.normalplus)
                binding.myadsimage.setImageResource(R.drawable.myaddnormal)
                binding.profileimage.setImageResource(R.drawable.person)

                binding.homee.setBackgroundColor(ContextCompat.getColor(this, R.color.white_transparent))
                binding.pluslayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white_transparent))
                binding.myadsss.setBackgroundColor(ContextCompat.getColor(this, R.color.white_transparent))
                binding.profilelayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white_transparent))

                binding.settingstext.visibility = View.VISIBLE
                binding.settingimage.setImageResource(R.drawable.ic_baseline_settings_24)
                val customDrawable = ContextCompat.getDrawable(this, R.drawable.round_back_settings)
                binding.settting.background = customDrawable
                val scaleXAnimation = ScaleAnimation(
                    0.8f, // fromX
                    1.0f, // toX
                    1.0f, // fromY
                    1.0f, // toY
                    Animation.RELATIVE_TO_SELF, // pivotXType
                    0.5f, // pivotXValue (center X)
                    Animation.RELATIVE_TO_SELF, // pivotYType
                    0.5f // pivotYValue (center Y)
                )
                scaleXAnimation.duration = 200
                scaleXAnimation.fillAfter = true
                val animationSet = AnimationSet(true)
                val alphaAnimation = AlphaAnimation(1.0f, 1.0f)
                alphaAnimation.duration = 200
                animationSet.addAnimation(scaleXAnimation)
                animationSet.addAnimation(alphaAnimation)
                binding.settting.startAnimation(animationSet)
                selectedTab =2
            }
        }


        binding.pluslayout.setOnClickListener{


            if(selectedTab!=3){
                binding.settingstext.visibility= View.GONE
                binding.hometext.visibility = View.GONE
                binding.myaddstext.visibility=View.GONE
                binding.profiletext.visibility=View.GONE

                binding.settingimage.setImageResource(R.drawable.settingnormal)
                binding.homeImage.setImageResource(R.drawable.ic_home_normal)
                binding.myadsimage.setImageResource(R.drawable.myaddnormal)
                binding.profileimage.setImageResource(R.drawable.person)

                binding.settting.setBackgroundColor(ContextCompat.getColor(this, R.color.white_transparent))
                binding.homee.setBackgroundColor(ContextCompat.getColor(this, R.color.white_transparent))
                binding.myadsss.setBackgroundColor(ContextCompat.getColor(this, R.color.white_transparent))
                binding.profilelayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white_transparent))

//                binding.plustext.visibility = View.VISIBLE
//                binding.plusimage.setImageResource(R.drawable.add_button)
                val customDrawable = ContextCompat.getDrawable(this, R.drawable.round_back_plus)
                binding.pluslayout.background = customDrawable
                val scaleXAnimation = ScaleAnimation(
                    0.8f, // fromX
                    1.0f, // toX
                    1.0f, // fromY
                    1.0f, // toY
                    Animation.RELATIVE_TO_SELF, // pivotXType
                    0.5f, // pivotXValue (center X)
                    Animation.RELATIVE_TO_SELF, // pivotYType
                    0.5f // pivotYValue (center Y)
                )
                scaleXAnimation.duration = 200
                scaleXAnimation.fillAfter = true
                val animationSet = AnimationSet(true)
                val alphaAnimation = AlphaAnimation(1.0f, 1.0f)
                alphaAnimation.duration = 200
                animationSet.addAnimation(scaleXAnimation)
                animationSet.addAnimation(alphaAnimation)
                binding.pluslayout.startAnimation(animationSet)
                selectedTab =3
                startActivity(Intent(this, AdCreateActivity::class.java))
            }
        }

        binding.myadsss.setOnClickListener{


            if(selectedTab!=4){
                showMyAdsFragment()
                binding.settingstext.visibility= View.GONE
//                binding.plustext.visibility = View.GONE
                binding.hometext.visibility=View.GONE
                binding.profiletext.visibility=View.GONE

                binding.settingimage.setImageResource(R.drawable.settingnormal)
//                binding.plusimage.setImageResource(R.drawable.normalplus)
                binding.homeImage.setImageResource(R.drawable.ic_home_normal)
                binding.profileimage.setImageResource(R.drawable.person)

                binding.settting.setBackgroundColor(ContextCompat.getColor(this, R.color.white_transparent))
                binding.pluslayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white_transparent))
                binding.homee.setBackgroundColor(ContextCompat.getColor(this, R.color.white_transparent))
                binding.profilelayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white_transparent))

                binding.myaddstext.visibility = View.VISIBLE
                binding.myadsimage.setImageResource(R.drawable.ic_baseline_favorite_24)
                val customDrawable = ContextCompat.getDrawable(this, R.drawable.round_back_myads)
                binding.myadsss.background = customDrawable
                val scaleXAnimation = ScaleAnimation(
                    0.8f, // fromX
                    1.0f, // toX
                    1.0f, // fromY
                    1.0f, // toY
                    Animation.RELATIVE_TO_SELF, // pivotXType
                    0.5f, // pivotXValue (center X)
                    Animation.RELATIVE_TO_SELF, // pivotYType
                    0.5f // pivotYValue (center Y)
                )
                scaleXAnimation.duration = 200
                scaleXAnimation.fillAfter = true
                val animationSet = AnimationSet(true)
                val alphaAnimation = AlphaAnimation(1.0f, 1.0f)
                alphaAnimation.duration = 200
                animationSet.addAnimation(scaleXAnimation)
                animationSet.addAnimation(alphaAnimation)
                binding.myadsss.startAnimation(animationSet)
                selectedTab =4
            }
        }

        binding.profilelayout.setOnClickListener{


            if(selectedTab!=5){
                showAccountFragment()
                binding.settingstext.visibility= View.GONE
//                binding.plustext.visibility = View.GONE
                binding.myaddstext.visibility=View.GONE
                binding.hometext.visibility=View.GONE

                binding.settingimage.setImageResource(R.drawable.settingnormal)
//                binding.plusimage.setImageResource(R.drawable.normalplus)
                binding.myadsimage.setImageResource(R.drawable.myaddnormal)
                binding.homeImage.setImageResource(R.drawable.ic_home_normal)

                binding.settting.setBackgroundColor(ContextCompat.getColor(this, R.color.white_transparent))
                binding.pluslayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white_transparent))
                binding.myadsss.setBackgroundColor(ContextCompat.getColor(this, R.color.white_transparent))
                binding.homee.setBackgroundColor(ContextCompat.getColor(this, R.color.white_transparent))

                binding.profiletext.visibility = View.VISIBLE
                binding.profileimage.setImageResource(R.drawable.ic_person)
                val customDrawable = ContextCompat.getDrawable(this, R.drawable.round_back_profile)
                binding.profilelayout.background = customDrawable
                val scaleXAnimation = ScaleAnimation(
                    0.8f, // fromX
                    1.0f, // toX
                    1.0f, // fromY
                    1.0f, // toY
                    Animation.RELATIVE_TO_SELF, // pivotXType
                    0.5f, // pivotXValue (center X)
                    Animation.RELATIVE_TO_SELF, // pivotYType
                    0.5f // pivotYValue (center Y)
                )
                scaleXAnimation.duration = 200
                scaleXAnimation.fillAfter = true
                val animationSet = AnimationSet(true)
                val alphaAnimation = AlphaAnimation(1.0f, 1.0f)
                alphaAnimation.duration = 200
                animationSet.addAnimation(scaleXAnimation)
                animationSet.addAnimation(alphaAnimation)
                binding.profilelayout.startAnimation(animationSet)
                selectedTab =5
            }
        }
        // Set a click listener for a button (assuming you have a button with id "button" in your layout)
//        binding.button.setOnClickListener {
//            // Start the AdCreateActivity when the button is clicked
//            startActivity(Intent(this, AdCreateActivity::class.java))
//        }

        // Show the HomeFragment by default when the activity is created
        showHomeFragment()
    }

    // Function to show the HomeFragment
    private fun showHomeFragment() {
        // Set the toolbar title
        binding.toolbarTitle.text = "Home"
        val fragment = HomeFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, fragment, "HomeFragment")
        fragmentTransaction.commit()
    }

    // Function to show the AccountFragment
    private fun showAccountFragment() {
        // Set the toolbar title
        binding.toolbarTitle.text = "Account"
        val fragment = AccountFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, fragment, "AccountFragment")
        fragmentTransaction.commit()
    }

    // Function to show the MyAdsFragment
    private fun showMyAdsFragment() {
        // Set the toolbar title
        binding.toolbarTitle.text = "My Ads"
        val fragment = MyAdsFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, fragment, "MyAdsFragment")
        fragmentTransaction.commit()
    }

    // Function to show the SettingsFragment
    private fun showSettingsFragment() {
        // Set the toolbar title
        binding.toolbarTitle.text = "Settings"
        val fragment = SettingsFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentsFl.id, fragment, "SettingsFragment")
        fragmentTransaction.commit()
    }
}
