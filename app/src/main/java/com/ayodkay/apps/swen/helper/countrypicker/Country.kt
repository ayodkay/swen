package com.ayodkay.apps.swen.helper.countrypicker

import android.os.Parcel
import android.os.Parcelable

class Country : Parcelable, Comparable<Country> {
    @JvmField
    var code: String? = null
    @JvmField
    var name: String? = null
    @JvmField
    var iso: String? = null
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(code)
        dest.writeString(name)
        dest.writeString(iso)
    }

    override fun compareTo(other: Country): Int {
        return name!!.compareTo(other.name!!)
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Country?> = object : Parcelable.Creator<Country?> {
            override fun createFromParcel(source: Parcel): Country? {
                val country = Country()
                country.code = source.readString()
                country.name = source.readString()
                country.iso = source.readString()
                return country
            }

            override fun newArray(size: Int): Array<Country?> {
                return arrayOfNulls(size)
            }
        }
    }
}