package org.angproj.sec

import kotlin.time.Duration.Companion.minutes

public abstract class FuzzPrefs {
    public val maxTotalTime: Long = 2.minutes.inWholeSeconds
}