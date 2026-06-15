package me.sleepyfish.imctools

object Main {

    val imguiBlockPairs: Map<String, String> = mapOf(
        // Core ImGui blocks
        "ImGui::Begin(" to "ImGui::End();",
        "ImGui::BeginChild(" to "ImGui::EndChild();",
        "ImGui::BeginChildFrame(" to "ImGui::EndChildFrame();",

        // Popups
        "ImGui::BeginPopup(" to "ImGui::EndPopup();",
        "ImGui::BeginPopupContextItem(" to "ImGui::EndPopup();",
        "ImGui::BeginPopupContextVoid(" to "ImGui::EndPopup();",
        "ImGui::BeginPopupContextWindow(" to "ImGui::EndPopup();",
        "ImGui::BeginPopupEx(" to "ImGui::EndPopup();",
        "ImGui::BeginPopupModal(" to "ImGui::EndPopup();",
        "ImGui::BeginComboPopup(" to "ImGui::EndPopup();",
        "ImGui::TableBeginContextMenuPopup(" to "ImGui::EndPopup();",

        "ImGui::BeginDragDropSource(" to "ImGui::EndDragDropSource();",
        "ImGui::BeginDragDropTarget(" to "ImGui::EndDragDropTarget();",
        "ImGui::BeginGroup(" to "ImGui::EndGroup();",
        "ImGui::BeginMainMenuBar(" to "ImGui::EndMainMenuBar();",
        "ImGui::BeginMenu(" to "ImGui::EndMenu();",
        "ImGui::BeginMenuBar(" to "ImGui::EndMenuBar();",
        "ImGui::BeginPopup(" to "ImGui::EndPopup();",
        "ImGui::BeginPopupContextItem(" to "ImGui::EndPopup();",
        "ImGui::BeginPopupContextVoid(" to "ImGui::EndPopup();",
        "ImGui::BeginPopupContextWindow(" to "ImGui::EndPopup();",
        "ImGui::BeginPopupModal(" to "ImGui::EndPopup();",
        "ImGui::BeginTabBar(" to "ImGui::EndTabBar();",
        "ImGui::BeginTabItem(" to "ImGui::EndTabItem();",
        "ImGui::BeginTooltip(" to "ImGui::EndTooltip();",
        "ImGui::BeginTable(" to "ImGui::EndTable();",
        "ImGui::BeginTableEx(" to "ImGui::EndTable();",
        "ImGui::BeginTabBarEx(" to "ImGui::EndTabBar();",

        // Push/Pop pairs
        "ImGui::PushClipRect(" to "ImGui::PopClipRect();",
        "ImGui::PushButtonRepeat(" to "ImGui::PopButtonRepeat();",
        "ImGui::PushAllowKeyboardFocus(" to "ImGui::PopAllowKeyboardFocus();",
        "ImGui::PushFont(" to "ImGui::PopFont();",
        "ImGui::PushID(" to "ImGui::PopID();",
        "ImGui::PushItemFlag(" to "ImGui::PopItemFlag();",
        "ImGui::PushItemWidth(" to "ImGui::PopItemWidth();",
        "ImGui::PushStyleColor(" to "ImGui::PopStyleColor();",
        "ImGui::PushStyleVar(" to "ImGui::PopStyleVar();",
        "ImGui::PushTextWrapPos(" to "ImGui::PopTextWrapPos();",
        "ImGui::PushMultiItemsWidths(" to "ImGui::PopItemWidth();",

        // New frame
        "ImGui::NewFrame();" to "ImGui::EndFrame();",

        // Win32 implementation
        "ImGui_ImplWin32_Init(" to "ImGui_ImplWin32_Shutdown();",
        "ImGui_ImplWin32_NewFrame(" to "ImGui_ImplWin32_Shutdown();",

        // Context management
        "ImGui::CreateContext(" to "ImGui::DestroyContext();",
        "ImGui::GetIO().Fonts->AddFont" to "ImGui::GetIO().Fonts->Clear();",

        // Platform windows
        "ImGui::GetPlatformIO().Platform_" to "ImGui::GetPlatformIO().Renderer_",

        // Render backends
        "ImGui_ImplOpenGL1_Init(" to "ImGui_ImplOpenGL1_Shutdown();",
        "ImGui_ImplOpenGL1_CreateFontsTexture(" to "ImGui_ImplOpenGL1_DestroyFontsTexture();",
        "ImGui_ImplOpenGL1_CreateDeviceObjects(" to "ImGui_ImplOpenGL1_DestroyDeviceObjects();",
        "ImGui_ImplOpenGL1_NewFrame(" to "ImGui_ImplOpenGL1_RenderDrawData(",

        "ImGui_ImplOpenGL2_Init(" to "ImGui_ImplOpenGL2_Shutdown();",
        "ImGui_ImplOpenGL2_CreateFontsTexture(" to "ImGui_ImplOpenGL2_DestroyFontsTexture();",
        "ImGui_ImplOpenGL2_CreateDeviceObjects(" to "ImGui_ImplOpenGL2_DestroyDeviceObjects();",
        "ImGui_ImplOpenGL2_NewFrame(" to "ImGui_ImplOpenGL2_RenderDrawData(",

        "ImGui_ImplOpenGL3_Init(" to "ImGui_ImplOpenGL3_Shutdown();",
        "ImGui_ImplOpenGL3_CreateFontsTexture(" to "ImGui_ImplOpenGL3_DestroyFontsTexture();",
        "ImGui_ImplOpenGL3_CreateDeviceObjects(" to "ImGui_ImplOpenGL3_DestroyDeviceObjects();",
        "ImGui_ImplOpenGL3_NewFrame(" to "ImGui_ImplOpenGL3_RenderDrawData(",

        "ImGui_ImplOpenGL4_Init(" to "ImGui_ImplOpenGL4_Shutdown();",
        "ImGui_ImplOpenGL4_CreateFontsTexture(" to "ImGui_ImplOpenGL4_DestroyFontsTexture();",
        "ImGui_ImplOpenGL4_CreateDeviceObjects(" to "ImGui_ImplOpenGL4_DestroyDeviceObjects();",
        "ImGui_ImplOpenGL4_NewFrame(" to "ImGui_ImplOpenGL4_RenderDrawData(",

        "ImGui_ImplOpenGL5_Init(" to "ImGui_ImplOpenGL5_Shutdown();",
        "ImGui_ImplOpenGL5_CreateFontsTexture(" to "ImGui_ImplOpenGL5_DestroyFontsTexture();",
        "ImGui_ImplOpenGL5_CreateDeviceObjects(" to "ImGui_ImplOpenGL5_DestroyDeviceObjects();",
        "ImGui_ImplOpenGL5_NewFrame(" to "ImGui_ImplOpenGL5_RenderDrawData(",

        "ImGui_ImplDX7_Init(" to "ImGui_ImplDX7_Shutdown();",
        "ImGui_ImplDX7_NewFrame(" to "ImGui_ImplDX7_RenderDrawData(",
        "ImGui_ImplDX7_CreateDeviceObjects(" to "ImGui_ImplDX7_DestroyDeviceObjects();",

        "ImGui_ImplDX8_Init(" to "ImGui_ImplDX8_Shutdown();",
        "ImGui_ImplDX8_NewFrame(" to "ImGui_ImplDX8_RenderDrawData(",
        "ImGui_ImplDX8_CreateDeviceObjects(" to "ImGui_ImplDX8_DestroyDeviceObjects();",

        "ImGui_ImplDX9_Init(" to "ImGui_ImplDX9_Shutdown();",
        "ImGui_ImplDX9_NewFrame(" to "ImGui_ImplDX9_RenderDrawData(",
        "ImGui_ImplDX9_CreateDeviceObjects(" to "ImGui_ImplDX9_DestroyDeviceObjects();",

        "ImGui_ImplDX10_Init(" to "ImGui_ImplDX10_Shutdown();",
        "ImGui_ImplDX10_NewFrame(" to "ImGui_ImplDX10_RenderDrawData(",
        "ImGui_ImplDX10_CreateDeviceObjects(" to "ImGui_ImplDX10_DestroyDeviceObjects();",

        "ImGui_ImplDX11_Init(" to "ImGui_ImplDX11_Shutdown();",
        "ImGui_ImplDX11_NewFrame(" to "ImGui_ImplDX11_RenderDrawData(",
        "ImGui_ImplDX11_CreateDeviceObjects(" to "ImGui_ImplDX11_DestroyDeviceObjects();",

        "ImGui_ImplDX12_Init(" to "ImGui_ImplDX12_Shutdown();",
        "ImGui_ImplDX12_NewFrame(" to "ImGui_ImplDX12_RenderDrawData(",
        "ImGui_ImplDX12_CreateDeviceObjects(" to "ImGui_ImplDX12_DestroyDeviceObjects();",

        "ImGui_ImplDX13_Init(" to "ImGui_ImplDX13_Shutdown();",
        "ImGui_ImplDX13_NewFrame(" to "ImGui_ImplDX13_RenderDrawData(",
        "ImGui_ImplDX13_CreateDeviceObjects(" to "ImGui_ImplDX13_DestroyDeviceObjects();",

        "ImGui_ImplDX14_Init(" to "ImGui_ImplDX14_Shutdown();",
        "ImGui_ImplDX14_NewFrame(" to "ImGui_ImplDX14_RenderDrawData(",
        "ImGui_ImplDX14_CreateDeviceObjects(" to "ImGui_ImplDX14_DestroyDeviceObjects();",

        "ImGui_ImplDX15_Init(" to "ImGui_ImplDX15_Shutdown();",
        "ImGui_ImplDX15_NewFrame(" to "ImGui_ImplDX15_RenderDrawData(",
        "ImGui_ImplDX15_CreateDeviceObjects(" to "ImGui_ImplDX15_DestroyDeviceObjects();",

        "ImGui_ImplVulkan_Init(" to "ImGui_ImplVulkan_Shutdown();",
        "ImGui_ImplVulkan_NewFrame(" to "ImGui_ImplVulkan_RenderDrawData(",
        "ImGui_ImplVulkan_CreateFontsTexture(" to "ImGui_ImplVulkan_DestroyFontsTexture();",
        "ImGui_ImplVulkan_CreateDeviceObjects(" to "ImGui_ImplVulkan_DestroyDeviceObjects();",

        // More context menu variants
        "ImGui::BeginContextMenu(" to "ImGui::EndContextMenu();",

        // List box
        "ImGui::ListBoxHeader(" to "ImGui::ListBoxFooter();",

        // TreeNode variants
        "ImGui::TreeNode(" to "ImGui::TreePop();",
        "ImGui::TreeNodeEx(" to "ImGui::TreePop();",
        "ImGui::CollapsingHeader(" to "ImGui::TreePop();",

        // Plotting
        "ImGui::PlotLines(" to "ImGui::EndPlot();",
        "ImGui::PlotHistogram(" to "ImGui::EndPlot();",

        // Docking
        "ImGui::DockSpace(" to "ImGui::EndDockSpace();",
        "ImGui::DockSpaceOverViewport(" to "ImGui::EndDockSpace();",

        // Viewports
        "ImGui::CreatePlatformWindow(" to "ImGui::DestroyPlatformWindow();",
        "ImGui::UpdatePlatformWindow(" to "ImGui::RenderPlatformWindowDefault();",

        // Input text
        "ImGui::InputTextMultiline(" to "ImGui::EndInputText();",

        // Columns
        "ImGui::Columns(" to "ImGui::NextColumn(); ImGui::ColumnsEnd();",
        "ImGui::BeginColumns(" to "ImGui::EndColumns();",

        // Memory editor
        "ImGui::MemoryEditor::DrawWindow(" to "ImGui::MemoryEditor::End();",

        // Debug utilities
        "ImGui::DebugStartCapture(" to "ImGui::DebugStopCapture();",
        "ImGui::DebugStartLogToClipboard(" to "ImGui::DebugStopLogToClipboard();",

        // Metrics window
        "ImGui::ShowMetricsWindow(" to "ImGui::EndMetricsWindow();",

        // Demo window
        "ImGui::ShowDemoWindow(" to "ImGui::EndDemoWindow();",

        // Style editor
        "ImGui::ShowStyleEditor(" to "ImGui::EndStyleEditor();",

        // Stack tools
        "ImGui::ShowStackToolWindow(" to "ImGui::EndStackToolWindow();",

        // About window
        "ImGui::ShowAboutWindow(" to "ImGui::EndAboutWindow();",

        // User guide
        "ImGui::ShowUserGuide(" to "ImGui::EndUserGuide();",

        // Platform support
        "ImGui::GetPlatformIO().Platform_CreateWindow(" to "ImGui::GetPlatformIO().Platform_DestroyWindow(",

        // More Vulkan specific
        "ImGui_ImplVulkan_LoadFunctions(" to "ImGui_ImplVulkan_FreeFunctions();",

        // Metal backend
        "ImGui_ImplMetal_Init(" to "ImGui_ImplMetal_Shutdown();",
        "ImGui_ImplMetal_NewFrame(" to "ImGui_ImplMetal_RenderDrawData(",
        "ImGui_ImplMetal_CreateFontsTexture(" to "ImGui_ImplMetal_DestroyFontsTexture();",

        // DirectX additional versions
        "ImGui_ImplDX6_Init(" to "ImGui_ImplDX6_Shutdown();",
        "ImGui_ImplDX16_Init(" to "ImGui_ImplDX16_Shutdown();",

        // OpenGL ES
        "ImGui_ImplOpenGLES1_Init(" to "ImGui_ImplOpenGLES1_Shutdown();",
        "ImGui_ImplOpenGLES2_Init(" to "ImGui_ImplOpenGLES2_Shutdown();",
        "ImGui_ImplOpenGLES3_Init(" to "ImGui_ImplOpenGLES3_Shutdown();",

        // WebGPU
        "ImGui_ImplWGPU_Init(" to "ImGui_ImplWGPU_Shutdown();",

        // SDL backends
        "ImGui_ImplSDL2_InitForOpenGL(" to "ImGui_ImplSDL2_Shutdown();",
        "ImGui_ImplSDL2_InitForVulkan(" to "ImGui_ImplSDL2_Shutdown();",
        "ImGui_ImplSDL2_InitForD3D(" to "ImGui_ImplSDL2_Shutdown();",
        "ImGui_ImplSDL2_InitForMetal(" to "ImGui_ImplSDL2_Shutdown();",

        // Glfw backends
        "ImGui_ImplGlfw_InitForOpenGL(" to "ImGui_ImplGlfw_Shutdown();",
        "ImGui_ImplGlfw_InitForVulkan(" to "ImGui_ImplGlfw_Shutdown();",
        "ImGui_ImplGlfw_InitForOther(" to "ImGui_ImplGlfw_Shutdown();",

        // Android
        "ImGui_ImplAndroid_Init(" to "ImGui_ImplAndroid_Shutdown();",

        // Allegro
        "ImGui_ImplAllegro5_Init(" to "ImGui_ImplAllegro5_Shutdown();",

        // Marmalade
        "ImGui_ImplMarmalade_Init(" to "ImGui_ImplMarmalade_Shutdown();"
    )

    fun initialize() {

    }

}