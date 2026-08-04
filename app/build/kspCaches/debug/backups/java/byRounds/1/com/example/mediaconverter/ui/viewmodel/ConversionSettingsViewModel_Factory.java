package com.example.mediaconverter.ui.viewmodel;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class ConversionSettingsViewModel_Factory implements Factory<ConversionSettingsViewModel> {
  @Override
  public ConversionSettingsViewModel get() {
    return newInstance();
  }

  public static ConversionSettingsViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ConversionSettingsViewModel newInstance() {
    return new ConversionSettingsViewModel();
  }

  private static final class InstanceHolder {
    private static final ConversionSettingsViewModel_Factory INSTANCE = new ConversionSettingsViewModel_Factory();
  }
}
