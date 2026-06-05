package com.gulshid.expensetracker.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.gulshid.expensetracker.R
import com.gulshid.expensetracker.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private val authDestinations = setOf(
        R.id.loginFragment,
        R.id.registerFragment
    )

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        // ✅ FIX: Apply saved theme preference BEFORE setContentView
        // so the correct theme is applied on Activity recreation after toggle.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_mode", true)
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        setupNavigation()
        observeDestinationChanges()
        checkAuthPersistence()
    }

    private fun checkAuthPersistence() {
        if (firebaseAuth.currentUser != null) {
            val options = NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, true)
                .build()
            navController.navigate(R.id.dashboardFragment, null, options)
        }
    }

    private fun setupNavigation() {
        binding.bottomNavigationView.setupWithNavController(navController)
        binding.navigationRailView?.setupWithNavController(navController)
    }

    private fun observeDestinationChanges() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isAuthScreen = destination.id in authDestinations
            if (isAuthScreen) {
                binding.bottomNavigationView.visibility = View.GONE
                binding.navigationRailView?.visibility  = View.GONE
            } else {
                if (binding.navigationRailView != null) {
                    binding.navigationRailView!!.visibility = View.VISIBLE
                    binding.bottomNavigationView.visibility  = View.GONE
                } else {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onSupportNavigateUp() = navController.navigateUp() || super.onSupportNavigateUp()
}