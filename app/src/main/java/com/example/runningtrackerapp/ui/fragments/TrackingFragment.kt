package com.example.runningtrackerapp.ui.fragments

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.runningtrackerapp.R
import com.example.runningtrackerapp.databinding.FragmentTrackingBinding
import com.example.runningtrackerapp.databinding.LayoutWarningDailogBinding
import com.example.runningtrackerapp.db.Run
import com.example.runningtrackerapp.services.PolyLine
import com.example.runningtrackerapp.services.TrackingServices
import com.example.runningtrackerapp.ui.viewmodels.MainViewModel
import com.example.runningtrackerapp.utils.Constants.ACTION_PAUSE_SERVICE
import com.example.runningtrackerapp.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningtrackerapp.utils.Constants.ACTION_STOP_SERVICE
import com.example.runningtrackerapp.utils.Constants.CANCEL_TRACKING_DIALOG_TAG
import com.example.runningtrackerapp.utils.Constants.MAP_ZOOM
import com.example.runningtrackerapp.utils.Constants.POLYLINE_COLOR
import com.example.runningtrackerapp.utils.Constants.POLYLINE_WIDTH
import com.example.runningtrackerapp.utils.TrackingUtility
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.math.round

@Suppress("RedundantSamConstructor")
@AndroidEntryPoint
class TrackingFragment: Fragment(R.layout.fragment_tracking) {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var _bind: FragmentTrackingBinding
    private val bind get() = _bind

    private var map: GoogleMap? = null
    private var menu: Menu? = null

    private var isTracking: Boolean = false
    private var pathPoints = mutableListOf<PolyLine>()
    private var currentTimeMillis: Long = 0L

    @set: Inject
    var weight: Float = 80f



    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _bind = FragmentTrackingBinding.inflate(inflater, container, false)

        // handle cancelDialog screen rotation
        if (savedInstanceState != null) {
            val cancelTrackingDialog =
                parentFragmentManager.findFragmentByTag(CANCEL_TRACKING_DIALOG_TAG) as CancelTrackingDialog?
            cancelTrackingDialog?.setYesListener{ stopRun()}
        }

        bind.map.onCreate(savedInstanceState)
        subscribeToObservers()
        setHasOptionsMenu(true)

        bind.btnFinish.setOnClickListener{
            zoomingToWholeTrack()
            endRunAndSaveToDataBase()
        }

        bind.btnStartToggle.setOnClickListener {toggleRun()}
        bind.map.getMapAsync{
            map = it
            addAllPolyLines()
        }

        return bind.root
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toobar_tracking_menu, menu)
        this.menu = menu
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){R.id.cancel_tracking -> cancelTrackingDialog()}
        return super.onOptionsItemSelected(item)
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (currentTimeMillis > 0L ) this.menu?.getItem(0)?.isVisible = true
    }



/*
    private fun cancelTrackingDialog(){
        val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val bindWarningDialog = LayoutWarningDailogBinding.inflate(LayoutInflater.from(requireContext()))
        dialogBuilder.setView(bindWarningDialog.root)
        val alertDialog: AlertDialog = dialogBuilder.create()
        View.OnClickListener {alertDialog.dismiss()}
        if(alertDialog.window != null) alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))

        bindWarningDialog.textTitle.text = "Cancel Current Run"
        bindWarningDialog.textMessage.text = ("Ur sure to cancel this Run! \n (This Run will be cleared!)")
        bindWarningDialog.imageIcon.setImageResource(R.drawable.ic_delete)
        bindWarningDialog.buttonYes.text = "Yes"
        bindWarningDialog.buttonNo.text = "NO"
        bindWarningDialog.buttonNo.setOnClickListener {alertDialog.dismiss()}
        bindWarningDialog.buttonYes.setOnClickListener {alertDialog.dismiss(); stopRun()}
        alertDialog.show()
    }
*/



    private fun cancelTrackingDialog(){
        CancelTrackingDialog().apply { setYesListener { stopRun() }
        }.show(parentFragmentManager, CANCEL_TRACKING_DIALOG_TAG)
    }



    private fun stopRun() {
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
        bind.timerView.text = "00:00:00:00"
    }



    private fun subscribeToObservers(){
        TrackingServices.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingServices.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            addLatestPolyLine()
            moveCamera()
        })

        TrackingServices.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            if (isTracking){
                currentTimeMillis = it
                val formattedTime = TrackingUtility.formattedStopWatchTimer(currentTimeMillis, true)
                bind.timerView.text = formattedTime
            }
        })
    }



    private fun toggleRun() =
        if (isTracking) {
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)



    private fun updateTracking(isTracking: Boolean){
        this.isTracking = isTracking
        if (!isTracking && currentTimeMillis > 0L){
            bind.btnStartToggle.text = "Continue"
            bind.btnFinish.visibility = View.VISIBLE
        }else if(isTracking){
            bind.btnStartToggle.text = "Pause"
            bind.btnFinish.visibility = View.GONE
            menu?.getItem(0)?.isVisible = true
        }
    }



    private fun moveCamera(){
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty())
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(pathPoints.last().last(), MAP_ZOOM))
    }



    private fun addAllPolyLines(){
        for (polyLine in pathPoints){
            val polylineOptions: PolylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyLine)
            map?.addPolyline(polylineOptions)
        }
    }



    private fun addLatestPolyLine(){
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1){
            val preLastLatLng: LatLng = pathPoints.last()[pathPoints.last().size -2]
            val lastLatLng: LatLng = pathPoints.last().last()
            val polyLineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polyLineOptions)
        }
    }



    private fun zoomingToWholeTrack(){
        val bounds: LatLngBounds.Builder = LatLngBounds.Builder()
        for (polyLine in pathPoints){
            for (pos in polyLine){
                bounds.include(pos)
            }
        }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                bind.map.width,
                bind.map.height,
                (bind.map.height * 0.05f).toInt()
            )
        )
    }



    private fun endRunAndSaveToDataBase(){
        map?.snapshot { bmp ->
            var distanceInMeters: Int = 0
            for (polyLine in pathPoints){
                distanceInMeters += TrackingUtility.calculatePolyLineLength(polyLine).toInt()
            }
            val avgSpeed: Float = round((distanceInMeters/1000f)/(currentTimeMillis/1000f/60/60)*10)/10f
            val dateTimeStamp: Long = Calendar.getInstance().timeInMillis
            val caloriesBurned: Int = ((distanceInMeters / 1000f) *weight).toInt()
            val run: Run = Run(bmp, dateTimeStamp, avgSpeed, distanceInMeters, currentTimeMillis, caloriesBurned)
            viewModel.insertRun(run)

            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                "The Run Ended and has been Saved",
                Snackbar.LENGTH_LONG)
                .show()
            stopRun()
        }
    }



    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingServices::class.java).also {
            it.action = action
            requireContext().startService(it)
        }




    override fun onResume() {
        super.onResume()
        bind.map.onResume()
    }

    override fun onStart() {
        super.onStart()
        bind.map.onStart()
    }

    override fun onStop() {
        super.onStop()
        bind.map.onStop()
    }

    override fun onPause() {
        super.onPause()
        bind.map.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        bind.map.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        bind.map.onSaveInstanceState(outState)
    }
}
