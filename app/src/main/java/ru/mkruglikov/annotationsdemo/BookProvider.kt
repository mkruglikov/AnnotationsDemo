package ru.mkruglikov.annotationsdemo

// Пример книги
internal class BookProvider {
    fun provide(): Book {
        return Book(
            id = "1",
            title = "Название книги",
            author = "Автор книги"
        )
    }
}