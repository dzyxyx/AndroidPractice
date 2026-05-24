package com.example.androidpractice.di

import org.koin.dsl.module

val appModule = module {
    single { FilterBadgeCache() }
}
