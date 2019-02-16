package me.fzzy.com.positionalnotes

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Criteria
import android.location.LocationManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import me.fzzy.com.positionalnotes.util.SonarThread
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, Observer {

    private lateinit var mMap: GoogleMap
    private lateinit var sonar: SonarThread

    companion object {
        val EXTRA_ADDRESS = "me.fzzy.com.positionalnotes.ADDRESS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
        mMap.setOnMarkerClickListener(this)

        sonar = SonarThread(this, mMap)
        sonar.addObserver(this)
        Thread(sonar).start()

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        else {
            mMap.isMyLocationEnabled = true
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()

            val location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false))

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 18.5f))

        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val intent = Intent(this, AddressOptionsActivity::class.java)
        val address = marker.tag as Address
        intent.putExtra(EXTRA_ADDRESS, address.getAddressLine(0))

        startActivity(intent)

        return true
    }

    override fun update(o: Observable?, arg: Any?) {
        if (arg is Address) {
            runOnUiThread {
                val marker = MarkerOptions()
                    .position(LatLng(arg.latitude, arg.longitude))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.button))
                    .title(arg.getAddressLine(0))
                    .anchor(0.5f, 0.5f)
                mMap.addMarker(marker).tag = arg

            }
        }
    }

}
