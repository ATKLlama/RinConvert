package com.example.mediaconverter.di;

import android.content.Context;
import com.example.mediaconverter.data.MediaDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class DatabaseModule_ProvideDatabaseFactory implements Factory<MediaDatabase> {
  private final Provider<Context> appContextProvider;

  public DatabaseModule_ProvideDatabaseFactory(Provider<Context> appContextProvider) {
    this.appContextProvider = appContextProvider;
  }

  @Override
  public MediaDatabase get() {
    return provideDatabase(appContextProvider.get());
  }

  public static DatabaseModule_ProvideDatabaseFactory create(Provider<Context> appContextProvider) {
    return new DatabaseModule_ProvideDatabaseFactory(appContextProvider);
  }

  public static MediaDatabase provideDatabase(Context appContext) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideDatabase(appContext));
  }
}
