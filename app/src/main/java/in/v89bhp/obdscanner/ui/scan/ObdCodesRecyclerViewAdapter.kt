package `in`.v89bhp.obdscanner.ui.scan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import `in`.v89bhp.obdscanner.R


class ObdCodesRecyclerViewAdapter(
    owner: LifecycleOwner, private val obdCodes: LiveData<List<Pair<String, String>>>,
    private val obdCodeClickedListener: ViewHolder.ObdCodeClickedListener
) :
    RecyclerView.Adapter<ObdCodesRecyclerViewAdapter.ViewHolder>() {


    class ViewHolder(view: View, val listener: ObdCodeClickedListener) : RecyclerView.ViewHolder(view) {
        interface ObdCodeClickedListener {
            fun onObdCodeClicked(adapterPosition: Int, ff: Boolean)
        }

        val obdCodeTextView: TextView = view.findViewById(R.id.obdCodeTextView)
        val typeTextView: TextView = view.findViewById(R.id.typeTextView)
        val freezeFrameButton: Button = view.findViewById(R.id.freezeFrameButton)
        val ignoredTextView: TextView = view.findViewById(R.id.ignoredTextView)

        init {
            view.findViewById<Button>(R.id.detailsButton).setOnClickListener {
                listener.onObdCodeClicked(adapterPosition, false)
            }

            freezeFrameButton.setOnClickListener {
               listener.onObdCodeClicked(adapterPosition, true)
            }
        }
    }

    private val dataObserver = Observer<List<Pair<String, String>>> {
        notifyDataSetChanged()
    }

    init {
        obdCodes.observe(owner, dataObserver)
    }

    override fun getItemCount(): Int =
        obdCodes.value?.size ?: 0


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val (obdCode, category) = obdCodes.value!![position]
        viewHolder.obdCodeTextView.text = obdCode
        val categoryToDisplay = category.removeSuffix(" (FF)")
        viewHolder.typeTextView.text = categoryToDisplay
        with(viewHolder.ignoredTextView) {
            if(categoryToDisplay == "Confirmed") {
                text = context.getString(R.string.no)
            } else {
                text = context.getString(R.string.yes)
                setTextColor(ContextCompat.getColor(context, R.color.green))
            }
        }
        viewHolder.freezeFrameButton.visibility = if(category.contains("FF")) View.VISIBLE else View.GONE
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.obd_code_list_item, parent, false)
        return ViewHolder(view, obdCodeClickedListener)
    }
}