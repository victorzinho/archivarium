package org.archivarium.inject;

import geomatico.events.EventBus;

import java.util.ResourceBundle;

import org.archivarium.Launcher;
import org.archivarium.ScoreProvider;
import org.archivarium.data.ScoreProviderDataHandler;
import org.archivarium.data.ScoreProviderDataSource;
import org.archivarium.impl.H2ScoreProvider;
import org.archivarium.ui.UIFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

public class ArchivariumModule extends AbstractModule implements
		Provider<ScoreProvider> {
	private ScoreProvider local;

	@Override
	protected void configure() {
		EventBus bus = EventBus.getInstance();

		bind(EventBus.class).toInstance(bus);
		bind(UIFactory.class).to(DefaultUIFactory.class);
		bind(ResourceBundle.class).toInstance(
				ResourceBundle.getBundle("archivarium"));
		bind(ScoreProvider.class).annotatedWith(Names.named("local"))
				.toProvider(this);
		install(new FactoryModuleBuilder()
				.implement(ScoreProviderDataSource.class,
						ScoreProviderDataSource.class)
				.implement(ScoreProviderDataHandler.class,
						ScoreProviderDataHandler.class)
				.build(ScoreDataFactory.class));
	}

	@Override
	public ScoreProvider get() {
		if (local == null) {
			try {
				local = new H2ScoreProvider(Launcher.getDatabase());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return local;
	}
}
