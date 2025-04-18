package me.sleepyfish.imctools.toolwindows

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import javax.swing.JLabel
import javax.swing.JPanel
import java.awt.BorderLayout

class MyDocumentationWindowFactory : ToolWindowFactory {

    var documentationLabel: JLabel? = null

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {

        // Create the content panel for the tool window
        val panel = JPanel(BorderLayout())

        // Create a label to display the documentation (initially empty or with placeholder text)
        documentationLabel = JLabel("Documentation will appear here")
        documentationLabel.let { panel.add(it, BorderLayout.CENTER) }

        // Access the editor (assuming there is an active editor in the project)
        val editor = EditorFactory.getInstance().allEditors.firstOrNull()
        if (editor != null) {
            // If an editor exists, add the caret listener
            editor.caretModel.addCaretListener(object : CaretListener {
                override fun caretPositionChanged(e: CaretEvent) {
                    updateDocumentation(editor)
                }
            })
        } else {
            // Wait until an editor becomes available
            // Register an editor listener to catch when an editor becomes available
            EditorFactory.getInstance().addEditorFactoryListener(object : EditorFactoryListener {
                override fun editorCreated(event: EditorFactoryEvent) {

                    // Add the caret listener once the editor is available
                        event.editor.caretModel.addCaretListener(object : CaretListener {
                        override fun caretPositionChanged(e: CaretEvent) {
                            updateDocumentation(event.editor)
                        }
                    })
                }

                override fun editorReleased(event: EditorFactoryEvent) {
                }
            })
        }
    }

    private fun updateDocumentation(editor: Editor) {
        // val document = editor.document
        // val currentLine = document.getLineNumber(editor.caretModel.offset)
        // val currentLineStart = document.getLineStartOffset(currentLine)
        // val currentLineText = document.getText(TextRange.create(currentLineStart, document.getLineEndOffset(currentLine)))
        // MyPluginNotifier.showInfo(editor.project, "Current line text: $currentLineText")
    }

}
