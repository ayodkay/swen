package com.ayodkay.apps.swen.helper.location

import android.content.Context
import android.location.Address
import android.location.Location
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.util.*

interface CoGeocoder {

    companion object {
        fun from(
            context: Context,
            locale: Locale = Locale.getDefault(),
            dispatcher: CoroutineDispatcher = Dispatchers.IO
        ): CoGeocoder = CoGeocoderImpl(context.applicationContext, locale, dispatcher)
    }

    /**
     * Returns an Address that is known to describe the area immediately surrounding the given [location], if available.
     * The returned address will be localized for the [locale] and defaults to [Locale.getDefault].
     *
     * The returned value may be obtained by means of a network lookup. The result is a best guess and is not
     * guaranteed to be meaningful or correct.
     */
    suspend fun getAddressFromLocation(
        location: Location,
        locale: Locale = Locale.getDefault()
    ): Address?

    /**
     * Returns an Address that is known to describe the area immediately surrounding the given [latitude] and
     * [longitude], if available. The returned address will be localized for the [locale] and defaults to
     * [Locale.getDefault].
     *
     * The returned value may be obtained by means of a network lookup. The result is a best guess and is not
     * guaranteed to be meaningful or correct.
     */
    suspend fun getAddressFromLocation(
        latitude: Double,
        longitude: Double,
        locale: Locale = Locale.getDefault()
    ): Address?

    /**
     * Returns an Address that is known to describe the named location, if available. The named location may be a place
     * name such as "Dalvik, Iceland", an address such as "1600 Amphitheatre Parkway, Mountain View, CA", an airport
     * code such as "SFO", etc..  The returned address will be localized for the [locale] and defaults to
     * [Locale.getDefault].
     *
     * The returned value will be obtained by means of a network lookup. The result is a best guess and is not
     * guaranteed to be meaningful or correct.
     */
    suspend fun getAddressFromLocationName(
        locationName: String,
        locale: Locale = Locale.getDefault()
    ): Address?

    /**
     * Returns an Address that is known to describe the named location, if available. The named location may be a place
     * name such as "Dalvik, Iceland", an address such as "1600 Amphitheatre Parkway, Mountain View, CA", an airport
     * code such as "SFO", etc.. The returned address will be localized for the [locale] and defaults to
     * [Locale.getDefault].
     *
     * A bounding box for the search results is specified by the Latitude and Longitude of the Lower Left point and
     * Upper Right point of the box.
     *
     * The returned value will be obtained by means of a network lookup. The result is a best guess and is not
     * guaranteed to be meaningful or correct.
     */
    suspend fun getAddressFromLocationName(
        locationName: String,
        lowerLeftLatitude: Double,
        lowerLeftLongitude: Double,
        upperRightLatitude: Double,
        upperRightLongitude: Double,
        locale: Locale = Locale.getDefault()
    ): Address?

    /**
     * Returns a list of Addresses that are known to describe the area immediately surrounding the given [location].
     * The returned addresses will be localized for the [locale] and defaults to [Locale.getDefault].
     *
     * The returned values may be obtained by means of a network lookup. The results are a best guess and are not
     * guaranteed to be meaningful or correct.
     */
    suspend fun getAddressListFromLocation(
        location: Location,
        locale: Locale = Locale.getDefault(),
        maxResults: Int = 5
    ): List<Address>

    /**
     * Returns a list of Addresses that are known to describe the area immediately surrounding the given [latitude]
     * and [longitude]. The returned addresses will be localized for the [locale] and defaults to [Locale.getDefault].
     *
     * The returned values may be obtained by means of a network lookup. The results are a best guess and are not
     * guaranteed to be meaningful or correct.
     */
    suspend fun getAddressListFromLocation(
        latitude: Double,
        longitude: Double,
        locale: Locale = Locale.getDefault(),
        maxResults: Int = 5
    ): List<Address>

    /**
     * Returns an array of Addresses that are known to describe the named location, which may be a place name such
     * as "Dalvik, Iceland", an address such as "1600 Amphitheatre Parkway, Mountain View, CA", an airport code such
     * as "SFO", etc..  The returned addresses will be localized for the [locale] and defaults to [Locale.getDefault].
     *
     * The returned values will be obtained by means of a network lookup. The results are a best guess and are not
     * guaranteed to be meaningful or correct.
     */
    suspend fun getAddressListFromLocationName(
        locationName: String,
        locale: Locale = Locale.getDefault(),
        maxResults: Int = 5
    ): List<Address>

    /**
     * Returns an array of Addresses that are known to describe the named location, which may be a place name such
     * as "Dalvik, Iceland", an address such as "1600 Amphitheatre Parkway, Mountain View, CA", an airport code such
     * as "SFO", etc.. The returned addresses will be localized for the [locale] and defaults to [Locale.getDefault].
     *
     * A bounding box for the search results is specified by the Latitude and Longitude of the Lower Left point and
     * Upper Right point of the box.
     *
     * The returned values will be obtained by means of a network lookup. The results are a best guess and are not
     * guaranteed to be meaningful or correct.
     */
    suspend fun getAddressListFromLocationName(
        locationName: String,
        lowerLeftLatitude: Double,
        lowerLeftLongitude: Double,
        upperRightLatitude: Double,
        upperRightLongitude: Double,
        locale: Locale = Locale.getDefault(),
        maxResults: Int = 5
    ): List<Address>
}
