package com.example.runningtrackerapp.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.runningtrackerapp.R
import com.example.runningtrackerapp.databinding.ActivityMainBinding
import com.example.runningtrackerapp.utils.Constants
import com.example.runningtrackerapp.utils.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_success_dailog.view.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding  // Binding Instance

    @Inject
    lateinit var sharedPref: SharedPreferences



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*---------------------[Initializing & Rendering]---------------------*/
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        val nameFromSetting: String? = sharedPref.getString(Constants.KEY_NAME, "")

        setSupportActionBar(bind.toolbar)
        if (nameFromSetting!!.isEmpty()) bind.toolbarTitle.text = "Runner Tracking Station"
        else bind.toolbarTitle.text = "Let's Start Again: $nameFromSetting"

        trackingFragmentNavigate(intent)


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment


        /*-------------------[Bottom Navigation Handling]---------------------*/
        bind.bottomNav.setupWithNavController(navHostFragment.findNavController()) // Bottom OnPress
        navHostFragment.navController.addOnDestinationChangedListener{ _, destination, _-> //Execution
                when(destination.id) {
                    R.id.settingFragment, R.id.runFragment, R.id.statisticsFragment
                    ->  bind.bottomNav.visibility = View.VISIBLE
                    else
                    ->  bind.bottomNav.visibility = View.GONE
                }
            }
    }


    /*-----------[Tracking-Fragment Navigation From Notification]------------*/
    private fun trackingFragmentNavigate(intent: Intent?) {
        if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT ) {
            bind.navHostFragmentContainer.findNavController().navigate(R.id.action_global_trackingFragment)
        }
    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        trackingFragmentNavigate(intent)
    }

}
