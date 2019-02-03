package me.fzzy.com.positionalnotes

import android.content.Context
import android.location.Address
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.location.Geocoder
import java.io.IOException
import android.R.string.cancel
import android.app.AlertDialog
import android.content.DialogInterface
import android.text.InputType
import android.widget.EditText
import com.google.android.gms.maps.model.MapStyleOptions


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var m_Text = ""

    private var location: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter an address")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        builder.setView(input)

        builder.setPositiveButton(
            "OK"
        ) { _, _ ->
            run {
                location = getLocationFromAddress(this, input.text.toString())
                mMap.addMarker(MarkerOptions().position(location!!).title("Marker"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18.5f))
            }
        }

        builder.setNegativeButton(
            "Cancel"
        ) { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
    }

    fun getLocationFromAddress(context: Context, strAddress: String): LatLng? {

        val coder = Geocoder(context)
        val address: List<Address>?
        val p1: LatLng?

        // May throw an IOException
        address = coder.getFromLocationName(strAddress, 5)
        if (address == null) {
            return null
        }

        val location = address[0]
        p1 = LatLng(location.latitude, location.longitude)

        return p1
    }


}
