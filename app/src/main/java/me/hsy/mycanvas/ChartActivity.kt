package me.hsy.mycanvas

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import me.hsy.mycanvas.ui.main.pie.PieChart
import me.hsy.mycanvas.ui.main.pie.PieBean


class ChartActivity : AppCompatActivity() {
    private var list: List<Int> =
        arrayListOf(
            Color.parseColor("#FFBB86FC"),
            Color.parseColor("#FF03DAC5"),
            Color.parseColor("#0DC341"),
        )
    private var nameList: List<String> =
        arrayListOf(
            "Hw & quiz",
            "Lab Assignment",
            "Examination"
        )

    private var accountList: List<Float> =
        arrayListOf(
            40f,
            30f,
            30f
        )
    private var itemList: MutableList<PieBean>? = null

    private var pieChart: PieChart? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)
        pieChart = findViewById(R.id.PieChart)
        itemList =  ArrayList()
        for (i in list.indices) {
            val bean = PieBean()

            bean.value = accountList[i]
            bean.item = nameList[i]
            itemList?.add(bean)
        }
        pieChart?.setData(itemList!!)
        pieChart?.startAnimation(2000)
    }

}