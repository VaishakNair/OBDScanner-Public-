package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import `in`.v89bhp.obdscanner.R
import pw.softwareengineer.v89bhp.BuildConfig
import pw.softwareengineer.v89bhp.viewmodels.GaugesViewModel

/**
 *
 * 1. Detects long-press and starts moving the gauge.
 * 2. Detects pinch gesture and scales the gauge.
 */
class GaugeTouchListener(
    private val context: Context, private val baseParameter: BaseParameter,
    private val viewModel: GaugesViewModel
) : View.OnTouchListener {
    private var longPressed: Boolean = false

    private var dx: Float = 0.0f
    private var dy: Float = 0.0f

    private val view: View by lazy {
        baseParameter.gaugeFrame
    }

    private val vibrator: Vibrator by lazy {
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    private val gestureDetector: GestureDetectorCompat by lazy {
        GestureDetectorCompat(context, LongPressListener())
    }

    private val scaleGestureDetector: ScaleGestureDetector by lazy {
        ScaleGestureDetector(context, PinchListener())
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)

        if (longPressed.not()) {
            return gestureDetector.onTouchEvent(event)
        } else {

            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> false

                MotionEvent.ACTION_MOVE -> {

                    (view.parent as View).x = event.rawX + dx
                    (view.parent as View).y = event.rawY + dy


                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    longPressed = false
                }
            }
            return true
        }
    }

    private inner class LongPressListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(event: MotionEvent?): Boolean {
            dx = view.x - (event?.rawX ?: 0.0f)
            dy = view.y - (event?.rawY ?: 0.0f)

            return true
        }

        override fun onLongPress(e: MotionEvent?) {
            if((view.parent as ViewGroup).scaleX == -1.0f) {// Restrict gauge movement when HUD mode
                // is turned on.
                Snackbar.make(view, R.string.hud_no_movement, Snackbar.LENGTH_LONG).show()
                return
            }

            longPressed = true
            // Shake the view
            view.startAnimation(
                AnimationUtils.loadAnimation(context, R.anim.shake)
            )

            // Provide vibration for shaking
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(125)
            } else {
                vibrator.vibrate(VibrationEffect.createOneShot(125, VibrationEffect.DEFAULT_AMPLITUDE))
            }
            Log.i(BuildConfig.APP_NAME, "Long press detected")
        }


        override fun onDoubleTap(e: MotionEvent?): Boolean = view.parent.let {

            (it as ViewGroup).removeView(view)
            ParameterHolder.removeParameter(baseParameter)
            viewModel.storeGauges()
            true
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            Log.i(TAG, "onSingleTapConfirmed")
            val settingsIcon = (view as FrameLayout).getChildAt(1)
            settingsIcon.visibility = if(settingsIcon.isVisible) View.GONE else View.VISIBLE
            return true
        }
    }

    private inner class PinchListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        private var scaleFactor: Float = view.scaleX
        override fun onScale(detector: ScaleGestureDetector?): Boolean {

            scaleFactor *= detector?.scaleFactor ?: 0.0f
            scaleFactor = Math.max(0.5f, Math.min(scaleFactor, 3f))


            view.scaleX = scaleFactor
            view.scaleY = scaleFactor

            return false // IMPORTANT: If 'true' is returned the gauge will shake violently
            // while scaling.
        }
    }

    private companion object {
        private const val TAG = "GaugeTouchListener"
    }
}