package com.example.astroguessr

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.astroguessr.data.Star
import kotlin.math.abs

interface OnStarSelectedListener {
    fun onStarSelected(star: Star)
}

class StarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    interface OnStarSelectedListener {
        fun onStarSelected(star: Star)
    }

    var onStarSelectedListener: OnStarSelectedListener? = null

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            stars.forEach { star ->
                val starX = convertRAtoX(star.ra)
                val starY = convertDECtoY(star.dec)
                if (abs(event.x - starX) < 30 && abs(event.y - starY) < 30) {
                    onStarSelectedListener?.onStarSelected(star)
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private var stars: List<Star> = emptyList()
    private val starPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        textSize = 24f
    }

    fun setStars(stars: List<Star>) {
        this.stars = stars
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawStarBackground(canvas)
        stars.forEach { drawStar(it, canvas) }
    }

    private fun drawStarBackground(canvas: Canvas) {
        // Background drawing logic
    }

    private fun drawStar(star: Star, canvas: Canvas) {
        val x = convertRAtoX(star.ra)
        val y = convertDECtoY(star.dec)
        canvas.drawCircle(x, y, 8f, starPaint)
    }

    private fun convertRAtoX(ra: Double): Float {
        return (width * (ra / 360.0)).toFloat()
    }

    private fun convertDECtoY(dec: Double): Float {
        return (height/2 - (height/180.0 * dec)).toFloat()
    }
}