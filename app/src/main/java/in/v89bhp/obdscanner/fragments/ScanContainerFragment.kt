package `in`.v89bhp.obdscanner.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.databinding.ScanContainerFragmentBinding


/**
 * Container fragment for tabbed ***ScanTroubleCodesFragment*** and ***ScanOtherFragment***
 */
class ScanContainerFragment: Fragment() {

    private lateinit var viewBinding: ScanContainerFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = ScanContainerFragmentBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewBinding.scanContainerPager.adapter = ViewPagerAdapter()

        TabLayoutMediator(viewBinding.scanContainerTabLayout, viewBinding.scanContainerPager) { tab, position ->
            tab.text = if(position == 0) getString(R.string.trouble_codes) else getString(R.string.other)
        }.attach()
    }




    private inner class ViewPagerAdapter :  FragmentStateAdapter(this@ScanContainerFragment) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment =
            if (position == 0) ScanTroubleCodesFragment() else ScanOtherFragment()
    }
}