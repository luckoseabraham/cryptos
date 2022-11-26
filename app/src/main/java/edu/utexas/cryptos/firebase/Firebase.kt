package edu.utexas.cryptos.firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.auth.data.model.User
import com.github.mikephil.charting.data.Entry
import com.google.firebase.firestore.FirebaseFirestore
import edu.utexas.cryptos.DetailsActivity
import edu.utexas.cryptos.model.TimeSeriesData
import edu.utexas.cryptos.model.UserConfig
import java.util.ArrayList
import java.util.concurrent.TimeUnit

class Firebase {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userConfigCollection = "userConfigs"
    private val timeSeriesCollection = "timeSeries"

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


    fun getTimeSeriesData(id : String, currency: String, timeSeries : String, data: MutableLiveData<TimeSeriesData>) {

        val pk = "${id}::${currency}::${timeSeries}"
        db.collection(timeSeriesCollection).document(pk)
            .get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    Log.d("Firebase", "DocumentSnapshot data: ${document.data}")
                    data.postValue(document.toObject(TimeSeriesData::class.java)!!)
                } else {
                    Log.d("Firebase", "No such document : $pk")

                    val count=30 // This seems to be a really good sweet spot between data frequency and UI
                    val range=180
                    var values = mutableMapOf<String, Float>()
                    var now  = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis())
                    for (i in 0 until count) {
                        var test = (now-2*(count - i))
                        if(timeSeries == DetailsActivity.time_24hr) {
                            test = (now-48*(count - i))
                        } else if (timeSeries == DetailsActivity.time_7d) {
                            test = (now-336*(count - i))
                        }
                        val temp = (Math.random() * range).toFloat() + 30
                        values[test.toString()] = temp
                    }
                    data.postValue(TimeSeriesData(currency,id,timeSeries,values))
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Firebase", "get getUserConfig failed with ", exception)
            }
    }


}