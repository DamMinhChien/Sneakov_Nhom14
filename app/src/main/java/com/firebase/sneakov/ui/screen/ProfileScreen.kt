package com.firebase.sneakov.ui.screen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.firebase.sneakov.R
import com.firebase.sneakov.data.model.Address
import com.firebase.sneakov.data.model.District
import com.firebase.sneakov.data.model.Province
import com.firebase.sneakov.data.model.Ward
import com.firebase.sneakov.data.request.UpdateUserRequest
import com.firebase.sneakov.ui.compose.Dialog
import com.firebase.sneakov.ui.compose.RefreshableLayout
import com.firebase.sneakov.viewmodel.AuthViewModel
import com.firebase.sneakov.viewmodel.CloudinaryViewModel
import com.firebase.sneakov.viewmodel.LocationViewModel
import com.firebase.sneakov.viewmodel.UserViewModel
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Camera
import compose.icons.fontawesomeicons.solid.Eye
import compose.icons.fontawesomeicons.solid.EyeSlash
import compose.icons.fontawesomeicons.solid.Save
import compose.icons.fontawesomeicons.solid.SignOutAlt
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel(),
    cloudinaryViewModel: CloudinaryViewModel = koinViewModel(),
    locationViewModel: LocationViewModel = koinViewModel(),
    onNavigateToAuth: () -> Unit
) {
    val context = LocalContext.current

    val authState by authViewModel.uiState.collectAsState()
    val userState by userViewModel.uiState.collectAsState()
    val cloudinaryState by cloudinaryViewModel.uiState.collectAsState()
    val locationState by locationViewModel.uiState.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var showDialogNav by remember { mutableStateOf(false) }

    var lastAction by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        userViewModel.fetchCurrentUser()
    }

    LaunchedEffect(authState.data, authState.error) {
        when {
            authState.data != null -> {
                when (lastAction) {
                    "update" -> Toast.makeText(context, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show()
                    "changePassword" -> Toast.makeText(context, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
                    "delete" -> showDialogNav = true
                    "logout" -> {
                        Toast.makeText(context, "Đăng xuất thành công", Toast.LENGTH_SHORT).show()
                        onNavigateToAuth()
                    }
                }
            }
            authState.error != null -> {
                val message = when (lastAction) {
                    "update" -> "Lỗi khi lưu thông tin"
                    "changePassword" -> "Lỗi khi đổi mật khẩu"
                    "delete" -> "Lỗi khi xóa tài khoản"
                    else -> "Lỗi: ${authState.error}"
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                authViewModel.dismissError()
            }
        }
    }

    val user = userState.data

    // --- State ---
    var name by remember(user) { mutableStateOf(user?.name.orEmpty()) }
    var phone by remember(user) { mutableStateOf(user?.phone.orEmpty()) }
    var avatarUrl by remember(user) { mutableStateOf(user?.avatarUrl.orEmpty()) }
    var province by remember(user) { mutableStateOf(user?.address?.province.orEmpty()) }
    var district by remember(user) { mutableStateOf(user?.address?.district.orEmpty()) }
    var municipality by remember(user) { mutableStateOf(user?.address?.municipality.orEmpty()) }
    var detail by remember(user) { mutableStateOf(user?.address?.detail.orEmpty()) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    var showPasswordFields by remember { mutableStateOf(false) }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var newPasswordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    var showOldPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    var provinces by remember { mutableStateOf<List<Province>>(emptyList()) }
    var districts by remember { mutableStateOf<List<District>>(emptyList()) }
    var wards by remember { mutableStateOf<List<Ward>>(emptyList()) }

    var provinceExpanded by remember { mutableStateOf(false) }
    var districtExpanded by remember { mutableStateOf(false) }
    var wardExpanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                avatarUrl = uri.toString()
                cloudinaryViewModel.uploadImage(context = context, uri = uri)
            }
        }
    )

    LaunchedEffect(cloudinaryState.data, cloudinaryState.error) {
        when {
            cloudinaryState.data != null -> {
                avatarUrl = cloudinaryState.data!!
                userState.data?.let { user ->
                    val address = Address(province, district, municipality, detail)
                    val request = UpdateUserRequest(name, phone, avatarUrl, address)
                    lastAction = "update"
                    authViewModel.updateUser(request)
                }
                Toast.makeText(context, "Upload ảnh thành công", Toast.LENGTH_SHORT).show()
            }

            cloudinaryState.error != null -> {
                Toast.makeText(context, cloudinaryState.error, Toast.LENGTH_LONG).show()
                cloudinaryViewModel.dismissError()
            }
        }
    }

    LaunchedEffect(Unit) { locationViewModel.getProvinces() }

    LaunchedEffect(locationState.data) {
        when (val data = locationState.data) {
            is List<*> -> {
                when {
                    data.firstOrNull() is Province -> provinces = data.filterIsInstance<Province>()
                    data.firstOrNull() is District -> districts = data.filterIsInstance<District>()
                    data.firstOrNull() is Ward -> wards = data.filterIsInstance<Ward>()
                }
            }
        }
    }

    RefreshableLayout(
        isRefreshing = userState.isLoading || authState.isLoading || cloudinaryState.isLoading || locationState.isLoading,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        onRefresh = { userViewModel.fetchCurrentUser() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (user == null) {
                Text("Không có dữ liệu người dùng", modifier = Modifier.align(Alignment.Center))
                return@Box
            }

            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- Avatar ---
                Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                    Image(
                        painter = rememberAsyncImagePainter(model = avatarUrl.ifBlank { R.drawable.men }),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            .clickable { imagePicker.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            FontAwesomeIcons.Solid.Camera,
                            contentDescription = "Chỉnh sửa ảnh",
                            tint = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))
                Text(
                    "Đã tham gia vào ngày ${
                        SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN")).format(user.createdAt)
                    }",
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(12.dp))

                // --- Email ---
                OutlinedTextField(
                    value = user.email,
                    onValueChange = {},
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, null) },
                    enabled = false,
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // --- Họ tên ---
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = if (it.isBlank()) "Không được để trống" else null
                    },
                    label = { Text("Họ và tên") },
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    isError = nameError != null,
                    supportingText = { nameError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth()
                )

                // --- Số điện thoại ---
                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        phoneError = when {
                            it.isBlank() -> "Không được để trống"
                            !Regex("^0\\d{9}$").matches(it) -> "Số điện thoại không hợp lệ"
                            else -> null
                        }
                    },
                    label = { Text("Số điện thoại") },
                    leadingIcon = { Icon(Icons.Default.Phone, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = phoneError != null,
                    supportingText = { phoneError?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth()
                )

                // --- Nút lưu thông tin ---
                Button(
                    onClick = {
                        nameError = if (name.isBlank()) "Không được để trống" else null
                        phoneError = when {
                            phone.isBlank() -> "Không được để trống"
                            !Regex("^0\\d{9}$").matches(phone) -> "Số điện thoại không hợp lệ"
                            else -> null
                        }
                        if (nameError == null && phoneError == null) {
                            val address = Address(province, district, municipality, detail)
                            val request = UpdateUserRequest(name, phone, avatarUrl, address)
                            lastAction = "update"
                            authViewModel.updateUser(request)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(FontAwesomeIcons.Solid.Save, null, Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Lưu thông tin cá nhân")
                }

                // --- Đổi mật khẩu ---
                OutlinedButton(
                    onClick = { showPasswordFields = !showPasswordFields },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        if (showPasswordFields) Icons.Default.KeyboardArrowUp else Icons.Default.Lock,
                        null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (showPasswordFields) "Ẩn đổi mật khẩu" else "Đổi mật khẩu")
                }

                AnimatedVisibility(showPasswordFields) {
                    Column {
                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = oldPassword,
                            onValueChange = { oldPassword = it },
                            label = { Text("Mật khẩu cũ") },
                            leadingIcon = { Icon(Icons.Default.Lock, null) },
                            trailingIcon = {
                                IconButton(onClick = { showOldPassword = !showOldPassword }) {
                                    Icon(
                                        if (showOldPassword) FontAwesomeIcons.Solid.Eye
                                        else FontAwesomeIcons.Solid.EyeSlash, null
                                    )
                                }
                            },
                            visualTransformation =
                                if (showOldPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = {
                                newPassword = it
                                newPasswordError = when {
                                    it.isBlank() -> "Không được để trống"
                                    it.length < 6 -> "Tối thiểu 6 ký tự"
                                    else -> null
                                }
                                confirmPasswordError =
                                    if (confirmPassword.isNotBlank() && confirmPassword != newPassword)
                                        "Mật khẩu xác nhận không khớp" else null
                            },
                            label = { Text("Mật khẩu mới") },
                            leadingIcon = { Icon(Icons.Default.Lock, null) },
                            trailingIcon = {
                                IconButton(onClick = { showNewPassword = !showNewPassword }) {
                                    Icon(
                                        if (showNewPassword) FontAwesomeIcons.Solid.Eye
                                        else FontAwesomeIcons.Solid.EyeSlash, null
                                    )
                                }
                            },
                            visualTransformation =
                                if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = newPasswordError != null,
                            supportingText = {
                                newPasswordError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                confirmPasswordError =
                                    if (it != newPassword) "Mật khẩu xác nhận không khớp" else null
                            },
                            label = { Text("Xác nhận mật khẩu mới") },
                            leadingIcon = { Icon(Icons.Default.Check, null) },
                            trailingIcon = {
                                IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                    Icon(
                                        if (showConfirmPassword) FontAwesomeIcons.Solid.Eye
                                        else FontAwesomeIcons.Solid.EyeSlash, null
                                    )
                                }
                            },
                            visualTransformation =
                                if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = confirmPasswordError != null,
                            supportingText = {
                                confirmPasswordError?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {
                                newPasswordError = when {
                                    newPassword.isBlank() -> "Không được để trống"
                                    newPassword.length < 6 -> "Tối thiểu 6 ký tự"
                                    else -> null
                                }
                                confirmPasswordError =
                                    if (confirmPassword != newPassword) "Mật khẩu xác nhận không khớp"
                                    else null

                                if (newPasswordError == null && confirmPasswordError == null) {
                                    lastAction = "changePassword"
                                    authViewModel.changePassword(oldPassword, newPassword)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(FontAwesomeIcons.Solid.Save, null, Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Cập nhật mật khẩu")
                        }
                    }
                }

                // --- Đăng xuất ---
                OutlinedButton(
                    onClick = {
                        lastAction = "logout"
                        authViewModel.logout()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.error)
                ) {
                    Icon(
                        FontAwesomeIcons.Solid.SignOutAlt,
                        contentDescription = "Đăng xuất",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Đăng xuất", color = MaterialTheme.colorScheme.error)
                }

                // --- Xóa tài khoản ---
                TextButton(
                    onClick = { showDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Xóa tài khoản")
                    Spacer(Modifier.width(8.dp))
                    Text("Xóa tài khoản", color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.height(100.dp))
            }

            // --- Dialog xác nhận xóa ---
            if (showDialog) {
                Dialog(
                    showDialog = showDialog,
                    title = "Xác nhận",
                    onDismiss = { showDialog = false },
                    onConfirm = {
                        showDialog = false
                        lastAction = "delete"
                        authViewModel.deleteUser()
                    }
                ) {
                    Text("Bạn có chắc muốn xóa tài khoản ${user.name} này không?")
                }
            }

            // --- Dialog điều hướng ---
            if (showDialogNav) {
                Dialog(
                    showDialog = showDialogNav,
                    title = "Thông báo",
                    onDismiss = {
                        showDialogNav = false
                        onNavigateToAuth()
                    },
                    onConfirm = {
                        showDialogNav = false
                        onNavigateToAuth()
                    }
                ) {
                    Text("Đã xóa tài khoản thành công, chuyển đến màn hình đăng nhập!")
                }
            }
        }
    }
}
