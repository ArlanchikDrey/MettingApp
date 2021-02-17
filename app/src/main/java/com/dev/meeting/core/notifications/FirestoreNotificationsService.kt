
package com.dev.meeting.core.notifications

import android.annotation.SuppressLint
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.dev.meeting.R
import com.dev.meeting.core.glide.GlideApp
import com.dev.meeting.ui.MainActivity


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirestoreNotificationsService: FirebaseMessagingService(), LifecycleObserver {

	private var isAppInForeground = false

	override fun onCreate() {
		super.onCreate()
		ProcessLifecycleOwner.get().lifecycle.addObserver(this)
	}

	override fun onDestroy() {
		super.onDestroy()
		ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
	}


	@OnLifecycleEvent(Lifecycle.Event.ON_START)
	private fun onForegroundStart() {
		isAppInForeground = true
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_STOP)
	private fun onForegroundStop() {
		isAppInForeground = false
	}

	override fun onMessageReceived(remoteMessage: RemoteMessage) {
		if(isAppInForeground) {
			// do foreground stuff on your activities
			Log.wtf("mylogs_FCMService",
			        "app is in foreground, unhandled notification: ${remoteMessage.data["CONTENT"]}")
		} else {
			// send a notification
			if (remoteMessage.data["TYPE"] == "NEW_MATCH") notifyNewMatch(remoteMessage)
			else notifyNewMessage(remoteMessage)
		}
	}


	private fun notifyNewMessage(remoteMessage: RemoteMessage){
		val conversation = bundleOf(
			"PARTNER_CITY" to remoteMessage.data["SENDER_CITY"],
			"PARTNER_GENDER" to remoteMessage.data["SENDER_GENDER"],
			"PARTNER_ID" to remoteMessage.data["SENDER_ID"],
			"CONVERSATION_ID" to remoteMessage.data["CONVERSATION_ID"]
		)

		val pendingIntent = NavDeepLinkBuilder(this)
			.setComponentName(MainActivity::class.java)
			.setGraph(R.navigation.main_navigation)
			.setDestination(R.id.chatFragment)
			.setArguments(conversation)
			.createPendingIntent()

		val notificationBuilder = NotificationCompat.Builder(
			this, getString(R.string.notification_channel_id_messages)
		)
			.setSmallIcon(R.drawable.ic_notification_message)
			.setContentTitle(remoteMessage.data["SENDER_NAME"])
			.setContentText(remoteMessage.data["CONTENT"])
			.setNumber(1)
			.setCategory(NotificationCompat.CATEGORY_MESSAGE)
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
			.setContentIntent(pendingIntent)

		val futureTarget = GlideApp.with(this)
			.asBitmap()
			.circleCrop()
			.load(remoteMessage.data["SENDER_PHOTO"])
			.submit()

		val bitmap = futureTarget.get()
		notificationBuilder.setLargeIcon(bitmap)

		GlideApp.with(this).clear(futureTarget)


		val notificationId = System.currentTimeMillis().toInt()
		// notificationId is a unique int for each notification that you must define
		NotificationManagerCompat.from(this).notify(notificationId, notificationBuilder.build())
	}

	private fun notifyNewMatch(remoteMessage: RemoteMessage){

		val pendingIntent = NavDeepLinkBuilder(this)
			.setComponentName(MainActivity::class.java)
			.setGraph(R.navigation.main_navigation)
			.setDestination(R.id.pairsFragment)
			.createPendingIntent()

		val notificationBuilder = NotificationCompat.Builder(this,
		                                                     getString(R.string.notification_channel_id_match))
			.setSmallIcon(R.drawable.ic_notification_match)
			.setContentTitle("It's a match!")
			.setContentText(remoteMessage.data["CONTENT"])
			.setNumber(1)
			.setCategory(NotificationCompat.CATEGORY_MESSAGE)
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
			.setContentIntent(pendingIntent)

		val futureTarget = GlideApp.with(this)
			.asBitmap()
			.load(R.mipmap.ic_launcher_round)
			.submit()

		val bitmap = futureTarget.get()
		notificationBuilder.setLargeIcon(bitmap)

		GlideApp.with(this).clear(futureTarget)
		

		val notificationId = System.currentTimeMillis().toInt()
		// notificationId is a unique int for each notification that you must define
		NotificationManagerCompat.from(this).notify(notificationId, notificationBuilder.build())
	}

}
