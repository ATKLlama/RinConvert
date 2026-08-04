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
public final class HistoryViewModel_Factory implements Factory<HistoryViewModel> {
  private final Provider<MediaRepository> mediaRepositoryProvider;

  public HistoryViewModel_Factory(Provider<MediaRepository> mediaRepositoryProvider) {
    this.mediaRepositoryProvider = mediaRepositoryProvider;
  }

  @Override
  public HistoryViewModel get() {
    return newInstance(mediaRepositoryProvider.get());
  }

  public static HistoryViewModel_Factory create(Provider<MediaRepository> mediaRepositoryProvider) {
    return new HistoryViewModel_Factory(mediaRepositoryProvider);
  }

  public static HistoryViewModel newInstance(MediaRepository mediaRepository) {
    return new HistoryViewModel(mediaRepository);
  }
}
