package `in`.v89bhp.obdscanner.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import `in`.v89bhp.obdscanner.R


class GaugeOperationsDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            AlertDialog.Builder(it)
                .setTitle(R.string.gauge_operations)
                .setMessage(R.string.gauge_operations_list)
                .setPositiveButton(android.R.string.ok, null).create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }
}