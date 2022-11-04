package edu.utexas.cryptos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.utexas.cryptos.api.Api
import edu.utexas.cryptos.firebase.Firebase
import edu.utexas.cryptos.model.Asset
import edu.utexas.cryptos.model.UserConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel() : ViewModel() {


    private var firebase = Firebase()
    private var firebaseAuthLiveData = FirestoreAuthLiveData()
    private val api = Api.create()

    //UserConfigs

    private var userConfig = MutableLiveData<UserConfig>()
    fun updateUser() {
        firebaseAuthLiveData.updateUser()
        val email = firebaseAuthLiveData.getCurrentUser()!!.email
        firebase.getUserConfig(email.toString(), userConfig)
    }

    fun observeUserConfig(): LiveData<UserConfig> {
        return userConfig
    }

    fun updateCurrencyPref(currency: String) {
        var tempUserConfig = userConfig.value
        tempUserConfig?.currency = currency
        firebase.setUserConfig(firebaseAuthLiveData.getCurrentUser()!!.email.toString(), tempUserConfig!!, userConfig)
    }


    //Assets
    private var assets = MutableLiveData<List<Asset>>()
    fun fetchAssets() {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext + Dispatchers.IO
        ) {
            assets.postValue(api.getAssetList().assets)
        }
    }

    fun observeAssets(): LiveData<List<Asset>> {
        return assets
    }
}