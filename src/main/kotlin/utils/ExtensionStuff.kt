package utils

import org.openrndr.math.Vector2

// Fill in the gaps in stuff I feel
// is missing in OpenRNDR
// Will probably mostly be adding
// stuff I'm used to from Processing

fun Vector2.limited(max: Double) =
        if (squaredLength > max*max) normalized * max
        else this

val Number.d get() = toDouble()

// From degrees to radians
fun Double.toRadians() = Math.toRadians(this)
fun Int.toRadians() = this.toDouble().toRadians()