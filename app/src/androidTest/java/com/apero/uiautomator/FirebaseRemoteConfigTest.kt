package com.apero.uiautomator

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.apero.uiautomator.config.TestConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Test class để kiểm tra kết nối đến Firebase Remote Config
 */
@RunWith(AndroidJUnit4::class)
class FirebaseRemoteConfigTest {
    
    private lateinit var context: Context
    
    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }
    
    @Test
    fun testRemoteConfigConnection() {
        println("Kiểm tra kết nối Firebase Remote Config")
        
        // Tải các giá trị cấu hình
        TestConfig.initFromRemoteConfig()
        
        // Kiểm tra các giá trị đã được tải
        assertNotNull("loginButtonColor không được null", TestConfig.loginButtonColor)
        
        // In các giá trị để kiểm tra
        println("Các giá trị từ Remote Config:")
        println("- loginButtonColor: ${TestConfig.loginButtonColor}")
    }
    
    /**
     * Test kết nối Firebase thực tế - uncomment và triển khai code trong môi trường thực
     */
    //@Test
    fun testRealFirebaseConnection() {
        println("Kiểm tra kết nối Firebase thực tế")
        
        // Khởi tạo kết nối Firebase
        try {
            val firebaseApp = FirebaseApp.getInstance()
            assertNotNull("FirebaseApp không được null", firebaseApp)
            
            // Khởi tạo Remote Config
            val remoteConfig = FirebaseRemoteConfig.getInstance()
            assertNotNull("FirebaseRemoteConfig không được null", remoteConfig)
            
            // Cấu hình fetch interval = 0
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build()
            remoteConfig.setConfigSettingsAsync(configSettings)
            
            // Cài đặt giá trị mặc định
            val defaults = HashMap<String, Any>()
            defaults["login_button_color"] = "#949BF4"
            defaults["login_button_text"] = "Login"
            defaults["email_hint"] = "Email"
            defaults["password_hint"] = "Password"
            remoteConfig.setDefaultsAsync(defaults)
            
            // Tạo latch để đồng bộ
            val latch = CountDownLatch(1)
            var fetchSuccessful = false
            
            // Fetch và activate
            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                fetchSuccessful = task.isSuccessful
                if (task.isSuccessful) {
                    println("Fetch thành công, đã nhận dữ liệu mới")
                    println("- loginButtonColor: ${remoteConfig.getString("login_button_color")}")
                    println("- loginButtonText: ${remoteConfig.getString("login_button_text")}")
                } else {
                    println("Fetch thất bại: ${task.exception?.message}")
                }
                latch.countDown()
            }
            
            // Đợi tối đa 10 giây
            latch.await(10, TimeUnit.SECONDS)
            
            // Kiểm tra kết quả
            if (fetchSuccessful) {
                println("Kết nối Firebase Remote Config thành công!")
            } else {
                println("Kết nối Firebase Remote Config thất bại!")
            }
        } catch (e: Exception) {
            println("Lỗi khi kết nối Firebase: ${e.message}")
            e.printStackTrace()
        }
    }
}