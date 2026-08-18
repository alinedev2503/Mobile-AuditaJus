package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

data class UserSettings(
    val isLoggedIn: Boolean = true,
    val userName: String = "Dr. Roberto Silva",
    val userEmail: String = "roberto.silva@contadorjuridico.com.br",
    val avatarUrl: String = "https://lh3.googleusercontent.com/aida-public/AB6AXuCqLYBxhvG9DoPh-NQcd_N9CTc4sV0Mlz3p5oHBfNsTk5dn5AhZa8M9V4Ebercte7M0Qo_tPw4FVdu497QVBxHt-rSUwns3ie550ndfq4Dv5jYCW61pbvPuKs5oKns8pmWfHsuQeQWctXAlWx-HM8_J2Mmy7wDj-nZMH_PLXhDf_cjTxYyqdakaA0DqYUZBwA22zEhsvhQRLCVyCw0YqAkMakqDu5GSly4huM7MBkUgvRily2DvKEcI",
    val isBiometricEnabled: Boolean = true,
    val isPushEnabled: Boolean = true,
    val isEmailEnabled: Boolean = false,
    val hasAcceptedTerms: Boolean = true,
    val hasCompletedOnboarding: Boolean = true,
    val isDarkMode: Boolean? = null,
    // Lawyer & Firm Customization
    val oabNumber: String = "123.456",
    val oabUf: String = "SP",
    val lawFirmName: String = "Silva & Associados Advocacia",
    val officeAddress: String = "Av. Paulista, 1000, Cj. 1402 - São Paulo/SP",
    val officePhone: String = "(11) 98765-4321",
    val logoUri: String = "",
    val useCustomLetterhead: Boolean = true
)

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val AVATAR_URL = stringPreferencesKey("avatar_url")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val PUSH_ENABLED = booleanPreferencesKey("push_enabled")
        val EMAIL_ENABLED = booleanPreferencesKey("email_enabled")
        val ACCEPTED_TERMS = booleanPreferencesKey("accepted_terms")
        val COMPLETED_ONBOARDING = booleanPreferencesKey("completed_onboarding")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val OAB_NUMBER = stringPreferencesKey("oab_number")
        val OAB_UF = stringPreferencesKey("oab_uf")
        val LAW_FIRM_NAME = stringPreferencesKey("law_firm_name")
        val OFFICE_ADDRESS = stringPreferencesKey("office_address")
        val OFFICE_PHONE = stringPreferencesKey("office_phone")
        val LOGO_URI = stringPreferencesKey("logo_uri")
        val USE_CUSTOM_LETTERHEAD = booleanPreferencesKey("use_custom_letterhead")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            isLoggedIn = prefs[Keys.IS_LOGGED_IN] ?: true,
            userName = prefs[Keys.USER_NAME] ?: "Dr. Roberto Silva",
            userEmail = prefs[Keys.USER_EMAIL] ?: "roberto.silva@contadorjuridico.com.br",
            avatarUrl = prefs[Keys.AVATAR_URL] ?: "https://lh3.googleusercontent.com/aida-public/AB6AXuCqLYBxhvG9DoPh-NQcd_N9CTc4sV0Mlz3p5oHBfNsTk5dn5AhZa8M9V4Ebercte7M0Qo_tPw4FVdu497QVBxHt-rSUwns3ie550ndfq4Dv5jYCW61pbvPuKs5oKns8pmWfHsuQeQWctXAlWx-HM8_J2Mmy7wDj-nZMH_PLXhDf_cjTxYyqdakaA0DqYUZBwA22zEhsvhQRLCVyCw0YqAkMakqDu5GSly4huM7MBkUgvRily2DvKEcI",
            isBiometricEnabled = prefs[Keys.BIOMETRIC_ENABLED] ?: true,
            isPushEnabled = prefs[Keys.PUSH_ENABLED] ?: true,
            isEmailEnabled = prefs[Keys.EMAIL_ENABLED] ?: false,
            hasAcceptedTerms = prefs[Keys.ACCEPTED_TERMS] ?: true,
            hasCompletedOnboarding = prefs[Keys.COMPLETED_ONBOARDING] ?: true,
            isDarkMode = prefs[Keys.DARK_MODE],
            oabNumber = prefs[Keys.OAB_NUMBER] ?: "123.456",
            oabUf = prefs[Keys.OAB_UF] ?: "SP",
            lawFirmName = prefs[Keys.LAW_FIRM_NAME] ?: "Silva & Associados Advocacia",
            officeAddress = prefs[Keys.OFFICE_ADDRESS] ?: "Av. Paulista, 1000, Cj. 1402 - São Paulo/SP",
            officePhone = prefs[Keys.OFFICE_PHONE] ?: "(11) 98765-4321",
            logoUri = prefs[Keys.LOGO_URI] ?: "",
            useCustomLetterhead = prefs[Keys.USE_CUSTOM_LETTERHEAD] ?: true
        )
    }

    suspend fun updateLawyerProfile(
        oabNumber: String,
        oabUf: String,
        lawFirmName: String,
        officeAddress: String,
        officePhone: String,
        logoUri: String,
        useCustomLetterhead: Boolean
    ) {
        context.dataStore.edit { prefs ->
            prefs[Keys.OAB_NUMBER] = oabNumber
            prefs[Keys.OAB_UF] = oabUf
            prefs[Keys.LAW_FIRM_NAME] = lawFirmName
            prefs[Keys.OFFICE_ADDRESS] = officeAddress
            prefs[Keys.OFFICE_PHONE] = officePhone
            prefs[Keys.LOGO_URI] = logoUri
            prefs[Keys.USE_CUSTOM_LETTERHEAD] = useCustomLetterhead
        }
    }

    suspend fun setDarkMode(isDark: Boolean?) {
        context.dataStore.edit { prefs ->
            if (isDark == null) {
                prefs.remove(Keys.DARK_MODE)
            } else {
                prefs[Keys.DARK_MODE] = isDark
            }
        }
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean, name: String? = null, email: String? = null) {
        context.dataStore.edit { prefs ->
            prefs[Keys.IS_LOGGED_IN] = isLoggedIn
            if (name != null) prefs[Keys.USER_NAME] = name
            if (email != null) prefs[Keys.USER_EMAIL] = email
        }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.BIOMETRIC_ENABLED] = enabled
        }
    }

    suspend fun setPushEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.PUSH_ENABLED] = enabled
        }
    }

    suspend fun setEmailEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.EMAIL_ENABLED] = enabled
        }
    }

    suspend fun setAcceptedTerms(accepted: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ACCEPTED_TERMS] = accepted
        }
    }

    suspend fun setCompletedOnboarding(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.COMPLETED_ONBOARDING] = completed
        }
    }
}
