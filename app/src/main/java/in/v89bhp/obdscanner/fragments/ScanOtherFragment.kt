package `in`.v89bhp.obdscanner.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer

import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.databinding.ScanOtherFragmentBinding
import `in`.v89bhp.obdscanner.helpers.Utilities
import `in`.v89bhp.obdscanner.ui.scan.ScanOtherViewModel

/**
 * Fetches information that need not be represented using gauges
 * like presence of oxygen sensors, distance run after resetting
 * MIL etc.
 */
class ScanOtherFragment : Fragment() {

    private lateinit var viewBinding: ScanOtherFragmentBinding

    private val viewModel: ScanOtherViewModel by activityViewModels()

    private var popupWindow: PopupWindow? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewBinding = ScanOtherFragmentBinding.inflate(inflater, container, false)
        viewBinding.lifecycleOwner = this
        viewBinding.viewModel = viewModel

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetching.observe(viewLifecycleOwner, Observer<Boolean> {
            if (it == false) {
                populateViewsFromOtherMap()
                viewModel.errorMessage?.let {

                    viewBinding.ff_error_text_view.text = it
                    viewModel.errorMessage = null
                }
            }
        })

        oxygenSensorTypeInfoView.setOnClickListener {
            // Dismiss any existing popup window:
            popupWindow?.dismiss()
            popupWindow = Utilities.showPopupWindow(layoutInflater, it, getString(R.string.oxygen_sensor_types_title),
                getString(R.string.oxygen_sensor_types))
        }

        // Start fetching other data if it's not already being fetched (because of a configuration change):
        if(viewModel.fetching.value!!.not()) viewModel.loadOtherData()
    }

    private fun populateViewsFromOtherMap() {
        generalLayout.apply {
            for((key, value) in viewModel.otherDataMap.entries) {
                if(key.contains(getString(R.string.oxygen), true).not()) {// Not related to oxygen sensors.
                    // O2 sensor data is displayed separately.
                    addView(getChildTextView(key, value))
                }
            }
        }

        oxygenSensorsLayout.apply {
            for(sensor in viewModel.oxygenSensorList) {
                addView(getChildTextView(sensor.substringBefore(':').trim(),
                    sensor.substringAfter(':').trim()))
            }
            for((key, value) in viewModel.otherDataMap.entries) {
                if(key.contains(getString(R.string.oxygen), true)) {// Oxygen sensor-related data
                    addView(getChildTextView(key, value))
                }
            }
        }

        oxygenSensorTypeTextView.text = viewModel.oxygenSensorType
    }

    private fun getChildTextView(key: String, value: String): TextView =
        TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                leftMargin = resources.getDimension(R.dimen.margin).toInt()
                bottomMargin = leftMargin
            }

            text = getString(R.string.key_colon_value, key, value)
        }

    override fun onPause() {
        super.onPause()
        popupWindow?.dismiss()
    }
}