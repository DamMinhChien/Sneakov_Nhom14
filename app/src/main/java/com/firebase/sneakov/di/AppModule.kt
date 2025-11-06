package com.firebase.sneakov.di

import androidx.lifecycle.ViewModel
import com.firebase.sneakov.data.api.CloudinaryApi
import com.firebase.sneakov.data.api.LocationApi
import com.firebase.sneakov.data.repository.AuthRepository
import com.firebase.sneakov.data.repository.BrandRepository
import com.firebase.sneakov.data.repository.CartRepository
import com.firebase.sneakov.data.repository.OrderRepository
import com.firebase.sneakov.data.repository.CloudinaryRepository
import com.firebase.sneakov.data.repository.LocationRepository
import com.firebase.sneakov.data.repository.ProductRepository
import com.firebase.sneakov.data.repository.ReviewRepository
import com.firebase.sneakov.data.repository.WishlistRepository
import com.firebase.sneakov.utils.Cloudinary
import com.firebase.sneakov.utils.Provinces
import com.firebase.sneakov.viewmodel.AuthViewModel
import com.firebase.sneakov.viewmodel.BrandViewModel
import com.firebase.sneakov.viewmodel.CartViewModel
import com.firebase.sneakov.viewmodel.OrderViewModel
import com.firebase.sneakov.viewmodel.BrandsNameViewModel
import com.firebase.sneakov.viewmodel.CloudinaryViewModel
import com.firebase.sneakov.viewmodel.ColorViewModel
import com.firebase.sneakov.viewmodel.DetailViewModel
import com.firebase.sneakov.viewmodel.HelperViewModel
import com.firebase.sneakov.viewmodel.LocationViewModel
import com.firebase.sneakov.viewmodel.ProductViewModel
import com.firebase.sneakov.viewmodel.ReviewViewModel
import com.firebase.sneakov.viewmodel.SearchViewModel
import com.firebase.sneakov.viewmodel.UserViewModel
import com.firebase.sneakov.viewmodel.WishlistViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.jvm.java

//val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")
val appModule = module {
    // Logging
    single {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    // Moshi
    single {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    // Cloudinary Retrofit
    single(named("cloudinary")) {
        Retrofit.Builder()
            .baseUrl(Cloudinary.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .build()
    }

    // Backend Retrofit
    single(named("provinces")) {
        Retrofit.Builder()
            .baseUrl(Provinces.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .build()
    }

    // API Service
    single {
        get<Retrofit>(named("cloudinary")).create(CloudinaryApi::class.java)
    }

    single {
        get<Retrofit>(named("provinces")).create(LocationApi::class.java)
    }

    // Firebase Instance
    single { FirebaseFirestore.getInstance() }
    single { FirebaseAuth.getInstance() }

    // Repository
    single { BrandRepository(get()) }
    single { AuthRepository(get(), get()) }
    single { ProductRepository(get()) }
    single { CartRepository(get()) }
    single { OrderRepository(get()) }
    single { ReviewRepository(get(), get()) }

    single { WishlistRepository(get(), get()) }
    single { CloudinaryRepository(get()) }
    single { LocationRepository(get()) }
    // ViewModel
    viewModel { BrandViewModel(get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { UserViewModel(get()) }
    viewModel { ProductViewModel(get()) }
    viewModel { DetailViewModel(get()) }
    viewModel { WishlistViewModel(get(), get()) }
    viewModel { HelperViewModel(get()) }
    viewModel { CloudinaryViewModel(get()) }
    viewModel { LocationViewModel(get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { BrandsNameViewModel(get()) }
    viewModel { ColorViewModel(get()) }
    viewModel { ReviewViewModel(get()) }
    viewModel { CartViewModel(get(), get(), get()) }
    viewModel { OrderViewModel(get(), get(), get()) }

}