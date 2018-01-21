package com.clumsy.scanbadge.app;

import com.clumsy.scanbadge.scanners.BadgeListScanner;
import com.clumsy.scanbadge.scanners.ScannerException;

public class ScanBadgeApp 
{
	private static final int DEFAULT_DPI_IPHONE = 326;
	
    public static void main( String[] args )
    {
    	if (args.length!=1) {
    		System.err.println("Syntax: ScanBadgeApp <full path to image file>");
    		System.exit(1);
    	}
    	BadgeListScanner scanner = new BadgeListScanner();
    	try {
    		// Load the image to be scanned
    		scanner.init();
	    	scanner.loadImage(args[0], DEFAULT_DPI_IPHONE);
    		
	    	// check it is a valid gym badge listing
			if (!scanner.isValid()) {
				System.out.println("Not a valid gym badge list screenshot.");
			} else {
				// analyze the image
				scanner.analyze();
			}
    	} catch (ScannerException ex) {
    		System.out.println(ex);
    	} finally {
    		scanner.close();
    	}
    	System.exit(0);
    }
}
