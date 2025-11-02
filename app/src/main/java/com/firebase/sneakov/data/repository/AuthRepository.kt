package com.firebase.sneakov.data.repository

import com.firebase.sneakov.data.model.User
import com.firebase.sneakov.data.request.LoginRequest
import com.firebase.sneakov.data.request.RegisterRequest
import com.firebase.sneakov.utils.CollectionName
import com.firebase.sneakov.utils.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class AuthRepository(private val db: FirebaseFirestore, private val auth: FirebaseAuth) {
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
    fun logout() {
        auth.signOut()
    }
}