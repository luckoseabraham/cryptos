package edu.utexas.cryptos.model

/**
 * This model stores the user configuration
 * The key or PK of the document is the emailAddress.lowercase()
 * This document lives in the userConfigs collection.
 */
data class UserConfig(
    // Auth information
    var currency: String = "USD",
    var favorites: List<String> = mutableListOf(),

)