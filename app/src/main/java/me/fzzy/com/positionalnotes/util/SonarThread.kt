package me.fzzy.com.positionalnotes.util

import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Criteria
import android.location.Geocoder
import android.location.LocationManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class SonarThread constructor(private val context: Context, private val mMap: GoogleMap) : Observable(), Runnable {

    private val random = Random()
    private val coder = Geocoder(context)

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val criteria = Criteria()

    private val addresses = arrayListOf<String>()

    fun getAllAddresses() {

    }

    override fun run() {
        while (true) {
            if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false))

                val layer = random.nextInt(3) + 1
                val degree = random.nextInt(12) * 30
                // divide by 2000 ~= 55 meters
                val t = degree + Math.round(Math.random() * 16 - 8)
                val r = (Math.random() + 0.2) * layer * 1.2

                val x = Math.cos(Math.toRadians(t.toDouble()))
                val y = Math.sin(Math.toRadians(t.toDouble()))

                val loc = LatLng(location.latitude + (x / 5000.0 * r), location.longitude + (y / 5000.0 * r))
                val address = coder.getFromLocation(loc.latitude, loc.longitude, 1)[0]

                if (!addresses.contains(address.getAddressLine(0))) {
                    addresses.add(address.getAddressLine(0))
                    setChanged()
                    notifyObservers(address)
                }

                Thread.sleep(500)
            }
        }
    }

}