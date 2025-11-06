package com.firebase.sneakov.data.mapper

import com.firebase.sneakov.data.model.ShippingAddress
import com.firebase.sneakov.data.model.User

/**
 * Chuyển đổi thông tin từ đối tượng User thành một đối tượng ShippingAddress.
 * Dùng để khởi tạo địa chỉ giao hàng mặc định cho một đơn hàng.
 */
fun User.toShippingAddress(): ShippingAddress {
    return ShippingAddress(
        name = this.name, // Lấy tên người dùng làm tên người nhận mặc định
        phone = this.phone, // Lấy SĐT người dùng làm SĐT người nhận mặc định
        province = this.address.province,
        district = this.address.district,
        commune = this.address.municipality, // Ánh xạ từ municipality -> commune
        detail = this.address.detail
    )
}
