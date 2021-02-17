
package com.dev.domain.pairs

import com.dev.domain.PaginationDirection
import com.dev.domain.user.data.UserItem
import io.reactivex.rxjava3.core.Single
import java.util.*

/**
 * This is the documentation block about the class
 */

interface PairsRepository {
	
	fun getPairs(
		user: UserItem,
		conversationTimestamp: Date,
		page: Int,
		direction: PaginationDirection
	): Single<List<MatchedUserItem>>

}