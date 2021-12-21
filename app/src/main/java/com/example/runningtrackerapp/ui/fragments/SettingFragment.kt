package com.example.runningtrackerapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.runningtrackerapp.R
import com.example.runningtrackerapp.databinding.FragmentSettingBinding
import com.example.runningtrackerapp.utils.Constants.KEY_NAME
import com.example.runningtrackerapp.utils.Constants.KEY_WEIGHT
import com.example.runningtrackerapp.utils.ITools.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


@AndroidEntryPoint
class SettingFragment: Fragment(R.layout.fragment_setting) {

    private lateinit var _bind: FragmentSettingBinding
    private val bind get() = _bind

    @Inject
    lateinit var sharedPref: SharedPreferences


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _bind = FragmentSettingBinding.inflate(inflater, container, false)

        loadFieldsFromSharedPref()

        bind.btnApplyChanges.setOnClickListener {
            val success: Boolean = applyChangesIntoSharedPref()
            if (success) {
                hideKeyboard()
                Snackbar.make(bind.root, "Change's Saved", Snackbar.LENGTH_LONG).show()
            }            else
                Snackbar.make(bind.root, " plz fill out all fields behind! ", Snackbar.LENGTH_LONG).show()
        }

        return bind.root
    }



    private fun loadFieldsFromSharedPref(){
        val nameFromSetting: String? = sharedPref.getString(KEY_NAME, "")
        val weightFromSetting: Float = sharedPref.getFloat(KEY_WEIGHT, 80f)
        bind.nameSettingInput.setText(nameFromSetting)
        bind.weightSettingInput.setText(weightFromSetting.toString())
    }


    private fun applyChangesIntoSharedPref(): Boolean{
        val nameFromSetting: String = bind.nameSettingInput.text.toString()
        val weightFromSetting: String = bind.weightSettingInput.text.toString()

        if (nameFromSetting.isEmpty() || weightFromSetting.isEmpty()) return false

        sharedPref.edit()
            .putString(KEY_NAME, nameFromSetting)
            .putFloat(KEY_WEIGHT, weightFromSetting.toFloat())
            .apply()
        val toolbarTxtFromSetting = "Let's go: $nameFromSetting"
        requireActivity().toolbar_title.text = toolbarTxtFromSetting

        return true
    }

}
