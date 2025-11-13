package com.shikidroid.resourceprovider

import android.content.ContentResolver
import android.content.Context
import android.util.TypedValue
import androidx.annotation.*
import androidx.core.content.ContextCompat
import java.io.InputStream
import javax.inject.Inject

/**
 * Класс - реализация поставщика ресурсов
 *
 * @property context контекст [Context] приложения
 *
 * @see ResourceProvider
 */
class ResourceProviderImpl @Inject constructor(
    private val context: Context
) : ResourceProvider {

    override fun getString(@StringRes resId: Int) =
        try {
            context.getString(resId)
        } catch (e: Exception) {
            ""
        }

    override fun getString(@StringRes resId: Int, vararg params: Any) =
        try {
            context.getString(resId, *params)
        } catch (e: Exception) {
            ""
        }

    override fun getStringArray(@ArrayRes resId: Int): Array<String> =
        try {
            context.resources.getStringArray(resId)
        } catch (e: Exception) {
            emptyArray()
        }

    override fun getDrawable(@DrawableRes resId: Int) =
        try {
            ContextCompat.getDrawable(context, resId)
        } catch (e: Exception) {
            null
        }

    override fun getColor(@ColorRes resId: Int) =
        try {
            ContextCompat.getColor(context, resId)
        } catch (e: Exception) {
            0
        }

    override fun getIdentifier(name: String, defType: String): Int =
        context
            .resources
            .getIdentifier(name, defType, context.packageName)

    override fun getResourceEntryName(id: Int): String =
        context
            .resources
            .getResourceEntryName(id)

    override fun getQuantityString(
        @PluralsRes resId: Int,
        quantity: Int,
        vararg params: Any?
    ): String =
        context
            .resources
            .getQuantityString(resId, quantity, *params)

    override fun getIntegerArray(resId: Int): Array<Int> =
        context
            .resources
            .getIntArray(resId)
            .toTypedArray()

    override fun getAsset(fileName: String): InputStream =
        context
            .resources
            .assets
            .open(fileName)

    override fun getRawResource(rawId: Int): InputStream =
        context
            .resources
            .openRawResource(rawId)

    override fun getColorFromAttr(@AttrRes value: Int): Int =
        TypedValue().apply {
            context.theme.resolveAttribute(value, this, true)
        }.data

    override fun getInteger(id: Int): Int =
        context.resources.getInteger(id)

    override fun getDimensionPixelSize(@DimenRes id: Int): Int =
        context.resources.getDimensionPixelSize(id)

    override fun getValue(id: Int, outValue: TypedValue, resolveRefs: Boolean) =
        context.resources.getValue(id, outValue, resolveRefs)

    override fun getUriString(@AnyRes id: Int): String =
        ContentResolver.SCHEME_ANDROID_RESOURCE + URI_PATH +
                context.resources.getResourcePackageName(id) + SLASH +
                context.resources.getResourceTypeName(id) + SLASH +
                context.resources.getResourceEntryName(id)

    companion object {
        const val URI_PATH = "://"
        const val SLASH = '/'
    }
}