package edu.utexas.cryptos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.utexas.cryptos.api.Api
import edu.utexas.cryptos.firebase.Firebase
import edu.utexas.cryptos.model.Asset
import edu.utexas.cryptos.model.DetailAsset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsViewModel() : ViewModel() {

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

}