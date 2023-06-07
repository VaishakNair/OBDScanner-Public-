package `in`.v89bhp.obdscanner.ui.gauges

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.obdparameters.SupportedPidsHolder
import pw.softwareengineer.v89bhp.room.entities.ParameterId

class PidsRecyclerViewAdapter(
    private val applicationContext: Context,
    owner: LifecycleOwner, val pids: LiveData<List<ParameterId>>,
    private val pidClickedListener: ViewHolder.PidClickedListener
) :
    RecyclerView.Adapter<PidsRecyclerViewAdapter.ViewHolder>() {


    class ViewHolder(
        view: View,
        val listener: PidClickedListener
    ) : RecyclerView.ViewHolder(view) {
        interface PidClickedListener {
            fun onPidClicked(adapterPosition: Int, disabled: Boolean)
        }

        val nameTextView: TextView = view.findViewById(R.id.name)
        val descriptionTextView: TextView = view.findViewById(R.id.description)

        init {
            view.setOnClickListener {
                listener.onPidClicked(
                    adapterPosition, nameTextView.currentTextColor ==
                            ContextCompat.getColor(nameTextView.context, R.color.light_grey)
                )

            }
        }
    }

    private val dataObserver = Observer<List<ParameterId>> {
        notifyDataSetChanged()
    }

    init {
        pids.observe(owner, dataObserver)
    }

    override fun getItemCount(): Int =
        pids.value?.size ?: 0


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val nameResourceEntryName = pids.value!![position].nameResourceEntryName
        val descriptionResourceEntryName = pids.value!![position].descriptionResourceEntryName
        viewHolder.nameTextView.text = applicationContext.run {
            getString(resources.getIdentifier(nameResourceEntryName, "string", packageName))
        }
        viewHolder.descriptionTextView.text = applicationContext.run {
            getString(resources.getIdentifier(descriptionResourceEntryName, "string", packageName))
        }
        val pid = pids.value!![position].pid

        val textColor = ContextCompat.getColor(
            viewHolder.nameTextView.context,
            if (SupportedPidsHolder.contains(pid)) android.R.color.black else R.color.light_grey
        )
        viewHolder.nameTextView.setTextColor(textColor)
        viewHolder.descriptionTextView.setTextColor(textColor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.pid_list_item, parent, false)
        return ViewHolder(view, pidClickedListener)
    }
}