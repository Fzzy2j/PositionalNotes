package me.fzzy.com.positionalnotes.util

import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Geocoder
import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng
import java.util.*

class SonarThread private constructor(
    private val locationManager: LocationManager,
    private val coder: Geocoder
) : Observable(), Runnable {

    private val random = Random()
    private val criteria = Criteria()

    companion object {
        private var instance: SonarThread? = null

        fun getInstance(context: Context, locationManager: LocationManager): SonarThread {
            if (instance == null) {
                val coder = Geocoder(context)
                AddressHolder.load(context)
                instance = SonarThread(locationManager, coder)
            }

            return instance!!
        }
    }

    override fun run() {
        for (known in AddressHolder.getAllAddresses()) {
            setChanged()
            notifyObservers(coder.getFromLocationName(known, 1)[0])
        }
        while (true) {
            try {
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

                if (!AddressHolder.exists(address.getAddressLine(0))) {
                    AddressHolder.addAddress(address)

                    setChanged()
                    notifyObservers(address)
                }
            } catch (e: SecurityException) {
                //TODO log
            }
            Thread.sleep(500)
        }
    }

}