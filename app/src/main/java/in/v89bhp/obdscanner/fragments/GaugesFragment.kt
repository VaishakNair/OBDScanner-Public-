package `in`.v89bhp.obdscanner.fragments

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import androidx.core.content.edit
import androidx.core.view.children
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.preference.DialogPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.github.anastr.speedviewlib.SpeedView
import com.github.anastr.speedviewlib.Speedometer
import com.github.anastr.speedviewlib.TubeSpeedometer
import com.google.android.material.snackbar.Snackbar
import `in`.v89bhp.obdscanner.services.LiveDataService
import `in`.v89bhp.obdscanner.ui.gauges.GaugesViewModel
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.databinding.GaugesFragmentBinding
import `in`.v89bhp.obdscanner.enums.HandlerMessageCodes
import `in`.v89bhp.obdscanner.helpers.BluetoothHelper
import `in`.v89bhp.obdscanner.helpers.ElmHelper
import `in`.v89bhp.obdscanner.helpers.Utilities
import `in`.v89bhp.obdscanner.obdparameters.ParameterHolder

/**
 * Fragment displaying the gauges selected by the user
 * // TODO Implement a multi screen layout just like in Torque app.
 */
class GaugesFragment : Fragment(){

    private var liveDataService: LiveDataService? = null
    private var mBound = false

    private val viewModel: GaugesViewModel by activityViewModels()

    private lateinit var viewBinding: GaugesFragmentBinding

    private val exitFullscreenSnackbar: Snackbar? by lazy {
        view?.let {Snackbar.make(it, R.string.fullscreenHint, Snackbar.LENGTH_LONG)}
    }

    /**
     * Receives message from bound <code>LiveDataService</code>
     */
    private val incomingHandler = @SuppressLint("HandlerLeak")
    object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when(msg.what) {
                HandlerMessageCodes.MESSAGE_ERROR_BUS_BUSY.ordinal -> {
                    exitFullscreenSnackbar?.dismiss()
                    Snackbar.make(view as View, R.string.bus_busy, Snackbar.LENGTH_LONG).setAction(R.string.try_again){
                        liveDataService?.sendPid()
                    }.show()
                }
            }
        }
    }


    private val messenger = Messenger(incomingHandler)


    /** Defines callbacks for service binding, passed to bindService()  */
    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            (binder as LiveDataService.LocalBinder).also {
                liveDataService = it.getService()
                mBound = true
                liveDataService!!.registerClientMessenger(messenger)
                liveDataService!!.sendPid()
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
            liveDataService = null
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.window?.decorView?.setOnSystemUiVisibilityChangeListener { flags ->
            val isFullScreen = (flags and View.SYSTEM_UI_FLAG_FULLSCREEN) != 0

//            requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout).also {
                if (!isFullScreen) {// System is not in full screen (immersive) mode. Hide 'exit full screen hint'
                    // snackbar (if any):
                    exitFullscreenSnackbar?.dismiss()
                    // Unlock a previously locked nav drawer:
//                    it.setDrawerLockMode(LOCK_MODE_UNLOCKED) TODO Lock and unlock nav drawer based on fullscreen mode.
                } else {// System is in full screen (immersive) mode.
                    // Prevent nav drawer from getting displayed:
//                    it.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)
                }
//            }

//            requireActivity().findViewById<View>(R.id.toolbar).visibility = if (!isFullScreen) View.VISIBLE else View.GONE
            // TODO Lock and unlock nav drawer based on fullscreen mode.
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        viewBinding = GaugesFragmentBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view as ViewGroup).setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewRemoved(parent: View?, child: View?) {
                if ((parent as ViewGroup).childCount == 0) {
                    activity?.invalidateOptionsMenu()
                }
            }

            override fun onChildViewAdded(parent: View?, child: View?) {

            }
        })
        viewModel.loadGauges(requireActivity() as FragmentActivity)
        addGaugesFromViewModel()

        BluetoothHelper.connectingLiveData.observe(viewLifecycleOwner, Observer<Boolean> { connecting ->
            Log.i(TAG, "Gauges Fragment Connecting status: ${BluetoothHelper.connectingLiveData.value}")
            if(isVisible) {// Gauges are visible
                if (!connecting) {
                    liveDataService?.let {
                        if(it.isError!!.value as Boolean) it.sendPid()// Bluetooth may be connected and an earlier attempt resulted in an error. Retry.
                    }
//                    if (liveDataService?.isError!!.value as Boolean) liveDataService?.sendPid()
                }
            }
        })

        // Show dialog for selecting language (Occurs once in the lifetime of the app):
