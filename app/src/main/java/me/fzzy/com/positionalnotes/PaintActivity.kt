package me.fzzy.com.positionalnotes

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView

class PaintActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_options)

        val address = intent.getStringExtra(MapsActivity.EXTRA_ADDRESS)

        findViewById<TextView>(R.id.addressView).text = address
    }
}