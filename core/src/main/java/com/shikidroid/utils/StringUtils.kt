package com.shikidroid.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import java.util.regex.Matcher
import java.util.regex.Pattern

object StringUtils {

    /**
     * Пустая строка.
     */
    const val EMPTY = ""

    /**
     * Пробел.
     */
    const val WHITESPACE = " "


    /** регулярное выражение, состоящее только из цифр */
    val DIGITS_REGEX = Regex("[0123456789]")

    /** регулярное выражение, состоящее из цифр и пустой строки*/
    val EMPTY_STRING_DIGITS_REGEX = Regex("^\$|[0123456789]")

    /** регулярное выражение для удаления пустых знаков из строки */
    val EMPTY_STRING_REGEX = Regex("(?m)^\\s*\\r?\\n|\\r?\\n\\s*(?!.*\\r?\\n)")

    /**
     * регулярное выражение для парсинга ссылки
     *
     * вида [character=177824]Ко Ямори[/character] или [url=https://ru.wikipedia.org/wiki/Гяру]Гяру[/url]
     */
    val SHIKIMORI_LINK_REGEX =
        Regex("(\\[{1})([a-z]{1,})(={1})([а-яА-яЁёa-zA-Z0-9:\\/\\\\\\\\.#_|@!?\$%^&;:*()+=<>\"№\\s-]{1,})(\\]{1})([а-яА-яЁёa-zA-Z0-9:\\/\\\\\\\\.#_|@!?\$%^&;:*()+=<>\"№\\s-]{1,})(\\[{1})(\\/{1})([a-z]{1,})(\\]{1})")

    /**
     * регулярное выражние для парсинга спойлеров
     */
    val SPOILER_REGEX = Regex("(\\[spoiler=спойлер\\]{1})")

    /**
     * Регулярное выражение для валидации email
     */
    private const val EMAIL_ADDRESS_REGX =
        "^([a-zA-Z0-9\\-!#\$%&'*+\\/=?^_`{|}~][a-zA-Z0-9!#\$%&'*+\\-\\/=?^_`{|}~.]{1,254}[a-zA-Z0-9!#\$%&'*+\\-\\/=?^_`{|}~]{1})(@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,60}[a-zA-Z0-9]{1})(\\.[a-zA-Z0-9]([a-zA-Z0-9\\-]{0,23})[a-zA-Z0-9])\$"

    /**
     * Цифры для проверки наличия их в строке
     */
    private val DIGITS_CHARS = "0123456789".toCharArray()

    /**
     * Регулярное выражение для проверки ФИО
     */
    private const val FULL_NAME_REGX =
        "^([А-Яа-яЁё]{1,}([\\-]{0,1}[А-Яа-яЁё]){0,}){1,1}(\\s[А-Яа-яЁё]{1,}([\\-]{0,1}[А-Яа-яЁё]){0,}){0,1}(\\s[А-Яа-яЁё]{1,}([\\-]{0,1}[А-Яа-яЁё]){0,}){0,1}\$"

    fun String.deleteEmptySpaces(): String {
        return this.replace(EMPTY_STRING_REGEX, "")
    }

    /**
     * Возвращает null, если строка пустая
     */
    fun String.getNullIfEmpty(): String? {
        return if (this.isEmpty().not()) this else null
    }

    /**
     * Возвращает первую или вторую строку, если они непустные, или вернёт пустую строку
     *
     * @param one первая строка
     * @param two вторая строка
     */
    fun getEmptyIfBothNull(one: String?, two: String?): String {
        if (one.isNullOrEmpty().not()) {
            return one.orEmpty()
        }
        if (one.isNullOrEmpty() && two.isNullOrEmpty().not()) {
            return two.orEmpty()
        }
        return EMPTY
    }

    /**
     * Метод для возврата пары списка
     * кортежа строк с типом ссылки, ссылкой, именем персонажа/человека
     * и строкой аннотации
     *
     * @param text строка текста
     * @param textSize размер текста
     * @param primaryColor цвет основного текста
     * @param annotationColor цвет аннотации
     * @param spoilerColor второй цвет аннотации
     */
    fun getAnnotationString(
        text: String?,
        textSize: TextUnit,
        primaryColor: Color,
        annotationColor: Color,
        spoilerColor: Color = annotationColor
    ): Pair<MutableList<Triple<String, String, String>>, AnnotatedString?> {

        // тип ссылки // ссылка // имя
        val typeLinkName = mutableListOf<Triple<String, String, String>>()

        // готовая строка описания с аннотациями для клика
        var annotatedString: AnnotatedString? = null

        if (text?.trim().isNullOrEmpty().not()) {

            text?.let { str ->

                var string = str

                if (str.contains("[/spoiler]")) {
                    string = str.replace("[/spoiler]", EMPTY)
                }

                // паттерн регулярного выражения
                val pattern = Pattern.compile(SHIKIMORI_LINK_REGEX.pattern)

                // совпадения регулярного выражения в строке
                val matcher = pattern.matcher(string)

                // пока есть совпадения, добавляем группы совпадений в список
                while (matcher.find()) {
                    matcher.group(2)?.let { type ->
                        matcher.group(4)?.let { link ->
                            matcher.group(6)?.let { name ->
                                typeLinkName.add(
                                    Triple(
                                        first = type,
                                        second = link,
                                        third = name
                                    )
                                )
                            }
                        }
                    }
                }

                annotatedString = buildAnnotatedString {

                    // если описание содержит совпадения с регулярным выражением,
                    // то разбиваем строку и добавляем аннотации
                    if (string.contains(SHIKIMORI_LINK_REGEX)) {

                        // список строк из изначальной строки, поделённой по регулярному выражению
                        val stringList =
                            string.split(SHIKIMORI_LINK_REGEX, limit = Int.MAX_VALUE)

                        for (i in 0 until typeLinkName.size) {

                            withStyle(
                                style = SpanStyle(
                                    color = primaryColor,
                                    fontSize = textSize,
                                    fontFamily = FontFamily.Default,
                                    fontWeight = FontWeight.Normal
                                )
                            ) {
                                append(text = stringList[i])
                            }

                            pushStringAnnotation(
                                tag = typeLinkName[i].third,
                                annotation = typeLinkName[i].second
                            )

                            withStyle(
                                style = SpanStyle(
                                    color = annotationColor,
                                    fontSize = textSize,
                                    fontFamily = FontFamily.Default,
                                    fontWeight = FontWeight.Normal
                                )
                            ) {
                                append(typeLinkName[i].third)
                            }
                            pop()

                            if (i == (typeLinkName.size - 1) && stringList[i] != stringList.last()) {
                                withStyle(
                                    style = SpanStyle(
                                        color = primaryColor,
                                        fontSize = textSize,
                                        fontFamily = FontFamily.Default,
                                        fontWeight = FontWeight.Normal
                                    )
                                ) {
                                    append(stringList.last())
                                }
                            }

                        }

                        // в противном случае просто добавляем строку из описания
                    } else {

                        withStyle(
                            style = SpanStyle(
                                color = primaryColor,
                                fontSize = textSize,
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Normal
                            )
                        ) {
                            append(text = string)
                        }

                    }
                }
            }
        }

        return Pair(first = typeLinkName, second = annotatedString)
    }

