package com.clumsy.scanbadge.scanners;

import java.awt.Rectangle;

import lombok.Data;

@Data
public class BoxedWords {
	Rectangle rect;
	private String words;
	
	public BoxedWords(int left, int top, int right, int bottom) {
		this.rect = new Rectangle(left, top, right-left, bottom-top);
	}

	// Grow the rectangle to the right so it consumes the specified rectangle
	public void combineRight(BoxedWords b) {
		this.words += " " + b.getWords();
		double x = this.rect.x;
		if (b.rect.x<this.rect.x) {
			x=b.rect.x;
		}
		double height = (b.rect.height+b.rect.y)-this.rect.y;
		double width = (b.rect.width+b.rect.x)-x;
		rect.setRect(x, this.rect.y, width, height);
	}	

	// Grow the rectangle down so it consumes the specified rectangle
	public void combineBottom(BoxedWords b) {
		this.words += " " + b.getWords();
		double x = this.rect.x;
		if (b.rect.x<this.rect.x) {
			x=b.rect.x;
		}
		double height = (b.rect.height+b.rect.y)-this.rect.y;
		double width = (b.rect.width+b.rect.x)-x;
		rect.setRect(x, this.rect.y, width, height);
	}	

	// Display the top left, bottom right
	@Override
	public String toString() {
	    return rect.x + "," + rect.y + " -> " + (rect.x + rect.width) + "," + (rect.y + rect.height);
	}
	 
	// Compare the rectangles
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof BoxedWords)) {
			return false;
		}
	    return rect.equals(((BoxedWords)o).rect);
	}
	 
	// Rectangles of text are in unique positions so this can be the hash code
	@Override
	public int hashCode() {
	    return rect.hashCode();
	}
}
