package `in`.v89bhp.obdscanner.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.github.anastr.speedviewlib.*
import `in`.v89bhp.obdscanner.databinding.GaugeTypePickerFragmentBinding

import `in`.v89bhp.obdscanner.R

class GaugeTypePickerFragment : Fragment() {

    private lateinit var viewBinding: GaugeTypePickerFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

       viewBinding =
           GaugeTypePickerFragmentBinding.inflate(
                inflater,
                container, false
            )
        viewBinding.clickHandler = this
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewBinding.awesomeSpeedometer.speedTo(2f)
        viewBinding.tubeSpeedometer.speedTo(3f)
        viewBinding.speedView.speedTo(5f)
//        deluxeSpeedView.speedTo(4f)
        viewBinding.raySpeedometer.speedTo(3.5f)



        PreferenceManager.getDefaultSharedPreferences(requireActivity().applicationContext).getString("gaugeType", null)?.let {
            selectRadioButton(it)
        } ?: viewBinding.awesomeSpeedometerRadioButton.setChecked(true)
    }

    private fun selectRadioButton(gaugeType: String) {
        when (gaugeType) {
            AwesomeSpeedometer::class.java.name -> viewBinding.awesomeSpeedometerRadioButton.isChecked = true
            TubeSpeedometer::class.java.name -> viewBinding.tubeSpeedometerRadioButton.isChecked = true
            SpeedView::class.java.name -> viewBinding.speedViewRadioButton.isChecked = true
//            DeluxeSpeedView::class.java.name -> viewBinding.deluxeSpeedViewRadioButton.isChecked = true
            RaySpeedometer::class.java.name -> viewBinding.raySpeedometerRadioButton.isChecked = true
        }
    }

    fun onRadioButtonClicked(view: View) {
        uncheckOtherRadioButtons(view)

        val gaugeType = when (view.id) {
            R.id.awesomeSpeedometerRadioButton -> {
                AwesomeSpeedometer::class.java.name
            }

            R.id.tubeSpeedometerRadioButton -> {
                TubeSpeedometer::class.java.name
            }

            R.id.speedViewRadioButton -> {
                SpeedView::class.java.name
            }
//            R.id.deluxeSpeedViewRadioButton -> {DeluxeSpeedView::class.java.name}
            R.id.raySpeedometerRadioButton -> {
                RaySpeedometer::class.java.name
            }

            else -> AwesomeSpeedometer::class.java.name
        }

        PreferenceManager.getDefaultSharedPreferences(requireActivity().applicationContext).edit()
            .putString("gaugeType", gaugeType).apply()
    }


    private fun uncheckOtherRadioButtons(clickedButton: View) {

        if ((clickedButton === viewBinding.awesomeSpeedometerRadioButton).not()) viewBinding.awesomeSpeedometerRadioButton.isChecked =
            false
        if ((clickedButton === viewBinding.tubeSpeedometerRadioButton).not()) viewBinding.tubeSpeedometerRadioButton.isChecked =
            false
        if ((clickedButton === viewBinding.speedViewRadioButton).not()) viewBinding.speedViewRadioButton.isChecked = false
//        if((clickedButton === viewBinding.deluxeSpeedViewRadioButton).not()) viewBinding.deluxeSpeedViewRadioButton.isChecked = false
        if ((clickedButton === viewBinding.raySpeedometerRadioButton).not()) viewBinding.raySpeedometerRadioButton.isChecked =
            false

    }

    private companion object {
        private const val TAG = "GaugeTypePickerFragment"
    }
}