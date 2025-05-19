package com.example.astroguessr

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.astroguessr.data.Star
import kotlin.math.abs

class StarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var isInitialized = false
    private var pendingStars: List<Star> = emptyList()
    private var correctStarId: Int? = null
    private var selectedStarId: Int? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            isInitialized = true
            calculateViewBounds()  // Recalculate when view resizes
            invalidate()
        }
    }


    var onStarSelectedListener: OnStarSelectedListener? = null

    // Drawing properties
    private val backgroundPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    }

    private val starPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 24f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private var stars: List<Star> = emptyList()
    private var selectedStar: Star? = null

    // Magnitude visualization parameters
    private val baseStarSize = 1f     // Size for magnitude 0 star
    private val sizeRange = 15f        // Max size variation

    init {
        viewTreeObserver.addOnGlobalLayoutListener {
            if (width > 0 && height > 0 && !isInitialized) {
                isInitialized = true
                Log.d("StarChart", "View dimensions initialized: ${width}x${height}")
                if (pendingStars.isNotEmpty()) {
                    setStars(pendingStars)
                    pendingStars = emptyList()
                }
            }
        }
    }

    fun showFeedback(correctStarId: Int?, selectedStarId: Int?) {
        this.correctStarId = correctStarId
        this.selectedStarId = selectedStarId
        this.selectedStar = null
        invalidate() // Redraw with highlights
    }

    fun setStars(stars: List<Star>) {
        if (isInitialized) {
            this.stars = stars
            calculateViewBounds()
            invalidate()
        } else {
            pendingStars = stars
        }
    }


    private var raMin = 0.0
    private var raMax = 360.0
    private var decMin = -90.0
    private var decMax = 90.0

    private fun calculateViewBounds() {
        if (stars.isEmpty()) return

        raMin = stars.minOf { it.ra }
        raMax = stars.maxOf { it.ra }
        decMin = stars.minOf { it.dec }
        decMax = stars.maxOf { it.dec }

        Log.d("StarChart", "RA range: $raMin to $raMax")
        Log.d("StarChart", "DEC range: $decMin to $decMax")

        // Handle edge case where all stars have same coordinates
        if (raMax - raMin == 0.0) {
            raMin -= 0.1
            raMax += 0.1
            Log.w("StarChart", "Adjusted RA range to prevent division by zero")
        }
        if (decMax - decMin == 0.0) {
            decMin -= 0.1
            decMax += 0.1
            Log.w("StarChart", "Adjusted DEC range to prevent division by zero")
        }


        // Add 10% padding
        val raPadding = (raMax - raMin) * 0.1
        val decPadding = (decMax - decMin) * 0.1
        raMin -= raPadding
        raMax += raPadding
        decMin -= decPadding
        decMax += decPadding
    }

    override fun onDraw(canvas: Canvas) {

        if (!isInitialized || width == 0 || height == 0) {
            Log.w("StarChart", "Skipping draw - view not initialized")
            return
        }
        super.onDraw(canvas)
        Log.d("StarChart", "onDraw called - view size: ${width}x${height}")

        if (width == 0 || height == 0) {
            Log.e("StarChart", "View has zero size! Check layout parameters")
            return
        }

        if (stars.isEmpty()) {
            Log.w("StarChart", "No stars to draw")
            return
        }

        // Draw background
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

        // Draw all stars
        stars.forEach { star ->
            starPaint.color = getStarColor(star)
            drawStar(star, canvas)
        }

        // Draw selected star highlight
        selectedStar?.let { star ->
            val (x, y) = getStarCoordinates(star)
            starPaint.color = Color.YELLOW
            canvas.drawCircle(x, y, getStarSize(star.mag) + 4, starPaint)
        }
    }

    private fun drawStar(star: Star, canvas: Canvas) {
        val (x, y) = getStarCoordinates(star)
        val size = getStarSize(star.mag)

        // Set star color based on magnitude
        starPaint.color = getStarColor(star)

        // Draw star body
        canvas.drawCircle(x, y, size, starPaint)

        // Draw star name for bright stars
//        if (star.mag < 2.5f) {
//            val label = star.name ?: star.bayer ?: ""
//            canvas.drawText(label, x, y - size - 8, textPaint)
//        }
    }

    private fun getStarCoordinates(star: Star): Pair<Float, Float> {
        val x = ((-star.ra + raMax) / (raMax - raMin)) * width
        val y = height - ((star.dec - decMin) / (decMax - decMin)) * height
        return Pair(x.toFloat(), y.toFloat())
    }

    private fun getStarSize(mag: Float): Float {
        // Invert magnitude scale (brighter = larger)
        val normalized = ((6 - mag) / 6)
        return baseStarSize + (normalized.coerceIn(0f, 1f) * sizeRange)
    }

    private fun getStarColor(star: Star): Int {
        return when {
            star.id == correctStarId -> Color.GREEN
            star.id == selectedStarId -> Color.RED
            else -> Color.WHITE
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (correctStarId == null) {
                    selectedStar = findStarAt(event.x, event.y)
                    selectedStar?.let {
                        onStarSelectedListener?.onStarSelected(it)
                        invalidate()
                    }
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun findStarAt(x: Float, y: Float): Star {
        return stars.minBy { star ->
            val (sx, sy) = getStarCoordinates(star)
            abs(sx - x) + abs(sy - y)
        }
    }

    interface OnStarSelectedListener {
        fun onStarSelected(star: Star)
    }
}