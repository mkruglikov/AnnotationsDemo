package ru.mkruglikov.annotations

import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

internal class JsonClassAnnotationsProcessor(
    private val environment: SymbolProcessorEnvironment, // Окружение в котором работает процессор
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        // Находим все символы, помеченные аннотацией. Символы — классы/функции/поля
        val symbols = resolver.getSymbolsWithAnnotation(ANNOTATION_CLASS_NAME)

        // Отбираем те, которые сейчас нельзя обработать
        val result = symbols.filter { !it.validate() }.toList()

        // К каждому найденному символу применяем Visitor
        symbols
            .filter { it is KSClassDeclaration && it.validate() }
            .forEach { it.accept(Visitor(environment), Unit) }

        return result
    }

    // "Посетитель" разных типов символов
    private class Visitor(val environment: SymbolProcessorEnvironment) : KSVisitorVoid() {

        // Обрабатываем каждый помеченный аннотацией класс
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            // Для имени нового файла берём название помеченного класса (Book) и добавляем к нему "JsonExt"
            val newFileName = classDeclaration.simpleName.asString().plus("JsonExt")

            // Создаём файл с помощью Kotlin Poet
            val fileSpec = FileSpec
                .builder(
                    packageName = classDeclaration.packageName.asString(),
                    fileName = newFileName
                )
                .addFunction(buildToJsonFunction(classDeclaration))
                .build()

            // Записываем созданный файл, используя CodeGenerator из окружения
            fileSpec.writeTo(environment.codeGenerator, aggregating = false)
        }

        private fun buildToJsonFunction(classDeclaration: KSClassDeclaration): FunSpec {
            // Достаём поля из аннотированного класса
            val properties = classDeclaration.getDeclaredProperties()

            // Создаём функцию
            return FunSpec.builder("toJson")
                .receiver(classDeclaration.toClassName())
                .returns(String::class)

                // Создаём StringBuilder и открываем json объект
                .addCode("return StringBuilder().appendLine(\"{\")")

                // Для каждого поля класса добавляем строку с именем этого поля и его строковым значением
                .apply {
                    properties.forEach { property ->
                        addCode("\n    .appendLine(\"   ${property.simpleName.getShortName()} = \${${property.simpleName.getShortName()}.toString()},\")")
                    }
                }

                // Закрываем json объект и собираем всё в одну строку
                .addCode("\n    .appendLine(\"}\").toString()")
                .build()
        }
    }

    private companion object {
        // Полное имя класса с пакетом
        val ANNOTATION_CLASS_NAME = JsonClass::class.qualifiedName!!
    }
}