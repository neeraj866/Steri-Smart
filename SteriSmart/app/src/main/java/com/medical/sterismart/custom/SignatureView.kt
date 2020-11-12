package com.medical.sterismart.custom

import android.content.Context
import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.util.AttributeSet
import android.util.Base64
import android.view.MotionEvent
import android.view.View
import java.io.ByteArrayOutputStream

class SignatureView(
    context: Context?,
    attrs: AttributeSet?
) :
    View(context, attrs) {
    private val paint = Paint()
    private val path = Path()
    var mBitmap: Bitmap? = null
    var callback: TouchCallback? = null
    private var canvas: Canvas? = null
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private val dirtyRect = RectF()
    var viewWidth = 0
    var viewHeight = 0
    var drawn = false
    var allowDraw = true

    interface TouchCallback {
        fun onTouch()
    }

    override fun onSizeChanged(
        w: Int,
        h: Int,
        oldWidth: Int,
        oldHeight: Int
    ) {
        super.onSizeChanged(w, h, oldWidth, oldHeight)
        if (w > 0 && h > 0) {
            viewWidth = w
            viewHeight = h
            val old =
                if (mBitmap == null) null else mBitmap!!.copy(Bitmap.Config.ARGB_8888, true)
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            canvas = Canvas(mBitmap!!)
            if (old != null && mBitmap != null) {
                canvas!!.drawBitmap(
                    old,
                    Rect(0, 0, old.width, old.height),
                    Rect(0, 0, mBitmap!!.width, mBitmap!!.height),
                    paint
                )
            }
        }
    }

    fun saveSignatureString(): String {
        return Base64.encodeToString(
            codec(
                mBitmap,
                CompressFormat.PNG,
                20
            ), Base64.NO_WRAP
        )
    }

    fun clear() {
        if (viewWidth > 0 && viewHeight > 0) {
            mBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888)
            canvas = Canvas(mBitmap!!)
            postInvalidate()
        }
        drawn = false
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(0)
        canvas.drawBitmap(mBitmap!!, 0.0f, 0.0f, paint)
        canvas.drawPath(path, paint)
        parent.requestDisallowInterceptTouchEvent(true)
    }

    fun setBitmap(b: Bitmap?) {
        if (b != null) {
            val sig = b.copy(Bitmap.Config.ARGB_8888, true)
            canvas!!.drawBitmap(
                sig,
                Rect(0, 0, sig.width, sig.height),
                Rect(0, 0, mBitmap!!.width, mBitmap!!.height),
                paint
            )
            postInvalidate()
        }
    }

    private fun touchStart(x: Float, y: Float) {
        path.reset()
        path.moveTo(x, y)
        lastTouchX = x
        lastTouchY = y
    }

    private fun touchMove(x: Float, y: Float) {
        if (allowDraw) {
            drawn = true
            val dx = Math.abs(x - lastTouchX)
            val dy = Math.abs(y - lastTouchY)
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                path.quadTo(
                    lastTouchX,
                    lastTouchY,
                    (lastTouchX + x) / 2.0f,
                    (lastTouchY + y) / 2.0f
                )
                lastTouchX = x
                lastTouchY = y
                if (callback != null) {
                    callback!!.onTouch()
                }
            }
        }
    }

    private fun touchUp() {
        if (allowDraw) {
            path.lineTo(lastTouchX, lastTouchY)
            canvas!!.drawPath(path, paint)
            path.reset()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val eventX = event.x
        val eventY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(eventX, eventY)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(eventX, eventY)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                touchUp()
                invalidate()
            }
            else -> //                debug("Ignored touch event: " + event.toString());
                return false
        }
        //
//        invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
//                (int) (dirtyRect.top - HALF_STROKE_WIDTH),
//                (int) (dirtyRect.right + HALF_STROKE_WIDTH),
//                (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));
//
//        lastTouchX = eventX;
//        lastTouchY = eventY;
        return true
    }

    //        private void debug(String string){
    //        }
    private fun expandDirtyRect(historicalX: Float, historicalY: Float) {
        if (historicalX < dirtyRect.left) {
            dirtyRect.left = historicalX
        } else if (historicalX > dirtyRect.right) {
            dirtyRect.right = historicalX
        }
        if (historicalY < dirtyRect.top) {
            dirtyRect.top = historicalY
        } else if (historicalY > dirtyRect.bottom) {
            dirtyRect.bottom = historicalY
        }
    }

    private fun resetDirtyRect(eventX: Float, eventY: Float) {
        dirtyRect.left = Math.min(lastTouchX, eventX)
        dirtyRect.right = Math.max(lastTouchX, eventX)
        dirtyRect.top = Math.min(lastTouchY, eventY)
        dirtyRect.bottom = Math.max(lastTouchY, eventY)
    }

    fun setTouchCallback(callback: TouchCallback?) {
        this.callback = callback
    }

    companion object {
        private const val TOUCH_TOLERANCE = 4.0f
        private const val STROKE_WIDTH = 5f
        private const val HALF_STROKE_WIDTH = STROKE_WIDTH / 2
        fun codec(
            src: Bitmap?, format: CompressFormat?,
            quality: Int
        ): ByteArray {
            val os = ByteArrayOutputStream()
            src!!.compress(format, quality, os)
            return os.toByteArray()
        }
    }

    init {
        paint.isAntiAlias = true
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = STROKE_WIDTH
    }
}
