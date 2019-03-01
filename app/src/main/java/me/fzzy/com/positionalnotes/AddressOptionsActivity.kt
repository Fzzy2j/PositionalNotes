package me.fzzy.com.positionalnotes

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.R.string.cancel
import android.app.AlertDialog
import android.content.DialogInterface
import android.text.InputType
import android.widget.EditText
import me.fzzy.com.positionalnotes.util.AddressHolder
import me.fzzy.com.positionalnotes.util.NoteType


class AddressOptionsActivity : AppCompatActivity() {

    lateinit var address: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_options)

        address = intent.getStringExtra(MapsActivity.EXTRA_ADDRESS)

        findViewById<TextView>(R.id.addressView).text = address
    }

    fun startPaintActivity(v: View) {
        lastNoteType = NoteType.PAINT
        dispatchTakePictureIntent()
    }

    fun startViolationActivity(v: View) {
        lastNoteType = NoteType.VIOLATION
        dispatchTakePictureIntent()
    }

    fun startLandscapeActivity(v: View) {
        lastNoteType = NoteType.LANDSCAPE
        dispatchTakePictureIntent()
    }

    val REQUEST_IMAGE_CAPTURE = 1

    private var lastUUID: UUID? = null
    private var lastFile: File? = null
    private var lastNoteType: NoteType? = null

    private fun dispatchTakePictureIntent() {
        lastUUID = UUID.randomUUID()
        lastFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "$lastUUID.jpg")
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                takePictureIntent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    FileProvider.getUriForFile(this, "me.fzzy.com.positionalnotes.fileprovider", lastFile!!)
                )
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Add Note")

                    val input = EditText(this)
                    input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                    builder.setView(input)

                    builder.setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        run {
                            val fzzy = AddressHolder.get(address, filesDir)
                            fzzy?.add(AddressHolder.NoteContainer(lastUUID!!, input.text.toString(), Date()), lastNoteType!!)

                            println("note resolved")
                        }
                    }
                    builder.setNegativeButton(
                        "Cancel"
                    ) { dialog, _ -> dialog.cancel() }
                    builder.show()
                }
            }
        }
    }

}