package com.gulshid.expensetracker.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            // Use NavOptions builder directly — no trailing lambda needed
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
                binding.navigationRailView?.visibility = View.GONE
            } else {
                if (binding.navigationRailView != null) {
                    binding.navigationRailView!!.visibility = View.VISIBLE
                    binding.bottomNavigationView.visibility = View.GONE
                } else {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}