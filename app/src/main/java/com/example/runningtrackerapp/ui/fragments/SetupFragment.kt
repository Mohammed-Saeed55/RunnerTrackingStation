package com.example.runningtrackerapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.runningtrackerapp.R
import com.example.runningtrackerapp.databinding.FragmentSetupBinding
import com.example.runningtrackerapp.utils.Constants
import com.example.runningtrackerapp.utils.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.runningtrackerapp.utils.Constants.KEY_NAME
import com.example.runningtrackerapp.utils.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_setup.*
import javax.inject.Inject


@AndroidEntryPoint
class SetupFragment: Fragment(R.layout.fragment_setup) {

    @Inject
    lateinit var sharedPref: SharedPreferences

    @set:Inject
    var isFirstTimeUserOpen = true

    private lateinit var _bind: FragmentSetupBinding
    private val bind get() = _bind


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _bind = FragmentSetupBinding.inflate(inflater, container, false)
        val nameFromSetting: String? = sharedPref.getString(Constants.KEY_NAME, "")


        if (!isFirstTimeUserOpen){
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_setupFragment_to_runFragment,
                savedInstanceState,
                navOptions
            )
        }


        bind.continueBtn.setOnClickListener{
            val success: Boolean = personalDataTOSharedPref()
            if(success)
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            else
                Snackbar.make(requireView(), "plz provide ur ser Name & Weight to continue", Snackbar.LENGTH_LONG).show()
        }


        return bind.root
    }




    private fun personalDataTOSharedPref(): Boolean{
        val name: String = name_input.text.toString()
        val weight: String = weight_input.text.toString()

        return if (name.isEmpty() || weight.isEmpty())
            false
        else {
            sharedPref.edit()
                .putString(KEY_NAME, name)
                .putFloat(KEY_WEIGHT, weight.toFloat())
                .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
                .apply()

            val toolbarText: String = "Let's go, $name"
            requireActivity().toolbar_title.text = toolbarText

            true
        }
    }
}
