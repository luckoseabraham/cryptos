package edu.utexas.cryptos.model

import com.google.firebase.firestore.DocumentId
import java.util.*

/**
 * This model stores the user configuration
 * The key or PK of the document is the emailAddress.lowercase()
 * This document lives in the userConfigs collection.
 */
data class UserConfig(
    // Auth information
    var currency: String = "USD",
    var favorites: List<String> = mutableListOf<String>(),
    // Written on the server
    // firestoreID is generated by firestore, used as primary key
)