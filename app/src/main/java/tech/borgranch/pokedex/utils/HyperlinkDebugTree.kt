package tech.borgranch.pokedex.utils

import timber.log.Timber

/** A tree which adds the filename, line-number and method call when debugging. */
class HyperlinkDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String {
        with(element) {
            return ("($fileName:$lineNumber)$methodName")
        }
    }
}
