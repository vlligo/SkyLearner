package com.example.astroguessr

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize
import com.example.astroguessr.data.Star

@Keep
@IgnoreExtraProperties
@Parcelize
data class Quiz(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var questions: List<Question> = emptyList(),
    var constellation: String = ""
) : Parcelable {
    constructor() : this("", "", "", emptyList(), "")
}

@Keep
@Parcelize
data class Question(
    var targetStarId: Int = 0,
    var options: List<Star> = emptyList()
) : Parcelable {
    constructor() : this(0, emptyList())
}