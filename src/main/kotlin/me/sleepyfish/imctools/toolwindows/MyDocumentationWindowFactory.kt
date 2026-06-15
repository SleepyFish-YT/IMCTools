package me.sleepyfish.imctools.toolwindows

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Font
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class MyDocumentationWindowFactory : ToolWindowFactory, Disposable {

    private var contentPanel: JPanel? = null
    private var disposed = false

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        if (disposed) return

        contentPanel = JPanel(BorderLayout()).apply {
            // Create header label
            val documentationLabel = JBLabel("Dear ImGui Documentation", SwingConstants.CENTER).apply {
                font = Font("Segoe UI", Font.BOLD, 18)
                border = JBUI.Borders.empty(10)
            }

            // Create documentation text area
            val documentationText = JBTextArea().apply {
                isEditable = false
                lineWrap = true
                wrapStyleWord = true
                font = Font("Segoe UI", Font.PLAIN, 14)
                text = """
                    Dear ImGui (Immediate Mode Graphical User Interface)
                    ====================================================
                    
                    Overview:
                    ---------
                    Dear ImGui is a bloat-free graphical user interface library for C++.
                    It outputs optimized vertex buffers that you can render in your 3D-pipeline enabled application.
                    
                    Key Features:
                    -------------
                    • Immediate mode GUI paradigm
                    • No external dependencies
                    • Fast and lightweight
                    • Highly customizable
                    • Portable (supports multiple rendering backends)
                    • Minimal state synchronization
                    
                    Common Use Cases:
                    -----------------
                    • Debugging tools and consoles
                    • Editor interfaces
                    • In-game UI
                    • Configuration dialogs
                    • Profiling tools
                    
                    Supported Platforms:
                    - Windows, Linux, macOS, iOS, Android
                    
                    Supported Rendering Backends:
                    - DirectX 9/10/11/12, OpenGL 2/3/4, Vulkan, Metal, WebGPU
                    
                    Basic Usage Example:
                    -------------------
                    // In your render loop
                    ImGui::Begin("My Window");
                    ImGui::Text("Hello, world!");
                    if (ImGui::Button("Click Me")) {
                        // Handle button click
                    }
                    ImGui::End();
                    
                    // Render ImGui
                    ImGui::Render();
                    ImGui_ImplOpenGL3_RenderDrawData(ImGui::GetDrawData());
                    
                    Getting Started:
                    ----------------
                    1. Download ImGui from GitHub: https://github.com/ocornut/imgui
                    2. Copy the imgui directory to your project
                    3. Implement backend for your rendering API
                    4. Initialize ImGui and start creating interfaces
                    
                    Useful Resources:
                    ----------------
                    • Official Repository: https://github.com/ocornut/imgui
                    • Wiki: https://github.com/ocornut/imgui/wiki
                    • Examples: Located in the imgui/examples/ folder
                """.trimIndent()
                caretPosition = 0
            }

            val scrollPane = JBScrollPane(documentationText).apply {
                border = JBUI.Borders.empty()
                verticalScrollBarPolicy = JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
            }

            add(documentationLabel, BorderLayout.NORTH)
            add(scrollPane, BorderLayout.CENTER)
        }

        val contentManager = toolWindow.contentManager
        val content = contentManager.factory.createContent(contentPanel, "ImGui Info", false)

        // Register this factory as a disposable parent for the content
        Disposer.register(content, this)

        contentManager.addContent(content)
    }

    override fun isApplicable(project: Project): Boolean = true

    override fun init(toolWindow: ToolWindow) {
        toolWindow.setTitle("Dear ImGui Documentation")
    }

    override fun dispose() {
        if (!disposed) {
            disposed = true
            contentPanel = null
        }
    }
}