package `in`.v89bhp.obdscanner.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.obdparameters.BaseParameter
import `in`.v89bhp.obdscanner.obdparameters.ParameterHolder
import `in`.v89bhp.obdscanner.ui.gauges.GaugePickerViewModel
import `in`.v89bhp.obdscanner.ui.gauges.GaugesViewModel
import `in`.v89bhp.obdscanner.ui.gauges.PidsRecyclerViewAdapter


/**
 * Fragment for selecting live data gauges
 */
class GaugePickerFragment : Fragment(), PidsRecyclerViewAdapter.ViewHolder.PidClickedListener {

    private lateinit var pidsRecyclerView: RecyclerView

    private val viewModel: GaugePickerViewModel by viewModels()

    /**
     * Passed to created parameter for handling gauge operations.
     */
    private val gaugesViewModel: GaugesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        val dataBinding =
            `in`.v89bhp.obdscanner.databinding.GaugePickerFragmentBinding.inflate(inflater, container, false)
        dataBinding.lifecycleOwner = this
        dataBinding.viewModel = viewModel
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pidsRecyclerView = view.findViewById<RecyclerView>(R.id.pids_recycler_view).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = PidsRecyclerViewAdapter(
                context,
                this@GaugePickerFragment,
                viewModel.pids,
                this@GaugePickerFragment
            )
        }

        viewModel.loadPids()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.gauge_picker_menu, menu)
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setOnQueryTextListener( object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.filterPids(newText ?: "")
                    return true
                }

                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }
            })
        }


    }

    override fun onPidClicked(adapterPosition: Int, disabled: Boolean) {
        Log.i(TAG, "Click received. Adapter position: $adapterPosition")

        if(disabled) {
            Snackbar.make(view as View, R.string.unsupported_parameter, Snackbar.LENGTH_LONG).show()
        } else {

            ParameterHolder.addParameter(
                Class.forName(
                    "`in`.v89bhp.obdscanner.obdparameters.Parameter${viewModel.pids.value!!.get(
                        adapterPosition
                    ).classNameSuffix
                    }"
                ).constructors[0].newInstance(
                    requireActivity(), gaugesViewModel, null
                ) as BaseParameter
            )

            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        // Hide keyboard
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.let{it.hideSoftInputFromWindow(requireView().windowToken, 0)}

        super.onDestroyView()
    }

    private companion object {
        private const val TAG = "GaugePickerFragment"
    }
}

