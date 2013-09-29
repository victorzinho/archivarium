package org.archivarium.inject;

import org.archivarium.ScoreProvider;
import org.archivarium.ScoreProviderException;
import org.archivarium.data.ScoreProviderDataHandler;
import org.archivarium.data.ScoreProviderDataSource;

public interface ScoreDataFactory {
	ScoreProviderDataSource createSource(ScoreProvider provider)
			throws ScoreProviderException;

	ScoreProviderDataHandler createHandler(ScoreProvider provider);
}
