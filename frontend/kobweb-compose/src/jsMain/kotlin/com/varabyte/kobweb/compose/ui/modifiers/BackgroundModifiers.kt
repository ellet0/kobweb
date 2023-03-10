package com.varabyte.kobweb.compose.ui.modifiers

import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.styleModifier
import org.jetbrains.compose.web.css.*

fun Modifier.background(value: String) = styleModifier {
    background(value)
}

fun Modifier.backgroundAttachment(backgroundAttachment: BackgroundAttachment) = styleModifier {
    backgroundAttachment(backgroundAttachment)
}

// TODO(#168): Remove before v1.0
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
    ReplaceWith(
        "styleModifier { backgroundAttachment(value) }",
        "com.varabyte.kobweb.compose.ui.styleModifier",
        "org.jetbrains.compose.web.css.backgroundAttachment"
    ),
)
fun Modifier.backgroundAttachment(value: String) = styleModifier {
    backgroundAttachment(value)
}

fun Modifier.backgroundBlendMode(vararg blendModes: MixBlendMode) = styleModifier {
    backgroundBlendMode(*blendModes)
}

fun Modifier.backgroundClip(backgroundClip: BackgroundClip) = styleModifier {
    backgroundClip(backgroundClip)
}

// TODO(#168): Remove before v1.0
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
    ReplaceWith(
        "styleModifier { backgroundClip(value) }",
        "com.varabyte.kobweb.compose.ui.styleModifier",
        "org.jetbrains.compose.web.css.backgroundClip"
    ),
)
fun Modifier.backgroundClip(value: String) = styleModifier {
    backgroundClip(value)
}

// TODO(#168): Remove before v1.0
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
    ReplaceWith(
        "styleModifier { property(\"background-color\", value) }",
        "com.varabyte.kobweb.compose.ui.styleModifier"
    ),
)
fun Modifier.backgroundColor(value: String) = styleModifier {
    property("background-color", value)
}

fun Modifier.backgroundColor(color: CSSColorValue) = styleModifier {
    backgroundColor(color)
}

fun Modifier.backgroundImage(value: String) = styleModifier {
    backgroundImage(value)
}

fun Modifier.backgroundOrigin(backgroundOrigin: BackgroundOrigin) = styleModifier {
    backgroundOrigin(backgroundOrigin)
}

// TODO(#168): Remove before v1.0
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
    ReplaceWith(
        "styleModifier { backgroundOrigin(value) }",
        "com.varabyte.kobweb.compose.ui.styleModifier",
        "org.jetbrains.compose.web.css.backgroundOrigin"
    ),
)
fun Modifier.backgroundOrigin(value: String) = styleModifier {
    backgroundOrigin(value)
}

fun Modifier.backgroundPosition(value: String) = styleModifier {
    backgroundPosition(value)
}

fun Modifier.backgroundRepeat(backgroundRepeat: BackgroundRepeat) = styleModifier {
    backgroundRepeat(backgroundRepeat)
}

fun Modifier.backgroundRepeat(horizontalRepeat: BackgroundRepeat.RepeatStyle, verticalRepeat: BackgroundRepeat.RepeatStyle) = styleModifier {
    backgroundRepeat(horizontalRepeat, verticalRepeat)
}

// TODO(#168): Remove before v1.0
@Deprecated(
    "All stringly-typed Kobweb Modifiers will be removed before v1.0. Use a richly-typed version or use styleModifier as a fallback.",
    ReplaceWith(
        "styleModifier { backgroundRepeat(value) }",
        "com.varabyte.kobweb.compose.ui.styleModifier",
        "org.jetbrains.compose.web.css.backgroundRepeat"
    ),
)
fun Modifier.backgroundRepeat(value: String) = styleModifier {
    backgroundRepeat(value)
}

fun Modifier.backgroundSize(value: String) = styleModifier {
    backgroundSize(value)
}
