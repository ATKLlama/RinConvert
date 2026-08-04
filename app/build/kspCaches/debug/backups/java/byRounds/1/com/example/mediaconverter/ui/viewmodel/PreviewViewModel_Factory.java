package com.example.mediaconverter.ui.viewmodel;

import android.content.Context;
import androidx.work.WorkManager;
import com.example.mediaconverter.domain.repository.MediaRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class PreviewViewModel_Factory implements Factory<PreviewViewModel> {
  private final Provider<Context> applicationContextProvider;

  private final Provider<WorkManager> workManagerProvider;

  private final Provider<MediaRepository> mediaRepositoryProvider;

  public PreviewViewModel_Factory(Provider<Context> applicationContextProvider,
      Provider<WorkManager> workManagerProvider,
      Provider<MediaRepository> mediaRepositoryProvider) {
    this.applicationContextProvider = applicationContextProvider;
    this.workManagerProvider = workManagerProvider;
    this.mediaRepositoryProvider = mediaRepositoryProvider;
  }

  @Override
  public PreviewViewModel get() {
    return newInstance(applicationContextProvider.get(), workManagerProvider.get(), mediaRepositoryProvider.get());
  }

  public static PreviewViewModel_Factory create(Provider<Context> applicationContextProvider,
      Provider<WorkManager> workManagerProvider,
      Provider<MediaRepository> mediaRepositoryProvider) {
    return new PreviewViewModel_Factory(applicationContextProvider, workManagerProvider, mediaRepositoryProvider);
  }

  public static PreviewViewModel newInstance(Context applicationContext, WorkManager workManager,
      MediaRepository mediaRepository) {
    return new PreviewViewModel(applicationContext, workManager, mediaRepository);
  }
}
