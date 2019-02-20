package me.fzzy.com.positionalnotes

import android.content.Intent
import android.location.Address
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView


class AddressOptionsActivity : AppCompatActivity() {

    lateinit var address: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_options)

        address = intent.getStringExtra(MapsActivity.EXTRA_ADDRESS)

        findViewById<TextView>(R.id.addressView).text = address
    }

    fun startPaintActivity(v: View) {
        val intent = Intent(this, PaintActivity::class.java)
        intent.putExtra(MapsActivity.EXTRA_ADDRESS, address)

        startActivity(intent)
    }

    fun startViolationActivity(v: View) {
        val intent = Intent(this, ViolationActivity::class.java)
        intent.putExtra(MapsActivity.EXTRA_ADDRESS, address)

        startActivity(intent)
    }

    fun startLandscapeActivity(v: View) {
        val intent = Intent(this, LandscapeActivity::class.java)
        intent.putExtra(MapsActivity.EXTRA_ADDRESS, address)

        startActivity(intent)
    }

}