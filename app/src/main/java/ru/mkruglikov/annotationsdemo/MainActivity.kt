package ru.mkruglikov.annotationsdemo

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val book: Book = BookProvider().provide()

        // Используем сгенерированный extension
        val bookJson: String = book.toJson()

        findViewById<TextView>(R.id.textView).text = bookJson
    }
}