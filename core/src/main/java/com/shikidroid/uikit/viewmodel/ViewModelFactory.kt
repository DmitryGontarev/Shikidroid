package com.shikidroid.uikit.viewmodel

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Обобщённая фабрика для создания вью модели
 * Её использует [ViewModelProvider]
 *
 * @param viewModelCreator лямбда для создания экземпляра вью модели
 * @param ViewModelDo лямбда для запуска методов вью модели после создания её экземпляра
 */
class ViewModelProviderFactory<VM : ViewModel>(
    private val viewModelCreator: () -> VM,
    private val ViewModelDo: ((VM) -> Unit)? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return (viewModelCreator.invoke() as T).apply {
            ViewModelDo?.invoke(this as VM)
        }
    }
}

/**
 * Метод создаёт свойство для вьюмодели с помощью [ViewModelProviderFactory].
 * Если не передать [viewModelCreator], то создаст вью модель по указанному типу, но в этом случае
 * конструктор вьюмодели должен быть без параметров
 *
 * @param VM тип вьюмодели
 * @param viewModelCreator лямбда для создания экземпляра вьюмодели
 * @param viewModelDo лямбда для запуска методов вью модели после создания её экземпляра
 */
inline fun <reified VM : ViewModel> ViewModelStoreOwner.viewModelFactory(
    noinline viewModelCreator: (() -> VM)? = null,
    noinline viewModelDo: ((VM) -> Unit)? = null
) = viewModelFactory(VM::class, viewModelCreator, viewModelDo)

/**
 * Метод создаёт свойство для вьюмодели с помощью [ViewModelProviderFactory].
 * Если не передать [viewModelCreator], то создаст вью модель по указанному типу, но в этом случае
 * конструктор вьюмодели должен быть без параметров
 *
 * @param VM тип вьюмодели
 * @param viewModelClass класс создаваемой ViewModel
 * @param viewModelCreator лямбда для создания экземпляра вьюмодели
 * @param viewModelDo лямбда для запуска методов вью модели после создания её экземпляра
 */
fun <VM : ViewModel> ViewModelStoreOwner.viewModelFactory(
    viewModelClass: KClass<VM>,
    viewModelCreator: (() -> VM)? = null,
    viewModelDo: ((VM) -> Unit)? = null
) = ViewModelLazy(
    viewModelClass,
    { viewModelStore },
    { ViewModelProviderFactory(viewModelCreator ?: { viewModelClass.createInstance() }, viewModelDo)  }
)

/**
 * Метод создаёт свойство для вьюмодели с помощью [ViewModelProviderFactory] из хранилища активити.
 * Если не передать [viewModelCreator], то создаст вью модель по указанному типу, но в этом случае
 * конструктор вьюмодели должен быть без параметров
 *
 * @param VM тип вьюмодели
 * @param viewModelCreator лямбда для создания экземпляра вьюмодели (может быть null, тогда попытается создать объект сам)
 * @param viewModelDo хук для выполнения действий после создания экземпляра вьюмодели, например, загрузки элементов
 */
inline fun <reified VM : ViewModel> ComponentActivity.componentActivityViewModelFactory(
    noinline viewModelCreator: (() -> VM)? = null,
    noinline viewModelDo: ((VM) -> Unit)? = null
) = componentActivityViewModelFactory(VM::class, viewModelCreator, viewModelDo)

/**
 * Метод создаёт свойство для вьюмодели с помощью [ViewModelProviderFactory] из хранилища активити.
 * Если не передать [viewModelCreator], то создаст вью модель по указанному типу, но в этом случае
 * конструктор вьюмодели должен быть без параметров
 *
 * @param VM тип вьюмодели
 * @param viewModelClass класс создаваемой ViewModel
 * @param viewModelCreator лямбда для создания экземпляра вьюмодели
 * @param viewModelDo лямбда для запуска методов вью модели после создания её экземпляра
 */
fun <VM: ViewModel> ComponentActivity.componentActivityViewModelFactory(
    viewModelClass: KClass<VM>,
    viewModelCreator: (() -> VM)? = null,
    viewModelDo: ((VM) -> Unit)? = null
) = ViewModelLazy(
    viewModelClass,
    { this.viewModelStore },
    { ViewModelProviderFactory(viewModelCreator ?: { viewModelClass.createInstance() }, viewModelDo) }
)