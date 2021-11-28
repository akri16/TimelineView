package com.akribase.timelineview

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import java.lang.IllegalArgumentException
import kotlin.math.ceil
import kotlin.math.floor
import android.content.res.TypedArray
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.*
import androidx.core.content.ContextCompat.getSystemService
import com.akribase.timelineview.databinding.EventViewBinding
import com.akribase.timelineview.databinding.TimelineViewBinding
import androidx.core.view.GestureDetectorCompat


class TimelineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {
    private val timelineViews = ArrayList<TimelineViewBinding>()
    private var timelineItemViews = ArrayList<View>()
    private var timeLineItems = ArrayList<EventI>()
    private val eventViewMargin = dpToPx(EVENT_VIEW_MARGIN_DP)

    private val timelineItemRects = ArrayList<Rect>()

    var timelineEvents: List<Event> = ArrayList()
    set(value) {

        timeLineItems.clear()
        value.forEach {
            timelineItemRects.add(Rect())
            timeLineItems.add(EventI(it, startTime))
        }
        field = value
        addTimelineItemViews()
        requestLayout()
    }

    var startTime = DEFAULT_START_LABEL
    set(value) {
        if (value !in 0 until TOTAL) {
            throw IllegalArgumentException("Start time has to be fom 0 to 23")
        }
        field = value
        initLabels()
        invalidate()
    }

    private var mDetector: GestureDetectorCompat

    @ColorInt
    var labelColor = DEFAULT_LABEL_COLOR

    @ColorInt
    var eventBg: Int = Color.BLUE

    @ColorInt
    var eventNameColor: Int = DEFAULT_TEXT_COLOR

    @ColorInt
    var eventTimeColor: Int = DEFAULT_TEXT_COLOR

    init {
        orientation = VERTICAL
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.TimelineView,
            0, 0)

        startTime = typedArray.getInt(R.styleable.TimelineView_startLabel, DEFAULT_START_LABEL)
        setBackgroundColor(typedArray.getColor(R.styleable.TimelineView_backgroundColor, DEFAULT_BG_COLOR))
        labelColor = typedArray.getColor(R.styleable.TimelineView_labelColor, DEFAULT_LABEL_COLOR)
        eventNameColor = typedArray.getColor(R.styleable.TimelineView_eventNameColor, DEFAULT_TEXT_COLOR)
        eventTimeColor = typedArray.getColor(R.styleable.TimelineView_eventTimeColor, DEFAULT_TEXT_COLOR)
        eventBg = typedArray.getColor(R.styleable.TimelineView_eventBackground, getColor(R.attr.colorPrimary))


        typedArray.recycle()

        initLabels()


        mDetector = GestureDetectorCompat(context, object: GestureDetector.SimpleOnGestureListener(){

            override fun onDown(event: MotionEvent): Boolean {
                return true
            }

            override fun onLongPress(e: MotionEvent?) {
                if (e != null) {
                    val i = timelineItemRects.indexOfFirst { it.contains(e.x.toInt(), e.y.toInt()) }
                    if (i != -1) {
                        vibrate()
                        timelineItemViews[i].callOnClick()
                    }
                }

            }
        })

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        val divWidth = timelineViews[0].divider.width
        timelineItemViews.forEachIndexed {index, view ->
            val left = (right - eventViewMargin - divWidth + 2*eventViewMargin).toInt()
            val top = getPosFromIndex(timeLineItems[index].startIndex)
            val bottom = getPosFromIndex(timeLineItems[index].endIndex)
            val right = (right - eventViewMargin).toInt()

            val widthSpec = MeasureSpec.makeMeasureSpec (right-left, MeasureSpec.EXACTLY)
            val heightSpec = MeasureSpec.makeMeasureSpec (bottom-top, MeasureSpec.EXACTLY)
            view.measure(widthSpec, heightSpec);
            view.layout(0, 0, right-left, bottom-top)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (mDetector.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        val divWidth = timelineViews[0].divider.width
        timelineItemViews.forEachIndexed {index, view ->
            val left = (right - eventViewMargin - divWidth + 2*eventViewMargin).toInt()
            val top = getPosFromIndex(timeLineItems[index].startIndex)
            val bottom = getPosFromIndex(timeLineItems[index].endIndex)
            val right = (right - eventViewMargin).toInt()

            timelineItemRects[index].set(left, top, right, bottom)

            canvas?.save()
            canvas?.translate(left.toFloat(), top.toFloat())
            view.draw(canvas)
            canvas?.restore()
        }
    }

    private fun initLabels() {
        timelineViews.clear()
        removeAllViewsInLayout()
        for (i in 0 until TOTAL) {
            val binding = TimelineViewBinding.inflate(LayoutInflater.from(context), this, false)
            binding.timelineLabel.text = getTime(i, startTime)
            binding.labelColor = labelColor
            timelineViews.add(binding)
            addView(binding.root)
        }
    }

    private fun addTimelineItemViews() {
        timelineItemViews.clear()
        for (item in timeLineItems) {
            val binding  = EventViewBinding.inflate(LayoutInflater.from(context))
            binding.event = item
            binding.cardView.setCardBackgroundColor(eventBg)
            binding.constrained = true
            binding.eventName.setTextColor(eventNameColor)
            binding.eventTime.setTextColor(eventTimeColor)

            timelineItemViews.add(binding.root)

            binding.root.setOnClickListener {
                ExpandedEventDialog(context, width, item, eventBg).show()
            }

            binding.executePendingBindings()
        }
    }

    private fun getTime(raw: Int, start: Int): String {
        var state = "AM"
        var time = (raw + start) % TOTAL

        if (time >= 12) state = "PM"
        if (time == 0) time = 12
        if (time > 12) time -= 12

        return "${"%2d".format(time)} $state"
    }

    private fun dpToPx(dp: Int) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    )

    private fun getPosFromIndex(index: Float): Int {
        val l = floor(index).toInt()
        val h = ceil(index).toInt()

        val lVal = timelineViews[l].let { it.root.top + it.divider.top }
        val hVal = timelineViews[h].let { it.root.top + it.divider.top }

        return (lVal + (index - l) * (hVal - lVal)).toInt()
    }

    private fun getColor(colorAttr: Int): Int {
        val typedValue = TypedValue()
        val a: TypedArray = context.obtainStyledAttributes(typedValue.data, intArrayOf(colorAttr))
        val color = a.getColor(0, 0)
        a.recycle()
        return color
    }

    private fun vibrate() {
        val v = getSystemService(context, Vibrator::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v?.vibrate(VibrationEffect.createOneShot(SELECT_VIBRATION_TIME_MS, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v?.vibrate(SELECT_VIBRATION_TIME_MS);
        }
    }

    companion object {
        const val TOTAL = 24
        const val SELECT_VIBRATION_TIME_MS = 60L
        const val DEFAULT_START_LABEL = 7
        const val DEFAULT_BG_COLOR = Color.BLACK
        const val DEFAULT_LABEL_COLOR = Color.DKGRAY
        const val EVENT_VIEW_MARGIN_DP = 20
        const val DEFAULT_TEXT_COLOR = Color.WHITE
    }

}