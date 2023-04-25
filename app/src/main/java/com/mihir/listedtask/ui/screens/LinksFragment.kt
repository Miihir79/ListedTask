package com.mihir.listedtask.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.mihir.listedtask.R
import com.mihir.listedtask.common.API_FAILED
import com.mihir.listedtask.common.NO_NETWORK
import com.mihir.listedtask.ui.vm.ViewModel
import com.mihir.listedtask.common.Utils
import com.mihir.listedtask.common.showToastMessage
import com.mihir.listedtask.data.model.InfoTileData
import com.mihir.listedtask.databinding.FragmentLinksBinding
import com.mihir.listedtask.ui.adapter.AdapterInfoTile
import com.mihir.listedtask.ui.adapter.AdapterLink
import com.mihir.listedtask.ui.dialogs.CustomLoader
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LinksFragment : Fragment() {

    private var _binding: FragmentLinksBinding? = null
    private val binding get() = _binding!!

    private val adapterInfo by lazy { AdapterInfoTile() }
    private val adapterLink by lazy { AdapterLink() }

    private val viewModel by lazy { ViewModelProvider(requireActivity())[ViewModel::class.java] }
    private val loader by lazy { CustomLoader(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLinksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvInfo.adapter = adapterInfo
        binding.recyclerLinks.adapter = adapterLink

        binding.textViewGreeting.text = Utils.greetUser()

        loader.startLoading()

        observe()

        binding.radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                binding.btnRecentLinks.id -> {
                    lifecycleScope.launch {
                        viewModel.resp.value?.data?.let {
                            // setting the recent link data to recycler
                            it.recent_links?.let { links -> adapterLink.linkItemData = links}
                        }
                    }
                }
                binding.btnTopLinks.id -> {
                    lifecycleScope.launch {
                        viewModel.resp.value?.data?.let {
                            // setting the top link data to recycler
                            it.top_links?.let { links -> adapterLink.linkItemData = links }

                        }
                    }
                }
                else -> {
                    context?.showToastMessage("Error")
                }
            }
        }

    }

    private fun observe() {
        viewModel.resp.observe(viewLifecycleOwner) {
            loader.stopLoading()

            // setting the top link data to recycler
            it.data.top_links?.let { links -> adapterLink.linkItemData = links }

            // setting the info card data
            val tileInfoData = arrayListOf<InfoTileData>()
            val item1 = InfoTileData(R.drawable.ic_click, it.today_clicks.toString(), "Today's Clicks")
            val item2 = InfoTileData(R.drawable.ic_location, it.top_location, "Top Location")
            val item3 = InfoTileData(R.drawable.ic_source, it.top_source, "Top Source")
            tileInfoData.add(item1)
            tileInfoData.add(item2)
            tileInfoData.add(item3)
            adapterInfo.infoItemData = tileInfoData

            setupChart(it.data.overall_url_chart)
        }

        lifecycleScope.launch {
            viewModel.apiStatus.collectLatest { status ->
                when (status) {
                    API_FAILED -> {
                        loader.stopLoading()
                        context?.showToastMessage("Something went wrong")
                    }
                    NO_NETWORK -> {
                        loader.stopLoading()
                        context?.showToastMessage("Please Check Your Internet")
                    }
                }
            }
        }

    }

    private fun setupChart(overallUrlChart: Map<String, Int>?) {
        val dataSet = ArrayList<ILineDataSet>()

        val entry = ArrayList<Entry>()
        for (i in 0 until overallUrlChart?.size!!) {
            // The key at position i from the set of Keys is referenced to get it's value and then converting it to float
            overallUrlChart[overallUrlChart.keys.toList()[i]]?.toFloat()?.let { Entry(i.toFloat(), it) }?.let { entry.add(it) }
        }

        val lineDataSet = LineDataSet(entry, "")
        lineDataSet.setDrawFilled(true)

        val gradient = ContextCompat.getDrawable(requireContext(), R.drawable.chart_gradient)
        lineDataSet.fillDrawable = gradient
        lineDataSet.setDrawCircles(false)
        lineDataSet.color = ContextCompat.getColor(requireContext(), R.color.blue_main)

        dataSet.add(lineDataSet)

        binding.chart.data = LineData(dataSet)
        binding.chart.description.isEnabled = false
        binding.chart.legend.isEnabled = false
        binding.chart.invalidate()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}