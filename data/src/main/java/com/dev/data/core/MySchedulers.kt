
package com.dev.data.core

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * app related schedulers class that used in repositoriesImpl to avoid direct dependencies on
 * RxJava library values and imports
 */

internal object MySchedulers {

	fun computation(): Scheduler = Schedulers.computation()
	fun trampoline(): Scheduler = Schedulers.trampoline()
	fun newThread(): Scheduler = Schedulers.newThread()
	fun io(): Scheduler = Schedulers.io()

}
