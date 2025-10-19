/**
 * @author 18bilkiewiczs
 * Class that stores the LCD registers, and the palettes that come as a result
 */

package com.szymon.gameboy.ppu;

import com.szymon.gameboy.ppu.utils.DisplayColour;
import com.szymon.gameboy.utils.Unsigned16;
import com.szymon.gameboy.utils.Unsigned8;

public class LCD 
{
    // ---------------------------------------------
 	// class variables
 	// ---------------------------------------------
	private DisplayColour[] BGColours;
	private DisplayColour[] SP1Colours;
	private DisplayColour[] SP2Colours;
	
	private final DisplayColour[] DEFAULT_COLOURS = 
		{DisplayColour.WHITE, DisplayColour.LIGHT, DisplayColour.DARK, DisplayColour.BLACK};
	
	private LCDRegisters lcdRegisters;
	
    // ---------------------------------------------
 	// constructors
 	// ---------------------------------------------
	public LCD()
	{	
		reset();
	}
	
	public void reset()
	{
		lcdRegisters = new LCDRegisters();
		
		BGColours = new DisplayColour[4];
		SP1Colours = new DisplayColour[4];
		SP2Colours = new DisplayColour[4];
		
		for (int i = 0; i < 4; i++)
		{
			BGColours[i] = DEFAULT_COLOURS[i];
			SP1Colours[i] = DEFAULT_COLOURS[i];
			SP2Colours[i] = DEFAULT_COLOURS[i];
		}
	}
	
    // ---------------------------------------------
 	// setters
 	// ---------------------------------------------
	public void writeRegisters(Unsigned16 u16Address, Unsigned8 u8Data)
	{
		lcdRegisters.writeMemory(u16Address, u8Data);
		
		//if a palette register has been updated, update the store palette
		if (u16Address.getValue() >= 0xFF47 && u16Address.getValue() <= 0xFF49)
		{
			int iPalletteNum = u16Address.getValue() - 0xFF47;
			updatePalette(u8Data, iPalletteNum);
		}
	}
	
    // ---------------------------------------------
 	// getters
 	// ---------------------------------------------
	public DisplayColour getBGColour(int iIndex)
	{
		return BGColours[iIndex];
	}
	
	public DisplayColour getSP1Colour(int iIndex)
	{
		return SP1Colours[iIndex];
	}
	
	public DisplayColour getSP2Colour(int iIndex)
	{
		return SP2Colours[iIndex];
	}
	
	public LCDRegisters getRegisters()
	{
		return lcdRegisters;
	}
	
	public Unsigned8 readRegisters(Unsigned16 u16Address)
	{
		return lcdRegisters.readMemory(u16Address);
	}
	
    // ---------------------------------------------
 	// methods
 	// ---------------------------------------------
	//updates a palette based on its number
	public void updatePalette(Unsigned8 u8Data, int iPalletteNum)
	{
		switch (iPalletteNum)
		{
		case 0:
			BGColours = updateSpecificPalette(BGColours, u8Data);
			break;
		case 1:
			SP1Colours = updateSpecificPalette(SP1Colours, u8Data);
			break;
		case 2:
			SP2Colours = updateSpecificPalette(SP2Colours, u8Data);
			break;
		}
	}
	
	//updates the values for a specific palette
	private DisplayColour[] updateSpecificPalette(DisplayColour[] palette, Unsigned8 u8Data)
	{
		palette[0] = DEFAULT_COLOURS[u8Data.getValue() & 0b11];
		palette[1] = DEFAULT_COLOURS[(u8Data.getValue() >>> 2) & 0b11];
		palette[2] = DEFAULT_COLOURS[(u8Data.getValue() >>> 4) & 0b11];
		palette[3] = DEFAULT_COLOURS[(u8Data.getValue() >>> 6) & 0b11];
		
		return palette;
	}
}
