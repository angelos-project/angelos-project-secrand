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
 * Represents the state of a process or operation.
 *
 * This enum class defines three states:
 * - INITIALIZE: The process is in the initialization phase, where setup and preparation are being performed.
 * - RUNNING: The process is currently active and executing its main tasks.
 * - FINISHED: The process has completed its execution and is no longer active.
 */
public enum class RunState {
    INITIALIZE, RUNNING, FINISHED
}