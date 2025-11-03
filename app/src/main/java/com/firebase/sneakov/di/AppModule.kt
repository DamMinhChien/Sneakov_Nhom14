package com.firebase.sneakov.di

import com.firebase.sneakov.data.repository.AuthRepository
import com.firebase.sneakov.data.repository.BrandRepository
import com.firebase.sneakov.data.repository.ProductRepository
import com.firebase.sneakov.data.repository.WishlistRepository
import com.firebase.sneakov.viewmodel.AuthViewModel
import com.firebase.sneakov.viewmodel.BrandViewModel
import com.firebase.sneakov.viewmodel.DetailViewModel
import com.firebase.sneakov.viewmodel.HelperViewModel
import com.firebase.sneakov.viewmodel.ProductViewModel
import com.firebase.sneakov.viewmodel.UserViewModel
import com.firebase.sneakov.viewmodel.WishlistViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

//val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")
val appModule = module {

    // Firebase Instance
    single { FirebaseFirestore.getInstance() }
    single { FirebaseAuth.getInstance() }

    // Repository
    single { BrandRepository(get()) }
    single { AuthRepository(get(), get()) }
    single { ProductRepository(get()) }
    single { WishlistRepository(get(), get()) }
    // ViewModel
    viewModel { BrandViewModel(get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { UserViewModel(get()) }
    viewModel { ProductViewModel(get()) }
    viewModel { DetailViewModel(get()) }
    viewModel { WishlistViewModel(get(), get()) }
    viewModel { HelperViewModel(get()) }

}