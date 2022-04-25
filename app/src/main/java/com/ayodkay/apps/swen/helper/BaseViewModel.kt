package com.ayodkay.apps.swen.helper

import androidx.lifecycle.ViewModel
import com.ayodkay.apps.swen.helper.event.SingleLiveEvent

open class BaseViewModel : ViewModel() {
    var source = ""
    var url = ""
    var image = ""
    var title = ""
    var content = ""
    var description = ""
}

typealias Event<T> = SingleLiveEvent<T>
typealias SimpleEvent = Event<Unit>

fun <T> Event<T>.trigger(value: T) = postValue(value)
fun SimpleEvent.trigger() = postValue(Unit)