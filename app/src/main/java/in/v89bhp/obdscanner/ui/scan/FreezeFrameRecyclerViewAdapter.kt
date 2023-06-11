package `in`.v89bhp.obdscanner.ui.scan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import `in`.v89bhp.obdscanner.R


class FreezeFrameRecyclerViewAdapter(
    owner: LifecycleOwner, val ffData: LiveData<List<String>>

) :
    RecyclerView.Adapter<FreezeFrameRecyclerViewAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ffTextView = view.findViewById<TextView>(R.id.ffTextView)
    }

    private val dataObserver = Observer<List<String>> {
        notifyDataSetChanged()
    }

    init {
        ffData.observe(owner, dataObserver)
    }

    override fun getItemCount(): Int =
        ffData.value?.size ?: 0


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.ffTextView.text = ffData.value!![position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.ff_list_item, parent, false)
        return ViewHolder(view)
    }
}