package com.example.cvicenie2.fragments
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.cvicenie2.BottomBar
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.cvicenie2.R
import com.example.cvicenie2.databinding.FragmentProfileBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.cvicenie2.broadcastReceivers.GeofenceBroadcastReceiver
import com.example.cvicenie2.data.DataRepository
import com.example.cvicenie2.viewmodels.ProfileViewModel
import com.example.cvicenie2.workers.MyWorker
import com.google.android.material.snackbar.Snackbar
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotation
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import java.util.Calendar


class ProfileFragment : Fragment() {
    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding
    private var selectedPoint: CircleAnnotation? = null
    private var lastLocation: Point? = null
    private val handler = Handler()
    private val TIME_CHECK_INTERVAL = 1 * 1 * 1000L // 1seconds
    private lateinit var annotationManager: CircleAnnotationManager
    @Volatile
    private var sharingMode: SharingMode = SharingMode.MANUAL

    enum class SharingMode {
        MANUAL,
        AUTOMATIC
    }

    private val PERMISSIONS_REQUIRED = when {
        Build.VERSION.SDK_INT >= 33 -> { // android 13
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        Build.VERSION.SDK_INT >= 29 -> { // android 10
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
        else -> {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }


    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                initLocationComponent()
                addLocationListeners()
            }
        }
    // Function to check if the current time is within a specified range
    private fun isTimeInRange(currentTime: Calendar, startTime: Calendar, endTime: Calendar): Boolean {
        return currentTime.after(startTime) && currentTime.before(endTime)
    }
    fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[ProfileViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            bnd.bottomBar.setActive(BottomBar.PROFILE)
            bnd.loadProfileBtn.setOnClickListener {
                val user = PreferenceData.getInstance().getUser(requireContext())
                user?.let {
                    viewModel.loadUser(it.id)
                }
            }
            bnd.logoutBtn.setOnClickListener {
                PreferenceData.getInstance().clearData(requireContext())
                it.findNavController().navigate(R.id.action_profile_intro)
            }
            bnd.changePasswordBtn.setOnClickListener {
                it.findNavController().navigate(R.id.action_profile_password)

            }
            bnd.editPhotoBtn.setOnClickListener {
                PreferenceData.getInstance().clearData(requireContext())
                it.findNavController().navigate(R.id.action_profile_photo)
            }
            annotationManager = bnd.mapView.annotations.createCircleAnnotationManager()

            val hasPermission = hasPermissions(requireContext())
            onMapReady(hasPermission)

            bnd.myLocation.setOnClickListener {
                if (!hasPermissions(requireContext())) {
                    requestPermissionLauncher.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                } else {
                    lastLocation?.let { refreshLocation(it) }
                    addLocationListeners()
                }
            }
            viewModel.profileResult.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    Snackbar.make(
                        bnd.loadProfileBtn,
                        it,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
            //bnd.locationSwitch.isChecked = sharingMode == SharingMode.MANUAL
            bnd.locationSwitch.isChecked = PreferenceData.getInstance().getSharing(requireContext())

            bnd.locationAutomatSwitch.isChecked = PreferenceData.getInstance().getSharingAuto(requireContext())
            //bnd.locationAutomatSwitch.isChecked = sharingMode == SharingMode.AUTOMATIC
            Log.d("ProfileFragmentCHECK", "sharing mode ${sharingMode}")

            bnd.locationSwitch.setOnCheckedChangeListener { _, checked ->
                Log.d("ProfileFragment", "sharing manual je $checked")
                if (checked) {
                    turnOnSharingManually()
                } else {
                    turnOffSharing()
                }
            }
            if(bnd.locationAutomatSwitch.isChecked){
                turnOnSharingAutomatically()
            }else turnOffSharingAutomatically()
            bnd.locationAutomatSwitch.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    Log.d("ProfileFragment", "sharing automatic je $checked")
                    turnOnSharingAutomatically()
                } else {
                    bnd.locationSwitch.isChecked = sharingMode == SharingMode.MANUAL
                    turnOffSharingAutomatically()

                }
            }
        }
    }
    private fun turnOffSharingAutomatically() {
        Log.d("ProfileFragment", "turnOffSharing")
        PreferenceData.getInstance().putSharingAuto(requireContext(), false)
        removeGeofence()
        sharingMode = SharingMode.MANUAL
        Log.d("ProfileFragmentModeSet", "$sharingMode")
        // Update UI state on the main thread
        handler.removeCallbacksAndMessages(timeCheckRunnable)
        requireActivity().runOnUiThread {
            binding.locationAutomatSwitch.isChecked = false
        }
    }
    // Function to handle automatic sharing toggle
    private fun turnOnSharingAutomatically() {
        // Set sharing mode to AUTOMATIC
        sharingMode = SharingMode.AUTOMATIC
        PreferenceData.getInstance().putSharingAuto(requireContext(), true)
        // Schedule a periodic check to toggle sharing based on time
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed(timeCheckRunnable, TIME_CHECK_INTERVAL)
    }

    // Function to handle manual sharing toggle
    private fun turnOnSharingManually() {
        // Set sharing mode to MANUAL
        sharingMode = SharingMode.MANUAL

        // Perform actions for manual sharing (similar to existing turnOnSharing logic)
        turnOnSharing()
        PreferenceData.getInstance().putSharing(requireContext(), true)
    }

    private val timeCheckRunnable = object : Runnable {
        override fun run() {
            // Check the current time and toggle sharing accordingly
            val currentTime = Calendar.getInstance()
            val startSharingTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            val endSharingTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 17)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            // Ensure that UI updates are done on the main thread
            requireActivity().runOnUiThread {
                Log.d("Profilemainthread", "$sharingMode")

                if (binding.locationAutomatSwitch.isChecked &&
                    sharingMode == SharingMode.AUTOMATIC &&
                    isTimeInRange(currentTime, startSharingTime, endSharingTime)
                ) {
                    // It's within the sharing time range, so turn on sharing
                    turnOnSharing()

                }
                if (!binding.locationAutomatSwitch.isChecked) {
                    handler.removeCallbacks(this)
                    sharingMode=SharingMode.MANUAL
                }


                if (sharingMode == SharingMode.AUTOMATIC && isFragmentVisible()) {
                    handler.postDelayed(this, TIME_CHECK_INTERVAL)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun turnOnSharing() {
       // Log.d("ProfileFragment", "turnOnSharing")

        if (!hasPermissions(requireContext())) {
            binding.locationSwitch.isChecked = false
            for (p in PERMISSIONS_REQUIRED) {
                requestPermissionLauncher.launch(p)
            }
            return
        }


        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) {
            // Logika pre prácu s poslednou polohou
            //Log.d("ProfileFragment", "poloha posledna ${it ?: "-"}")
            if (it == null) {
                Log.e("ProfileFragment", "poloha neznama geofence nevytvoreny")
            } else {
                setupGeofence(it)
            }
        }

    }

    private fun turnOffSharing() {
        Log.d("ProfileFragment", "turnOffSharing")
        PreferenceData.getInstance().putSharing(requireContext(), false)
        removeGeofence()
    }
    @SuppressLint("MissingPermission")
    private fun setupGeofence(location: Location) {
        val geofencingClient = LocationServices.getGeofencingClient(requireActivity())
        val geofence = Geofence.Builder()
            .setRequestId("my-geofence")
            .setCircularRegion(location.latitude, location.longitude, 150f) // 150m polomer
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()
        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val intent = Intent(requireActivity(), GeofenceBroadcastReceiver::class.java)
        val geofencePendingIntent =
            PendingIntent.getBroadcast(
                requireActivity(),
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
            addOnSuccessListener {
                // Geofences boli úspešne pridané
                Log.d("ProfileFragment", "geofence vytvoreny")
                viewModel.updateGeofence(location.latitude, location.longitude, 150.0)
                runWorker()
            }
            addOnFailureListener {
                // Chyba pri pridaní geofences
                it.printStackTrace()
                binding.locationSwitch.isChecked = false
                PreferenceData.getInstance().putSharing(requireContext(), false)
            }
        }
    }

    private fun removeGeofence() {
        Log.d("ProfileFragment", "geofence zruseny")
        val geofencingClient = LocationServices.getGeofencingClient(requireActivity())
        geofencingClient.removeGeofences(listOf("my-geofence"))
        viewModel.removeGeofence()
        cancelWorker()
    }

    private fun runWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val repeatingRequest = PeriodicWorkRequestBuilder<MyWorker>(
            15, TimeUnit.MINUTES, // repeatInterval
            5, TimeUnit.MINUTES // flexInterval
        )
            .setConstraints(constraints)
            .addTag("myworker-tag")
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "myworker",
            ExistingPeriodicWorkPolicy.KEEP, // or REPLACE
            repeatingRequest
        )
    }

    private fun cancelWorker() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork("myworker")

    }
    private fun onMapReady(enabled: Boolean) {
        binding.mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(14.3539484, 49.8001304))
                .zoom(2.0)
                .build()
        )
        binding.mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            if (enabled) {
                initLocationComponent()
                addLocationListeners()
            }
        }

        binding.mapView.getMapboxMap().addOnMapClickListener {
            if (hasPermissions(requireContext())) {
                onCameraTrackingDismissed()
            }
            true
        }
    }


    private fun initLocationComponent() {
        val locationComponentPlugin = binding.mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.pulsingEnabled = true
        }

    }

    private fun addLocationListeners() {
        binding.mapView.location.addOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
        binding.mapView.gestures.addOnMoveListener(onMoveListener)

    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {

        refreshLocation(it)
    }

    private fun refreshLocation(point: Point) {
        binding.mapView.getMapboxMap()
            .setCamera(CameraOptions.Builder().center(point).zoom(14.0).build())
        binding.mapView.gestures.focalPoint =
            binding.mapView.getMapboxMap().pixelForCoordinate(point)
        lastLocation = point
        addMarker(point)

    }

    private fun addMarker(point: Point) {

        if (selectedPoint == null) {
            annotationManager.deleteAll()
            val pointAnnotationOptions = CircleAnnotationOptions()
                .withPoint(point)
                .withCircleRadius(100.0)
                .withCircleOpacity(0.2)
                .withCircleColor("#000")
                .withCircleStrokeWidth(2.0)
                .withCircleStrokeColor("#ffffff")
            selectedPoint = annotationManager.create(pointAnnotationOptions)
        } else {
            selectedPoint?.let {
                it.point = point
                annotationManager.update(it)
            }
        }
    }


    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }


    private fun onCameraTrackingDismissed() {
        binding.mapView.apply {
            location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
            gestures.removeOnMoveListener(onMoveListener)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(timeCheckRunnable)
        binding.mapView.apply {
            location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
            gestures.removeOnMoveListener(onMoveListener)
        }
    }
    private fun isFragmentVisible(): Boolean {
        return isAdded && isVisible && userVisibleHint && !isRemoving
    }
}