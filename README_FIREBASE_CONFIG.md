# Hướng Dẫn Thiết Lập Firebase Remote Config

Firebase Remote Config là công cụ cho phép bạn thay đổi giao diện và hành vi của ứng dụng mà không cần phát hành phiên bản mới. Tài liệu này hướng dẫn cách thiết lập và sử dụng Firebase Remote Config trong ứng dụng Android.

## 1. Tạo Dự Án Firebase

1. Truy cập [Firebase Console](https://console.firebase.google.com/)
2. Nhấn "Add project" (Thêm dự án)
3. Đặt tên cho dự án, ví dụ: "MyAppRemoteConfig"
4. Làm theo hướng dẫn để hoàn tất việc tạo dự án

## 2. Đăng Ký Ứng Dụng Android

1. Trong Firebase Console, chọn dự án của bạn
2. Nhấn biểu tượng Android để đăng ký ứng dụng Android
3. Nhập package name của ứng dụng (`com.apero.divkit_demo_xml` trong trường hợp này)
4. Nhấn "Register app" (Đăng ký ứng dụng)
5. Tải xuống file `google-services.json`
6. Đặt file `google-services.json` vào thư mục `app/` của dự án Android

## 3. Cấu Hình Gradle

Dự án đã được cấu hình sẵn với các thư viện Firebase cần thiết:

```gradle
// build.gradle (Project)
buildscript {
    dependencies {
        classpath 'com.google.gms:google-services:4.4.1'
    }
}

// app/build.gradle
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'com.google.gms.google-services'
}

dependencies {
    implementation 'com.google.firebase:firebase-config-ktx:21.4.1'
    implementation 'com.google.firebase:firebase-analytics-ktx:21.6.0'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3'
}
```

## 4. Thiết Lập Remote Config trong Firebase Console

1. Trong Firebase Console, chọn dự án của bạn
2. Từ menu bên trái, chọn "Remote Config"
3. Thêm các tham số sau:
   - Key: `login_button_color`, Value: `#2196F3` (hoặc mã màu khác)
   - Key: `login_button_text`, Value: `Đăng nhập`
   - Key: `email_hint`, Value: `Nhập email`
   - Key: `password_hint`, Value: `Nhập mật khẩu`
4. Nhấn "Publish changes" để xuất bản cấu hình

## 5. Kích Hoạt Firebase trong Ứng Dụng

Để kích hoạt Firebase trong tests, mở file `app/src/androidTest/java/com/apero/uiautomator/FirebaseRemoteConfigTest.kt`:

1. Bỏ comment dòng `@Test` trước phương thức `testRealFirebaseConnection()`
2. Bỏ comment đoạn code bên trong phương thức
3. Chạy test để kiểm tra kết nối với Firebase

## 6. Sử dụng Firebase Remote Config trong Test

Dự án đã được cài đặt để sử dụng TestConfig thay cho việc gọi trực tiếp Firebase:

```kotlin
// Khởi tạo và tải giá trị từ Firebase Remote Config
TestConfig.initFromRemoteConfig()

// Sử dụng giá trị trong tests
val expectedColor = Color.parseColor(TestConfig.loginButtonColor)
device.wait(Until.hasObject(By.text(TestConfig.loginButtonText)), TIMEOUT)
```

## 7. Khắc Phục Sự Cố

### Không thể kết nối đến Firebase

Kiểm tra:
- File `google-services.json` đã đúng và đặt đúng vị trí
- Package name trong file `google-services.json` khớp với package name trong `build.gradle`
- Thiết bị/máy ảo có kết nối internet
- Đã thêm đầy đủ thư viện Firebase

### Remote Config không trả về giá trị mong đợi

Kiểm tra:
- Đã xuất bản thay đổi trong Firebase Console
- Đã đặt `minimumFetchIntervalInSeconds = 0` để luôn fetch giá trị mới
- Các key trong mã nguồn khớp với các key trong Firebase Console

## 8. Tài Liệu Tham Khảo

- [Firebase Remote Config - Tài liệu chính thức](https://firebase.google.com/docs/remote-config)
- [Mẫu ứng dụng Firebase Remote Config](https://github.com/firebase/quickstart-android/tree/master/config)

---

Chúc bạn thành công với việc triển khai Firebase Remote Config!