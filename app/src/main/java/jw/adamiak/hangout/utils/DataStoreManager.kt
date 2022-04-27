package jw.adamiak.hangout.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import jw.adamiak.hangout.data.local.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import java.io.IOException
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("user_data")

class DataStoreManager @Inject constructor(@ApplicationContext context: Context) {

	private val myDataStore = context.dataStore

	companion object {
		val usernameDataStoreKey = stringPreferencesKey("username")
		val emailDataStoreKey = stringPreferencesKey("user_email")
		val passwordDataStoreKey = stringPreferencesKey("user_password")
	}

	suspend fun saveUsername(username: String) {
		myDataStore.edit {
			it[usernameDataStoreKey] = username
		}
	}

	suspend fun saveEmail(email: String) {
		myDataStore.edit {
			it[emailDataStoreKey] = email
		}
	}

	suspend fun savePassword(password: String) {
		myDataStore.edit {
			it[passwordDataStoreKey] = password
		}
	}

	suspend fun getUserEmail(): Flow<String?> {
		return myDataStore.data
			.catch { exception ->
				if (exception is IOException) {
					emit(emptyPreferences())
				}
		}.map { preferences ->
				val userEmail = preferences[emailDataStoreKey]
				userEmail
			}
	}

	val getUserDataFlow: Flow<UserData> = myDataStore.data
		.catch { exception ->
		if (exception is IOException) {
			emit(emptyPreferences())
		}
	}.map { preferences ->
		val username = preferences[usernameDataStoreKey]
		val email = preferences[emailDataStoreKey]
		val password = preferences[passwordDataStoreKey]

		UserData(username, email, password)
	}


}