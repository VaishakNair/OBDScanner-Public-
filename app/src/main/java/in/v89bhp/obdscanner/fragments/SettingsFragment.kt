package `in`.v89bhp.obdscanner.fragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.github.anastr.speedviewlib.AwesomeSpeedometer
import com.github.anastr.speedviewlib.DeluxeSpeedView
import com.github.anastr.speedviewlib.RaySpeedometer
import com.github.anastr.speedviewlib.SpeedView
import com.github.anastr.speedviewlib.TubeSpeedometer
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.helpers.Utilities


class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        private const val TAG = "SettingsFragment"
    }

    private val handler = Handler()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_settings, rootKey)

        findPreference<Preference>("distance")?.let {
            it.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                it.summary = getString(if(newValue as String == "km") R.string.kilometre else R.string.mile)
                true
            }
        }

        findPreference<Preference>("temperature")?.let {
            it.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                it.summary = getString(if(newValue as String == "c") R.string.celsius else R.string.fahrenheit)
                true
            }
        }

        findPreference<Preference>("pressure")?.let {
            it.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                it.summary = getString(if(newValue as String == "p") R.string.psi else R.string.bar)
                true
            }
        }

        findPreference<ListPreference>("language")?.let {

            it.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                it.summary = it.entries[it.findIndexOfValue(newValue as String)]

                handler.postDelayed(Runnable {
                    Utilities.restartApplication(requireActivity(), "settingsfragment")
                }, 100)

                true
            }
        }
    }

    override fun onResume() {
        Log.i(TAG, "onResume()")
        super.onResume()
        updateSummary()
    }

    /**
     * Update summary field of preferences.
     */
    private fun updateSummary() {
        findPreference<ListPreference>("distance")?.let {
            it.summary = it.entry
        }

        findPreference<ListPreference>("temperature")?.let {
            it.summary = it.entry
        }

        findPreference<ListPreference>("pressure")?.let {
            it.summary = it.entry
        }

        findPreference<ListPreference>("language")?.let {
            it.summary = it.entry
        }

        findPreference<Preference>("gaugeType")?.let {
            val gaugeType: String? = PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getString("gaugeType", null)
            val gaugeName = gaugeType?.let { gaugeType ->
                getGaugeName(gaugeType)
            } ?: getString(R.string.awesome_speedometer)
            it.summary = gaugeName
        }
    }

    private fun getGaugeName(gaugeType: String) =
        when(gaugeType) {
            AwesomeSpeedometer::class.java.name -> getString(R.string.awesome_speedometer)
            TubeSpeedometer::class.java.name -> getString(R.string.tube_speedometer)
            SpeedView::class.java.name -> getString(R.string.speed_view)
            DeluxeSpeedView::class.java.name -> getString(R.string.deluxe_speed_view)
            RaySpeedometer::class.java.name -> getString(R.string.ray_speedometer)
            else -> getString(R.string.awesome_speedometer)
        }

//    private fun restartApplication() {
//        // Restart application:
//        val homeActivityClass: Class<*> = Class.forName(if(BuildConfig.FLAVOR_category == "free")
//                    "pw.softwareengineer.v89bhp.ui.MainActivityFree" else
//                    "pw.softwareengineer.v89bhp.ui.MainActivityPaid")
//        val intent = Intent(
//                        requireContext(),
//                        homeActivityClass
//                    ).putExtra(MainActivity.EXTRA_DESTINATION_ID, R.id.settings_dest)
//        requireActivity().finishAffinity() // Finishes all activities.
//        requireActivity().startActivity(intent)    // Start the launch activity
//        exitProcess(0)
//    }
}