package com.animeinjection.weeblist.injection;

import javax.inject.Qualifier;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class Qualifiers {
  @Qualifier
  @Retention(RUNTIME)
  @Documented
  @Target({FIELD, PARAMETER, METHOD})
  public @interface ApplicationContext {}

  @Qualifier
  @Retention(RUNTIME)
  @Documented
  @Target({FIELD, PARAMETER, METHOD})
  public @interface Ui {}
}
