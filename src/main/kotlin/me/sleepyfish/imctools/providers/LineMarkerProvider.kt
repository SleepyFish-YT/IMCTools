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

    private data class ColorMatch(
        val typeName: String,
        val isFloat: Boolean,
        val useBraces: Boolean,
        val color: Color,
        val callStart: Int,
        val callEnd: Int,
    )

    companion object {
        private val CALL_OPEN = Regex(
            """(ImColor|ImVec4|float\[4\])\s*([({])"""
        )
        private val NUMBER = Regex("""(\d+(?:\.\d+)?)\s*f?""")
    }

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element.firstChild != null)
            return null

        if (element is PsiWhiteSpace || element is PsiComment)
            return null

        val tokenText = element.text
        if (tokenText != "ImColor" && tokenText != "ImVec4" && tokenText != "float")
            return null

        val callElement = findCallAncestor(element) ?: return null
        val callText = callElement.text.replace(Regex("""\s+"""), " ")

        if (isInsideStringLiteral(element))
            return null

        val match = parseColorCall(callText) ?: return null

        return buildLineMarkerInfo(callElement, match)
    }

    private fun findCallAncestor(element: PsiElement): PsiElement? {
        var current: PsiElement? = element.parent
        var depth = 0

        while (current != null && depth < 8) {
            val normalized = current.text.replace(Regex("""\s+"""), " ").trimStart()
            val m = CALL_OPEN.find(normalized)

            if (m != null && m.range.first == 0) {
                val closeDelim = if (m.groupValues[2] == "{") "}" else ")"
                if (normalized.contains(closeDelim)) return current
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
            if ("string" in typeName || "literal" in typeName && "char" !in typeName) {
                val text = current.text
                if (text.startsWith("\"") || text.startsWith("'")) return true
            }

            current = current.parent
        }

        return false
    }

    private fun parseColorCall(text: String): ColorMatch? {
        val openMatch = CALL_OPEN.find(text) ?: return null
        val typeName = openMatch.groupValues[1]
        val openDelim = openMatch.groupValues[2]
        val closeDelim = if (openDelim == "{") "}" else ")"

        val argsStart = openMatch.range.last + 1
        var depth = 1
        var idx = argsStart

        while (idx < text.length && depth > 0) {
            when (text[idx]) {
                '(', '{' -> depth++
                ')', '}' -> depth--
            }

            if (depth > 0)
                idx++
        }

        if (depth != 0)
            return null

        val argsEnd = idx
        val callEnd = argsEnd + 1
        val argSpan = text.substring(argsStart, argsEnd)

        val args = splitArgs(argSpan)
        if (args.size < 3 || args.size > 4)
            return null

        val isFloat = args.any { it.contains('.') || it.trimEnd().endsWith('f') }

        val numbers = args.map { arg ->
            NUMBER.find(arg.trim())?.groupValues?.get(1)?.toFloatOrNull() ?: return null
        }

        val color = if (isFloat) {
            Color(
                (numbers[0].coerceIn(0f, 1f) * 255).toInt(),
                (numbers[1].coerceIn(0f, 1f) * 255).toInt(),
                (numbers[2].coerceIn(0f, 1f) * 255).toInt(),
                if (numbers.size == 4)
                    (numbers[3].coerceIn(0f, 1f) * 255).toInt() else 255,
            )
        } else {
            Color(
                numbers[0].toInt().coerceIn(0, 255),
                numbers[1].toInt().coerceIn(0, 255),
                numbers[2].toInt().coerceIn(0, 255),
                if (numbers.size == 4)
                    numbers[3].toInt().coerceIn(0, 255) else 255,
            )
        }

        return ColorMatch(
            typeName = typeName,
            isFloat = isFloat,
            useBraces = (openDelim == "{"),
            color = color,
            callStart = openMatch.range.first,
            callEnd = callEnd,
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
                ',' -> if (depth == 0) {
                    result += args.substring(start, i)
                    start = i + 1
                }
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

    private fun buildLineMarkerInfo(
        element: PsiElement,
        match: ColorMatch,
    ): LineMarkerInfo<PsiElement> {
        val icon = createIcon(match.color)

        return LineMarkerInfo(
            element,
            element.textRange,
            icon,
            { "Click to open colour picker" },
            { _, elt ->
                val project = elt.project
                val editor = findEditor(project, elt) ?: return@LineMarkerInfo

                ColorChooserService.instance.showPopup(
                    project,
                    match.color,
                    editor,
                    { newColor, _ ->
                        applyColorChange(project, editor, elt, match, newColor)
                    },
                    true,
                    true,
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

            val openIdx = rawText.indexOfFirst { it == '(' || it == '{' }
            if (openIdx == -1)
                return@runWriteCommandAction

            val closeChar = if (rawText[openIdx] == '{') '}' else ')'

            var depth = 1
            var closeIdx = openIdx + 1

            while (closeIdx < rawText.length && depth > 0) {
                when (rawText[closeIdx]) {
                    '(', '{' -> depth++
                    ')', '}' -> depth--
                }
                if (depth > 0) closeIdx++
            }

            if (depth != 0)
                return@runWriteCommandAction

            val argSpan = rawText.substring(openIdx + 1, closeIdx)
            val argRanges = splitArgRanges(argSpan).map {
                (it.first + openIdx + 1)..(it.last + openIdx + 1)
            }

            if (argRanges.size < 3 || argRanges.size > 4)
                return@runWriteCommandAction

            val useFloat = originalMatch.isFloat
            val newValues = if (useFloat) listOf(
                "%.2ff".format(newColor.red   / 255f),
                "%.2ff".format(newColor.green / 255f),
                "%.2ff".format(newColor.blue  / 255f),
                "%.2ff".format(newColor.alpha / 255f),
            ) else listOf(
                "${newColor.red}",
                "${newColor.green}",
                "${newColor.blue}",
                "${newColor.alpha}",
            )

            val docBase = elementRange.startOffset
            for (i in argRanges.indices.reversed()) {
                val argText = rawText.substring(argRanges[i])
                val numMatch = NUMBER.find(argText) ?: continue
                val numDocStart = docBase + argRanges[i].first + numMatch.range.first
                val rawAfterNum = argText.substring(numMatch.range.last + 1)

                val hasSuffix = rawAfterNum.trimStart().startsWith('f') &&
                        (rawAfterNum.trimStart().length == 1 || !rawAfterNum.trimStart()[1].isLetterOrDigit())

                val suffixLen = if (hasSuffix) rawAfterNum.indexOf('f') + 1 else 0
                val numDocEnd = docBase + argRanges[i].first + numMatch.range.last + 1 + suffixLen

                if (numDocStart < numDocEnd && numDocEnd <= document.textLength) {
                    document.replaceString(numDocStart, numDocEnd, newValues[i])
                }
            }
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
                ',' -> if (depth == 0) {
                    result += start until i
                    start = i + 1
                }
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