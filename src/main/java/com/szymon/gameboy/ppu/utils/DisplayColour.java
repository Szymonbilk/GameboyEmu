/**
 * @author 18bilkiewiczs
 * Enum to represent the 4 possible output colours
 * These colours also store their RGB values, allowing these to be changed
 */

package com.szymon.gameboy.ppu.utils;

public enum DisplayColour 
{
	BLACK(0x000000),
	DARK(0x555555),
	LIGHT(0xAAAAAA),
	WHITE(0xFFFFFF);
	
	private int iRGB;
	
	private DisplayColour(int iVal)
	{
		setRGB(iVal);
	}
	
	public void setRGB(int iVal)
	{
		this.iRGB = iVal;
	}
	
	public int getRGB()
	{
		return iRGB;
	}
}
