package com.clumsy.scanbadge.scanners;

public interface Scanner {
	void init() throws ScannerException;
	boolean isValid();
	void close();
}
