package me.hsy.mycanvas.ui.main.pie

import kotlin.jvm.JvmOverloads
import android.animation.TimeInterpolator
import android.view.animation.DecelerateInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import me.hsy.mycanvas.R
import java.util.ArrayList
import kotlin.math.cos
import kotlin.math.sin

class PieChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var colorList: List<Int> =
        arrayListOf(
            Color.parseColor("#FFBB86FC"),
            Color.parseColor("#FF03DAC5"),
            Color.parseColor("#0DC341"),
            Color.parseColor("#FF3700B3")
        )
    private var pieRadius = 0
    private var percentageLength = 0
    private var percentageWidth = 0
    private var percentageTextSize = 0

    private var mCenterX = 0f
    private var mCenterY = 0f

    private var mDataList: MutableList<PieBean>? = null
    private var sum = 0f
    private var percent = 0f
    private var isAnimationOn = false

    init {
        mDataList = ArrayList()

        val attrs = context.obtainStyledAttributes(attrs, R.styleable.PieChart, defStyleAttr, 0)
        pieRadius = dp2px(context, attrs.getInt(R.styleable.PieChart_outer_radius, 100).toFloat())
        percentageLength = dp2px(context, attrs.getInt(R.styleable.PieChart_percentage_length, 7).toFloat())
        percentageWidth = dp2px(context, attrs.getInt(R.styleable.PieChart_percentage_width, 1).toFloat())
        percentageTextSize = sp2px(context, attrs.getInt(R.styleable.PieChart_percentage_size, 12).toFloat())
        attrs.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (0 == mDataList!!.size) return
        drawArcPath(canvas)
    }

    private fun drawArcPath(canvas: Canvas) {
        var startAngle = 0f // start Angle of each section
        for (i in mDataList!!.indices) {
            var radius = pieRadius.toFloat()

            // boundary
            val boundaryRect = RectF(
                mCenterX - radius,
                mCenterY - radius,
                mCenterX + radius,
                mCenterY + radius
            )

            val paint = Paint()
            val path = Path()

            paint.isAntiAlias = false
            paint.color = mDataList!![i].color

            // Angle to cover according to the process of animation
            var swapAngle = mDataList!![i].value / sum * (360f)
            if (isAnimationOn) {
                swapAngle *= percent
            }
            // Arc Path
            path.moveTo(mCenterX, mCenterY)
            path.arcTo(boundaryRect, startAngle, swapAngle)
            path.close()


            // start; stop point of the line leading to the percentage text
            val startX =
                (radius * cos(Math.toRadians((startAngle + swapAngle / 2).toDouble()))).toFloat()
            val startY =
                (radius * sin(Math.toRadians((startAngle + swapAngle / 2).toDouble()))).toFloat()
            val stopX =
                ((radius + percentageLength) * cos(Math.toRadians((startAngle + swapAngle / 2).toDouble()))).toFloat()
            val stopY =
                ((radius + percentageLength) * sin(Math.toRadians((startAngle + swapAngle / 2).toDouble()))).toFloat()

            paint.strokeWidth = percentageWidth.toFloat()
            canvas.drawLine(
                mCenterX + startX,
                mCenterY + startY,
                mCenterX + stopX,
                mCenterY + stopY,
                paint
            )
            try {
                val percentageText = mDataList!![i].value.toInt().toString() + "%"
                val itemName = mDataList!![i].item
                val bounds = Rect()
                paint.getTextBounds(percentageText, 0, percentageText.length, bounds)
                val textWith = bounds.width()
                val textHeight = bounds.height()

                paint.textSize = percentageTextSize.toFloat()
                if (stopX > 0) {
                    //在 1   2象限
                    canvas.drawLine(
                        mCenterX + stopX,
                        mCenterY + stopY,
                        mCenterX + stopX + percentageLength,
                        mCenterY + stopY,
                        paint
                    )
                    canvas.drawText(
                        percentageText,
                        mCenterX + stopX + percentageLength,
                        mCenterY + stopY,
                        paint
                    )
                    canvas.drawText(
                        itemName,
                        mCenterX + stopX + percentageLength,
                        mCenterY + stopY + textHeight * 4,
                        paint
                    )
                } else {
                    //在  3  4象限
                    canvas.drawLine(
                        mCenterX + stopX,
                        mCenterY + stopY,
                        mCenterX + stopX - percentageLength,
                        mCenterY + stopY,
                        paint
                    )
                    canvas.drawText(
                        percentageText,
                        mCenterX + stopX - percentageLength - paint.measureText(percentageText),
                        mCenterY + stopY,
                        paint
                    )
                    canvas.drawText(
                        itemName,
                        mCenterX + stopX - percentageLength - paint.measureText(itemName),
                        mCenterY + stopY + textHeight * 4,
                        paint
                    )
                }
            }
            catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

            // start Angle of next Section
            startAngle += swapAngle
            canvas.drawPath(path, paint)
        }
    }

    private val interpolator: TimeInterpolator = DecelerateInterpolator()
    fun startAnimation(duration: Int) {
        isAnimationOn = true
        val mAnimator = ValueAnimator.ofFloat(0f, 1f)
        mAnimator.duration = duration.toLong()
        mAnimator.interpolator = interpolator
        mAnimator.addUpdateListener { animation ->
            percent = animation.animatedValue as Float
            invalidate()
        }
        mAnimator.start()
    }

    override fun onSizeChanged(width: Int, height: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(width, height, oldw, oldh)
        mCenterX = (width / 2).toFloat()
        mCenterY = (height / 2).toFloat()
    }

    fun setData(data: List<PieBean>) {
        mDataList!!.clear()
        mDataList!!.addAll(data)
        sum = 0f
        for (i in data.indices) {
            sum += data[i].value
            data[i].color = colorList[i]
        }
        invalidate()
    }

    companion object {
        private fun dp2px(context: Context, dpValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }

        private fun sp2px(context: Context, spValue: Float): Int {
            val fontScale = context.resources.displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }
    }
}