package `in`.v89bhp.obdscanner.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.obdparameters.BaseParameter
import `in`.v89bhp.obdscanner.obdparameters.ParameterHolder


class GaugeSettingsDialogFragment : DialogFragment() {

    private lateinit var parameter: BaseParameter
    private lateinit var maxValueEditText: TextInputEditText
    private lateinit var audioAlertSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parameter = ParameterHolder.parameterList[requireArguments().getInt(KEY_PARAMETER_INDEX)]
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            AlertDialog.Builder(it)
                .setView(getCustomView())
                .setPositiveButton(android.R.string.ok){ dialog, which ->
                    parameter.maxAlertValue = maxValueEditText.text.toString().toFloat()
                    parameter.audioAlert = audioAlertSwitch.isChecked
                }.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        val TAG = GaugeSettingsDialogFragment::class.java.simpleName
        const val KEY_TITLE = "title"
        const val KEY_MAX_INPUT_EDIT_TEXT_HINT = "max_input_edit_text_hint"
        const val KEY_MAX_VALUE = "max_default_value"
        const val KEY_PARAMETER_INDEX = "parameter_index"
        const val KEY_AUDIO_ALERT = "audio_alert"
    }

    private fun getCustomView(): View {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.gauge_settings_dialog, null)
        view.findViewById<TextView>(R.id.titleTextView).text = requireArguments().getString(KEY_TITLE)
        view.findViewById<TextInputLayout>(R.id.textInputLayout).hint = requireArguments().getString(KEY_MAX_INPUT_EDIT_TEXT_HINT)
        maxValueEditText = view.findViewById(R.id.maxValueEditText)
        maxValueEditText.setText(requireArguments().getString(KEY_MAX_VALUE))
        audioAlertSwitch = view.findViewById(R.id.audioAlertSwitch)
        audioAlertSwitch.isChecked = requireArguments().getBoolean(KEY_AUDIO_ALERT)
        return view
    }
}