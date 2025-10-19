/**
 * @author 18bilkiewiczs
 * Class to contain all of the Game Boy's RAM
 */

package com.szymon.gameboy.memory;

import com.szymon.gameboy.utils.Unsigned16;
import com.szymon.gameboy.utils.Unsigned8;

public class RAM 
{
	// ---------------------------------------------
	// class variables
	// ---------------------------------------------
	final private int iWRAM_SIZE = 0x2000;
	final private int iHRAM_SIZE = 0x80;
	final private int iWRAM_START_ADDRESS = 0xC000;
	final private int iHRAM_START_ADDRESS = 0xFF80;
	
	private Unsigned8[] u8WRAM;	//Work RAM, main RAM to be used
	private Unsigned8[] u8HRAM;	//High RAM, small section of RAM
	
	// ---------------------------------------------
	// constructors
	// ---------------------------------------------
	public RAM()
	{
		u8WRAM = new Unsigned8[iWRAM_SIZE];
		u8HRAM = new Unsigned8[iHRAM_SIZE];
		//initialise all the numbers
		for (int i = 0; i < iWRAM_SIZE; i++)
		{
			u8WRAM[i] = new Unsigned8();
		}
		for (int i = 0; i < iHRAM_SIZE; i++)
		{
			u8HRAM[i] = new Unsigned8();
		}
	}
	
	// ---------------------------------------------
	// setters
	// ---------------------------------------------
	public void writeWRAM(Unsigned16 u16Address, Unsigned8 u8Data)
	{
		int iAddress = u16Address.getValue();
		iAddress -= iWRAM_START_ADDRESS;
		u8WRAM[iAddress].setValue(u8Data);
	}
	
	public void writeHRAM(Unsigned16 u16Address, Unsigned8 u8Data)
	{
		int iAddress = u16Address.getValue();
		iAddress -= iHRAM_START_ADDRESS;
		u8HRAM[iAddress].setValue(u8Data);;
	}
	
	// ---------------------------------------------
	// getters
	// ---------------------------------------------
	public Unsigned8 readWRAM(Unsigned16 u16Address)
	{
		int iAddress = u16Address.getValue();
		iAddress -= iWRAM_START_ADDRESS;
		return (u8WRAM[iAddress]);
	}
	
	public Unsigned8 readHRAM(Unsigned16 u16Address)
	{
		int iAddress = u16Address.getValue();
		iAddress -= iHRAM_START_ADDRESS;
		return (u8HRAM[iAddress]);
	}
}
