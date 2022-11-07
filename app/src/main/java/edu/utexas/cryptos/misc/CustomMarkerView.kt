package edu.utexas.cryptos.misc

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import edu.utexas.cryptos.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class CustomMarkerView(context: Context?, layoutResource: Int) :
    MarkerView(context, layoutResource) {
    private val tvContent: TextView = findViewById<View>(R.id.tvContent) as TextView

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        val mFormat = SimpleDateFormat("HH:mm dd MMM", Locale.ENGLISH)
        if (e != null) {
            val millis = TimeUnit.MINUTES.toMillis(e.x.toLong())
            tvContent.text = e.y.toString() + " at " + mFormat.format(Date(millis))
        }
        super.refreshContent(e, highlight)
    }

}