package com.firebase.sneakov.viewmodel

import androidx.lifecycle.viewModelScope
import com.firebase.sneakov.data.model.Review
import com.firebase.sneakov.data.repository.ReviewRepository
import com.firebase.sneakov.utils.BaseViewModel
import com.firebase.sneakov.utils.Result
import kotlinx.coroutines.launch

class ReviewViewModel(private val repo: ReviewRepository) : BaseViewModel<List<Review>>() {
    fun fetchReviewsByProductId(productId: String) {
        viewModelScope.launch {
            setLoading(true)
            when (val result = repo.getReviewsByProduct(productId)) {
                is Result.Success -> {
                    setData(result.data)
                    setAction("get")
                }

                is Result.Error -> setError(result.message)
            }
            setLoading(false)
        }
    }

    fun createReview(review: Review) {
        viewModelScope.launch {
            setLoading(true)
            when (val result = repo.createReview(review)) {
                is Result.Success -> {
                    fetchReviewsByProductId(review.productId)
                    setAction("add")
                }

                is Result.Error -> setError(result.message)
            }
            setLoading(false)
        }
    }

    fun updateReview(review: Review) {
        viewModelScope.launch {
            setLoading(true)
            when (val result = repo.updateReview(review)) {
                is Result.Success -> {
                    fetchReviewsByProductId(review.productId)
                    setAction("update")
                }

                is Result.Error -> setError(result.message)
            }
            setLoading(false)
        }
    }

    fun removeReview(productId: String) {
        viewModelScope.launch {
            setLoading(true)
            when (val result = repo.removeReview(productId)) {
                is Result.Success -> {
                    fetchReviewsByProductId(productId)
                    setAction("delete")
                }

                is Result.Error -> setError(result.message)
            }
            setLoading(false)
        }
    }
}