package org.archivarium.inject;

import org.archivarium.ScoreProvider;
import org.archivarium.ScoreProviderException;
import org.archivarium.data.ScoreProviderDataHandler;
import org.archivarium.data.ScoreProviderDataSource;
import org.archivarium.data.ScoreSchema;

public interface ScoreDataFactory {
	ScoreProviderDataSource createSource(ScoreProvider provider,
			ScoreSchema schema) throws ScoreProviderException;

	ScoreProviderDataHandler createHandler(ScoreProvider provider,
			ScoreSchema schema);
}
