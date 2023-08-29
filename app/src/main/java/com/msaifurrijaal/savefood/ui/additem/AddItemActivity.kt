package com.msaifurrijaal.savefood.ui.additem

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.msaifurrijaal.savefood.R
import com.msaifurrijaal.savefood.databinding.ActivityAddItemBinding
import com.msaifurrijaal.savefood.ui.location.LocationActivity

class AddItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddItemBinding
    private var location: String? = null
    private var currentLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onAction()
    }

    private fun onAction() {
        binding.apply {
            ivLocation.setOnClickListener {
                val intent = Intent(this@AddItemActivity, LocationActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                location = data?.getStringExtra("updatedLocation")
                currentLocation = LatLng(
                    data?.getStringExtra("Lat")!!.toDouble(),
                    data?.getStringExtra("Lng")!!.toDouble()
                )
                Log.d("MainActivity", currentLocation.toString())
                binding.etLocation.setText(location)
            }
        }
    }

    companion object {
        const val REQUEST_CODE = 101
    }

}