package me.sleepyfish.imctools.providers

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.ui.ColorChooserService
import java.awt.Color
import java.awt.Component
import java.awt.Graphics
import javax.swing.Icon

class ImGuiLineMarkerProvider : LineMarkerProvider {

    private enum class ColorFormat { FLOAT, INT, HEX, IM_COL32 }

    private data class ColorMatch(
        val typeName: String,
        val format: ColorFormat,
        val useBraces: Boolean,
        val color: Color,
        val callStart: Int,
        val callEnd: Int,
    )

    companion object {
        private val CALL_OPEN = Regex("""(ImColor|ImVec4|ImU32|float\[4\])\s*([({])""")
        private val IM_COL32_OPEN = Regex("""(IM_COL32)\s*(\()""")
        private val HEX_LITERAL = Regex("""0[xX][0-9A-Fa-f]{8}\b""")
        private val NUMBER = Regex("""(\d+(?:\.\d+)?)\s*f?""")
    }

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element.firstChild != null) return null
        if (element is PsiWhiteSpace || element is PsiComment) return null
        if (isInsideStringLiteral(element)) return null

        val tokenText = element.text

        return when {
            tokenText == "IM_COL32" -> {
                val callElement = findCallAncestorFor(element, IM_COL32_OPEN) ?: return null
                val normalised = callElement.text.replace(Regex("""\s+"""), " ")
                val match = parseImCol32Call(normalised) ?: return null
                buildLineMarkerInfo(callElement, match)
            }
            tokenText == "ImColor" || tokenText == "ImVec4" || tokenText == "ImU32" || tokenText == "float" -> {
                val callElement = findCallAncestorFor(element, CALL_OPEN) ?: return null
                val normalised = callElement.text.replace(Regex("""\s+"""), " ")
                val match = parseColorCall(normalised) ?: return null
                buildLineMarkerInfo(callElement, match)
            }
            else -> null
        }
    }

    private fun findCallAncestorFor(element: PsiElement, pattern: Regex): PsiElement? {
        var current: PsiElement? = element.parent
        var depth = 0
        while (current != null && depth < 8) {
            val normalised = current.text.replace(Regex("""\s+"""), " ").trimStart()
            val m = pattern.find(normalised)
            if (m != null && m.range.first == 0) {
                val close = if (m.groupValues[2] == "{") "}" else ")"
                if (normalised.contains(close)) return current
            }
            current = current.parent
            depth++
        }
        return null
    }

    private fun isInsideStringLiteral(element: PsiElement): Boolean {
        var current: PsiElement? = element.parent
        while (current != null) {
            val typeName = current.node?.elementType?.toString()?.lowercase() ?: ""
            if ("string" in typeName || ("literal" in typeName && "char" !in typeName)) {
                val text = current.text
                if (text.startsWith("\"") || text.startsWith("'")) return true
            }
            current = current.parent
        }
        return false
    }

    private fun extractArgSpan(text: String, openMatch: MatchResult): Triple<String, Int, Int>? {
        val argsStart = openMatch.range.last + 1
        var depth = 1
        var idx = argsStart
        while (idx < text.length && depth > 0) {
            when (text[idx]) {
                '(', '{' -> depth++
                ')', '}' -> depth--
            }
            if (depth > 0) idx++
        }
        if (depth != 0) return null
        return Triple(text.substring(argsStart, idx).trim(), argsStart, idx)
    }

    private fun parseImCol32Call(text: String): ColorMatch? {
        val openMatch = IM_COL32_OPEN.find(text) ?: return null
        val (argSpan, _, argsEnd) = extractArgSpan(text, openMatch) ?: return null
        val callEnd = argsEnd + 1

        val args = splitArgs(argSpan)
        if (args.size != 4) return null

        val numbers = args.map { arg ->
            NUMBER.find(arg.trim())?.groupValues?.get(1)?.toFloatOrNull() ?: return null
        }

        val color = Color(
            numbers[0].toInt().coerceIn(0, 255),
            numbers[1].toInt().coerceIn(0, 255),
            numbers[2].toInt().coerceIn(0, 255),
            numbers[3].toInt().coerceIn(0, 255),
        )

        return ColorMatch(
            typeName  = "IM_COL32",
            format    = ColorFormat.IM_COL32,
            useBraces = false,
            color     = color,
            callStart = openMatch.range.first,
            callEnd   = callEnd,
        )
    }

    private fun parseColorCall(text: String): ColorMatch? {
        val openMatch = CALL_OPEN.find(text) ?: return null
        val typeName = openMatch.groupValues[1]
        val openDelim = openMatch.groupValues[2]
        val (argSpan, _, argsEnd) = extractArgSpan(text, openMatch) ?: return null
        val callEnd = argsEnd + 1

        val hexMatch = HEX_LITERAL.find(argSpan)
        if (hexMatch != null && splitArgs(argSpan).size == 1) {
            val packed = hexMatch.value.drop(2).toLongOrNull(16) ?: return null
            val a = ((packed shr 24) and 0xFF).toInt()
            val b = ((packed shr 16) and 0xFF).toInt()
            val g = ((packed shr  8) and 0xFF).toInt()
            val r = ((packed       ) and 0xFF).toInt()
            return ColorMatch(
                typeName  = typeName,
                format    = ColorFormat.HEX,
                useBraces = (openDelim == "{"),
                color     = Color(r, g, b, a),
                callStart = openMatch.range.first,
                callEnd   = callEnd,
            )
        }

        val args = splitArgs(argSpan)
        if (args.size < 3 || args.size > 4) return null

        val isFloat = args.any { it.contains('.') || it.trimEnd().endsWith('f') }

        val numbers = args.map { arg ->
            NUMBER.find(arg.trim())?.groupValues?.get(1)?.toFloatOrNull() ?: return null
        }

        val color = if (isFloat) {
            Color(
                (numbers[0].coerceIn(0f, 1f) * 255).toInt(),
                (numbers[1].coerceIn(0f, 1f) * 255).toInt(),
                (numbers[2].coerceIn(0f, 1f) * 255).toInt(),
                if (numbers.size == 4) (numbers[3].coerceIn(0f, 1f) * 255).toInt() else 255,
            )
        } else {
            Color(
                numbers[0].toInt().coerceIn(0, 255),
                numbers[1].toInt().coerceIn(0, 255),
                numbers[2].toInt().coerceIn(0, 255),
                if (numbers.size == 4) numbers[3].toInt().coerceIn(0, 255) else 255,
            )
        }

        return ColorMatch(
            typeName  = typeName,
            format    = if (isFloat) ColorFormat.FLOAT else ColorFormat.INT,
            useBraces = (openDelim == "{"),
            color     = color,
            callStart = openMatch.range.first,
            callEnd   = callEnd,
        )
    }

    private fun splitArgs(args: String): List<String> {
        val result = mutableListOf<String>()
        var depth = 0
        var start = 0
        for (i in args.indices) {
            when (args[i]) {
                '(', '{' -> depth++
                ')', '}' -> depth--
                ',' -> if (depth == 0) { result += args.substring(start, i); start = i + 1 }
            }
        }
        result += args.substring(start)
        return result.map { it.trim() }.filter { it.isNotEmpty() }
    }

    private fun createIcon(color: Color): Icon = object : Icon {
        override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
            g.color = Color(0, 0, 0, 80)
            g.drawRect(x, y, iconWidth - 1, iconHeight - 1)
            g.color = color
            g.fillRect(x + 1, y + 1, iconWidth - 2, iconHeight - 2)
        }
        override fun getIconWidth() = 12
        override fun getIconHeight() = 12
    }

    private fun buildLineMarkerInfo(element: PsiElement, match: ColorMatch): LineMarkerInfo<PsiElement> {
        return LineMarkerInfo(
            element,
            element.textRange,
            createIcon(match.color),
            { "Click to open colour picker" },
            { _, elt ->
                val project = elt.project
                val editor = findEditor(project, elt) ?: return@LineMarkerInfo
                ColorChooserService.instance.showPopup(
                    project, match.color, editor,
                    { newColor, _ -> applyColorChange(project, editor, elt, match, newColor) },
                    true, true,
                )
            },
            GutterIconRenderer.Alignment.RIGHT,
            { "IMCTools-ColorPicker-${element.textRange.startOffset}" },
        )
    }

    private fun applyColorChange(
        project: Project,
        editor: Editor,
        element: PsiElement,
        originalMatch: ColorMatch,
        newColor: Color,
    ) {
        WriteCommandAction.runWriteCommandAction(project) {
            val document = editor.document
            val elementRange = element.textRange
            val rawText = document.getText(elementRange)
            val docBase = elementRange.startOffset

            val openIdx = rawText.indexOfFirst { it == '(' || it == '{' }
            if (openIdx == -1) return@runWriteCommandAction

            var depth = 1
            var closeIdx = openIdx + 1
            while (closeIdx < rawText.length && depth > 0) {
                when (rawText[closeIdx]) {
                    '(', '{' -> depth++
                    ')', '}' -> depth--
                }
                if (depth > 0) closeIdx++
            }
            if (depth != 0) return@runWriteCommandAction

            val argSpan = rawText.substring(openIdx + 1, closeIdx)

            when (originalMatch.format) {
                ColorFormat.HEX -> {
                    val hexMatch = HEX_LITERAL.find(argSpan) ?: return@runWriteCommandAction
                    val packed = (newColor.alpha.toLong() shl 24) or
                            (newColor.blue.toLong()  shl 16) or
                            (newColor.green.toLong() shl  8) or
                            (newColor.red.toLong())
                    val start = docBase + openIdx + 1 + hexMatch.range.first
                    val end   = docBase + openIdx + 1 + hexMatch.range.last + 1
                    if (start < end && end <= document.textLength)
                        document.replaceString(start, end, "0x%08X".format(packed))
                }

                ColorFormat.INT, ColorFormat.IM_COL32 -> {
                    replaceIntArgs(
                        argSpan, openIdx, rawText, docBase, document,
                        listOf("${newColor.red}", "${newColor.green}", "${newColor.blue}", "${newColor.alpha}"),
                    )
                }

                ColorFormat.FLOAT -> {
                    replaceIntArgs(
                        argSpan, openIdx, rawText, docBase, document,
                        listOf(
                            "%.2ff".format(newColor.red   / 255f),
                            "%.2ff".format(newColor.green / 255f),
                            "%.2ff".format(newColor.blue  / 255f),
                            "%.2ff".format(newColor.alpha / 255f),
                        ),
                    )
                }
            }
        }
    }

    private fun replaceIntArgs(
        argSpan: String,
        openIdx: Int,
        rawText: String,
        docBase: Int,
        document: com.intellij.openapi.editor.Document,
        newValues: List<String>,
    ) {
        val argRanges = splitArgRanges(argSpan).map {
            (it.first + openIdx + 1)..(it.last + openIdx + 1)
        }
        if (argRanges.size < 3 || argRanges.size > 4) return

        for (i in argRanges.indices.reversed()) {
            val argText = rawText.substring(argRanges[i])
            val numMatch = NUMBER.find(argText) ?: continue
            val numStart = docBase + argRanges[i].first + numMatch.range.first
            val tail = argText.substring(numMatch.range.last + 1)
            val hasSuffix = tail.trimStart().firstOrNull() == 'f' &&
                    tail.trimStart().getOrNull(1)?.isLetterOrDigit() != true
            val suffixLen = if (hasSuffix) tail.indexOf('f') + 1 else 0
            val numEnd = docBase + argRanges[i].first + numMatch.range.last + 1 + suffixLen
            if (numStart < numEnd && numEnd <= document.textLength)
                document.replaceString(numStart, numEnd, newValues[i])
        }
    }

    private fun splitArgRanges(args: String): List<IntRange> {
        val result = mutableListOf<IntRange>()
        var depth = 0
        var start = 0
        for (i in args.indices) {
            when (args[i]) {
                '(', '{' -> depth++
                ')', '}' -> depth--
                ',' -> if (depth == 0) { result += start until i; start = i + 1 }
            }
        }
        result += start until args.length
        return result
    }

    private fun findEditor(project: Project, element: PsiElement): Editor? {
        val file = element.containingFile ?: return null
        val document = file.viewProvider.document ?: return null
        return EditorFactory.getInstance().getEditors(document, project).firstOrNull()
    }
}
