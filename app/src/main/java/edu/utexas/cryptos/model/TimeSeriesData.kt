package edu.utexas.cryptos.model

import edu.utexas.cryptos.DetailsActivity


/**
 * This model stores the user configuration
 * The key or PK of the document is the <id>::<currency>::<time>.
 * For example, PK for record of BTC for 7days in USD will look like BTC::USD::7D
 * This document lives in the timeSeries collection.
 */

data class TimeSeriesData(
    // Auth information
    var currency: String = "USD",
    var id: String = "BTC",
    var window: String = DetailsActivity.time_1hr,
    var data: Map<String, Float> = mutableMapOf(),
)