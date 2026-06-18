package com.example.calcmaster

import android.inputmethodservice.InputMethodService
import android.view.View
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.calcmaster.ui.theme.CalcMasterTheme

class CalcMasterInputMethod : InputMethodService(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private var registryLifecycle: LifecycleRegistry? = null
    private var storeViewModel: ViewModelStore? = null
    private var controllerSavedState: SavedStateRegistryController? = null

    override val lifecycle: Lifecycle
        get() = registryLifecycle ?: LifecycleRegistry(this).also { registryLifecycle = it }

    override val viewModelStore: ViewModelStore
        get() = storeViewModel ?: ViewModelStore().also { storeViewModel = it }

    override val savedStateRegistry: SavedStateRegistry
        get() = controllerSavedState?.savedStateRegistry ?: run {
            val controller = SavedStateRegistryController.create(this)
            controllerSavedState = controller
            controller.savedStateRegistry
        }

    override fun onCreate() {
        super.onCreate()
        // Inicialización segura del estado interno antes de tocar cualquier vista
        if (controllerSavedState == null) {
            controllerSavedState = SavedStateRegistryController.create(this)
        }
        controllerSavedState?.performRestore(null)
        registryLifecycle?.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onCreateInputView(): View {
        return ComposeView(this).apply {
            // Vinculamos de forma segura los dueños del árbol de la vista
            setViewTreeLifecycleOwner(this@CalcMasterInputMethod)
            setViewTreeViewModelStoreOwner(this@CalcMasterInputMethod)
            setViewTreeSavedStateRegistryOwner(this@CalcMasterInputMethod)
            
            setContent {
                CalcMasterTheme {
                    HeliosSystemKeyboard(
                        onInputText = { text -> 
                            currentInputConnection?.commitText(text, 1) 
                        },
                        onBackspace = { 
                            currentInputConnection?.deleteSurroundingText(1, 0) 
                        },
                        onPanic = { 
                            requestHideSelf(0) 
                        }
                    )
                }
            }
        }.also {
            // Activamos el ciclo de vida de manera segura
            registryLifecycle?.handleLifecycleEvent(Lifecycle.Event.ON_START)
            registryLifecycle?.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
    }

    override fun onDestroy() {
        registryLifecycle?.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        registryLifecycle?.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        registryLifecycle?.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        storeViewModel?.clear()
        super.onDestroy()
    }
}
