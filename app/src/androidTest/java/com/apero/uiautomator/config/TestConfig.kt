package com.apero.uiautomator.config

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Lớp chứa cấu hình cho tests, có thể được thay thế bằng Firebase Remote Config trong triển khai thực tế
 */
object TestConfig {
    // Màu nút Login
    var loginButtonColor = "null"
    
    /**
     * Khởi tạo cấu hình từ Firebase Remote Config
     * Sử dụng phương thức này khi bạn muốn lấy cấu hình từ Firebase
     */
    fun initFromRemoteConfig() {
        try {
            val context = ApplicationProvider.getApplicationContext<Context>()
            try {
                FirebaseApp.getInstance()
            } catch (e: IllegalStateException) {
                FirebaseApp.initializeApp(context)
            }
            val remoteConfig = Firebase.remoteConfig
            
            // Cấu hình thời gian fetch = 0 để luôn lấy dữ liệu mới
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 0
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
            
            // Đặt giá trị mặc định
            val defaults = mapOf(
                "login_button_color" to "null"
            )
            remoteConfig.setDefaultsAsync(defaults)
            
            // Fetch và activate cấu hình
            val latch = CountDownLatch(1)
            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loginButtonColor = remoteConfig.getString("login_button_color")
                }
                latch.countDown()
            }
            
            // Đợi tối đa 5 giây
            latch.await(5, TimeUnit.SECONDS)
        } catch (e: Exception) {
            // Sử dụng giá trị mặc định nếu có lỗi
            println("Lỗi khi tải cấu hình từ Firebase: ${e.message}")
        }
    }
}