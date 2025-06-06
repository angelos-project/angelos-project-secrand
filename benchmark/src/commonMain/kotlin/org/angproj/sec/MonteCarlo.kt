package org.angproj.sec

import org.angproj.sec.rand.AbstractRandom
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.TimeSource

/**
 * MonteCarlo is a class that implements the Monte Carlo method to estimate the value of Pi.
 * It generates random points in a unit square and counts how many fall inside a unit circle.
 * The estimated value of Pi is calculated based on the ratio of points inside the circle to the total number of points.
 */
public class MonteCarlo internal constructor(private val numSamples: Int, rg: () -> AbstractRandom) {
    private val random: AbstractRandom = rg()
    private var piEstimate = 0.0
    private var duration: Duration = Duration.INFINITE

    /**
     * Estimates the value of Pi using the Monte Carlo method.
     * The method generates random points in a unit square and counts how many fall inside a unit circle.
     *
     * @return the estimated value of Pi
     */
    public fun estimatePi(): Double {
        val startTime = TimeSource.Monotonic.markNow()

        var insideCircle = 0

        for (i in 0..<numSamples) {
            val x: Double = random.readDouble()
            val y: Double = random.readDouble()
            if (getDistance(x, y, 0.0, 0.0) <= 1) {
                insideCircle++
            }
        }
        duration = startTime.elapsedNow() // Duration in nanoseconds

        piEstimate = 4.0 * insideCircle / numSamples
        return piEstimate
    }

    /**
     * Returns a string representation of the MonteCarlo object.
     *
     * @return a string containing the class name, estimated Pi, number of samples, and duration
     */
    override fun toString(): String {
        return "Class: " + random.toString() + "\n" +
                "Estimated Pi: " + piEstimate + "\n" +
                "Deviation: " + (piEstimate - PI).toFloat() + "\n" +
                "Number of samples: " + numSamples + "\n" +
                "Duration: " + duration.toString() + "\n"
    }

    private companion object {
        /**
         * Calculates the distance between two points in a 2D space.
         *
         * @param x1 x-coordinate of the first point
         * @param y1 y-coordinate of the first point
         * @param x2 x-coordinate of the second point
         * @param y2 y-coordinate of the second point
         * @return the distance between the two points
         */
        fun getDistance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
            return sqrt((x2 - x1).pow(2.0) + (y2 - y1).pow(2.0))
        }
    }
}