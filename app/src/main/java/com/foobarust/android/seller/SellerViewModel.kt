package com.foobarust.android.seller

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData

class SellerViewModel @ViewModelInject constructor() : ViewModel() {

    val promotionItems = liveData {
        emit(listOf(
            PromotionItem(photoUrl = "https://firebasestorage.googleapis.com/v0/b/foobar-group-delivery-app.appspot.com/o/test_images%2Fsample_image_1.jpg?alt=media&token=4453e3c8-de2e-4863-8e3f-e9347f73c2a0"),
            PromotionItem(photoUrl = "https://firebasestorage.googleapis.com/v0/b/foobar-group-delivery-app.appspot.com/o/test_images%2Fsample_image_2.jpg?alt=media&token=3c16ab78-aa1b-4c59-b918-efac5e69f17b"),
            PromotionItem(photoUrl = "https://firebasestorage.googleapis.com/v0/b/foobar-group-delivery-app.appspot.com/o/test_images%2Fsample_image_3.jpg?alt=media&token=4c5e8ace-aa3e-40e5-81c1-46597e128b9b"),
            PromotionItem(photoUrl = "https://firebasestorage.googleapis.com/v0/b/foobar-group-delivery-app.appspot.com/o/test_images%2Fsample_image_4.jpg?alt=media&token=ccc9f16a-f2f1-4c8e-865a-89daeb49121d"),
            PromotionItem(photoUrl = "about:blank")
        ))
    }
}