package edu.utexas.cryptos.model

import com.google.gson.annotations.SerializedName


enum class Currency {
    AUD,
    JPY,
    NZD,
    GBP,
    CAD,
    USD,
    EUR
}

data class Quote (
    @SerializedName("price")
    val price: Float,
    @SerializedName("volume_24h")
    val volume_24h: Float,
    @SerializedName("market_cap")
    val market_cap: Float,
    @SerializedName("fully_diluted_market_cap")
    val fully_diluted_market_cap: Float,
)

data class Asset(
    @SerializedName("quote")
    val quote: Map<Currency, Quote>,
    @SerializedName("asset_id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("website")
    val website: String,
    @SerializedName("ethereum_contract_address")
    val ethContractAddress: String,
    @SerializedName("pegged")
    val pegged: String,
    @SerializedName("price")
    val price: String,
    @SerializedName("volume_24h")
    val volume_24h: String,
    @SerializedName("change_1h")
    val change_1h: String,
    @SerializedName("change_24h")
    val change_24h: String,
    @SerializedName("change_7d")
    val change_7d: String,
    @SerializedName("total_supply")
    val total_supply: String,
    @SerializedName("circulating_supply")
    val circulating_supply: String,
    @SerializedName("max_supply")
    val max_supply: String,
    @SerializedName("market_cap")
    val market_cap: String,
    @SerializedName("fully_diluted_market_cap")
    val fully_diluted_market_cap: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("created_at")
    val created_at: String,
    @SerializedName("updated_at")
    val updated_at: String,
)

data class Assets(
    @SerializedName("assets")
    val assets: List<Asset>
)