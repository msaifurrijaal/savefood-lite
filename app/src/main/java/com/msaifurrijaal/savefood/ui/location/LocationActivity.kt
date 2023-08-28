package com.msaifurrijaal.savefood.ui.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.databinding.ActivityLocationBinding
import com.msaifurrijaal.savefood.utils.convertToAddressAsync
import com.msaifurrijaal.savefood.utils.showDialogError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import java.lang.StringBuilder

class LocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityLocationBinding
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationManager: LocationManager
    private lateinit var locationSettingsRequest: LocationSettingsRequest
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var task: Task<LocationSettingsResponse>
    private var isRequestingUpdateLocation = false
    private var lastKnownLocation: LatLng? = null
    private var location: String? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val location = locationResult.lastLocation
            val lat = location?.latitude
            val lon = location?.longitude
            val position = LatLng(lat!!, lon!!)

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM), 2000, null)

            isRequestingUpdateLocation = true
            successGetLocation()

            fusedLocationProviderClient?.removeLocationUpdates(this)
        }
    }

    private val mapPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initMap()
        initLocation()
        onAction()

    }

    private fun onAction() {
        binding.apply {
            fabBackChooseLocation.setOnClickListener {
                val intent = Intent()
                intent.putExtra("updatedLocation", location)
                intent.putExtra("Lat", lastKnownLocation?.latitude.toString())
                intent.putExtra("Lng", lastKnownLocation?.longitude.toString())
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            fabCurrentLocationChooseLocation.setOnClickListener { requestLocation() }
        }
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initLocation() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            interval = 1000 * 5.toLong()
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest
            .Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)
        task = client.checkLocationSettings(builder.build())

        locationSettingsRequest = builder.build()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQUEST_CODE_MAP_PERMISSIONS -> {
                if (grantResults.isNotEmpty()) {
                    var isHasPermission = false
                    val permissionNotGrantedMessage = StringBuilder()

                    for (i in permissions.indices) {
                        isHasPermission = grantResults[i] == PackageManager.PERMISSION_GRANTED

                        if (!isHasPermission) {
                            permissionNotGrantedMessage.append("${permissions[i]}\n")
                        }
                    }
                    if (isHasPermission) {
                        requestUpdateLocation()
                    } else {
                        val message = permissionNotGrantedMessage.toString() + "\n" + getString(R.string.not_granted)
                        showDialogError(this, message)
                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val defaultLocation = LatLng(-6.879372155435071, 107.58995007164813)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM))

        mMap.setOnCameraMoveListener {
            loadingLocation()
        }

        mMap.setOnCameraIdleListener {
            lastKnownLocation = mMap.cameraPosition.target
            val address = lastKnownLocation?.let {
                lifecycleScope.launch {
                    val convertedAddress = lastKnownLocation?.convertToAddressAsync(this@LocationActivity)
                    withContext(Dispatchers.Main) {
                        if (convertedAddress != null) {
                            location = convertedAddress
                            binding.tvAddressChooseLocation.text = convertedAddress
                            successGetLocation()
                        } else {
                            loadingLocation()
                        }
                    }
                }
            }
        }
        requestLocation()
    }

    @SuppressLint("MissingPermission")
    private fun requestUpdateLocation() {
        if (checkMapPermissions()) {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = false

            loadingLocation()

            fusedLocationProviderClient?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun requestLocation() {
        if (checkMapPermissions()) {
            if (isLocationEnabled()) {
                requestUpdateLocation()
            } else {
                goTurnOnGps()
            }
        } else {
            setRequestMapPermissions()
        }
    }

    private fun setRequestMapPermissions() {
        requestPermissions(mapPermissions, REQUEST_CODE_MAP_PERMISSIONS)
    }

    private fun goTurnOnGps() {
        task.addOnFailureListener {
            if (it is ResolvableApiException) {
                try {
                    it.startResolutionForResult(this, REQUEST_CODE_LOCATION)
                } catch (sendEx : IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }
            }
        }
    }

    private fun isLocationEnabled() : Boolean {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return true
        }
        return false
    }

    private fun checkMapPermissions(): Boolean {
        var isHasPermission = false
        for (permission in mapPermissions) {
            isHasPermission = ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
        return isHasPermission
    }

    private fun successGetLocation() {
        binding.apply {
            pbLocation.visibility = View.GONE
            tvAddressChooseLocation.visibility = View.VISIBLE
            btnChooseLocation.visibility = View.VISIBLE
        }
    }

    private fun loadingLocation() {
        binding.apply {
            tvAddressChooseLocation.visibility = View.INVISIBLE
            btnChooseLocation.visibility = View.INVISIBLE
            pbLocation.visibility = View.VISIBLE
        }
    }

    companion object {
        const val REQUEST_CODE_MAP_PERMISSIONS = 1000
        const val REQUEST_CODE_LOCATION = 2000
        const val DEFAULT_ZOOM = 18F

    }
}