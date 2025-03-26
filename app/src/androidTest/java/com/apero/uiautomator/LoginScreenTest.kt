package com.apero.uiautomator

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import android.graphics.Bitmap
import android.graphics.Color
import java.io.File

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    companion object {
        private const val PACKAGE_NAME = "com.apero.uiautomator"
        private const val LAUNCH_TIMEOUT = 5000L
        private const val UI_TIMEOUT = 2000L
    }

    private lateinit var device: UiDevice

    @Before
    fun setUp() {

        // Khởi tạo UiDevice
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        // Trở về màn hình chính
        device.pressHome()

        // Wait for launcher
        val launcherPackage: String = device.launcherPackageName
        assertThat(launcherPackage, notNullValue())
        device.wait(
            Until.hasObject(By.pkg(launcherPackage).depth(0)),
            LAUNCH_TIMEOUT
        )

        // Khởi động ứng dụng - Cách 1: Sử dụng Intent trực tiếp để khởi động MainActivity
        val context = ApplicationProvider.getApplicationContext<Context>()
        /*
        val intent = Intent(context, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)*/

        // Hoặc Cách 2: Sử dụng package manager với package name chính xác
        val intent = context.packageManager.getLaunchIntentForPackage(PACKAGE_NAME)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        } else {
            throw RuntimeException("Could not get launch intent for $PACKAGE_NAME")
        }

        // Wait for the app to appear
        device.wait(
            Until.hasObject(By.pkg(PACKAGE_NAME).depth(0)),
            LAUNCH_TIMEOUT
        )
        
        // Tìm nút Login trên MainActivity và click để mở LoginActivity
        val loginButton: UiObject2 = device.findObject(By.res(PACKAGE_NAME, "login_button"))
        assertNotNull("Không tìm thấy nút login trên MainActivity", loginButton)
        loginButton.click()
        
        // Đợi LoginActivity hiển thị
        device.wait(Until.hasObject(By.text("Login")), LAUNCH_TIMEOUT)
    }

    @Test
    fun testLoginWithValidCredentials() {
        // Đợi cho LoginActivity hiển thị
        device.wait(Until.hasObject(By.text("Login")), LAUNCH_TIMEOUT)
        
        // Tìm trường đăng nhập bằng ID
        val emailField: UiObject2 = device.findObject(By.res(PACKAGE_NAME, "edtEmail"))
        assertNotNull("Không tìm thấy trường email", emailField)

        // Nhập email
        emailField.text = "user@example.com"

        // Tìm trường mật khẩu
        val passwordField: UiObject2 = device.findObject(By.res(PACKAGE_NAME, "password_input"))
        assertNotNull("Không tìm thấy trường mật khẩu", passwordField)

        // Nhập mật khẩu
        passwordField.text = "password123"

        // Tìm nút đăng nhập
        val loginButton: UiObject2 = device.findObject(By.res(PACKAGE_NAME, "submit_button"))
        assertNotNull("Không tìm thấy nút đăng nhập", loginButton)

        // Nhấn nút đăng nhập
        loginButton.click()

        // Kiểm tra đăng nhập thành công bằng cách xác nhận Toast hiển thị
        // Đợi toast message hiển thị (text chứa "Đăng nhập thành công")
        // Tăng thời gian chờ và sử dụng cách phát hiện Toast khác
        /*Thread.sleep(1000) // Chờ một chút để Toast xuất hiện
        val toastShown = device.wait(Until.hasObject(By.textContains("Đăng nhập thành công").pkg("android")), 3000L)
        assertTrue("Không hiển thị thông báo đăng nhập thành công", toastShown)
*/
        // Hoặc xác nhận đăng nhập thành công bằng cách kiểm tra đã trở về MainActivity
        // Thay vì phụ dependencies vào Toast, kiểm tra MainActivity đã được hiển thị
        device.wait(Until.gone(By.res(PACKAGE_NAME, "submit_button")), 3000L)
        assertTrue("Không chuyển về MainActivity sau khi đăng nhập", !device.hasObject(By.res(
            PACKAGE_NAME, "submit_button")))

        // Kiểm tra đã quay về MainActivity (có nghĩa là LoginActivity đã finish)
        // Đợi một chút để activity chuyển đổi
    }

    @Test
    fun testEmptyFieldsValidation() {
        // Tìm nút đăng nhập mà không nhập thông tin
        val loginButton: UiObject2 = device.findObject(By.res(PACKAGE_NAME, "submit_button"))
        assertNotNull("Không tìm thấy nút đăng nhập", loginButton)

        // Nhấn nút đăng nhập
        loginButton.click()

        // Kiểm tra thông báo lỗi trường trống
        val emailError = device.hasObject(By.res(PACKAGE_NAME, "email_error"))
        assertTrue("Không hiển thị lỗi email trống", emailError)

        val passwordError = device.hasObject(By.res(PACKAGE_NAME, "password_error"))
        assertTrue("Không hiển thị lỗi mật khẩu trống", passwordError)
    }

    @Test
    fun testForgotPasswordLink() {
        // Tìm link quên mật khẩu bằng text
        val forgotPasswordLink: UiObject2 = device.findObject(By.text("Quên mật khẩu?"))
        assertNotNull("Không tìm thấy link quên mật khẩu", forgotPasswordLink)

        // Nhấn vào link
        forgotPasswordLink.click()

        // Kiểm tra màn hình đặt lại mật khẩu hiển thị
        val resetScreen = device.wait(Until.hasObject(By.res(PACKAGE_NAME, "reset_password_screen")), UI_TIMEOUT)
        assertTrue("Màn hình đặt lại mật khẩu không hiển thị", resetScreen)
    }

    @Test
    fun testNavigateToRegister() {
        // Tìm nút đăng ký
        val registerButton: UiObject2 = device.findObject(By.text("Đăng ký"))
        assertNotNull("Không tìm thấy nút đăng ký", registerButton)

        // Nhấn nút đăng ký
        registerButton.click()

        // Kiểm tra màn hình đăng ký hiển thị
        val registerScreen = device.wait(Until.hasObject(By.res(PACKAGE_NAME, "register_screen")), UI_TIMEOUT)
        assertTrue("Màn hình đăng ký không hiển thị", registerScreen)
    }

    @Test
    fun testLoginButtonBackgroundColor() {
        // Tìm nút login bằng ID
        val loginButton: UiObject2 = device.findObject(By.res(PACKAGE_NAME, "submit_button"))
        assertNotNull("Không tìm thấy nút login", loginButton)

        // Khởi tạo biến có mã màu "#000000" (màu đen)
//        val failColor = Color.parseColor("#000000")
        val trueColor = Color.parseColor("#949BF4")

        // Lấy màu nền thực tế của button
        // Sử dụng getBackgroundColor (cần extension function)
        val actualColor = getBackgroundColorOfView(loginButton)

        // Kiểm tra màu nền của button có trùng với màu mong đợi không
        assertEquals("Màu nền của button không phải là #000000", trueColor, actualColor)
    }

    // Hàm hỗ trợ để lấy màu nền của UiObject2
    private fun getBackgroundColorOfView(uiObject: UiObject2): Int {
        val bounds = uiObject.visibleBounds
        // Lưu screenshot vào file tạm thời
        val screenshotFile = File.createTempFile("screenshot", ".png")
        device.takeScreenshot(screenshotFile)

        // Sử dụng Bitmap để đọc màu từ screenshot
        val bitmap = android.graphics.BitmapFactory.decodeFile(screenshotFile.absolutePath)

        // Lấy màu tại các vị trí cạnh thay vì điểm giữa để tránh xung đột với text
        // Kiểm tra 4 điểm ở các góc (cách mép một chút để tránh các hiệu ứng bo góc)
        val padding = 5
        val topLeft = bitmap.getPixel(bounds.left + padding, bounds.top + padding)
        val topRight = bitmap.getPixel(bounds.right - padding, bounds.top + padding)
        val bottomLeft = bitmap.getPixel(bounds.left + padding, bounds.bottom - padding)
        val bottomRight = bitmap.getPixel(bounds.right - padding, bounds.bottom - padding)

        // Xóa file tạm
        screenshotFile.delete()

        // Kiểm tra xem các điểm có cùng màu không, nếu có thì đó là màu nền
        // Nếu không thì lấy giá trị phổ biến nhất
        if (topLeft == topRight && topLeft == bottomLeft && topLeft == bottomRight) {
            return topLeft
        } else {
            // Trả về màu phổ biến nhất từ 4 điểm
            val colors = arrayOf(topLeft, topRight, bottomLeft, bottomRight)
            return colors.groupBy { it }.maxByOrNull { it.value.size }?.key ?: topLeft
        }
    }
}