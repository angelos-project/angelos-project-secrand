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
package org.angproj.sec.util

/**
 * Unconditionally throws the specified exception from [exc] of type [T].
 *
 * This function is intended for scenarios where an exception needs to be thrown unconditionally.
 * The caller provides a lambda [exc] that constructs the exception to be thrown, allowing for custom exception types and messages.
 * By using a reified type parameter, the function enables flexible error signaling and supports a wide range of exception types.
 *
 * @param exc A lambda that returns an instance of the exception type [T] to be thrown.
 * @throws T The exception instance returned by [exc] is thrown unconditionally.
 */
public inline fun <reified T: Throwable> ensure(exc: () -> T): Nothing {
    throw exc()
}

/**
 * Evaluates the specified boolean condition [expr] and throws an exception of type [T] if the condition is false.
 *
 * This function is intended for enforcing preconditions, invariants, or postconditions in a generic and type-safe manner.
 * The caller provides a lambda [exc] that constructs the exception to be thrown, allowing for custom exception types and messages.
 * By using a reified type parameter, the function enables flexible error signaling and supports a wide range of exception types.
 *
 * @param expr The boolean condition to evaluate. If this condition is false, the exception provided by [exc] is thrown.
 * @param exc A lambda that returns an instance of the exception type [T] to be thrown if [expr] is false.
 * @throws T If [expr] evaluates to false, the exception instance returned by [exc] is thrown.
 */
public inline fun <reified T: Throwable> ensure(expr: Boolean, exc: () -> T) { if (!expr) ensure(exc) }