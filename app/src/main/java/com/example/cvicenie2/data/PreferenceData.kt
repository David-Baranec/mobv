import android.content.Context
import android.content.SharedPreferences
import com.example.cvicenie2.config.AppConfig
import com.example.cvicenie2.data.model.User

class PreferenceData private constructor() {

    private fun getSharedPreferences(context: Context?): SharedPreferences? {
        return context?.getSharedPreferences(
            shpKey, Context.MODE_PRIVATE
        )
    }

    fun clearData(context: Context?) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }

    fun putUser(context: Context?, user: User?) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        user?.toJson()?.let {
            editor.putString(userKey, it)
        } ?: editor.remove(userKey)

        editor.apply()
    }

    fun getUser(context: Context?): User? {
        val sharedPref = getSharedPreferences(context) ?: return null
        val json = sharedPref.getString(userKey, null) ?: return null

        return User.fromJson(json)
    }
    fun putSharing(context: Context?, sharing: Boolean) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        editor.putBoolean(sharingKey, sharing)
        editor.apply()
    }

    fun getSharing(context: Context?): Boolean {
        val sharedPref = getSharedPreferences(context) ?: return false
        val sharing = sharedPref.getBoolean(sharingKey, false)

        return sharing
    }
    fun putSharingAuto(context: Context?, sharing: Boolean) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        editor.putBoolean(sharingKeyAuto, sharing)
        editor.apply()
    }

    fun getSharingAuto(context: Context?): Boolean {
        val sharedPref = getSharedPreferences(context) ?: return false
        val sharing = sharedPref.getBoolean(sharingKeyAuto, false)

        return sharing
    }
    companion object {
        @Volatile
        private var INSTANCE: PreferenceData? = null

        private val lock = Any()

        fun getInstance(): PreferenceData =
            INSTANCE ?: synchronized(lock) {
                INSTANCE
                    ?: PreferenceData().also { INSTANCE = it }
            }

        private const val shpKey = AppConfig.SharedPreferences_KEY
        private const val userKey = "userKey"
        private const val sharingKey = "sharingKey"
        private const val sharingKeyAuto = "sharingKeyAuto"



    }

}