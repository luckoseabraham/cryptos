package edu.utexas.cryptos

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import com.google.android.material.tabs.TabLayout
import edu.utexas.cryptos.databinding.ActivityDetailsBinding
import edu.utexas.cryptos.misc.CustomMarkerView
import edu.utexas.cryptos.model.Asset
import edu.utexas.cryptos.model.Currency
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap


class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding


    private val viewModel: DetailsViewModel by viewModels()

    companion object {
        const val titleKey = "titleKey"
        const val idKey = "idKey"
        const val currKey = "currKey"

        const val time_1hr = "1HR"
        const val time_24hr = "24HR"
        const val time_7d = "7d"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val parentExtras = intent.extras
        val title = parentExtras?.getCharSequence(titleKey).toString()
        val id = parentExtras?.getCharSequence(idKey).toString()
        val curr = parentExtras?.getCharSequence(currKey).toString()

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = title
        setContentView(binding.root)

        viewModel.fetchAsset(id)
        var colorMap = HashMap<String, Int>()
        viewModel.observeAsset().observe(this){
            Log.d("LUKE", "got asset for details from observe $it")
//            binding.desc.text = it.asset.description
            val change_1h = it.asset.change_1h.toFloat()
            binding.oneHour.text = String.format("%.2f", change_1h)  + "%"
            if(change_1h<=0) {
                colorMap[time_1hr] = Color.RED
            } else  {
                colorMap[time_1hr] =Color.GREEN
            }
            binding.oneHour.setTextColor(colorMap[time_1hr]!!)

            val change_24h = it.asset.change_24h.toFloat()
            binding.oneDay.text = String.format("%.2f", change_24h)  + "%"
            if(change_24h<=0) {
                colorMap[time_24hr] = Color.RED
            } else  {
                colorMap[time_24hr] =Color.GREEN
            }
            binding.oneDay.setTextColor(colorMap[time_24hr]!!)

            val change_7d = it.asset.change_7d.toFloat()
            binding.sevenDay.text = String.format("%.2f", change_7d) + "%"
            if(change_7d<=0) {
                colorMap[time_7d] = Color.RED
            } else  {
                colorMap[time_7d] =Color.GREEN
            }
            binding.sevenDay.setTextColor(colorMap[time_7d]!!)

            val price = it.asset.quote[Currency.valueOf(curr)]?.price!!
            binding.currentPrice.text = Asset.currencyIconMap[Currency.valueOf(curr)] + String.format("%.5f", price)


            binding.marketCap.text = Asset.currencyIconMap[Currency.valueOf(curr)] + String.format("%.3f", it.asset.quote[Currency.valueOf(curr)]?.market_cap)
            binding.circSupply.text = it.asset.circulating_supply

        }


        val chart = binding.chart
        chart.setDrawGridBackground(false)

        // draw points over time
        chart.animateX(1500)
        chart.setTouchEnabled(true);

        viewModel.fetchTimeSeries(id,curr, time_1hr)


        viewModel.observeTimeSeries().observe(this) {
            var values = createEntry(it.data, it.window)
            var dataSet = createDataSet(values, id, colorMap.getOrDefault(it.window, Color.RED))
            val lineData = LineData(dataSet)
            chart.data = lineData
            chart.legend.isEnabled = false
            chart.invalidate() // refresh
            chart.notifyDataSetChanged()
        }



        val mv = CustomMarkerView(this, R.layout.marker_layout)
        mv.chartView = chart
        chart.marker = mv
        chart.setXAxisRenderer(
            CustomXAxisRenderer(
                chart.viewPortHandler,
                chart.xAxis,
                chart.getTransformer(YAxis.AxisDependency.LEFT)
            )
        )
        var xAxis = chart.xAxis
        xAxis.labelRotationAngle = -30f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.valueFormatter = object : ValueFormatter() {
            private val mFormat = SimpleDateFormat("HH:mm  \n dd MMM", Locale.ENGLISH)
            override fun getFormattedValue(value: Float): String {
                var millis = value.toLong()
                Log.d("Timeformatingraph"," formatted value from $value is ${mFormat.format(Date(millis))}")
                return mFormat.format(Date(millis))
            }
        }

        var yAxisLeft = chart.axisLeft
        yAxisLeft.setDrawGridLines(false)

        var yAxisRight = chart.axisRight
        yAxisRight.setDrawGridLines(false)

        chart.description.isEnabled = false

        chart.setDrawGridBackground(false)

//        val values = ArrayList<Entry>()
//        val count=30 // This seems to be a really good sweet spot between data frequency and UI
//        val range=180
//
//        for (i in 0 until count) {
//            val `val`: Float = (Math.random() * range).toFloat() - 30
//            values.add(Entry(i.toFloat(), `val`))
//        }
//
//        var set1: LineDataSet
//        entries.add(Entry(1990.toFloat(), 20.toFloat()))
//        entries.add(Entry(1991.toFloat(), 30.toFloat()))
//        entries.add(Entry(1992.toFloat(), 40.toFloat()))
//        entries.add(Entry(1993.toFloat(), 40.toFloat()))
//        entries.add(Entry(1994.toFloat(), 30.toFloat()))
//        entries.add(Entry(1995.toFloat(), 40.toFloat()))
//        entries.add(Entry(1996.toFloat(), 20.toFloat()))
//        entries.add(Entry(1997.toFloat(), 40.toFloat()))
//        entries.add(Entry(1998.toFloat(), 40.toFloat()))
//        entries.add(Entry(1999.toFloat(), 10.toFloat()))
//        entries.add(Entry(2000.toFloat(), 40.toFloat()))
//        entries.add(Entry(2001.toFloat(), 5.toFloat()))
//        entries.add(Entry(2002.toFloat(), 40.toFloat()))
//        entries.add(Entry(2003.toFloat(), 40.toFloat()))
//        entries.add(Entry(2004.toFloat(), 40.toFloat()))
//        entries.add(Entry(2005.toFloat(), 40.toFloat()))
//        entries.add(Entry(2006.toFloat(), 40.toFloat()))
//        entries.add(Entry(2007.toFloat(), 40.toFloat()))





        var window = time_1hr

        binding.timeButtons.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    //tab.position
                    Log.d("LUKE", "Tab selected is ${tab.position}")
                    when(tab.position) {
                        0 -> {
                            //1hour
                          viewModel.fetchTimeSeries(id,curr,time_1hr)
                            window = time_1hr
                        }
                        1 -> {
                            //24 hour
                            viewModel.fetchTimeSeries(id,curr,time_24hr)
                            window = time_24hr

                        }
                        else -> {
                            //7 days
                            viewModel.fetchTimeSeries(id,curr,time_24hr)
                            window = time_24hr
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            }
        )
        GlobalScope.launch {
            while (coroutineContext.isActive) {
                delay(10000L)
                viewModel.fetchTimeSeries(id,curr, window)
                viewModel.fetchAsset(id)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        return if (id == android.R.id.home) {
            // If user clicks "up", then it it as if they clicked OK.  We commit
            // changes and return to parent
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            this.finish()
            return true
        } else super.onOptionsItemSelected(item)
    }

    fun createDataSet(entries: List<Entry>, id: String, color: Int) : LineDataSet{

        val dataSet = LineDataSet(entries, id)
        dataSet.cubicIntensity = 0.2f
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
//        dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        dataSet.lineWidth = 1.8f
        dataSet.circleRadius = 4f
        dataSet.color = color;
        dataSet.setCircleColor(color);
        dataSet.highLightColor = color;
        dataSet.fillColor = color;
        dataSet.fillAlpha = 110;
        // set the filled area
        dataSet.setDrawFilled(true)
        // set color of filled area

        // set color of filled area
        if (Utils.getSDKInt() >= 18) {
            // drawables only supported on api level 18 and above
            var drawableColor = R.drawable.fade_red
            if(color == Color.GREEN) {
                drawableColor = R.drawable.fade_green
            }
            val drawable = ContextCompat.getDrawable(this, drawableColor)
            dataSet.fillDrawable = drawable
        } else {
            dataSet.fillColor = color
        }
        return dataSet
    }

    private fun createEntry(yValues: Map<String, Float>, timeWindow: String) : ArrayList<Entry> {
        var entryValues = ArrayList<Entry>()
        var sortedValues = yValues.toSortedMap()
        var count = 0
        for (i  in sortedValues.entries) {
            entryValues.add(count, Entry(i.key.toFloat(), i.value))
            count += 1
        }
        Log.d("Timeformatingraph", "Tab selected is ${entryValues}")
        return entryValues
    }


}


class CustomXAxisRenderer(viewPortHandler: ViewPortHandler?, xAxis: XAxis?, trans: Transformer?) :
    XAxisRenderer(viewPortHandler, xAxis, trans) {
    override fun drawLabel(
        c: Canvas?,
        formattedLabel: String,
        x: Float,
        y: Float,
        anchor: MPPointF?,
        angleDegrees: Float
    ) {
        val line = formattedLabel.split("\n").toTypedArray()
        Utils.drawXAxisValue(
            c,
            line[0], x, y, mAxisLabelPaint, anchor, angleDegrees
        )
        Utils.drawXAxisValue(
            c,
            line[1],
            x + mAxisLabelPaint.textSize,
            y + mAxisLabelPaint.textSize,
            mAxisLabelPaint,
            anchor,
            angleDegrees
        )
    }
}

//class CustomXAxisRenderer(viewPortHandler: ViewPortHandler?, xAxis: XAxis?, trans: Transformer?) : XAxisRenderer(viewPortHandler, xAxis, trans) {
//    override fun drawLabel(c: Canvas?, formattedLabel: String, x: Float, y: Float, anchor: MPPointF?, angleDegrees: Float) {
//        val line: List<String> = formattedLabel.split("\n")
//        Utils.drawXAxisValue(c, line[0], x, y, mAxisLabelPaint, anchor, angleDegrees)
//        for (i in 1 until line.size) {
//            Utils.drawXAxisValue(c, line[i], x, y + mAxisLabelPaint.textSize * i,
//                mAxisLabelPaint, anchor, angleDegrees)
//        }
//    }
//}