    /**
     * Метод для возврата списка пар
     * кортежа строк с типом ссылки, ссылкой, именем персонажа/человека
     * и строкой аннотации
     *
     * @param string строка текста
     * @param textSize размер текста
     * @param primaryColor цвет основного текста
     * @param annotationColor цвет аннотации
     * @param spoilerColor второй цвет аннотации
     */
    fun toDescriptionSpoilerAnnotation(
        string: String?,
        textSize: TextUnit,
        primaryColor: Color,
        annotationColor: Color,
        spoilerColor: Color = annotationColor
    ): List<Pair<MutableList<Triple<String, String, String>>, AnnotatedString?>> {

        // список пар кортежа и строки аннотации
        val descSpoilList =
            mutableListOf<Pair<MutableList<Triple<String, String, String>>, AnnotatedString?>>()

        // строка описания
        var description: String? = null

        // строка спойлеров
        var spoiler: String? = null

        when {
            string?.contains(SPOILER_REGEX) == true -> {
                val descSpoil = string.split(SPOILER_REGEX)
                description = descSpoil.first()
                spoiler = descSpoil.last()
            }
            string?.contains("[spoiler]") == true -> {
                val descSpoil = string.split("[spoiler]")
                description = descSpoil.first()
                spoiler = descSpoil.last()
            }
            else -> {
                description = string
            }
        }

        val descriptionPairs = getAnnotationString(
            text = description,
            textSize = textSize,
            primaryColor = primaryColor,
            annotationColor = annotationColor,
            spoilerColor = spoilerColor
        )

        val spoilerPairs = getAnnotationString(
            text = spoiler,
            textSize = textSize,
            primaryColor = primaryColor,
            annotationColor = annotationColor,
            spoilerColor = spoilerColor
        )

        // тип ссылки // ссылка // имя
        val descriptionTypeLinkName = descriptionPairs.first

        // тип ссылки // ссылка // имя
        val spoilerTypeLinkName = spoilerPairs.first

        // готовая строка описания с аннотациями для клика
        val descriptionAnnotated: AnnotatedString? = descriptionPairs.second

        // готовая строка спойлеров с аннотациями для клика
        val spoilerAnnotated: AnnotatedString? = spoilerPairs.second

        descSpoilList.add(descriptionTypeLinkName to descriptionAnnotated)
        descSpoilList.add(spoilerTypeLinkName to spoilerAnnotated)

        return descSpoilList
    }


    /**
     * Удаляет все символы из строки кроме цифр
     *
     * @return строка состоящая только из цифр
     */
    fun String.toOnlyDigits(): String {
        return this.toCharArray().filter { it in DIGITS_CHARS }.joinToString("")
    }

    /**
     * Проверяет строку регулярным вырожением для почты
     *
     * @return true если строка подходит
     */
    fun String.isValidEmail(): Boolean {
        val matcher: Matcher = Pattern.compile(EMAIL_ADDRESS_REGX).matcher(this)
        matcher.find()
        val userName = runCatching { matcher.toMatchResult().group(1) }.getOrNull()
        val host = runCatching { matcher.toMatchResult().group(2) }.getOrNull()
        val domain = runCatching { matcher.toMatchResult().group(3) }.getOrNull()
        return matcher.matches() && userName.orEmpty().contains("..", false).not()
    }

    /**
     * Функция для проверки содержит ли строка только символ ноль
     */
    fun String.containsOnlyZero(): Boolean {
        return this.filter { it != '0' }.isEmpty()
    }

    /**
     * Возвращает индекс первого элемента в строке, который не равен символу ноля
     * либо вернёт ноль, если такого элемента не найдено
     */
    fun String.firstNotZeroIndex(): Int {
        val char = this.find { char -> char != '0' }
        return char?.let { c -> indexOf(c) } ?: 0
    }

    /**
     * Возвращает строку с первой заглавной буквой и удалёнными подчёркиваниями
     */
    fun String.toUppercaseAndDeleteUnderscore(): String {
        return this.replaceFirstChar {
            it.uppercase()
        }.replace(oldChar = '_', newChar = ' ')
    }
}