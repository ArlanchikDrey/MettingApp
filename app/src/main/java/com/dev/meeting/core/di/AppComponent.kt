
package com.dev.meeting.core.di

import android.app.Application
import com.dev.meeting.core.di.modules.AuthModule
import com.dev.meeting.core.di.modules.DataSourceModule
import com.dev.meeting.core.di.modules.FirebaseModule
import com.dev.meeting.core.di.modules.RepositoryModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [
	AuthModule::class,
	DataSourceModule::class,
	FirebaseModule::class,
	RepositoryModule::class
])
@Singleton
interface AppComponent {

	@Component.Builder
	interface Builder {

		@BindsInstance
		fun application(application: Application): Builder

		fun build(): AppComponent
	}

}