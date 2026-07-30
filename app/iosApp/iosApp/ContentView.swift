import UIKit
import SwiftUI
import Shared

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

// iOS 26+ Liquid Glass tab bar: a native TabView owns tab switching (and gets the system glass
// material automatically), while each tab wraps its own ComposeUIViewController that keeps
// owning all navigation within that tab (coin list -> coin detail, etc.) exactly as before.
// Scoped to iPhone only in ContentView below — iPad keeps the existing Compose-driven adaptive
// layout (rail/expanded panel in RootScreen.kt), which a plain two-item TabView doesn't replace.
@available(iOS 26.0, *)
private struct CryptoTabComposeView: UIViewControllerRepresentable {
    @Binding var isBottomBarHidden: Bool

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.CryptoTabViewController { isVisible in
            isBottomBarHidden = !isVisible.boolValue
        }
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

@available(iOS 26.0, *)
private struct StocksTabComposeView: UIViewControllerRepresentable {
    @Binding var isBottomBarHidden: Bool

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.StocksTabViewController { isVisible in
            isBottomBarHidden = !isVisible.boolValue
        }
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

// isBottomBarHidden is shared across both tabs' callbacks: only the Crypto tab ever reports
// false (opening a coin detail screen on compact width — see AppShellViewModel.setDetailMode),
// Stocks always reports true, so there's no fight for control since just one tab is on screen.
@available(iOS 26.0, *)
private struct NativeTabContentView: View {
    @State private var isBottomBarHidden = false

    var body: some View {
        TabView {
            Tab("Crypto", systemImage: "bitcoinsign.circle") {
                CryptoTabComposeView(isBottomBarHidden: $isBottomBarHidden)
                    .ignoresSafeArea(.all)
            }
            Tab("Stocks", systemImage: "chart.line.uptrend.xyaxis") {
                StocksTabComposeView(isBottomBarHidden: $isBottomBarHidden)
                    .ignoresSafeArea(.all)
            }
        }
        .toolbar(isBottomBarHidden ? .hidden : .visible, for: .tabBar)
    }
}

struct ContentView: View {
    var body: some View {
        if #available(iOS 26.0, *), UIDevice.current.userInterfaceIdiom == .phone {
            NativeTabContentView()
        } else {
            ComposeView()
                .ignoresSafeArea(.keyboard)
                .ignoresSafeArea(.container) // Compose has own keyboard handler
        }
    }
}

