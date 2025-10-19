/**
 * @author 18bilkiewiczs
 * Class to store the relevant graphics memory
 * (OAM and VRAM)
 */

package com.szymon.gameboy.memory;

import com.szymon.gameboy.utils.Unsigned16;
import com.szymon.gameboy.utils.Unsigned8;

public class VRAM 
{
	// ---------------------------------------------
	// class variables
	// ---------------------------------------------
	final private int iOAM_SIZE = 0xA0;
	final private int iVRAM_SIZE = 0x2000;
	final private int iOAM_START_ADDRESS = 0xFE00;
	final private int iVRAM_START_ADDRESS = 0x8000;
	
	private Unsigned8[] u8OAM;
	private Unsigned8[] u8VRAM;
	
	// ---------------------------------------------
	// constructors
	// ---------------------------------------------
	public VRAM()
	{
		u8OAM = new Unsigned8[iOAM_SIZE];
		u8VRAM = new Unsigned8[iVRAM_SIZE];
		//initialise all the numbers
		for (int i = 0; i < iOAM_SIZE; i++)
		{
			u8OAM[i] = new Unsigned8();
		}
		for (int i = 0; i < iVRAM_SIZE; i++)
		{
			u8VRAM[i] = new Unsigned8();
		}
	}
	
	// ---------------------------------------------
	// setters
	// ---------------------------------------------
	public void writeOAM(Unsigned16 u16Address, Unsigned8 u8Data)
	{
		int iAddress = u16Address.getValue();
		iAddress -= iOAM_START_ADDRESS;
		u8OAM[iAddress].setValue(u8Data);
	}
	
	public void writeVRAM(Unsigned16 u16Address, Unsigned8 u8Data)
	{
		int iAddress = u16Address.getValue();
		iAddress -= iVRAM_START_ADDRESS;
		u8VRAM[iAddress].setValue(u8Data);;
	}
	
	// ---------------------------------------------
	// getters
	// ---------------------------------------------
	public Unsigned8 readOAM(Unsigned16 u16Address)
	{
		int iAddress = u16Address.getValue();
		iAddress -= iOAM_START_ADDRESS;
		return (u8OAM[iAddress]);
	}
	
	public Unsigned8 readVRAM(Unsigned16 u16Address)
	{
		int iAddress = u16Address.getValue();
		iAddress -= iVRAM_START_ADDRESS;
		return (u8VRAM[iAddress]);
	}
}
