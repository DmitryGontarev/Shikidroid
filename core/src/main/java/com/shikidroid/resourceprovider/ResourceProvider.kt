package com.shikidroid.resourceprovider

import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.AnyRes
import androidx.annotation.ArrayRes
import androidx.annotation.AttrRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.IntegerRes
import androidx.annotation.PluralsRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import java.io.InputStream

/**
 * Интерфейс - поставщик ресурсов приложения
 * Дает доступ к ресурсам приложения
 */
interface ResourceProvider {

    /**
     * Получения строки по айди ресурса
     * @param resId - id строки
     */
    fun getString(@StringRes resId: Int): String

    /**
     * @param resId - id строки
     * @param params - параметры для форматирования строки
     */
    fun getString(@StringRes resId: Int, vararg params: Any): String

    /**
     * Получение массива строк по айди ресурса
     *
     * @param resId - id ресурса
     */
    fun getStringArray(@ArrayRes resId: Int): Array<String>

    /**
     * Получение drawable по айди ресурса
     * @param resId - id drawable
     */
    fun getDrawable(@DrawableRes resId: Int): Drawable?

    /**
     * Получение цвета по айди ресурса
     * @param resId - id ресурса
     */
    fun getColor(@IdRes resId: Int): Int

    /**
     * Получение идентификатора ресурса по имени и типу ресурса
     * @param name - имя ресурса
     * @param defType - тип ресурса
     */
    @AnyRes
    fun getIdentifier(name: String, defType: String): Int

    /**
     * Возвращает имя для идентификатора ресурса, по [id]
     *
     * @param id - идентификатор ресурса для которого необходимо получить имя ресурса.
     */
    fun getResourceEntryName(@AnyRes id: Int): String

    /**
     * Возвращает отформатированную quantity string
     * @param resId - айди ресурса R.plurals
     * @param quantity - число
     * @param params - параметры для форматирования
     */
    fun getQuantityString(@PluralsRes resId: Int, quantity: Int, vararg params: Any?): String

    /**
     * Возвращает массив целых чисел по id ресурса
     * @param resId - id ресурса
     */
    fun getIntegerArray(@ArrayRes resId: Int): Array<Int>

    /**
     * Получения asset-а
     * @param fileName - имя файла
     */
    fun getAsset(fileName: String): InputStream

    /**
     * Получение raw-ресурса
     * @param rawId идентификатор ресурса R.raw
     * @return [InputStream]
     */
    fun getRawResource(@RawRes rawId: Int): InputStream

    /**
     * Возворащает цвет из атрибута.
     * @param value атрибут.
     */
    fun getColorFromAttr(@AttrRes value: Int): Int

    /**
     * Возвращает число из ресусрсов по его идентификтаору
     *
     * @param id идентификатор ресурса
     * @return значение числа, указзоного ресурса
     */
    fun getInteger(@IntegerRes id: Int): Int

    /**
     * Возвращает размер в пикселях из ресусрсов по его идентификтаору
     *
     * @param id идентификатор ресурса
     * @return размер в пикселях указанного ресурса
     */
    fun getDimensionPixelSize(@DimenRes id: Int): Int

    /**
     * Возвращает сырые данные, ассоциированные с указанным идентификатором
     *
     * @param id идентификатор ресурса
     * @param outValue холдер для результата
     * @param resolveRefs резолвить ли ссылки в ресурсе
     */
    fun getValue(@AnyRes id: Int, outValue: TypedValue, resolveRefs: Boolean)

    /**
     * Возвращает путь до ресурса
     *
     * @param id идентификатор ресурса
     */
    fun getUriString(@AnyRes id: Int): String

    companion object {

        /**
         * Тип ресурса drawable
         */
        const val RES_TYPE_DRAWABLE = "drawable"
    }
}