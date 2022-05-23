package com.ayodkay.apps.swen.helper.onesignal

data class Notification(
    val group: String,
    val data: Array<Array<String>>,
    val smallIconRes: String,
    val iconUrl: String,
    val buttons: String,
    val shouldShow: Boolean,
    var pos: Int,
) {
    fun getTitle(pos: Int): String {
        return data[pos][0]
    }

    fun getMessage(pos: Int): String {
        return data[pos][1]
    }

    fun getLargeIconUrl(pos: Int): String {
        return data[pos][2]
    }

    fun getBigPictureUrl(pos: Int): String {
        return data[pos][3]
    }

    fun getUrl(pos: Int): String {
        return data[pos][4]
    }

    fun shouldShow(): Boolean {
        return shouldShow
    }

    val templatePos: Int
        get() {
            if (pos > 2) pos = 0
            return pos++
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Notification

        if (group != other.group) return false
        if (!data.contentDeepEquals(other.data)) return false
        if (smallIconRes != other.smallIconRes) return false
        if (iconUrl != other.iconUrl) return false
        if (buttons != other.buttons) return false
        if (shouldShow != other.shouldShow) return false
        if (pos != other.pos) return false

        return true
    }

    override fun hashCode(): Int {
        var result = group.hashCode()
        result = 31 * result + data.contentDeepHashCode()
        result = 31 * result + smallIconRes.hashCode()
        result = 31 * result + iconUrl.hashCode()
        result = 31 * result + buttons.hashCode()
        result = 31 * result + shouldShow.hashCode()
        result = 31 * result + pos
        return result
    }
}
