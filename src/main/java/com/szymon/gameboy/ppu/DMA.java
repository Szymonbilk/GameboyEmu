/**
 * @author 18bilkiewiczs
 * Class that adds DMA (Direct Memory Access) functionality
 * This is used as a quick way to copy data into VRAM
 */

package com.szymon.gameboy.ppu;

import com.szymon.gameboy.memory.Memory;
import com.szymon.gameboy.utils.Unsigned16;
import com.szymon.gameboy.utils.Unsigned8;

public class DMA 
{
    // ---------------------------------------------
 	// class variables
 	// ---------------------------------------------
	private boolean bActive;	//indicates whether DMA mode is active
	private Unsigned8 u8Counter;	//used as a counter variable
	private Unsigned8 u8ValueForMem;	//stores the value to be saved in memory
	private int iStartDelay;
	private Memory memory;
	
    // ---------------------------------------------
 	// constructors
 	// ---------------------------------------------
	public DMA()
	{
		bActive = false;
		u8Counter = new Unsigned8();
		u8ValueForMem = new Unsigned8();
		iStartDelay = 0;
	}
	
    // ---------------------------------------------
 	// getters
 	// ---------------------------------------------
	public boolean getActive()
	{
		return bActive;
	}
	
	// ---------------------------------------------
	// methods
	// ---------------------------------------------
	public void initDMA(Memory mem)
	{
		memory = mem;
	}
	
	//starts DMA mode, with the starting address represented by u8Start
	public void startDMA(Unsigned8 u8Start)
	{
		bActive = true;
		u8Counter.setValue(0);
		iStartDelay = 2;
		u8ValueForMem.setValue(u8Start);
	}
	
	//this is run every cycle, ensuring DMA is ran when needed
	public void tickDMA()
	{
		if (!bActive)
		{
			return;
		}
		
		if (iStartDelay > 0)
		{
			iStartDelay--;
			return;
		}
		
		Unsigned16 u16MemAddr = new Unsigned16(u8ValueForMem.getValue() * 0x100);
		u16MemAddr.add(u8Counter);
		Unsigned16 u16OAMAddr = new Unsigned16(0xFE00);
		u16OAMAddr.add(u8Counter);
		memory.writeMemory(u16OAMAddr, memory.readMemory(u16MemAddr));
		
		u8Counter.increment();
		
		if (u8Counter.getValue() < 0xA0)
		{
			bActive = true;
		}
		else 
		{
			bActive = false;
		}
	}
}
