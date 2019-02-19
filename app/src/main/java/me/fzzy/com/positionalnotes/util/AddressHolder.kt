package me.fzzy.com.positionalnotes.util

import android.content.Context
import android.location.Address
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.*

object AddressHolder {

    private val gson = Gson()

    private val a = A()

    fun addAddress(address: String) {
        a.addresses.add(address)
    }

    fun addAddress(address: Address) {
        addAddress(address.getAddressLine(0))
    }

    fun exists(address: String): Boolean {
        return a.addresses.contains(address)
    }

    fun exists(address: Address): Boolean {
        return a.addresses.contains(address.getAddressLine(0))
    }

    fun getAllAddresses(): ArrayList<String> {
        return a.addresses
    }

    fun load(context: Context) {
        val file = File(context.filesDir, "addresses.json")
        if (!file.exists()) return
        val streamReader = InputStreamReader(FileInputStream(file), "UTF-8")
        val jsonReader = JsonReader(streamReader)
        val holder = gson.fromJson<A>(jsonReader, A::class.java) as A

        for (s in holder.addresses) {
            if (!a.addresses.contains(s)) a.addresses.add(s)
        }
    }

    fun save(context: Context) {
        val file = File(context.filesDir, "addresses.json")
        val bufferWriter = BufferedWriter(FileWriter(file.absoluteFile, false))
        val save = gson.toJson(a)
        bufferWriter.write(save)
        bufferWriter.close()
    }

    private class A {
        val addresses = arrayListOf<String>()
    }

}