package com.clumsy.scanbadge.scanners;

import static org.bytedeco.javacpp.lept.pixDestroy;
import static org.bytedeco.javacpp.lept.pixRead;
import static org.bytedeco.javacpp.lept.pixGetWidth;
import static org.bytedeco.javacpp.lept.pixGetHeight;

import org.bytedeco.javacpp.lept.PIX;
import org.bytedeco.javacpp.tesseract.TessBaseAPI;

public class BaseScanner implements Scanner {
	protected TessBaseAPI api;
	protected PIX image;
    protected int width;
    protected int height;

	public BaseScanner() {
		this.api = new TessBaseAPI();
	}

	public void close() {
		if (image != null) {
	 	    pixDestroy(image);
		    image = null;
		}
	    api.End();
        api.close();
        api = null;
	}

	public void loadImage(final String filename, final int dpi) throws ScannerException {
		image = pixRead(filename);
		if ( image == null ) {
			throw new ScannerException("Could not load image.");
		}
		api.SetImage(image);
		api.SetSourceResolution(dpi);
		width=pixGetWidth(image);
		height=pixGetHeight(image);
	}

	public void init() throws ScannerException {
		if (api.Init(System.getenv("TESSDATA_PREFIX"), "ENG") != 0) {
			throw new ScannerException("Failed to initialize tesseract");
        }
	}

	public boolean isValid() {
		return true;
	}
    
}
