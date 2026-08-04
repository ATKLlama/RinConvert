package com.example.mediaconverter.ui.viewmodel;

import com.example.mediaconverter.domain.repository.MediaRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<MediaRepository> mediaRepositoryProvider;

  public HomeViewModel_Factory(Provider<MediaRepository> mediaRepositoryProvider) {
    this.mediaRepositoryProvider = mediaRepositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(mediaRepositoryProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<MediaRepository> mediaRepositoryProvider) {
    return new HomeViewModel_Factory(mediaRepositoryProvider);
  }

  public static HomeViewModel newInstance(MediaRepository mediaRepository) {
    return new HomeViewModel(mediaRepository);
  }
}
