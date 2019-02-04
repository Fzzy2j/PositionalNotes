package me.fzzy.com.positionalnotes

import android.content.Context
import android.location.Address
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import android.location.Geocoder
import java.io.IOException
import android.R.string.cancel
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.text.InputType
import android.widget.EditText
import com.google.android.gms.maps.model.*
import java.util.jar.Manifest
import org.json.JSONObject
import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.text.method.TextKeyListener.clear
import android.util.Log
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

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
                val marker = MarkerOptions()
                    .position(location!!)
                    .title("Marker")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.button))

                mMap.addMarker(marker)
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
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        else
            mMap.isMyLocationEnabled = true
    }

    override fun onMarkerClick(marker: Marker): Boolean {

        return true
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

    private fun loadNearByPlaces(latitude: Double, longitude: Double)

    //YOU Can change this type at your own will, e.g hospital, cafe, restaurant.... and see how it all works
    {


        mMap.clear()
        val i = intent
        val type = "any"

        val googlePlacesUrl = StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
        googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude)
        googlePlacesUrl.append("&radius=").append(500)
        googlePlacesUrl.append("&types=").append(type)
        googlePlacesUrl.append("&sensor=true")
        googlePlacesUrl.append("&key=${R.string.google_maps_key}")

        val request = JsonObjectRequest(googlePlacesUrl.toString(),
            Response.Listener<JSONObject> { result ->
                Log.i(FragmentActivity.TAG, "onResponse: Result= $result")
                parseLocationResult(result)
            },
            Response.ErrorListener { error ->
                Log.e(FragmentActivity.TAG, "onErrorResponse: Error= $error")
                Log.e(FragmentActivity.TAG, "onErrorResponse: Error= " + error.getMessage())
            })

        AppController.getInstance().addToRequestQueue(request)
    }

}
