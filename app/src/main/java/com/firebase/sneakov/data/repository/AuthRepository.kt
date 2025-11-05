package com.firebase.sneakov.data.repository

import com.firebase.sneakov.data.model.User
import com.firebase.sneakov.data.request.LoginRequest
import com.firebase.sneakov.data.request.RegisterRequest
import com.firebase.sneakov.data.request.UpdateUserRequest
import com.firebase.sneakov.utils.CollectionName
import com.firebase.sneakov.utils.Result
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class AuthRepository(private val db: FirebaseFirestore, private val auth: FirebaseAuth) {
    /**
     * Cung cấp ID của người dùng hiện tại một cách nhanh chóng.
     * Trả về null nếu không có ai đăng nhập.
     */
    val currentUserId: String?
        get() = auth.currentUser?.uid
    suspend fun register(request: RegisterRequest): Result<Unit> {
        return try {
            val result =
                auth.createUserWithEmailAndPassword(request.email, request.password).await()
            val userId = result.user?.uid ?: Result.Error("Không lấy được UID")

            val user = User(
                id = userId.toString(),
                name = request.name,
                email = request.email
                // role, avatarUrl, createdAt, phone, address đã có default
            )

            db.collection(CollectionName.USERS)
                .document(userId.toString())
                .set(user, SetOptions.merge())
                .await()

            db.collection(CollectionName.USERS)
                .document(userId.toString())
                .update("createdAt", FieldValue.serverTimestamp())
                .await()

            Result.Success(Unit)
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.Error("Email đã được sử dụng")
        } catch (e: FirebaseAuthWeakPasswordException) {
            Result.Error("Mật khẩu quá yếu: ${e.reason}")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.Error("Email không hợp lệ")
        } catch (e: FirebaseFirestoreException) {
            Result.Error("Lỗi khi lưu dữ liệu vào Firestore: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Có lỗi xảy ra: ${e.message ?: "Lỗi không xác định"}")
        }
    }
    suspend fun login(request: LoginRequest): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(request.email, request.password).await()
            Result.Success(Unit)

        } catch (e: FirebaseAuthInvalidUserException) {
            Result.Error("Người dùng không tồn tại")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.Error("Email hoặc mật khẩu không đúng")
        } catch (e: Exception) {
            Result.Error("Có lỗi xảy ra: ${e.message ?: "Lỗi không xác định"}")
        }
    }
    suspend fun getCurrentUser(): Result<User> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.Error("Người dùng chưa đăng nhập")

            val snapshot = db.collection(CollectionName.USERS)
                .document(userId)
                .get()
                .await()

            val user = snapshot.toObject(User::class.java)
                ?: return Result.Error("Không tìm thấy thông tin user")

            Result.Success(user)
        } catch (e: Exception) {
            Result.Error("Có lỗi xảy ra: ${e.message ?: "Lỗi không xác định"}")
        }
    }
    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> {
        return try {
            if (oldPassword.isBlank() || newPassword.isBlank()) {
                return Result.Error("Vui lòng nhập đầy đủ mật khẩu cũ và mới")
            }

            val user = FirebaseAuth.getInstance().currentUser
                ?: return Result.Error("Người dùng chưa đăng nhập")

            val email = user.email
                ?: return Result.Error("Không tìm thấy email của người dùng")

            // B1: Xác thực lại bằng mật khẩu cũ
            val credential = EmailAuthProvider.getCredential(email, oldPassword)
            user.reauthenticate(credential).await()

            // B2: Cập nhật mật khẩu mới
            user.updatePassword(newPassword).await()

            Result.Success(Unit)
        } catch (e: FirebaseAuthWeakPasswordException) {
            Result.Error("Mật khẩu quá yếu: ${e.reason}")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            // Khi mật khẩu cũ sai
            Result.Error("Mật khẩu cũ không chính xác")
        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            // Phòng trường hợp Firebase vẫn yêu cầu reauthenticate
            Result.Error("Vui lòng đăng nhập lại trước khi đổi mật khẩu")
        } catch (e: Exception) {
            Result.Error("Có lỗi xảy ra: ${e.message ?: "Lỗi không xác định"}")
        }
    }
    suspend fun updateUser(request: UpdateUserRequest): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.Error("Người dùng chưa đăng nhập")

            val updates = mutableMapOf<String, Any?>()

            request.name?.let { updates["name"] = it }
            request.phone?.let { updates["phone"] = it }
            request.avatarUrl?.let { updates["avatar_url"] = it }

            request.address?.let { address ->
                updates["address.province"] = address.province
                updates["address.district"] = address.district
                updates["address.municipality"] = address.municipality
                updates["address.detail"] = address.detail
            }

            db.collection(CollectionName.USERS)
                .document(userId)
                .update(updates)
                .await()

            Result.Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Result.Error("Lỗi khi lưu dữ liệu vào Firestore: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Có lỗi xảy ra: ${e.message ?: "Lỗi không xác định"}")
        }
    }
    suspend fun deleteUser(): Result<Unit> {
        return try {
            val user = FirebaseAuth.getInstance().currentUser
                ?: return Result.Error("Người dùng chưa đăng nhập")

            user.delete().await()

            Result.Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Result.Error("Lỗi khi xóa dữ liệu từ Firestore: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Có lỗi xảy ra: ${e.message ?: "Lỗi không xác định"}")
        }
    }
    suspend fun sendResetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.Success(Unit)
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.Error("Email này chưa được đăng ký hoặc đã bị vô hiệu hóa.")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.Error("Định dạng email không hợp lệ.")
        } catch (e: FirebaseNetworkException) {
            Result.Error("Không có kết nối Internet.")
        } catch (e: Exception) {
            Result.Error("Có lỗi xảy ra khi gửi email khôi phục: ${e.message ?: "Lỗi không xác định"}")
        }
    }
    fun logout() {
        auth.signOut()
    }
}