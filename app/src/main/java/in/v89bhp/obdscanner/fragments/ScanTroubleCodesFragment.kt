package `in`.v89bhp.obdscanner.fragments

import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.databinding.ScanTroubleCodesFragmentBinding
import `in`.v89bhp.obdscanner.helpers.Utilities
import `in`.v89bhp.obdscanner.ui.scan.FreezeFrameRecyclerViewAdapter
import `in`.v89bhp.obdscanner.ui.scan.ObdCodesRecyclerViewAdapter
import `in`.v89bhp.obdscanner.ui.scan.ScanTroubleCodesViewModel
import `in`.v89bhp.obdscanner.ui.scan.ScanUiState


/**
 * Fragment used to scan and display diagnostic trouble codes (DTCs) if any.
 */
class ScanTroubleCodesFragment : Fragment(), ObdCodesRecyclerViewAdapter.ViewHolder.ObdCodeClickedListener  {


    private lateinit var obdCodesRecyclerView: RecyclerView

    private var errorSnackbar: Snackbar? = null

    private val viewModel: ScanTroubleCodesViewModel by activityViewModels()

    private var popupWindow: PopupWindow? = null

    private lateinit var viewBinding: ScanTroubleCodesFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScanUiState.scanTroubleCodesFragment = this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewBinding = ScanTroubleCodesFragmentBinding.inflate(inflater, container, false)
        viewBinding.lifecycleOwner = this
        viewBinding.viewModel = viewModel
        return viewBinding.root
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
                   }
        })

        viewBinding.clearCodesButton.setOnClickListener {
            ScanUiState.showClearTroubleCodesDialog = true
        }

        viewBinding.confirmedInfoView.setOnClickListener {
            // Dismiss any existing popup window:
            popupWindow?.dismiss()
            popupWindow = Utilities.showPopupWindow(layoutInflater, it, getString(R.string.confirmed), getString(R.string.confirmed_hint))
        }

        viewBinding.pendingInfoView.setOnClickListener {
            // Dismiss any existing popup window:
            popupWindow?.dismiss()
            popupWindow = Utilities.showPopupWindow(layoutInflater, it, getString(R.string.pending), getString(R.string.pending_hint))
        }

        viewBinding.permanentInfoView.setOnClickListener {
            // Dismiss any existing popup window:
            popupWindow?.dismiss()
            popupWindow = Utilities.showPopupWindow(layoutInflater, it, getString(R.string.permanent), getString(R.string.permanent_hint))
        }
    }



    fun clearTroubleCodes() {
        viewModel.clearCodes()
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
            requireParentFragment().childFragmentManager.commit {
                val ffFragment = FreezeFrameFragment()
                ffFragment.arguments = Bundle().apply { putString(FreezeFrameFragment.KEY_ARG, viewModel.obdCodes.value!![adapterPosition].first) }
//                replace(R.id.scan_container_pager, ffFragment) // TODO
                addToBackStack("fffragment")
            }
//            requireParentFragment().findNavController().navigate(ScanContainerFragmentDirections.actionScanContainerFragmentDestToFreezeFrameDest(viewModel.obdCodes.value!![adapterPosition].first))
        }
    }

    override fun onPause() {
        super.onPause()
        popupWindow?.dismiss()
    }
}