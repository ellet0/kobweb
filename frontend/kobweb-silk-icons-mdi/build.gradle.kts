import com.varabyte.kobweb.gradle.publish.FILTER_OUT_MULTIPLATFORM_PUBLICATIONS
import com.varabyte.kobweb.gradle.publish.set

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    id("com.varabyte.kobweb.internal.publish")
}

group = "com.varabyte.kobweb"
version = libs.versions.kobweb.libs.get()

private val GENERATED_SRC_ROOT = "build/generated/kobweb-silk-icons-mdi/src/jsMain/kotlin"

kotlin {
    js {
        browser()
        binaries.executable()
    }

    sourceSets {
        val jsMain by getting {
            kotlin.srcDir(GENERATED_SRC_ROOT)
            dependencies {
                implementation(compose.runtime)
                implementation(compose.html.core)

                api(project(":frontend:kobweb-compose"))
            }
        }
    }
}

kobwebPublication {
    artifactId.set("kobweb-silk-icons-mdi")
    description.set("A collection of Kobweb Silk components that directly wrap Material Design icons")
    filter.set(FILTER_OUT_MULTIPLATFORM_PUBLICATIONS)
}

enum class IconStyle {
    FILLED,
    OUTLINED,
    ROUNDED,
    SHARP,
    TWO_TONED;
}

val generateIconsTask = tasks.register("generateIcons") {
    val srcFile = layout.projectDirectory.file("md-icon-list.txt")
    val dstFile =
        layout.projectDirectory.file("$GENERATED_SRC_ROOT/com/varabyte/kobweb/silk/components/icons/mdi/MdIcons.kt")

    inputs.files(srcFile)
    outputs.file(dstFile)

    doLast {
        val iconRawNames = srcFile.asFile
            .readLines()
            .asSequence()
            .filter { line -> !line.startsWith("#") }
            .associate { line ->
                // Convert icon name to function name, e.g.
                // align-left -> MdiAlignLeft
                line.split("=", limit = 2).let { parts ->
                    val style = when (parts[0]) {
                        "mdi" -> IconStyle.FILLED
                        "mdio" -> IconStyle.OUTLINED
                        "mdir" -> IconStyle.ROUNDED
                        "mdis" -> IconStyle.SHARP
                        "mdit" -> IconStyle.TWO_TONED
                        else -> throw GradleException("Unexpected style grouping: ${parts[0]}")
                    }
                    val names = parts[1]

                    style to names.split(",")
                }
            }

        // For each icon name, figure out what categories they are in. This will affect the function signature we generate.
        val iconStyleGroups = mutableMapOf<String, MutableSet<IconStyle>>()
        iconRawNames.forEach { entry ->
            val category = entry.key
            entry.value.forEach { rawName ->
                iconStyleGroups.computeIfAbsent(rawName) { mutableSetOf() }.add(category)
            }
        }

        val iconMethodEntries = iconStyleGroups
            .map { entry ->
                // TODO(194): Figure out how ligature fallbacks to work, if people report us breaking on legacy browsers.
                //  See also: https://developers.google.com/fonts/docs/material_icons#using_the_icons_in_html
                val rawName = entry.key

                @Suppress("DEPRECATION") // capitalize is way more readable than a direct replacement
                val methodName = "Mdi" + rawName.split("_").joinToString("") { it.capitalize() }
                val styles = entry.value

                when {
                    // A rare solo-style icon? No need to allow a user to pass in a style parameter in that case
                    styles.size == 1 -> {
                        "@Composable fun $methodName(modifier: Modifier = Modifier) = MdIcon(\"$rawName\", modifier, ${styles.first().name})"
                    }

                    styles.isEmpty() -> {
                        // This shouldn't be possible, but just in case...
                        throw GradleException("Unexpected icon entry with no styles: $entry")
                    }

                styles.size == IconStyle.values().size -> {
                    // This icon supports all styles. No need to assert input parameters.
                    "@Composable fun $methodName(modifier: Modifier = Modifier, style: IconStyle = FILLED) = MdIcon(\"$rawName\", modifier, style)"
                }

                    else -> {
                        // Not all styles are valid. For now, we throw an exception to inform the user about this, but we
                        // can always decide to relax this later.
                        "@Composable fun $methodName(modifier: Modifier = Modifier, style: IconStyle = ${styles.first().name}) = MdIcon(\"$rawName\", modifier, style.also { it.assertValidStyle(\"$methodName\", ${styles.joinToString { it.name }}) })"
                    }
                }
            }

        val iconsCode = """
    //@formatter:off
    @file:Suppress("unused", "SpellCheckingInspection")

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // THIS FILE IS AUTOGENERATED.
    //
    // Do not edit this file by hand. Instead, update `md-icon-list.txt` in the module root and run the Gradle
    // task "generateIcons"
    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    package com.varabyte.kobweb.silk.components.icons.mdi

    import androidx.compose.runtime.*
    import com.varabyte.kobweb.compose.ui.Modifier
    import com.varabyte.kobweb.compose.ui.toAttrs
    import com.varabyte.kobweb.silk.components.icons.mdi.IconStyle.FILLED
    import com.varabyte.kobweb.silk.components.icons.mdi.IconStyle.OUTLINED
    import com.varabyte.kobweb.silk.components.icons.mdi.IconStyle.ROUNDED
    import com.varabyte.kobweb.silk.components.icons.mdi.IconStyle.SHARP
    import com.varabyte.kobweb.silk.components.icons.mdi.IconStyle.TWO_TONED
    import org.jetbrains.compose.web.dom.Span
    import org.jetbrains.compose.web.dom.Text

    enum class IconStyle {
        FILLED,
        OUTLINED,
        ROUNDED,
        SHARP,
        TWO_TONED;
    }

    private fun IconStyle.toClassNameSuffix(): String {
        return when (this) {
            FILLED -> ""
            OUTLINED -> "-outlined"
            ROUNDED -> "-round"
            SHARP -> "-sharp"
            TWO_TONED -> "-two-tone"
        }
    }

    private fun IconStyle.assertValidStyle(methodName: String, vararg validStyles: IconStyle) {
        if (this !in validStyles) {
            error("Attempted to render \"${'$'}methodName\" with style ${'$'}this. Must be one of: ${'$'}{validStyles.joinToString()}")
        }
    }

    @Composable
    fun MdIcon(
        name: String,
        modifier: Modifier,
        style: IconStyle = FILLED,
    ) {
        Span(
            attrs = modifier.toAttrs { classes("material-icons${'$'}{style.toClassNameSuffix()}") }
        ) {
            Text(name)
        }
    }

    ${iconMethodEntries.joinToString("\n")}
        """.trimIndent()

        dstFile.asFile.writeText(iconsCode)
    }
}

tasks.named("compileKotlinJs") {
    dependsOn(generateIconsTask)
}

tasks.named("jsSourcesJar") {
    dependsOn(generateIconsTask)
}
