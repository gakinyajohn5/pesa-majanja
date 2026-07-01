package com.pesamjanja.app.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.pesamjanja.app.PesaMjanjaApplication

/** Resolves the Application instance from the current Compose context. */
@Composable
fun rememberPesaApp(): PesaMjanjaApplication {
    val context = LocalContext.current.applicationContext
    return context as PesaMjanjaApplication
}
