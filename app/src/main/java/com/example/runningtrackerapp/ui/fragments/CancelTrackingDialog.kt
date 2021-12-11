package com.example.runningtrackerapp.ui.fragments

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.runningtrackerapp.R
import com.example.runningtrackerapp.databinding.LayoutWarningDailogBinding

class CancelTrackingDialog: DialogFragment() {

    private var yesListener: (()-> Unit)? = null
    fun setYesListener(listener: ()-> Unit){yesListener = listener}

    private fun cancelTrackingDialog() : Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val bindWarningDialog = LayoutWarningDailogBinding.inflate(LayoutInflater.from(requireContext()))
        dialogBuilder.setView(bindWarningDialog.root)
        val alertDialog: AlertDialog = dialogBuilder.create()
        View.OnClickListener {alertDialog.dismiss()}
        if(alertDialog.window != null) alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))

        bindWarningDialog.textTitle.text = "Cancel Current Run!"
        bindWarningDialog.textMessage.text = ("Ur sure to cancel this Run? \n *This Run will be cleared..!")
        bindWarningDialog.imageIcon.setImageResource(R.drawable.ic_delete)
        bindWarningDialog.buttonYes.text = "Yes"
        bindWarningDialog.buttonNo.text = "NO"
        bindWarningDialog.buttonNo.setOnClickListener {alertDialog.dismiss()}
        bindWarningDialog.buttonYes.setOnClickListener { yesListener?.let {yes -> yes()}; alertDialog.dismiss()}
        return alertDialog
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return cancelTrackingDialog()
    }
}
