package com.vcyberpunk.notes.di

import com.vcyberpunk.notes.data.repository.NotesRepositoryImpl
import com.vcyberpunk.notes.domain.repository.NotesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DomainModule {

    @Singleton
    @Binds
    fun bindNotesRepository(impl: NotesRepositoryImpl): NotesRepository

}