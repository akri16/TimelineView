package com.akribase.timelineview

import java.text.SimpleDateFormat
import java.util.*

internal data class EventI(
    val name: String,
    val startIndex: Float,
    val endIndex: Float,
    val disp: String
) {

    constructor(event: Event, start: Int): this(
        event.name,
        getHourIndex(event.startTime, start),
        getHourIndex(event.endTime, start),
        "${convertUnixToString(event.startTime)} - ${convertUnixToString(event.endTime)}"
    )

    companion object {
        private fun convertUnixToString(time: Long) = SimpleDateFormat("hh:mm a", Locale.getDefault())
            .format(Date(time * 1000))


        private fun getHourIndex(time: Long, start: Int): Float {
            val formatter = SimpleDateFormat("HH mm", Locale.getDefault())

            val hm = formatter.format(Date(time * 1000)).split(" ")
            var h = hm[0].toFloat()
            val m = hm[1].toInt()
            h = h + m/60f - start

            if (h < 0) h += TimelineView.TOTAL
            return h
        }

    }

}
