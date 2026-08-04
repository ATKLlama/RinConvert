package com.example.mediaconverter.data.repository;

import com.example.mediaconverter.data.HistoryDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class MediaRepositoryImpl_Factory implements Factory<MediaRepositoryImpl> {
  private final Provider<HistoryDao> historyDaoProvider;

  public MediaRepositoryImpl_Factory(Provider<HistoryDao> historyDaoProvider) {
    this.historyDaoProvider = historyDaoProvider;
  }

  @Override
  public MediaRepositoryImpl get() {
    return newInstance(historyDaoProvider.get());
  }

  public static MediaRepositoryImpl_Factory create(Provider<HistoryDao> historyDaoProvider) {
    return new MediaRepositoryImpl_Factory(historyDaoProvider);
  }

  public static MediaRepositoryImpl newInstance(HistoryDao historyDao) {
    return new MediaRepositoryImpl(historyDao);
  }
}
