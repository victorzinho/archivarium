package org.archivarium.data;

import static org.mockito.Mockito.mock;
import geomatico.events.EventBus;

import java.util.ResourceBundle;

import org.archivarium.ArchivariumConfig;
import org.archivarium.inject.DefaultUIFactory;
import org.archivarium.inject.ScoreDataFactory;
import org.archivarium.ui.UIFactory;
import org.junit.Before;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class AbstractArchivariumTest {
	protected ArchivariumConfig config;

	@Before
	public void setUp() throws Exception {
		Module module = new AbstractModule() {
			@Override
			protected void configure() {
				config = mock(ArchivariumConfig.class);
				bind(EventBus.class).toInstance(EventBus.getInstance());
				bind(ArchivariumConfig.class).toInstance(config);
				bind(UIFactory.class).to(DefaultUIFactory.class);
				bind(ResourceBundle.class).toInstance(
						ResourceBundle.getBundle("archivarium"));
				install(new FactoryModuleBuilder()
						.implement(ScoreProviderDataSource.class,
								ScoreProviderDataSource.class)
						.implement(ScoreProviderDataHandler.class,
								ScoreProviderDataHandler.class)
						.build(ScoreDataFactory.class));
			}
		};
		Injector injector = Guice.createInjector(module);
		injector.injectMembers(this);

		EventBus.getInstance().removeAllHandlers();
	}

}
