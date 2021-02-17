
package com.dev.data.core.log

import com.dev.data.BuildConfig

/**
 * Own solution for logging operations
 * Good enough to not use Timber or any other third-party loggers
 */

interface DebugConfig {
	
	val isEnabled: Boolean
	val logger: MyLogger
	
	object Default : DebugConfig {
		override val isEnabled: Boolean = BuildConfig.DEBUG
		override val logger: MyLogger = if (isEnabled) MyLogger.Debug else MyLogger.Default
	}
	
	object Enabled : DebugConfig {
		override val isEnabled: Boolean = true
		override val logger: MyLogger = MyLogger.Debug
	}
}