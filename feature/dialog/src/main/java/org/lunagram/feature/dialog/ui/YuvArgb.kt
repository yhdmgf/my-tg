package org.lunagram.feature.dialog.ui

import java.nio.ByteBuffer

private fun clip(value: Int): Int = value.coerceIn(0, 255)
