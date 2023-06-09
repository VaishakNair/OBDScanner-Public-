package `in`.v89bhp.obdscanner.obdparameters

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.github.anastr.speedviewlib.Speedometer
import `in`.v89bhp.obdscanner.BuildConfig
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.helpers.Utilities
import `in`.v89bhp.obdscanner.ui.gauges.GaugeSettingsDialogState
import `in`.v89bhp.obdscanner.ui.gauges.GaugesAppBarState
import `in`.v89bhp.obdscanner.ui.gauges.GaugesViewModel


/**
 * Convert dp to pixel. Logic copied from *Gauge* class of *speedview* library.
 */
private fun dpToPx(context: Context, dp: Float) = dp * context.resources.displayMetrics.density;

abstract class BaseParameter(
    var context: Context,
    private val viewModel: ViewModel?,
    val pid: String,
    private val gaugeUnit: String,
    private val ticks: List<Float>,
    private val min: Float,
    private val max: Float,
    val parameterName: String,
    unitTextSize: Float = dpToPx(context, 15f),
    private val gaugeType: String? = null
) {

    private val red: Int = context.resources.getColor(android.R.color.holo_red_light)

    /**
     * The actual value of this parameter
     */
    var value: Float = 0f

    var valueString: String = Utilities.NO_DATA

    /**
     * String for unit set by user
     */
    var unit: String = ""

    protected var processResponseStartIndex: Int = 4
    protected var processResponseEndIndex: Int = 0

    protected var processFFResponseStartIndex: Int = 6
    protected var processFFResponseEndIndex: Int = 0

    var gaugeInitialized: Boolean = false

    /**
     * Upper bound for making needle red with optional audio alert.
     */
    var maxAlertValue: Float = Float.MAX_VALUE

    /**
     * Play beep sound when **value** exceeds **maxAlertValue**
     */
    var audioAlert: Boolean = false
    private var toneGenerator: ToneGenerator? = null

    private var defaultIndicatorColor: Int = 0
    private var defaultLowSpeedColor: Int = 0

    val gaugeFrame: FrameLayout by lazy {
        val gauge = this.gauge
        FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            gauge.layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            addView(gauge)
            addView(settingsIcon)
        }
    }

    val settingsIcon: ImageView by lazy {
        ImageView(context).apply {
            layoutParams = FrameLayout.LayoutParams(100, 100, Gravity.TOP or Gravity.END)
            background = context.resources.getDrawable(R.drawable.circle_accent)
            setPadding(10, 10, 10, 10)
            setImageDrawable(context.resources.getDrawable(R.drawable.ic_settings))
            visibility = View.GONE // Initially hidden
            setOnClickListener {
                showSettingsDialog()
            }
        }
    }

    val gauge: Speedometer by lazy {
        val gauge = instantiateGauge()


        gauge.unit = gaugeUnit
        gauge.unitTextSize = unitTextSize
        gauge.setEndDegree(405)
        gauge.isWithTremble = false
//        gauge.tickNumber  = 8

        gauge.minSpeed = min
        gauge.maxSpeed = max
        gauge.ticks = ticks


        // Touch listener is needed only while displaying gauges.  Can be ignored while using this class for processing freeze frame data (with freeze frame view model)
        viewModel?.let {
            if (it is GaugesViewModel) gauge.setOnTouchListener(
                GaugeTouchListener(
                    context,
                    this,
                    it
                )
            )
        }

        defaultIndicatorColor = gauge.indicatorColor
        defaultLowSpeedColor = gauge.lowSpeedColor

        gaugeInitialized = true

        gauge
    }

    private fun instantiateGauge(): Speedometer {
        // 'this.gaugeType' will have a non-null value when parameter is restored
        // from a JSON file. This helps in restoring correct gauges across sessions.
        val gaugeType =
            this.gaugeType ?: PreferenceManager.getDefaultSharedPreferences(context).getString(
                "gaugeType",
                "com.github.anastr.speedviewlib.AwesomeSpeedometer"
            ) as String

        return Class.forName(
            gaugeType
        ).constructors[0].newInstance(
            context
        ) as Speedometer
    }

