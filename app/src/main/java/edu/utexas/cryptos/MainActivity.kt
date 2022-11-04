package edu.utexas.cryptos

import android.R
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationBarView
import edu.utexas.cryptos.databinding.ActivityMainBinding
import edu.utexas.cryptos.model.Currency
import kotlinx.coroutines.*
import java.security.AccessController.getContext


class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("MainActivity", "sign in successful")
            viewModel.updateUser()
            viewModel.fetchAssets()
            //Check if the user has config record.
            //if no, default it.
            //Then call the fragment for showing favorites

        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Log.d("MainActivity", "sign in failed ${result}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        AuthInit(viewModel, signInLauncher)

        GlobalScope.launch {
            while (coroutineContext.isActive) {
                delay(2000L)
                viewModel.fetchAssets()
            }
        }

        viewModel.observeAssets().observe(this){ data ->
            data.forEach {
                Log.d("LUKE", "Got asset : $it.id")
            }
        }

        val spinnerArray: MutableList<String> = ArrayList()
        enumValues<Currency>().forEach {
            spinnerArray.add(it.name)
        }
        val adapter = ArrayAdapter(
            this, R.layout.simple_spinner_item, spinnerArray
        )
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        val spinner : Spinner = binding.contentMain.currency
        spinner.adapter = adapter

        viewModel.observeUserConfig().observe(this) {
            spinner.setSelection(adapter.getPosition(it.currency))
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val curr = adapter.getItem(position)
                Log.d("LUKE", "On item selected invoked. $curr")
                if(viewModel.observeUserConfig().value != null && curr != viewModel.observeUserConfig().value?.currency) {
                    //update currency in firebase
                    Log.d("LUKE", "inside if condition. $curr")
                    viewModel.updateCurrencyPref(curr!!)
                    //TODO also refresh the prices in the views. But probably inside the fragments.
                }
            }
        }

//        val chart = binding.chart
//
//        var entries = mutableListOf<Entry>()
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
//
//
//
//        val dataSet = LineDataSet(entries, "Label")
//        dataSet.cubicIntensity = 0.2f
//        dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
//        dataSet.lineWidth = 1.8f
//        dataSet.circleRadius = 4f
//        val lineData = LineData(dataSet)
//        chart.data = lineData
//        chart.invalidate() // refresh
    }
}