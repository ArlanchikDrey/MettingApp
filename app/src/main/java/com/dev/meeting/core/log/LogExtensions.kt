
package com.dev.meeting.core.log

import com.dev.meeting.core.MeetingApp

fun logWarn(tag: String = "mylogs", message: String) =
	MeetingApp.debug.logger.logWarn(tag, message)
fun logWarn(clazz: Class<Any>, message: String) =
	com.dev.data.core.log.logWarn("mylogs_${clazz.simpleName}", message)



fun logError(tag: String = "mylogs", message: String) =
	MeetingApp.debug.logger.logError(tag, message)
fun logError(clazz: Class<Any>, message: String) =
	logError("mylogs_${clazz.simpleName}", message)



fun logDebug(tag: String = "mylogs", message: String) =
	MeetingApp.debug.logger.logDebug(tag, message)
fun logDebug(clazz: Class<Any>, message: String) =
	logDebug("mylogs_${clazz.simpleName}", message)



fun logInfo(tag: String = "mylogs", message: String) =
	MeetingApp.debug.logger.logInfo(tag, message)
fun logInfo(clazz: Class<Any>, message: String) =
	logInfo("mylogs_${clazz.simpleName}", message)



fun logWtf(tag: String = "mylogs", message: String) =
	MeetingApp.debug.logger.logWtf(tag, message)
fun logWtf(clazz: Class<Any>, message: String) =
	logWtf("mylogs_${clazz.simpleName}", message)
