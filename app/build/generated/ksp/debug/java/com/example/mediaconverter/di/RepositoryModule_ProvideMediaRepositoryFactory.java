package com.example.mediaconverter.di;

import com.example.mediaconverter.data.repository.MediaRepositoryImpl;
import com.example.mediaconverter.domain.repository.MediaRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class RepositoryModule_ProvideMediaRepositoryFactory implements Factory<MediaRepository> {
  private final Provider<MediaRepositoryImpl> repositoryImplProvider;

  public RepositoryModule_ProvideMediaRepositoryFactory(
      Provider<MediaRepositoryImpl> repositoryImplProvider) {
    this.repositoryImplProvider = repositoryImplProvider;
  }

  @Override
  public MediaRepository get() {
    return provideMediaRepository(repositoryImplProvider.get());
  }

  public static RepositoryModule_ProvideMediaRepositoryFactory create(
      Provider<MediaRepositoryImpl> repositoryImplProvider) {
    return new RepositoryModule_ProvideMediaRepositoryFactory(repositoryImplProvider);
  }

  public static MediaRepository provideMediaRepository(MediaRepositoryImpl repositoryImpl) {
    return Preconditions.checkNotNullFromProvides(RepositoryModule.INSTANCE.provideMediaRepository(repositoryImpl));
  }
}
