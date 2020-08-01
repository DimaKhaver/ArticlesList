package com.example.articleslist.presentation.uiflow.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.example.articleslist.R
import com.example.articleslist.presentation.helpers.AnimUtils
import java.util.*
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.min


class ElasticDragDismissFrameLayout @JvmOverloads constructor(
    context: Context?, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0, defStyleRes: Int = 0
) :
    FrameLayout(context!!, attrs, defStyleAttr, defStyleRes) {
    private var dragDismissDistance = Float.MAX_VALUE
    private var dragDismissFraction = -1f
    private var dragDismissScale = 1f
    private var shouldScale = false
    private var dragElasticity = 0.8f

    private var totalDrag = 0f
    private var draggingDown = false
    private var draggingUp = false
    private var mLastActionEvent = 0
    private var callbacks: MutableList<ElasticDragDismissCallback>? = null


    init {
        val typedArray = getContext().obtainStyledAttributes(
            attrs, R.styleable.ElasticDragDismissFrameLayout, 0, 0)

        if (typedArray.hasValue(R.styleable.ElasticDragDismissFrameLayout_dragDismissDistance)) {
            dragDismissDistance = typedArray.getDimensionPixelSize(
                R.styleable.ElasticDragDismissFrameLayout_dragDismissDistance, 0).toFloat()

        } else if (typedArray.hasValue(R.styleable.ElasticDragDismissFrameLayout_dragDismissFraction)) {
            dragDismissFraction = typedArray.getFloat(
                R.styleable.ElasticDragDismissFrameLayout_dragDismissFraction,
                dragDismissFraction
            )
        }
        if (typedArray.hasValue(R.styleable.ElasticDragDismissFrameLayout_dragDismissScale)) {
            dragDismissScale = typedArray.getFloat(
                R.styleable.ElasticDragDismissFrameLayout_dragDismissScale,
                dragDismissScale
            )
            shouldScale = dragDismissScale != 1f
        }
        if (typedArray.hasValue(R.styleable.ElasticDragDismissFrameLayout_dragElasticity)) {
            dragElasticity = typedArray.getFloat(
                R.styleable.ElasticDragDismissFrameLayout_dragElasticity,
                dragElasticity
            )
        }
        typedArray.recycle()
    }

    abstract class ElasticDragDismissCallback {

        open fun onDrag(
            elasticOffset: Float, elasticOffsetPixels: Float,
            rawOffset: Float, rawOffsetPixels: Float) { }

        open fun onDragDismissed() { }
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return nestedScrollAxes and View.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        if (draggingDown && dy > 0 || draggingUp && dy < 0) {
            dragScale(dy)
            consumed[1] = dy
        }
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int,
                                dxUnconsumed: Int, dyUnconsumed: Int) {
        dragScale(dyUnconsumed)
    }

    override fun onInterceptTouchEvent(motionEvent: MotionEvent): Boolean {
        mLastActionEvent = motionEvent.action
        return super.onInterceptTouchEvent(motionEvent)
    }

    override fun onStopNestedScroll(child: View) {
        if (abs(totalDrag) >= dragDismissDistance) {
            dispatchDismissCallback()
        } else { // settle back to natural position
            if (mLastActionEvent == MotionEvent.ACTION_DOWN) {
                translationY = 0f
                scaleX = 1f
                scaleY = 1f
            } else {
                animate()
                    .translationY(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200L)
                    .setInterpolator(AnimUtils.getFastOutSlowInInterpolator(context))
                    .setListener(null)
                    .start()
            }
            totalDrag = 0f
            draggingUp = false
            draggingDown = draggingUp
            dispatchDragCallback(0f, 0f, 0f, 0f)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (dragDismissFraction > 0f) {
            dragDismissDistance = h * dragDismissFraction
        }
    }

    fun addListener(listener: ElasticDragDismissCallback) {
        if (callbacks == null)
            callbacks = ArrayList()

        callbacks!!.add(listener)
    }

    fun removeListener(listener: ElasticDragDismissCallback?) {
        if (callbacks != null && callbacks!!.size > 0)
            callbacks!!.remove(listener)
    }

    private fun dragScale(scroll: Int) {
        if (scroll == 0) return

        totalDrag += scroll.toFloat()

        // track the direction & set the pivot point for scaling
        // don't double track i.e. if start dragging down and then reverse, keep tracking as
        // dragging down until they reach the 'natural' position
        if (scroll < 0 && !draggingUp && !draggingDown) {
            draggingDown = true
            if (shouldScale) pivotY = height.toFloat()
        } else if (scroll > 0 && !draggingDown && !draggingUp) {
            draggingUp = true
            if (shouldScale) pivotY = 0f
        }
        // how far have we dragged relative to the distance to perform a dismiss
        // (0–1 where 1 = dismiss distance). Decreasing logarithmically as we approach the limit
        var dragFraction = log10(1 + (abs(totalDrag) / dragDismissDistance).toDouble()).toFloat()

        // calculate the desired translation given the drag fraction
        var dragTo = dragFraction * dragDismissDistance * dragElasticity
        if (draggingUp) {
            // as we use the absolute magnitude when calculating the drag fraction, need to
            // re-apply the drag direction
            dragTo *= -1f
        }
        translationY = dragTo
        if (shouldScale) {
            val scale = 1 - (1 - dragDismissScale) * dragFraction
            scaleX = scale
            scaleY = scale
        }

        // if we've reversed direction and gone past the settle point then clear the flags to
        // allow the list to get the scroll events & reset any transforms
        if (draggingDown && totalDrag >= 0
            || draggingUp && totalDrag <= 0
        ) {
            dragFraction = 0f
            dragTo = dragFraction
            totalDrag = dragTo
            draggingUp = false
            draggingDown = draggingUp
            translationY = 0f
            scaleX = 1f
            scaleY = 1f
        }
        dispatchDragCallback(
            dragFraction, dragTo,
            min(1f, abs(totalDrag) / dragDismissDistance), totalDrag
        )
    }

    private fun dispatchDragCallback(
        elasticOffset: Float, elasticOffsetPixels: Float,
        rawOffset: Float, rawOffsetPixels: Float
    ) {
        if (callbacks != null && !callbacks!!.isEmpty()) {
            for (callback in callbacks!!) {
                callback.onDrag(
                    elasticOffset, elasticOffsetPixels,
                    rawOffset, rawOffsetPixels
                )
            }
        }
    }

    private fun dispatchDismissCallback() {
        if (callbacks != null && !callbacks!!.isEmpty()) {
            for (callback in callbacks!!) {
                callback.onDragDismissed()
            }
        }
    }


    class SystemHelper(private val fragment: Fragment): ElasticDragDismissCallback() {
        override fun onDragDismissed() {
            fragment.activity?.onBackPressed()
        }
    }
}
