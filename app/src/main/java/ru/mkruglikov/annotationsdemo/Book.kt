package ru.mkruglikov.annotationsdemo

import ru.mkruglikov.annotations.JsonClass

@JsonClass
data class Book(
    val id: String,
    val title: String,
    val author: String,
)
