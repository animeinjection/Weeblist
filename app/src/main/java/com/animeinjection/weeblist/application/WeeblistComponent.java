package com.animeinjection.weeblist.application;

import com.animeinjection.weeblist.injection.AppModule;
import com.animeinjection.weeblist.injection.Qualifiers.ApplicationContext;
import dagger.BindsInstance;
import dagger.Component;

@Component(modules = {AppModule.class})
interface WeeblistComponent {

  @Component.Builder
  interface Builder {
    @BindsInstance
    Builder application(@ApplicationContext WeeblistApplication application);

    WeeblistComponent build();
  }

  void inject(WeeblistApplication weeblistApplication);
}
