package me.sleepyfish.imctools.listeners

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import me.sleepyfish.imctools.Main
import me.sleepyfish.imctools.notification.MyPluginNotifier

class EnterKeyInterceptor(private val originalHandler: EditorActionHandler) : EditorActionHandler() {

    @Deprecated("Deprecated in Java")
    override fun execute(editor: Editor, dataContext: DataContext?) {
        val project = editor.project ?: return originalHandler.execute(editor, dataContext)

        val psiFile = PsiDocumentManager.getInstance(project)
            .getPsiFile(editor.document) ?: return originalHandler.execute(editor, dataContext)

        val caret = editor.caretModel.currentCaret
        val document = editor.document
        val lineNum = document.getLineNumber(caret.offset)
        val lineStart = document.getLineStartOffset(lineNum)
        val lineEnd = document.getLineEndOffset(lineNum)
        val lineText = document.getText(TextRange(lineStart, lineEnd))

        val currentLineWithCursor = lineText.substring(0, caret.offset - lineStart)

        // Check if current line matches any ImGui block start
        val (blockStart, blockEnd) = Main.imguiBlockPairs.entries.firstOrNull { (start, _) ->
            currentLineWithCursor.contains(start) && currentLineWithCursor.endsWith(");")
        } ?: return originalHandler.execute(editor, dataContext)

        try {
            // Get indentation
            val currentLineStart = document.getLineStartOffset(lineNum)
            val currentLineText = document.getText(TextRange.create(currentLineStart, lineEnd))
            val tabSpace = currentLineText.takeWhile { it.isWhitespace() }

            WriteCommandAction.runWriteCommandAction(project) {
                val textToInsert = buildString {
                    append("${tabSpace}{\n")
                    append("${tabSpace}    \n") // empty line for cursor
                    append("${tabSpace}}\n")
                    append("${tabSpace}$blockEnd")
                }

                val insertionPoint = document.getLineStartOffset(lineNum + 1) - 1
                document.insertString(insertionPoint, textToInsert)

                val emptyLineOffset = document.getLineStartOffset(lineNum + 2)

                ApplicationManager.getApplication().invokeLater {
                    editor.caretModel.moveToOffset(emptyLineOffset)
                }

                MyPluginNotifier.showInfo(project, "Block " + blockStart + " detected!")
            }
        } catch (e: Exception) {
            MyPluginNotifier.showWarning(project, "Error: ${e.message}")
        }

        originalHandler.execute(editor, dataContext)
    }

}