//        PreferenceManager.getDefaultSharedPreferences(context).also{
//            if(it.getBoolean("language_chooser_shown", false).not()) {
//                it.edit(commit = true) {putBoolean("language_chooser_shown", true)  }
//                Utilities.showLanguageOptions(parentFragmentManager, this)
//            }
//        }

    }

    // Callback for list preference dialog fragment:
//    override fun <T : Preference?> findPreference(keyy: CharSequence): T? {
//        val listPreference= ListPreference(context).apply {
//            dialogTitle = getString(R.string.select_language)
//            key = "language"
//            entries = context.resources.getStringArray(R.array.language_pref_entries)
//            entryValues = context.resources.getStringArray(R.array.language_pref_values)
//            setDefaultValue("en")
//            setValueIndex(0)
//        }
//        listPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
//            Log.i(TAG, "New value: $newValue")
//            if((preference as ListPreference).value != newValue) {
//                PreferenceManager.getDefaultSharedPreferences(context as Context)
//                    .edit(commit = true) { putString("language", newValue as String) }
//                Utilities.restartApplication(requireActivity())
//            }
//            true
//        }
//        return listPreference as T
//    }

    override fun onPause() {
        super.onPause()

        liveDataService?.stopSending = true

        liveDataService?.unregisterClientMessenger()

        requireActivity().unbindService(serviceConnection)

        ElmHelper.liveDataServiceJustExited = true
    }

    override fun onResume() {
        super.onResume()

        requireActivity().bindService(Intent(activity, LiveDataService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)

        activity?.invalidateOptionsMenu()

        if (ParameterHolder.parameterAdded) {
            invalidateGauges()
            ParameterHolder.parameterAdded = false
        }
    }

    private fun hideSystemUi() {
        exitFullscreenSnackbar?.show()

        activity?.window?.decorView?.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE
    }


    private fun invalidateGauges() {
        (view as ViewGroup).removeViews(0, ParameterHolder.parameterCount)
        addGaugesFromViewModel()
    }

    private fun addGaugesFromViewModel() {
        for (parameter in ParameterHolder.parameterList) {
            parameter.context = requireActivity() // Update context to be the newly created activity as this is used to show gauge settings dialog fragment.
            (view as FrameLayout).addView(parameter.gaugeFrame)// TODO Replace framelayout with some other layout
        }

        setGaugeTickTextColors()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.gauges_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.fullscreen).setVisible((view as ViewGroup).childCount != 0)
        menu.findItem(R.id.info).setVisible((view as ViewGroup).childCount != 0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.fullscreen -> {
                hideSystemUi()
                true
            }
            R.id.gauge_picker_dest -> item.onNavDestinationSelected(findNavController())
            R.id.info -> {
                GaugeOperationsDialogFragment().show(childFragmentManager, "gauge operations")
                true
            }
            R.id.toggle_hud -> {
                toggleHud()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }


    override fun onDestroyView() {
        (view as ViewGroup).removeViews(0, ParameterHolder.parameterCount)

        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()

        viewModel.storeGauges()

    }

    private fun toggleHud() {
        viewBinding.gaugesFrameLayout.apply {
            scaleX = if(scaleX != -1.0f) -1.0f else 1.0f

            background = context.resources.getDrawable(if(scaleX == -1.0f) android.R.color.background_dark else android.R.color.white)

            setGaugeTickTextColors()
        }
    }

    /**
     * Sets tick and text colors of gauges depending on HUD mode.
     */
    private fun setGaugeTickTextColors() {
        viewBinding.gaugesFrameLayout.apply {
            val color =
                context.resources.getColor(if (scaleX == -1.0f) android.R.color.white else android.R.color.black)

            for (childView in children) {
                val gauge = (childView as FrameLayout).children.elementAt(0)
                if (gauge is TubeSpeedometer || gauge is SpeedView) {// TODO Update when new gauges are added
                    (gauge as Speedometer).apply {
                        textColor = color
                        speedTextColor = color
                        unitTextColor = color
                    }
                }
            }
        }
    }

    private companion object {
        private const val TAG = "GaugesFragment"
    }
}

