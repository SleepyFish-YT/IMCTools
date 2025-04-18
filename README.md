# IntelliJ Platform Plugin for CLion
##### This plugin is made on the IntelliJ Platform Example from JetBrains.

# 🛠️ ImGui Productivity Tools - IMCTools
**Turbocharge your Dear ImGui workflow with smart color editing + auto-formatting!**  

## ✨ Features  

### 🎨 Visual Color Editing
- Click gutter icons to modify colors directly in your code
- Supports all ImGui color formats:
  ```cpp
  ImColor(255, 0, 0)                // Integer RGB
  ImVec4(1.0f, 0.5f, 0.0f, 1.0f)    // Float RGBA
  float[4]{0.2f, 0.2f, 0.2f, 1.0f}  // Array style
  ```
  
### Examples:

![imgui_draw.cpp:145](https://github.com/user-attachments/assets/d3388d73-ed1b-49de-8a63-4e8d09c368b3)
![imgui_draw.cpp:193](https://github.com/user-attachments/assets/310a099b-08d6-472e-8e02-275fe016ba3c)
Same color picker we all love from IntelliJ IDE when working with jawa.awt.Color
  
### 📏 Smart Code Formatting
Automatic line breaks for ImGui blocks:
- Proper indentation for widget hierarchies
- Formatting preserves your code style

This code:
```cpp
ImGui::Begin("Window"); {user_enter}
```
Will be autmatically formatted to this:
```cpp
ImGui::Begin("Window");
{
  {user_cursor_will_be_set_here}
}
ImGui::End();
```

### 🚀 Installation
Marketplace: Not Uploaded yet.
Manual:
1. Download the .jar from releases
2. Go in your plugins window
3. Press the gear icon and select install from disk
4. Select the .jar

### 🔌 Compatibility
- Primary IDE: CLion (other JetBrains IDEs may work)
- Languages: C/C++
- ImGui Versions: Works with all Dear ImGui versions

###### Signed by SleepyFish.
