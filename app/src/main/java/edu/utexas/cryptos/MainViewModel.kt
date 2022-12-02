package edu.utexas.cryptos

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.*
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import edu.utexas.cryptos.api.Api
import edu.utexas.cryptos.firebase.Firebase
import edu.utexas.cryptos.model.Asset
import edu.utexas.cryptos.model.UserConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {


    private var firebase = Firebase()
    private var firebaseAuthLiveData = FirestoreAuthLiveData()
    private val api = Api.create()

    //UserConfigs

    fun login(signInLauncher: ActivityResultLauncher<Intent>) {

        val user = FirebaseAuth.getInstance().currentUser
        if(user == null) {
            // Choose authentication providers
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build())

            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build()
            signInLauncher.launch(signInIntent)

            // Create and launch sign-in intent
            // XXX Write me. Set authentication providers and start sign-in for user
            // setIsSmartLockEnabled(false) solves some problems
        } else {
            updateUser()
            fetchAssets()
        }
    }

    fun signOut() {
        firebaseAuthLiveData.logout()
    }

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
        val tempUserConfig = userConfig.value
        tempUserConfig?.currency = currency
        firebase.setUserConfig(firebaseAuthLiveData.getCurrentUser()!!.email.toString(), tempUserConfig!!, userConfig)
    }

    fun removeFavorite(id : String) {
        val tempUserConfig = userConfig.value
        val tempFavorites = tempUserConfig!!.favorites.toMutableList()
        tempFavorites.remove(id)
        tempUserConfig.favorites = tempFavorites
        firebase.setUserConfig(firebaseAuthLiveData.getCurrentUser()!!.email.toString(), tempUserConfig, userConfig)
    }

    fun setFavorite(id : String) {
        val tempUserConfig = userConfig.value
        val tempFavorites = tempUserConfig!!.favorites.toMutableList()
        tempFavorites.add(id)
        tempUserConfig.favorites = tempFavorites
        firebase.setUserConfig(firebaseAuthLiveData.getCurrentUser()!!.email.toString(), tempUserConfig, userConfig)
    }

    //Assets
    private var assets = MutableLiveData<List<Asset>>()
    fun fetchAssets() {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext + Dispatchers.IO
        ) {
            val tempAssets = api.getAssetList().assets
            assets.postValue(tempAssets)
            if (userConfig.value?.favorites != null) {
                favoriteAssets.postValue(
                    tempAssets.filter {
                        val result = userConfig.value?.favorites!!.contains(it.id)
                        result
                    }
                )
            }
        }
    }

    fun getAssetAt(position: Int) : Asset? {
        return searchAssets.value?.getOrNull(position)
    }

    //Favorites
    private var favoriteAssets = MutableLiveData<List<Asset>>()
    fun observeFavoriteAssets(): LiveData<List<Asset>> {
        return favoriteAssets
    }

    fun getFavoriteAt(position: Int) : Asset {
        return searchFavorites.value?.get(position)!!
    }

    //Search
    private var searchTerm = MutableLiveData<String>()
    fun setSearchTerm(search: String) {
        searchTerm.postValue(search)
    }

    fun getSearchTerm() : String{
        return searchTerm.value ?: ""
    }

    private val searchFavorites = MediatorLiveData<List<Asset>>().apply {
        addSource(favoriteAssets) { postList ->
            value = postList.filter {
                it.searchFor(searchTerm.value ?: "")
            }
        }
        addSource(searchTerm) { searchTerm ->
            value = favoriteAssets.value?.filter {
                it.searchFor(searchTerm)
            }
        }
    }

    fun observeSearchFavorites() : LiveData<List<Asset>> {
        return searchFavorites
    }


    private val searchAssets = MediatorLiveData<List<Asset>>().apply {
        addSource(assets) { postList ->
            value = postList.filter {
                it.searchFor(searchTerm.value ?: "")
            }
        }
        addSource(searchTerm) { searchTerm ->
            value = assets.value?.filter {
                it.searchFor(searchTerm)
            }
        }
    }

    fun observeSearchAssets() : LiveData<List<Asset>> {
        return searchAssets
    }

    //Details page

    // Convenient place to put it as it is shared
    companion object {
        fun doDetails(context: Context, asset: Asset, curr: String) {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra(DetailsActivity.titleKey, "${asset.id} - ${asset.name}")
            intent.putExtra(DetailsActivity.idKey, asset.id)
            intent.putExtra(DetailsActivity.currKey, curr)
            context.startActivity(intent)
        }
    }
}