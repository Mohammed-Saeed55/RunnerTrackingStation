package com.example.runningtrackerapp.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.runningtrackerapp.R
import com.example.runningtrackerapp.adapter.RunAdapter
import com.example.runningtrackerapp.databinding.FragmentRunBinding
import com.example.runningtrackerapp.ui.viewmodels.MainViewModel
import com.example.runningtrackerapp.utils.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.runningtrackerapp.utils.SortedTypes
import com.example.runningtrackerapp.utils.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


@Suppress("RedundantSamConstructor")
@AndroidEntryPoint
class RunFragment: Fragment(R.layout.fragment_run), EasyPermissions.PermissionCallbacks
{
    private val viewModel: MainViewModel by viewModels()
    private lateinit var _bind: FragmentRunBinding
    private val bind get() = _bind
    private lateinit var runAdapter: RunAdapter


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _bind = FragmentRunBinding.inflate(inflater, container, false)


        requestPermission()
        setupRecyclerView()

        viewModel.runs.observe(viewLifecycleOwner, Observer { runAdapter.submitList(it) })

        when(viewModel.sortType){
            SortedTypes.DATE -> bind.spinnerFilter.setSelection(0)
            SortedTypes.DISTANCE -> bind.spinnerFilter.setSelection(1)
            SortedTypes.CALORIES_BURNED -> bind.spinnerFilter.setSelection(2)
            SortedTypes.RUNNING_TIME -> bind.spinnerFilter.setSelection(3)
            SortedTypes.AVG_SPEED -> bind.spinnerFilter.setSelection(4)
        }

        bind.spinnerFilter.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                when(pos){
                    0 -> viewModel.sortRuns(SortedTypes.DATE)
                    1 -> viewModel.sortRuns(SortedTypes.DISTANCE)
                    2 -> viewModel.sortRuns(SortedTypes.CALORIES_BURNED)
                    3 -> viewModel.sortRuns(SortedTypes.RUNNING_TIME)
                    4 -> viewModel.sortRuns(SortedTypes.AVG_SPEED)
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }



        bind.fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)


        }
        return bind.root
    }




    private fun setupRecyclerView() = bind.runsRecyclerView.apply {
        runAdapter = RunAdapter()
        adapter = runAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }


    private fun requestPermission() {
        if (TrackingUtility.hasLocationPermissions(requireContext())) return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            EasyPermissions.requestPermissions(
                this,
                "U have to accept Location to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        else
            EasyPermissions.requestPermissions(
                this,
                "U have to accept Location to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
    }


    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) =
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms))
            AppSettingsDialog.Builder(this).build().show()
        else
            requestPermission()

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            this)
    }
}
