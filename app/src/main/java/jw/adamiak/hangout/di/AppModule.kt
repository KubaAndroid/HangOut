package jw.adamiak.hangout.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jw.adamiak.hangout.utils.DataStoreManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

	@Provides
	@Singleton
	fun provideDataStoreManager(@ApplicationContext appContext: Context): DataStoreManager =
		DataStoreManager(appContext)



}