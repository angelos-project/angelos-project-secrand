/**
 * Copyright (c) 2025 by Kristoffer Paulsson <kristoffer.paulsson@talenten.se>.
 *
 * This software is available under the terms of the MIT license. Parts are licensed
 * under different terms if stated. The legal terms are attached to the LICENSE file
 * and are made available on:
 *
 *      https://opensource.org/licenses/MIT
 *
 * SPDX-License-Identifier: MIT
 *
 * Contributors:
 *      Kristoffer Paulsson - initial implementation
 */
package org.angproj.sec

import org.angproj.sec.stat.Benchmark
import org.angproj.sec.stat.BenchmarkObject
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.TimeSource

public abstract class MonteObject<E>(obj: E) : BenchmarkObject<E>(obj) {
    public abstract val bufferSize: Int

    public abstract fun readNextDouble(data: DoubleArray)
}

/**
 * MonteCarlo is a class that implements the Monte Carlo method to estimate the value of Pi.
 * It generates random points in a unit square and counts how many fall inside a unit circle.
 * The estimated value of Pi is calculated based on the ratio of points inside the circle to the total number of points.
 */
public class MonteCarlo<B>(
    samples: Int, config: () -> MonteObject<B>
) : Benchmark<B, MonteObject<B>>(samples, config) {

    private var piEstimate = 0.0
    private var duration: Duration = Duration.INFINITE

    private val doubleData = DoubleArray(obj.bufferSize)
    private var doublePos = 0

    private fun getDouble(): Double {
        if(doublePos > doubleData.lastIndex) {
            obj.readNextDouble(doubleData)
            doublePos = 0
        }
        return doubleData[doublePos++]
    }

    /**
     * Calculates the distance between two points in a 2D space.
     *
     * @param x1 x-coordinate of the first point
     * @param y1 y-coordinate of the first point
     * @param x2 x-coordinate of the second point
     * @param y2 y-coordinate of the second point
     * @return the distance between the two points
     */
    private fun getDistance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
        return sqrt((x2 - x1).pow(2.0) + (y2 - y1).pow(2.0))
    }

    /**
     * Estimates the value of Pi using the Monte Carlo method.
     * The method generates random points in a unit square and counts how many fall inside a unit circle.
     *
     * @return the estimated value of Pi
     */
    override fun calculateData() {
        val startTime = TimeSource.Monotonic.markNow()
        var insideCircle = 0

        for (i in 0..<samples) {
            val x: Double = getDouble()
            val y: Double = getDouble()
            if (getDistance(x, y, 0.0, 0.0) <= 1) {
                insideCircle++
            }
        }
        duration = startTime.elapsedNow() // Duration in nanoseconds

        piEstimate = 4.0 * insideCircle / samples
    }

    /**
     * Returns a string representation of the MonteCarlo object.
     *
     * @return a string containing the class name, estimated Pi, number of samples, and duration
     */
    override fun toString(): String {
        return "Estimated Pi: " + piEstimate + "\n" +
                "Deviation: " + (piEstimate - PI).toFloat() + "\n" +
                "Number of samples: " + samples + "\n" +
                "Duration: " + duration.toString() + "\n"
    }
}