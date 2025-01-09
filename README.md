This is a Kotlin Multiplatform project targeting Android, iOS, Web, Desktop.

<table>
  <tr>
    <th>Android</th>
    <th>iOS</th>
  </tr>
  <tr>
    <td><video src="https://github.com/user-attachments/assets/addecced-d20f-42d6-a02d-27293ce14f4d"></td>
    <td><video src="https://github.com/user-attachments/assets/d69b64ae-29c7-4c4c-8144-edc40bcc315f"></td>
  </tr>
  <tr>
    <th colspan="2">Desktop</th>
  </tr>
  <tr>
    <td colspan="2"><video src="https://github.com/user-attachments/assets/0e80f837-89d0-407e-a636-1b4a4b92390f"></td>
  </tr>
  <tr>
    <th colspan="2">Web</th>
  </tr>
  <tr>
    <td colspan="2"><video src="https://github.com/user-attachments/assets/48bc0159-ca51-45b8-b8b4-b79aa8e8f44b"></td>
  </tr>
</table>





* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform, 
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [GitHub](https://github.com/JetBrains/compose-multiplatform/issues).

You can open the web application by running the `:composeApp:wasmJsBrowserDevelopmentRun` Gradle task.
