package dev.eyosiyas.smsblocker.fragment

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import dev.eyosiyas.smsblocker.R
import dev.eyosiyas.smsblocker.adapter.ViewPagerAdapter
import dev.eyosiyas.smsblocker.databinding.FragmentViewPagerBinding
import dev.eyosiyas.smsblocker.util.PrefManager
import java.util.*

class ViewPagerFragment : Fragment() {
    private lateinit var binder: FragmentViewPagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val locale = Locale(PrefManager(requireContext()).locale)
        val configuration: Configuration = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
        } else
            configuration.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
            requireContext().createConfigurationContext(configuration)
        else
            resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binder = FragmentViewPagerBinding.bind(inflater.inflate(R.layout.fragment_view_pager, container, false))
        val fragmentList = arrayListOf(
                MessageFragment(),
                BlockedMessagesFragment()
        )

        val adapter = ViewPagerAdapter(
                fragmentList,
                requireActivity().supportFragmentManager,
                lifecycle
        )

        binder.viewPager.adapter = adapter
        TabLayoutMediator(binder.tabLayout, binder.viewPager) { tab, pos ->
            tab.text = when (pos) {
                0 -> getString(R.string.tab_inbox)
                1 -> getString(R.string.tab_spam)
                else -> getString(R.string.app_name)
            }
        }.attach()
        return binder.root
    }
}