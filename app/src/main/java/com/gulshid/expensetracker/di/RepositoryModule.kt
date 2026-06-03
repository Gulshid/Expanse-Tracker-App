package com.gulshid.expensetracker.di

import com.gulshid.expensetracker.data.repository.AuthRepositoryImpl
import com.gulshid.expensetracker.data.repository.ExpenseRepositoryImpl
import com.gulshid.expensetracker.domain.repository.AuthRepository
import com.gulshid.expensetracker.domain.repository.ExpenseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(
        expenseRepositoryImpl: ExpenseRepositoryImpl
    ): ExpenseRepository
}
