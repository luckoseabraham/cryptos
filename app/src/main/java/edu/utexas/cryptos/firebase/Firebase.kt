package edu.utexas.cryptos.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.auth.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import edu.utexas.cryptos.model.UserConfig

class Firebase {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userConfigCollection = "userConfigs"

    fun getUserConfig(userName: String, userConfigLiveData: MutableLiveData<UserConfig>) {
        db.collection(userConfigCollection).document(userName.lowercase())
            .get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    Log.d("Firebase", "DocumentSnapshot data: ${document.data}")
                    userConfigLiveData.postValue(document.toObject(UserConfig::class.java)!!)
                } else {
                    Log.d("Firebase", "No such document")
                    //Create initial record for the user.
                    val userConfig = UserConfig(
                        "USD",
                        listOf("USDT", "BTC", "BUSD")
                    )
                    this.setUserConfig(userName, userConfig, userConfigLiveData)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Firebase", "get getUserConfig failed with ", exception)
            }
    }

    fun setUserConfig(userName: String, userConfig: UserConfig, userConfigLiveData: MutableLiveData<UserConfig> ) {

        Log.d("Firebase", "Creating userConfig for user $userName")
        db.collection(userConfigCollection).document(userName.lowercase())
            .set(userConfig)
            .addOnSuccessListener {
                this.getUserConfig(userName, userConfigLiveData)
            }
    }

}