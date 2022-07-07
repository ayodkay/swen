package com.ayodkay.apps.swen.helper.di

import com.ayodkay.apps.swen.helper.BaseViewModel
import com.ayodkay.apps.swen.helper.mixpanel.MixPanelInterface
import com.ayodkay.apps.swen.helper.mixpanel.MixpanelImplementation
import com.ayodkay.apps.swen.helper.onesignal.OneSignalImplementation
import com.ayodkay.apps.swen.helper.onesignal.OneSignalInterface
import com.ayodkay.apps.swen.helper.onesignal.OneSignalNotificationSender
import com.ayodkay.apps.swen.view.bookmarks.BookmarksViewModel
import com.ayodkay.apps.swen.view.home.HomeViewModel
import com.ayodkay.apps.swen.view.link.LinksViewModel
import com.ayodkay.apps.swen.view.location.LocationViewModel
import com.ayodkay.apps.swen.view.main.MainActivityViewModel
import com.ayodkay.apps.swen.view.search.SearchViewModel
import com.ayodkay.apps.swen.view.settings.SettingsViewModel
import com.ayodkay.apps.swen.view.splash.SplashViewModel
import com.ayodkay.apps.swen.view.theme.ThemeViewModel
import com.ayodkay.apps.swen.view.viewimage.ViewImageViewModel
import com.ayodkay.apps.swen.view.viewnews.ViewNewsViewModel
import com.ayodkay.apps.swen.view.webview.WebViewViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<MixPanelInterface> { MixpanelImplementation() }
    single<OneSignalInterface> { OneSignalImplementation(get()) }
    single { OneSignalNotificationSender }

    viewModel { BaseViewModel(get()) }
    viewModel { BookmarksViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { LinksViewModel(get()) }
    viewModel { LocationViewModel(get()) }
    viewModel { MainActivityViewModel(get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { SplashViewModel(get()) }
    viewModel { ThemeViewModel(get()) }
    viewModel { ViewNewsViewModel(get()) }
    viewModel { ViewImageViewModel(get()) }
    viewModel { WebViewViewModel(get()) }
}
