package org.archivarium.ui.data;

public class TableDataException extends Exception {
	public TableDataException() {
	}

	public TableDataException(String message) {
		super(message);
	}

	public TableDataException(Throwable cause) {
		super(cause);
	}

	public TableDataException(String message, Throwable cause) {
		super(message, cause);
	}

	public TableDataException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
