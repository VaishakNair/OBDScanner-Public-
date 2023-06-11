package `in`.v89bhp.obdscanner.fragments

import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.scan_completed.*
import pw.softwareengineer.v89bhp.BuildConfig
import pw.softwareengineer.v89bhp.BuildConfig.APP_NAME
import pw.softwareengineer.v89bhp.R
import pw.softwareengineer.v89bhp.databinding.ScanTroubleCodesFragmentBinding
import pw.softwareengineer.v89bhp.helpers.Utilities
import pw.softwareengineer.v89bhp.recyclerviewadapters.ObdCodesRecyclerViewAdapter
import pw.softwareengineer.v89bhp.ui.dialogs.ConfirmationDialogFragment
import pw.softwareengineer.v89bhp.viewmodels.ScanTroubleCodesViewModel

/**
 * Fragment used to scan and display diagnostic trouble codes (DTCs) if any.
 */
class ScanTroubleCodesFragment : Fragment(), ObdCodesRecyclerViewAdapter.ViewHolder.ObdCodeClickedListener, ConfirmationDialogFragment.OnConfirmationListener {

    private var mInterstitialAd: InterstitialAd? = null

    private lateinit var obdCodesRecyclerView: RecyclerView

    private var errorSnackbar: Snackbar? = null

    private val viewModel: ScanTroubleCodesViewModel by lazy {
        ViewModelProviders.of(this).get(ScanTroubleCodesViewModel::class.java)
    }

    private var popupWindow: PopupWindow? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        if(firebaseRemoteConfig.getBoolean("showAds")) {
            // Load interstitial ad to be shown after successful scan completion
            mInterstitialAd = InterstitialAd(context)
            mInterstitialAd!!.adUnitId = if(BuildConfig.DEBUG) "ca-app-pub-3940256099942544/1033173712"
            else "ca-app-pub-7047031590135602/9603170810"
            mInterstitialAd!!.loadAd(AdRequest.Builder().build())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val dataBinding = ScanTroubleCodesFragmentBinding.inflate(inflater, container, false)
        dataBinding.lifecycleOwner = this
        dataBinding.viewModel = viewModel
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        obdCodesRecyclerView = view.findViewById<RecyclerView>(R.id.obd_codes_recycler_view).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ObdCodesRecyclerViewAdapter(
                this@ScanTroubleCodesFragment,
                viewModel.obdCodes,
                this@ScanTroubleCodesFragment
            )
        }

        viewModel.scanning.observe(viewLifecycleOwner, Observer<Boolean> {
            if (it == false) {
                viewModel.errorMessage?.let {
                    obdCodesRecyclerView.adapter?.notifyDataSetChanged()
                    errorSnackbar?.let {
                        if (it.isShown()) {// Already showing an error message, so suppress this message.
                            viewModel.errorMessage = null
                            return@Observer
                        }
                    }
                    errorSnackbar = Snackbar.make(
                        view,
                        it, Snackbar.LENGTH_LONG
                    ).apply { show() }
                    viewModel.errorMessage = null
                    return@Observer
                }
                // No error. Show the interstitial ad (if any):
                mInterstitialAd?.let {
                    if (mInterstitialAd!!.isLoaded) {
                        mInterstitialAd!!.show()
                    } else {
                        Log.d(APP_NAME, "The interstitial wasn't loaded yet.")
                    }
                }
            }
        })

        clearCodesButton.setOnClickListener {
            showConfirmationDialogFragment()
        }

        confirmedInfoView.setOnClickListener {
            // Dismiss any existing popup window:
            popupWindow?.dismiss()
            popupWindow = Utilities.showPopupWindow(layoutInflater, it, getString(R.string.confirmed), getString(R.string.confirmed_hint))
        }

        pendingInfoView.setOnClickListener {
            // Dismiss any existing popup window:
            popupWindow?.dismiss()
            popupWindow = Utilities.showPopupWindow(layoutInflater, it, getString(R.string.pending), getString(R.string.pending_hint))
        }

        permanentInfoView.setOnClickListener {
            // Dismiss any existing popup window:
            popupWindow?.dismiss()
            popupWindow = Utilities.showPopupWindow(layoutInflater, it, getString(R.string.permanent), getString(R.string.permanent_hint))
        }
    }



    private fun showConfirmationDialogFragment() {
        val clearDtcConfirmationDialogFragment = ConfirmationDialogFragment()

        val args = Bundle()
        args.putString(ConfirmationDialogFragment.KEY_MESSAGE, getString(R.string.clear_dtc_confirmation_message))
        clearDtcConfirmationDialogFragment.arguments = args
        clearDtcConfirmationDialogFragment.setTargetFragment(this, 0)

        clearDtcConfirmationDialogFragment.show(fragmentManager as FragmentManager, ConfirmationDialogFragment.TAG)
    }

    override fun onConfirmation(affirmative: Boolean, confirmationContextString: String?) {
        if(affirmative) {
            viewModel.clearCodes()
        }
    }

    override fun onObdCodeClicked(adapterPosition: Int, ff: Boolean) {
        if (!ff) {
            val webSearchIntent = Intent(Intent.ACTION_WEB_SEARCH)
            webSearchIntent.putExtra(SearchManager.QUERY, viewModel.obdCodes.value!!.get(adapterPosition).first + " obd code")
            try {
                startActivity(webSearchIntent)
            } catch (ex: ActivityNotFoundException) {
                Snackbar.make(
                    view as View,
                    getString(R.string.web_browser_absent), Snackbar.LENGTH_LONG
                ).show()
            }
        } else {// Freeze frame icon clicked
            requireParentFragment().findNavController().navigate(ScanContainerFragmentDirections.actionScanContainerFragmentDestToFreezeFrameDest(viewModel.obdCodes.value!![adapterPosition].first))
        }
    }

    override fun onPause() {
        super.onPause()
        popupWindow?.dismiss()
    }
}