package `in`.v89bhp.obdscanner.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.databinding.FreezeFrameFragmentBinding
import `in`.v89bhp.obdscanner.ui.scan.FreezeFrameRecyclerViewAdapter
import `in`.v89bhp.obdscanner.ui.scan.FreezeFrameViewModel


/**
 * Fragment used to show freeze frame data for a particular DTC.
 */
class FreezeFrameFragment : Fragment() {

    private lateinit var viewBinding: FreezeFrameFragmentBinding

    private lateinit var ffRecyclerView: RecyclerView

    private val viewModel: FreezeFrameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewBinding = FreezeFrameFragmentBinding.inflate(inflater, container, false)
        viewBinding.lifecycleOwner = this
        viewBinding.viewModel = viewModel
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ffRecyclerView = view.findViewById<RecyclerView>(R.id.freeze_frame_recycler_view).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = FreezeFrameRecyclerViewAdapter(
                this@FreezeFrameFragment,
                viewModel.ffData
            )
        }

        viewModel.fetching.observe(viewLifecycleOwner, Observer<Boolean> {
            if (it == false) {
                ffRecyclerView.adapter?.notifyDataSetChanged()
                viewModel.errorMessage?.let {

                    viewBinding.ffErrorTextView.text = it
                    viewModel.errorMessage = null
                }
                viewBinding.dtcTextView.text =  resources.getString(R.string.freeze_frame_dtc).format(requireArguments().getString(KEY_ARG))
            }
        })

        // Start fetching freeze frame data if it's not already being fetched (because of a configuration change):
        if(viewModel.fetching.value!!.not()) viewModel.loadFFData()

    }

    companion object {
        const val KEY_ARG = "fffragment.obdcode"
    }
}