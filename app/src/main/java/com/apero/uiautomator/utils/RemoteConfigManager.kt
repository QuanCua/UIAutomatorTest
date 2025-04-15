package com.apero.uiautomator.utils

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

/**
 * Lớp quản lý Firebase Remote Config
 * Sử dụng Singleton pattern để khởi tạo và truy xuất dữ liệu từ Remote Config
 */
class RemoteConfigManager private constructor() {

    companion object {
        private var instance: RemoteConfigManager? = null
        private lateinit var remoteConfig: FirebaseRemoteConfig
        
        // Giá trị mặc định cho Remote Config
        private val defaults = mapOf(
            "login_button_color" to "#949BF4",
            "login_button_text" to "Login",
            "email_hint" to "Email",
            "password_hint" to "Password"
        )
        
        /**
         * Khởi tạo singleton instance
         */
        fun getInstance(context: Context): RemoteConfigManager {
            if (instance == null) {
                instance = RemoteConfigManager()
                // Khởi tạo Firebase nếu cần
                try {
                    FirebaseApp.getInstance()
                } catch (e: IllegalStateException) {
                    FirebaseApp.initializeApp(context)
                }
                
                // Cấu hình Remote Config
                remoteConfig = Firebase.remoteConfig
                val configSettings = remoteConfigSettings {
                    minimumFetchIntervalInSeconds = 3600 // 1 giờ
                }
                remoteConfig.setConfigSettingsAsync(configSettings)
                remoteConfig.setDefaultsAsync(defaults)
            }
            return instance!!
        }
        
        /**
         * Cấu hình thời gian fetch mới cho môi trường test
         */
        fun setFetchIntervalForTests() {
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 0
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
        }
    }
    
    /**
     * Fetch và activate cấu hình mới từ Remote Config
     * @return Task để xử lý kết quả fetch
     */
    fun fetchAndActivate(): Task<Boolean> {
        return remoteConfig.fetchAndActivate()
    }
    
    /**
     * Lấy giá trị string từ Remote Config
     */
    fun getString(key: String): String {
        return remoteConfig.getString(key)
    }
    
    /**
     * Lấy giá trị boolean từ Remote Config
     */
    fun getBoolean(key: String): Boolean {
        return remoteConfig.getBoolean(key)
    }
    
    /**
     * Lấy giá trị long từ Remote Config
     */
    fun getLong(key: String): Long {
        return remoteConfig.getLong(key)
    }
    
    /**
     * Lấy giá trị double từ Remote Config
     */
    fun getDouble(key: String): Double {
        return remoteConfig.getDouble(key)
    }
}