package edu.utexas.cryptos

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import edu.utexas.cryptos.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding


    private val viewModel: DetailsViewModel by viewModels()

    companion object {
        const val titleKey = "titleKey"
        const val idKey = "idKey"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val parentExtras = intent.extras
        val title = parentExtras?.getCharSequence(titleKey).toString()
        val id = parentExtras?.getCharSequence(idKey).toString()

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = title
        setContentView(binding.root)

        viewModel.fetchAsset(id)

        viewModel.observeAsset().observe(this){
            Log.d("LUKE", "got asset for details from observe $it")
            binding.desc.text = it.asset.description
            val change_1h = it.asset.change_1h.toFloat()
            binding.oneHour.text = String.format("%.2f", change_1h)
            if(change_1h<0) {
                binding.oneHour.setTextColor(Color.RED)
            } else  {
                binding.oneHour.setTextColor(Color.GREEN)
            }

            val change_24h = it.asset.change_24h.toFloat()
            binding.oneDay.text = String.format("%.2f", change_24h)
            if(change_24h<0) {
                binding.oneDay.setTextColor(Color.RED)
            } else  {
                binding.oneDay.setTextColor(Color.GREEN)
            }

            val change_7d = it.asset.change_7d.toFloat()
            binding.sevenDay.text = String.format("%.2f", change_7d)
            if(change_7d<0) {
                binding.sevenDay.setTextColor(Color.RED)
            } else  {
                binding.sevenDay.setTextColor(Color.GREEN)
            }


            binding.marketCap.text = it.asset.market_cap
            binding.circSupply.text = it.asset.circulating_supply
        }


        val chart = binding.chart

        var entries = mutableListOf<Entry>()
        entries.add(Entry(1990.toFloat(), 20.toFloat()))
        entries.add(Entry(1991.toFloat(), 30.toFloat()))
        entries.add(Entry(1992.toFloat(), 40.toFloat()))
        entries.add(Entry(1993.toFloat(), 40.toFloat()))
        entries.add(Entry(1994.toFloat(), 30.toFloat()))
        entries.add(Entry(1995.toFloat(), 40.toFloat()))
        entries.add(Entry(1996.toFloat(), 20.toFloat()))
        entries.add(Entry(1997.toFloat(), 40.toFloat()))
        entries.add(Entry(1998.toFloat(), 40.toFloat()))
        entries.add(Entry(1999.toFloat(), 10.toFloat()))
        entries.add(Entry(2000.toFloat(), 40.toFloat()))
        entries.add(Entry(2001.toFloat(), 5.toFloat()))
        entries.add(Entry(2002.toFloat(), 40.toFloat()))
        entries.add(Entry(2003.toFloat(), 40.toFloat()))
        entries.add(Entry(2004.toFloat(), 40.toFloat()))
        entries.add(Entry(2005.toFloat(), 40.toFloat()))
        entries.add(Entry(2006.toFloat(), 40.toFloat()))
        entries.add(Entry(2007.toFloat(), 40.toFloat()))

        val dataSet = LineDataSet(entries, "Label")
        dataSet.cubicIntensity = 0.2f
        dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        dataSet.lineWidth = 1.8f
        dataSet.circleRadius = 4f
//        dataSet.color = Color.WHITE;
//        dataSet.setCircleColor(Color.WHITE);
        dataSet.highLightColor = Color.WHITE;
        dataSet.setFillColor(Color.RED);
        dataSet.setFillAlpha(110);
        val lineData = LineData(dataSet)
        chart.data = lineData
//        chart.setBackgroundColor(Color.GREEN)
        chart.invalidate() // refresh
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
}