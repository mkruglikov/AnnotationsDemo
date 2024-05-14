# AnnotationsDemo
 
Простейший пример использования аннотаций и KSP для сериализации JSON, используя [kotlin poet](https://square.github.io/kotlinpoet/)

В модуле `annotations` лежит процессор, который сгенерирует extension функцию `.toJson()` для каждого класса, помеченного аннотацией `@JsonClass`