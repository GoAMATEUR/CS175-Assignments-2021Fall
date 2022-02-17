package com.bytedance.compicatedcomponent.homework

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.util.*
import kotlin.math.cos
import kotlin.math.sin
import android.os.Handler
import android.os.Looper
import android.os.Message

/**
 *  author : Huang Siyuan
 *  time   : 2021/11/2
 *  desc   :
 */
class ClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val FULL_ANGLE = 360

        private const val CUSTOM_ALPHA = 140
        private const val FULL_ALPHA = 255

        private const val POINTER_TYPE_SECOND = 2
        private const val POINTER_TYPE_MINUTES = 1
        private const val POINTER_TYPE_HOURS = 0

        private const val DEFAULT_PRIMARY_COLOR: Int = Color.WHITE
        private const val DEFAULT_SECONDARY_COLOR: Int = Color.LTGRAY

        private const val DEFAULT_DEGREE_STROKE_WIDTH = 0.010f

        private const val RIGHT_ANGLE = 90

        private const val UNIT_DEGREE = (6 * Math.PI / 180).toFloat() // 一个小格的度数

        private const val CLK_AUTO_REFRESH = 0 // automatically refresh the clock status
    }

    private var panelRadius = 200.0f // 表盘半径

    private var hourPointerLength = 0f // 指针长度
    private var minutePointerLength = 0f
    private var secondPointerLength = 0f
    private var hourValueRadius = 0f
    private var digitToCenter = 0f

    private var resultWidth = 0
    private  var centerX: Int = 0
    private  var centerY: Int = 0
    private  var radius: Int = 0

    private var degreesColor = 0

    private var calendar: Calendar? = null
    private var now: Date? = null
    private var nowHours: Int = 0
    private var nowMinutes: Int = 0
    private var nowSeconds: Int = 0

    //接收刷新信号Handler
    private val refreshHandler: Handler = Handler(Looper.getMainLooper()) { msg ->
        when(msg.what){
            CLK_AUTO_REFRESH->this.invalidate()
        }
        true
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply{
        textAlign = Paint.Align.CENTER
    } // Painter for texts

    private val needlePaint: Paint
    init {
        degreesColor = DEFAULT_PRIMARY_COLOR
        needlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        needlePaint.style = Paint.Style.FILL_AND_STROKE
        needlePaint.strokeCap = Paint.Cap.ROUND
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size: Int
        val width = measuredWidth
        val height = measuredHeight
        val widthWithoutPadding = width - paddingLeft - paddingRight
        val heightWithoutPadding = height - paddingTop - paddingBottom
        size = if (widthWithoutPadding > heightWithoutPadding) {
            heightWithoutPadding
        } else {
            widthWithoutPadding
        }
        setMeasuredDimension(size + paddingLeft + paddingRight, size + paddingTop + paddingBottom)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        resultWidth = if (height > width) width else height
        val halfWidth = resultWidth / 2
        centerX = halfWidth
        centerY = halfWidth
        radius = halfWidth
        panelRadius = radius.toFloat()
        hourPointerLength = panelRadius - 500
        minutePointerLength = panelRadius - 250
        secondPointerLength = panelRadius - 150
        hourValueRadius = panelRadius - 180
        digitToCenter = panelRadius - 350
        currentTime()
        drawDigitalTimer(canvas)
        drawDegrees(canvas)
        drawHoursValues(canvas)
        drawNeedles(canvas)


        // todo 1: 每一秒刷新一次，让指针动起来
        val msg = Message.obtain(refreshHandler, CLK_AUTO_REFRESH)
        //refreshHandler.sendMessageDelayed(msg, 1000)
        refreshHandler.sendMessageDelayed(msg, 1000 - System.currentTimeMillis() % 1000)
        // This ensures millisecond synchronization with system time
    }

    /**
     * Update current time.
     *
     * @param canvas
     */
    private fun currentTime(){
        calendar = Calendar.getInstance()
        now = calendar!!.time
        nowHours = now!!.hours
        nowMinutes = now!!.minutes
        nowSeconds = now!!.seconds
        //Log.d("@=>", "$nowHours:$nowMinutes:$nowSeconds")
    }


    private fun drawDegrees(canvas: Canvas) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = resultWidth * DEFAULT_DEGREE_STROKE_WIDTH
            color = degreesColor
        }
        val rPadded: Int = centerX - (resultWidth * 0.01f).toInt()
        val rEnd: Int = centerX - (resultWidth * 0.05f).toInt()
        var i = 0
        while (i < FULL_ANGLE) {
            // transparency
            if (i % RIGHT_ANGLE != 0 && i % 15 != 0) {
                paint.alpha = CUSTOM_ALPHA
            } else {
                paint.alpha = FULL_ALPHA
            }
            val startX = (centerX + rPadded * cos(Math.toRadians(i.toDouble())))
            val startY = (centerX - rPadded * sin(Math.toRadians(i.toDouble())))
            val stopX = (centerX + rEnd * cos(Math.toRadians(i.toDouble())))
            val stopY = (centerX - rEnd * sin(Math.toRadians(i.toDouble())))
            canvas.drawLine(
                startX.toFloat(),
                startY.toFloat(),
                stopX.toFloat(),
                stopY.toFloat(),
                paint
            )
            i += 6
        }
    }

    /**
     * Draw Hour Text Values. I only draw 12, 3, 6, 9 to give it a clean look.
     *
     * @param canvas
     */
    private fun drawHoursValues(canvas: Canvas) {
        textPaint.textSize = 70f
        textPaint.color = Color.WHITE
        textPaint.typeface = Typeface.DEFAULT_BOLD
        for (i in 0..3){
            var hour = if (i == 0) {
                12
            } else {
                i * 3
            }
            var degree = i * 15 * UNIT_DEGREE
            val (textWidth, textHeight) = getContentSize("$hour")
            canvas.drawText("$hour",
                centerX.toFloat() + hourValueRadius * sin(degree),
                centerY.toFloat() - hourValueRadius * cos(degree) + textHeight / 2f ,
                textPaint )
        }
        //canvas.drawText("Text", centerX.toFloat(), centerY.toFloat(), paint )
    }

    /**
     * Draw hours, minutes needles
     * Draw progress that indicates hours needle disposition.
     *
     * @param canvas
     */
    private fun drawNeedles(canvas: Canvas) {
        // The current time is moved outside this function
        // 画秒针
        drawPointer(canvas, POINTER_TYPE_SECOND, nowSeconds)
        // 画分针
        // todo 2: 画分针
        drawPointer(canvas, POINTER_TYPE_MINUTES, nowMinutes)
        // 画时针
        val part = nowMinutes / 12
        drawPointer(canvas, POINTER_TYPE_HOURS, 5 * nowHours + part)
    }


    private fun drawPointer(canvas: Canvas, pointerType: Int, value: Int) {
        val degree: Float
        var pointerHeadXY = FloatArray(2)
        needlePaint.strokeWidth = resultWidth * DEFAULT_DEGREE_STROKE_WIDTH
        when (pointerType) {
            POINTER_TYPE_HOURS -> {
                degree = value * UNIT_DEGREE
                needlePaint.color = Color.WHITE
                pointerHeadXY = getPointerHeadXY(hourPointerLength, degree)
            }
            POINTER_TYPE_MINUTES -> {
                degree = value * UNIT_DEGREE
                needlePaint.color = DEFAULT_SECONDARY_COLOR
                pointerHeadXY = getPointerHeadXY(minutePointerLength, degree)
            }
            POINTER_TYPE_SECOND -> {
                degree = value * UNIT_DEGREE
                needlePaint.color = Color.GREEN
                pointerHeadXY = getPointerHeadXY(secondPointerLength, degree)
            }
        }
        canvas.drawLine(
            centerX.toFloat(), centerY.toFloat(),
            pointerHeadXY[0], pointerHeadXY[1], needlePaint
        )
    }

    private fun getPointerHeadXY(pointerLength: Float, degree: Float): FloatArray {
        val xy = FloatArray(2)
        xy[0] = centerX + pointerLength * sin(degree)
        xy[1] = centerY - pointerLength * cos(degree)
        return xy
    }

    /**
     * Draw a digital clock sync with the clock
     *
     * @param canvas
     */
    private fun drawDigitalTimer(canvas: Canvas){
        textPaint.typeface = Typeface.createFromAsset(context.assets, "fonts/Technology.ttf")
        textPaint.color = DEFAULT_SECONDARY_COLOR
        textPaint.textSize = 180f
        val h = if(nowHours>9) "$nowHours" else "0$nowHours"
        val s = if(nowSeconds>9) "$nowSeconds" else "0$nowSeconds"
        val m = if(nowMinutes>9) "$nowMinutes" else "0$nowMinutes"
        canvas.drawText("$h:$m:$s", centerX.toFloat(), centerY + digitToCenter, textPaint)
    }

    /**
     * Get the size of a string content
     *
     * @param canvas
     */
    private fun getContentSize(content:String): Pair<Int, Int> {
        val bounds = Rect()
        textPaint.getTextBounds(content, 0, content.length, bounds)
        val textWith = bounds.width()
        val textHeight = bounds.height()
        return textWith to textHeight
    }
}