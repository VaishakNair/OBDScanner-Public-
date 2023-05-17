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
import kotlinx.android.synthetic.main.gauge_type_picker_fragment.*

class GaugeTypePickerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val dataBinding =
           GaugeTypePickerFragmentBinding.inflate(
                inflater,
                container, false
            )
        dataBinding.clickHandler = this
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        awesomeSpeedometer.speedTo(2f)
        tubeSpeedometer.speedTo(3f)
        speedView.speedTo(5f)
//        deluxeSpeedView.speedTo(4f)
        raySpeedometer.speedTo(3.5f)



        PreferenceManager.getDefaultSharedPreferences(requireContext()).getString("gaugeType", null)?.let {
            selectRadioButton(it)
        } ?: awesomeSpeedometerRadioButton.setChecked(true)
    }

    private fun selectRadioButton(gaugeType: String) {
        when (gaugeType) {
            AwesomeSpeedometer::class.java.name -> awesomeSpeedometerRadioButton.isChecked = true
            TubeSpeedometer::class.java.name -> tubeSpeedometerRadioButton.isChecked = true
            SpeedView::class.java.name -> speedViewRadioButton.isChecked = true
//            DeluxeSpeedView::class.java.name -> deluxeSpeedViewRadioButton.isChecked = true
            RaySpeedometer::class.java.name -> raySpeedometerRadioButton.isChecked = true
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

        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putString("gaugeType", gaugeType).apply()
    }


    private fun uncheckOtherRadioButtons(clickedButton: View) {

        if ((clickedButton === awesomeSpeedometerRadioButton).not()) awesomeSpeedometerRadioButton.isChecked =
            false
        if ((clickedButton === tubeSpeedometerRadioButton).not()) tubeSpeedometerRadioButton.isChecked =
            false
        if ((clickedButton === speedViewRadioButton).not()) speedViewRadioButton.isChecked = false
//        if((clickedButton === deluxeSpeedViewRadioButton).not()) deluxeSpeedViewRadioButton.isChecked = false
        if ((clickedButton === raySpeedometerRadioButton).not()) raySpeedometerRadioButton.isChecked =
            false

    }

    private companion object {
        private const val TAG = "GaugeTypePickerFragment"
    }
}