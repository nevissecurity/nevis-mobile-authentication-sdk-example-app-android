/**
 * Nevis Mobile Authentication SDK Example App
 *
 * Copyright © 2023. Nevis Security AG. All rights reserved.
 */

package ch.nevis.exampleapp.logging

import timber.log.Timber

/**
 * Adds a new logging method to [Timber.Tree] class that logs special SDK related messages.
 * Unfortunately, Kotlin does not allow to add a new static method to a Java class like [Timber], so we have to add this new method to [Timber.Tree] class.
 * Because this method added to [Timber.Tree] class, to call it, first you have to get the [Timber] logger as a tree. E.g.:
 *
 * Timber.asTree().sdk("message")
 *
 * @param message The SDK related message to log.
 */
fun Timber.Tree.sdk(message: String) {
    log(ExampleAppTimberDebugTree.PRIORITY_SDK_EVENT, message)
}