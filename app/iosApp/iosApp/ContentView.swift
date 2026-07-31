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
    @Binding var isDetailActive: Bool

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.CryptoTabViewController { isVisible in
            isDetailActive = !isVisible.boolValue
        }
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

@available(iOS 26.0, *)
private struct StocksTabComposeView: UIViewControllerRepresentable {
    @Binding var isDetailActive: Bool

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.StocksTabViewController { isVisible in
            isDetailActive = !isVisible.boolValue
        }
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

// `.toolbar(_:for: .tabBar)` only reliably hides the floating Liquid Glass tab bar when the view
// setting it lives under a NavigationStack — applied directly on the bare TabView (no
// NavigationStack ancestor) the preference has nothing to propagate through and is ignored.
// Each tab therefore gets its own NavigationStack (nav bar hidden — Compose draws its own top
// bar) purely so this toggle has somewhere to attach; no actual push/pop happens; Compose keeps
// fully owning the coin list <-> coin detail transition and its own crossfade animation.
@available(iOS 26.0, *)
private struct NativeTabContentView: View {
    @State private var isCryptoDetailActive = false
    @State private var isStocksDetailActive = false

    var body: some View {
        TabView {
            Tab("Crypto", systemImage: "bitcoinsign.circle") {
                NavigationStack {
                    CryptoTabComposeView(isDetailActive: $isCryptoDetailActive)
                        .ignoresSafeArea(.all)
                        .toolbar(.hidden, for: .navigationBar)
                        .toolbar(isCryptoDetailActive ? .hidden : .visible, for: .tabBar)
                }
            }
            Tab("Stocks", systemImage: "chart.line.uptrend.xyaxis") {
                NavigationStack {
                    StocksTabComposeView(isDetailActive: $isStocksDetailActive)
                        .ignoresSafeArea(.all)
                        .toolbar(.hidden, for: .navigationBar)
                        .toolbar(isStocksDetailActive ? .hidden : .visible, for: .tabBar)
                }
            }
        }
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

