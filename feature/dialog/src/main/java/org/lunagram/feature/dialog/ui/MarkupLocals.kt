package org.lunagram.feature.dialog.ui

import androidx.compose.runtime.staticCompositionLocalOf
import org.lunagram.core.markup.KotlinMarkupParser
import org.lunagram.core.markup.MarkupParser

internal val LocalMarkupParser = staticCompositionLocalOf<MarkupParser> { KotlinMarkupParser() }
