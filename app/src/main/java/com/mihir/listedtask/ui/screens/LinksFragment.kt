package com.mihir.listedtask.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
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
        lineDataSet.setDrawValues(false)
        lineDataSet.color = ContextCompat.getColor(requireContext(), R.color.blue_main)

        dataSet.add(lineDataSet)
        val rightAxis = binding.chart.axisRight
        val xAxis = binding.chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = MonthValueFormatter()
        rightAxis.isEnabled = false
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
class MonthValueFormatter : ValueFormatter() {

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        if (value < 5f) {
            return "Jan"
        } else if (value < 10f) {
            return "Feb"
        } else if (value < 15f) {
            return "Mar"

        } else if (value < 20f) {
            return "Apr"

        } else if (value < 25f) {
            return "May"

        } else if (value < 30f) {
            return "Jun"

        } else if (value < 35f) {
            return "Jul"

        } else if (value < 40f) {
            return "Aug"

        } else if (value < 45f) {
            return "Sept"

        } else if (value < 50f) {
            return "Oct"

        } else if (value < 55f) {
            return "Nov"

        } else {
            return "Dec"

        }
    }
}