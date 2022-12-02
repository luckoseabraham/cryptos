package edu.utexas.cryptos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.utexas.cryptos.api.Api
import edu.utexas.cryptos.firebase.Firebase
import edu.utexas.cryptos.model.DetailAsset
import edu.utexas.cryptos.model.TimeSeriesData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsViewModel : ViewModel() {

    private var firebase = Firebase()
    private val api = Api.create()


    //AssetID

    private var asset = MutableLiveData<DetailAsset>()

    fun observeAsset() : LiveData<DetailAsset> {
        return asset
    }

    fun fetchAsset(id: String) {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext + Dispatchers.IO
        ) {
            val tempAsset = api.getAssetDetails(id)
            asset.postValue(tempAsset)
        }
    }

    //TimeSeries

    private var timeSeries = MutableLiveData<TimeSeriesData>()

    fun observeTimeSeries() : LiveData<TimeSeriesData> {
        return timeSeries
    }

    fun fetchTimeSeries(id: String, currency: String, timeWindow: String) {
        firebase.getTimeSeriesData(id, currency, timeWindow, timeSeries)
    }

}