package com.varabyte.kobweb.silk.components.document

import androidx.compose.runtime.*
import com.varabyte.kobweb.compose.css.ListStyleType
import com.varabyte.kobweb.compose.css.StyleVariable
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.dom.ElementRefScope
import com.varabyte.kobweb.compose.dom.registerRefScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.components.style.*
import com.varabyte.kobweb.silk.theme.colors.rememberColorMode
import kotlinx.browser.document
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Ul
import org.w3c.dom.*

val TocBorderColorVar by StyleVariable<CSSColorValue>(prefix = "silk")

val TocStyle by ComponentStyle.base(prefix = "silk") {
    Modifier
        .listStyle(ListStyleType.None)
        .textAlign(TextAlign.Start)
}

val TocBorderedVariant by TocStyle.addVariantBase {
    Modifier
        .borderRadius(5.px)
        .border(1.px, LineStyle.Solid, TocBorderColorVar.value())
        .padding(1.cssRem)
}

private fun HTMLCollection.walk(onEach: (Element) -> Boolean) {
    (0 until length)
        .mapNotNull { i: Int -> this[i] }
        .forEach { child ->
            if (onEach(child)) {
                child.children.walk(onEach)
            }
        }
}

/**
 * Generates a table of contents for the current page, by searching the page for header elements with IDs.
 *
 * It's important that each header element has an ID, as this is what the TOC will link to. This is a standard format
 * output by markdown, but you may need to add IDs manually if you're adding Composables directly:
 *
 * ```
 * Toc()
 * H2(Modifier.id("h1").toAttrs()) { Text("Header 1") }
 * H3(Modifier.id("h1s1").toAttrs()) { Text("Subheader 1.1") }
 * H3(Modifier.id("h1s2").toAttrs()) { Text("Subheader 1.2") }
 * H2(Modifier.id("h2").toAttrs()) { Text("Header 2") }
 * H2(Modifier.id("h3").toAttrs()) { Text("Header 3") }
 * H3(Modifier.id("h3s1").toAttrs()) { Text("Subheader 3.1") }
 * ```
 *
 * @param minHeaderLevel The minimum header level to start paying attention to; any lower level headers will be skipped
 *   over. This defaults to 2 and not 1 because `H1` is usually the title of the page and not included in the TOC.
 * @param maxHeaderLevel The maximum header level to pay attention to; any higher level headers will be skipped over.
 */
@Composable
fun Toc(
    modifier: Modifier = Modifier,
    variant: ComponentVariant? = null,
    minHeaderLevel: Int = 2,
    maxHeaderLevel: Int = 3,
    indent: CSSNumeric = 1.cssRem,
    ref: ElementRefScope<HTMLUListElement>? = null,
) {
    require(minHeaderLevel in 1..6) { "Toc minHeaderLevel must be in range 1..6, got $minHeaderLevel" }
    require(maxHeaderLevel in 1..6) { "Toc maxHeaderLevel must be in range 1..6, got $maxHeaderLevel" }
    require(maxHeaderLevel >= minHeaderLevel) { "Toc maxHeaderLevel must be >= minHeaderLevel, got $minHeaderLevel > $maxHeaderLevel" }

    val acceptedHeaderNames = (minHeaderLevel ..maxHeaderLevel).map { level -> "H$level" }
    val colorMode by rememberColorMode()

    Ul(TocStyle.toModifier(variant).then(modifier).toAttrs()) {
        registerRefScope(ref)

        DisposableEffect(colorMode) {
            val element = scopeElement

            document.body!!.children.walk { child ->
                if (child is HTMLHeadingElement
                    && child.id.isNotBlank()
                    && child.nodeName in acceptedHeaderNames
                ) {
                    val headingText = child.textContent ?: return@walk false

                    val indentCount = acceptedHeaderNames.indexOf(child.nodeName)

                    val li = document.createElement("li") as HTMLLIElement
                    li.setAttribute("style", "padding-left:${indentCount * indent}")

                    val link = document.createElement("a") as HTMLAnchorElement
                    link.setAttribute("href", "#${child.id}")
                    link.setAttribute("class", "silk-link silk-link_${colorMode.name.lowercase()}")
                    link.appendChild(document.createTextNode(headingText))
                    li.appendChild(link)
                    element.appendChild(li)
                }

                when {
                    child === element -> false
                    else -> true
                }
            }

            onDispose {
                element.children.walk { it.remove(); false }
                check(element.firstChild == null)
            }
        }
    }

}