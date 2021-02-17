
package com.dev.data.core

import kotlin.math.round


//round any float to exactly digits count after comma
fun Float.roundTo(decimals: Int): Float {
	var multiplier = 1.0
	repeat(decimals) { multiplier *= 10 }
	return (round(this * multiplier) / multiplier).toFloat()
}

//round any double to exactly digits count after comma
fun Double.roundTo(decimals: Int): Double {
	var multiplier = 1.0
	repeat(decimals) { multiplier *= 10 }
	return round(this * multiplier) / multiplier
}