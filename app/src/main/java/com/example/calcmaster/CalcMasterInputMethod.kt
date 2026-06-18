package com.example.calcmaster

import android.inputmethodservice.InputMethodService
import android.view.View
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.SavedStateRegistry
import com.example.calcmaster.ui.theme.CalcMasterTheme

class CalcMasterInputMethod : InputMethodService(), ViewModelStoreOwner, SavedStateRegistryOwner {

    // --- AGREGA ESTO PARA SOLUCIONAR EL ERROR ---
    override val lifecycle: androidx.lifecycle.Lifecycle
        get() = androidx.lifecycle.LifecycleRegistry(this)
    // --------------------------------------------

    private val mViewModelStore = ViewModelStore()
    private val mSavedStateRegistryController = SavedStateRegistryController.create(this)
    
    // ... el resto de tu código sigue igual abajo ...


    private val mViewModelStore = ViewModelStore()
    private val mSavedStateRegistryController = SavedStateRegistryController.create(this)

    override val viewModelStore: ViewModelStore
        get() = mViewModelStore

    override val savedStateRegistry: SavedStateRegistry
        get() = mSavedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        mSavedStateRegistryController.performRestore(null)
        super.onCreate()
    }

    override fun onCreateInputView(): View {
        return ComposeView(this).apply {
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
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewModelStore.clear()
    }
}
