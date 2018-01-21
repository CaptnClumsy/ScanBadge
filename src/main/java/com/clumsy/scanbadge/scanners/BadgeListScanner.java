package com.clumsy.scanbadge.scanners;

import java.awt.Point;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.tesseract.ResultIterator;

public class BadgeListScanner extends BaseScanner {

	// List of characters that can appear in known gym names
	private static final String VALID_GYM_NAME_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890&'-,";
	// Roughly where the list of badges appears on the image
	private static final int BADGE_PAGE_TOP = 128;

	private BytePointer outText;

	// Check if image is indeed a list of gym badges
	public boolean isValid() {
		api.SetVariable("tessedit_char_whitelist", "GYM BADGES");
		api.SetRectangle(0, 42, width, 86);
		outText = api.GetUTF8Text();
		if (outText == null) {
			return false;
		}
		final String str = outText.getString();
        outText.putString("");
        if (str.contains("GYM") && str.contains("BADGES")) {
        	return true;
        } else {
        	return false;
        }
	}
	
	public void analyze() throws ScannerException {
		// Restrict OCR to the part of the image with gym badges
		api.SetRectangle(0, BADGE_PAGE_TOP, width, height-BADGE_PAGE_TOP);
		// Restrict OCR to just certain characters
		api.SetVariable("tessedit_char_whitelist", VALID_GYM_NAME_CHARS);
		// Scan the image
		api.Recognize(null);
		// Iterate over the image word by word
		final int level = org.bytedeco.javacpp.tesseract.RIL_WORD;
		ResultIterator r = api.GetIterator();
		List<BoxedWords> gymList = new ArrayList<BoxedWords>();
		String thisWord = null;
		do {
			// get the scanned word
		    BytePointer bytes = r.GetUTF8Text(level);
			try {
				thisWord = bytes.getString("UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new ScannerException(e.getMessage());
			}
			// get its position
		    IntPointer x1 = new IntPointer(0);
		    IntPointer y1 = new IntPointer(0);
		    IntPointer x2 = new IntPointer(0);
		    IntPointer y2 = new IntPointer(0);
		    // store it in our list of rectangles of text
		    r.BoundingBox(level, x1, y1, x2, y2);
		    BoxedWords b = new BoxedWords(x1.get(), y1.get(), x2.get(), y2.get());
		    b.setWords(thisWord);
		    // Is this rectangle close to the right of an existing one?
		    boolean found = false;
		    for (int i=gymList.size(); i>0; i--) {
		    	BoxedWords gym = gymList.get(i-1);
		    	// If top left of new rectangle is near top right of this gym
		    	// then combine the words
		    	double leftGap = Point.distance(gym.getRect().getX()+gym.getRect().getWidth(), 
		    			gym.getRect().getY(), b.getRect().getX(), b.getRect().getY());
		    	if (leftGap<30) {
		    		found = true;
		    		gym.combineRight(b);
		    		break;
		    	}
		    }
		    if (!found) {
		    	gymList.add(b);
		    }
		} while (r.Next(level));
		
		List<BoxedWords> newGymList = new ArrayList<BoxedWords>(gymList);
	    for (BoxedWords gym : gymList) {
	    	for (int i=0; i<gymList.size(); i++) {
	    		BoxedWords nextGym = gymList.get(i);
	    		if (gym==nextGym || nextGym==null) {
	    			continue;
	    		}
		    	// If next gym is below current gym on the page
	    		if (nextGym.rect.y>gym.rect.y) {
	    			// find the middle of the two rectangles
	    			double nextMiddle = nextGym.rect.x+(nextGym.rect.width/2);
	    			double gymMiddle = gym.rect.x+(gym.rect.width/2);
	    			double leftGap = Math.abs(nextMiddle-gymMiddle);
	    			// If top left of new rectangle is near bottom left of this gym
			    	// and the middle of the words is aligned, then combine the words
		    	    double topGap = nextGym.getRect().getY()-(gym.getRect().getY()+gym.getRect().height);
		    	    if (topGap<30 && leftGap<30) {
		    		    gym.combineBottom(nextGym);
		    		    newGymList.remove(nextGym);
		    		    newGymList.remove(gym);
		    		    newGymList.add(gym);
		    		    break;
		    	    }
	    		}
	    	}
	    }
		
		for (BoxedWords gym : newGymList) {
			// Debug...display what was found
		    System.out.println("word: " + gym.getWords() + "\n" + gym);
		}
	}

	public void close() {
		outText.deallocate();
		super.close();
	}
}