//    private fun showSettingsDialog() {
//        val mDialogFragment = GaugeSettingsDialogFragment()
//        val arguments = Bundle()
//        arguments.putInt(GaugeSettingsDialogFragment.KEY_PARAMETER_INDEX, ParameterHolder.getParameterIndex(this))
//        arguments.putString(GaugeSettingsDialogFragment.KEY_TITLE, "$parameterName ($unit)")
//        arguments.putString(GaugeSettingsDialogFragment.KEY_MAX_INPUT_EDIT_TEXT_HINT, "For values above ($unit)")
//        if(maxAlertValue == Float.MAX_VALUE) {// Max alert value has not been set yet
//            maxAlertValue = gauge.maxSpeed
//        }
//        arguments.putString(GaugeSettingsDialogFragment.KEY_MAX_VALUE, maxAlertValue.toString())
//        arguments.putBoolean(GaugeSettingsDialogFragment.KEY_AUDIO_ALERT, audioAlert)
//
//        mDialogFragment.arguments = arguments
//        mDialogFragment.show((context as FragmentActivity).supportFragmentManager,
//            GaugeSettingsDialogFragment.TAG
//        )
//    }

    private fun showSettingsDialog() {
        if (maxAlertValue == Float.MAX_VALUE) {// Max alert value has not been set yet
            maxAlertValue = gauge.maxSpeed
        }

        GaugesAppBarState.gaugeSettingsDialogState = GaugeSettingsDialogState(
            show = true,
            parameterIndex = ParameterHolder.getParameterIndex(this),
            title = "$parameterName ($unit)",
            label = "For values above ($unit)",
            maxValue = maxAlertValue.toString(),
            audioAlert = audioAlert

        )
    }

    /**
     * Called when a response to the request for this parameter
     * is received
     */
    open fun processResponse(response: String) {
        val responseFiltered = response.filter {
            // Remove spaces and carriage returns
            it != ' ' && it != '\r'
        }

        if (responseFiltered.contains("NODATA") || responseFiltered.startsWith("7F")) {
            gauge.speedToNoData(Utilities.SPEED_TO_DURATION) // TODO alter duration param

            // Reset gauge indicator color:
            gauge.indicatorColor = defaultIndicatorColor
            gauge.lowSpeedColor = defaultLowSpeedColor
            // Turn off audio alert if it's on:
            toneGenerator?.let {
                it.stopTone()
                it.release()
                toneGenerator = null
            }
            return
        }

        try {
            calculateValue(
                Integer.parseInt(
                    if (processResponseEndIndex == 0) responseFiltered.substring(
                        processResponseStartIndex
                    )
                    else responseFiltered.substring(
                        processResponseStartIndex,
                        processResponseEndIndex
                    ), 16
                ).toFloat()
            )
        } catch (ex: NumberFormatException) {
            Log.e(BuildConfig.APP_NAME, ex.message ?: ex.toString())
            value = 0f
        } catch (ex: StringIndexOutOfBoundsException) {
            Log.e(BuildConfig.APP_NAME, ex.message ?: ex.toString())
            value = 0f
        }

        valueString = value.toString()

        if (gaugeInitialized) {

            if (value > maxAlertValue) {
                // Set indicator needle to red color:
                gauge.indicatorColor = red
                gauge.lowSpeedColor = red

                if (audioAlert) {
                    // Turn on audio alert if it's not already on
                    if (toneGenerator == null) {
                        toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
                        toneGenerator!!.startTone(ToneGenerator.TONE_SUP_ERROR)
                    }
                } else {
                    // Turn off audio alert
                    toneGenerator?.let {
                        it.stopTone()
                        it.release()
                        toneGenerator = null
                    }
                }

            } else {
                gauge.indicatorColor = defaultIndicatorColor
                gauge.lowSpeedColor = defaultLowSpeedColor
                // Turn off audio alert if it's on
                toneGenerator?.let {
                    it.stopTone()
                    it.release()
                    toneGenerator = null
                }
            }

            gauge.speedTo(value, Utilities.SPEED_TO_DURATION)// TODO alter duration param
        }
    }

    /**
     * Called when a response to the freeze frame request for this parameter
     * is received
     */
    open fun processFFResponse(response: String) {
        val responseFiltered = response.filter {
            // Remove spaces and carriage returns
            it != ' ' && it != '\r'
        }

        if (responseFiltered.contains("NODATA") || responseFiltered.startsWith("7F")) {
            valueString = Utilities.NO_DATA
            return
        }

        try {
            calculateValue(
                Integer.parseInt(
                    if (processFFResponseEndIndex == 0) responseFiltered.substring(
                        processFFResponseStartIndex
                    )
                    else responseFiltered.substring(
                        processFFResponseStartIndex,
                        processFFResponseEndIndex
                    ), 16
                ).toFloat()
            )
        } catch (ex: NumberFormatException) {
            Log.e(BuildConfig.APP_NAME, ex.message ?: ex.toString())
            value = 0f
            return
        } catch (ex: StringIndexOutOfBoundsException) {
            Log.e(BuildConfig.APP_NAME, ex.message ?: ex.toString())
            value = 0f
            return
        }

        valueString = value.toString()
    }

    /**
     * PID specific calculation logic to be implemented by subclasses.
     */
    abstract fun calculateValue(response: Float)

}