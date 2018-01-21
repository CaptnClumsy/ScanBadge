package com.clumsy.scanbadge.scanners;

import org.bytedeco.javacpp.BytePointer;

public class PokemonScanner extends BaseScanner {

	private BytePointer outText;
	
	public String getCP() {
		api.SetVariable("tessedit_char_whitelist", "CP1234567890");
		api.SetRectangle(186, 68, 303, 69);  // 186,68  489,137
		outText = api.GetUTF8Text();
		String strCP = outText.getString();
        outText.putString("");
        return strCP;
	}
	
	public void close() {
		outText.deallocate();
		super.close();
	}
}
