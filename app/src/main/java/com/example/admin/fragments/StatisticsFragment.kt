package com.example.admin.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.admin.R
import com.example.admin.databinding.FragmentSettingsBinding
import com.example.admin.databinding.FragmentStatisticsBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

class StatisticsFragment : Fragment() {
    private lateinit var barChart: BarChart
    private lateinit var barList1: ArrayList<BarEntry>
    private lateinit var barList2: ArrayList<BarEntry>
    private lateinit var barList3: ArrayList<BarEntry>
    private lateinit var binding: FragmentStatisticsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatisticsBinding.inflate(layoutInflater)
        barChart = binding.barChart
        barList1 = ArrayList()
        barList2 = ArrayList()
        barList3 = ArrayList()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barList1.add(BarEntry(1f, 352f))
        barList1.add(BarEntry(1f, 690f))
        barList1.add(BarEntry(1f, 374f))
        barList1.add(BarEntry(1f, 330f))
        barList1.add(BarEntry(1f, 460f))

        barList2.add(BarEntry(1f, 1800f))
        barList2.add(BarEntry(2f, 1035f))
        barList2.add(BarEntry(3f, 935f))
        barList2.add(BarEntry(4f, 825f))
        barList2.add(BarEntry(5f, 1590f))

        val barDataSet1 = BarDataSet(barList1, "Amount")
        barDataSet1.color = Color.RED

        val barDataSet2 = BarDataSet(barList2, "Total")
        barDataSet2.color = Color.BLUE

        val types = arrayOf(
            "Fish",
            "Egg",
            "Fruit",
            "Vegetable",
            "Milk"
        )

        val barData = BarData(barDataSet1, barDataSet2)
        barChart.data = barData

        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(types)
        xAxis.setCenterAxisLabels(true)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1F
        xAxis.isGranularityEnabled = true
        barChart.isDragEnabled = true
        barChart.setVisibleXRangeMaximum(3f)

        val barSpace = 0.02f
        val groupSpace = 0.75f
        val groupCount = 5

        barData.barWidth = 0.1f
        barChart.xAxis.axisMinimum = 0f
        barChart.axisLeft.axisMinimum = 0f
        barChart.xAxis.axisMaximum =
            0 + barChart.barData.getGroupWidth(groupSpace, barSpace) * groupCount
        barChart.groupBars(0f, groupSpace, barSpace)
        barChart.invalidate()
    }
}