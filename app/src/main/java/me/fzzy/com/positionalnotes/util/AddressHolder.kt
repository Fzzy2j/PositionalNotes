package me.fzzy.com.positionalnotes.util

import android.content.Context
import android.location.Address
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

object AddressHolder {

    private val gson = Gson()

    private val a = ListHolder()
    private class ListHolder { val addresses = arrayListOf<FzzyAddress>() }

    fun addAddress(address: Address, dir: File): FzzyAddress {
        for (f in a.addresses) {
            if (f.name == address.getAddressLine(0)) return f
        }
        val fzzy = AddressHolder.FzzyAddress(address, dir)
        a.addresses.add(fzzy)
        save(dir)
        return fzzy
    }

    fun get(address: Address, dir: File): FzzyAddress? {
        return get(address.getAddressLine(0), dir)
    }

    fun get(address: String, dir: File): FzzyAddress? {
        if (a.addresses.isEmpty()) load(dir)
        for (add in a.addresses) {
            if (add.name == address) return add
        }
        return null
    }

    fun exists(address: Address, dir: File): Boolean {
        if (a.addresses.isEmpty()) load(dir)
        for (f in a.addresses) {
            if (f.name == address.getAddressLine(0)) return true
        }
        return false
    }

    fun getAllAddresses(dir: File): ArrayList<FzzyAddress> {
        if (a.addresses.isEmpty()) load(dir)
        return a.addresses
    }

    private fun load(dir: File) {
        val file = File(dir, "addresses.json")
        if (!file.exists()) return
        val streamReader = InputStreamReader(FileInputStream(file), "UTF-8")
        val jsonReader = JsonReader(streamReader)
        val holder = gson.fromJson<ListHolder>(jsonReader, ListHolder::class.java) as ListHolder

        for (s in holder.addresses) {
            if (!a.addresses.contains(s)) a.addresses.add(s)
        }
    }

    private fun save(dir: File) {
        val file = File(dir, "addresses.json")
        val bufferWriter = BufferedWriter(FileWriter(file.absoluteFile, false))
        val save = gson.toJson(a)
        bufferWriter.write(save)
        bufferWriter.close()
    }

    class FzzyAddress {

        var name: String

        var latitude: Double = 0.0
        var longitude: Double = 0.0

        var dir: File

        private var violations: ArrayList<NoteContainer>
        private var notices: ArrayList<NoteContainer>
        private var landscapeNotices: ArrayList<NoteContainer>

        constructor(address: Address, dir: File) {
            name = address.getAddressLine(0)
            latitude = address.latitude
            longitude = address.longitude
            this.dir = dir
            violations = arrayListOf()
            notices = arrayListOf()
            landscapeNotices = arrayListOf()
        }

        constructor(name: String, position: LatLng, dir: File) {
            this.name = name
            latitude = position.latitude
            longitude = position.longitude
            this.dir = dir
            violations = arrayListOf()
            notices = arrayListOf()
            landscapeNotices = arrayListOf()
        }

        fun addViolation(violation: NoteContainer) {
            add(violation, NoteType.VIOLATION)
        }

        fun addNotice(notice: NoteContainer) {
            add(notice, NoteType.PAINT)
        }

        fun addLandscapeNotice(notice: NoteContainer) {
            add(notice, NoteType.LANDSCAPE)
        }

        fun add(note: NoteContainer, type: NoteType) {
            when (type) {
                NoteType.VIOLATION -> violations.add(note)
                NoteType.PAINT -> notices.add(note)
                NoteType.LANDSCAPE -> landscapeNotices.add(note)
            }
            AddressHolder.save(dir)
        }
    }

    class NoteContainer constructor(var uuid: UUID, var string: String, var date: Date)

}