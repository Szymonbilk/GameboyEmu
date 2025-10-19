/**
 * @author 18bilkiewiczs
 * Class to handle cycling of the system (appropriately ticks the CPU)
 */

package com.szymon.gameboy;

import com.szymon.gameboy.cpu.CPUInterrupts;
import com.szymon.gameboy.cpu.utils.InterruptType;
import com.szymon.gameboy.memory.Memory;
import com.szymon.gameboy.ppu.DMA;
import com.szymon.gameboy.ppu.PPU;
import com.szymon.gameboy.utils.Unsigned16;
import com.szymon.gameboy.utils.Unsigned8;

public class Cycle
{
	// ---------------------------------------------
	// class variables
	// ---------------------------------------------
	private Memory memory;
	private DMA dma;
	private CPUInterrupts interrupts;
	
	private PPU ppu;
	
	private final Unsigned16 u16TIMA_ADDRESS = new Unsigned16(0xFF05);
	private final Unsigned16 u16TMA_ADDRESS = new Unsigned16(0xFF06);
	private final Unsigned16 u16TAC_ADDRESS = new Unsigned16(0xFF07);
	
	private Unsigned8 u8TIMA;
	private Unsigned8 u8TMA;
	private Unsigned8 u8TAC;
	
	private int iTicks;
	
	private int iOverflowTicks;
	private boolean bOverflow;
	private boolean bExecuteOverflow;
	
	private final int iCYCLES_PER_FRAME = 69905;
	private int iCurrentCycles;
	private final long lTARGET_TIME = 16;
	private long lStartTime;
	private long lCurrTime;
	
	// ---------------------------------------------
	// constructors
	// ---------------------------------------------
	//must be created with the same memory used by the CPU
	public Cycle(Memory mem, DMA dmaIn, CPUInterrupts interruptsIn, PPU ppuIn)
	{
		reset(mem, dmaIn, interruptsIn, ppuIn);
	}
	
	// ---------------------------------------------
	// methods
	// ---------------------------------------------
	public void reset(Memory mem, DMA dmaIn, CPUInterrupts interruptsIn, PPU ppuIn)
	{
		ppu = ppuIn;
		
		iTicks = 0;
		dma = dmaIn;
		iOverflowTicks = 0;
		bOverflow = false;
		bExecuteOverflow = false;
		
		memory = mem;
		dma = dmaIn;
		interrupts = interruptsIn;
		
		u8TIMA = memory.readMemory(u16TIMA_ADDRESS);
		u8TMA = memory.readMemory(u16TMA_ADDRESS);
		u8TAC = memory.readMemory(u16TAC_ADDRESS);
		
		iCurrentCycles = 0;
		lStartTime = System.currentTimeMillis();
	}
	
	//cycles the system for the given number of cycles (machine cycles, which is 1/4 of the actual frequency)
	public void cycleEmu(int iCycles)
	{
		for (int i = 0; i < iCycles; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				iCurrentCycles++;
				
				iTicks++;
				tickTimer();
				ppu.tickPPU();
				
				if (iCurrentCycles >= iCYCLES_PER_FRAME)
				{
					//busy wait until the time for a frame has passed
					do
					{
						lCurrTime = System.currentTimeMillis();
					} while ((lCurrTime - lStartTime) < lTARGET_TIME);
					
					iCurrentCycles = 0;
					lStartTime = System.currentTimeMillis();
				}
			}
			
			dma.tickDMA();
		}
	}
	
	//update all the timer registers as appropriate
	private void tickTimer()
	{
		memory.incrementDIV();
		iTicks++;
		
		boolean bUpdateTimer = u8TAC.getBit(2);
		int iUpdateTicks = 0;
		
		if (bUpdateTimer)
		{
			switch (u8TAC.getValue() & 0b11)
			{
			case 0b00:
				iUpdateTicks = 1024;
				break;
			case 0b01:
				iUpdateTicks = 16;
				break;
			case 0b10:
				iUpdateTicks = 64;
				break;
			case 0b11:
				iUpdateTicks = 256;
				break;
			}
			
			if (iTicks % iUpdateTicks == 0)
			{
				//only increment if not in overflow mode
				if (!bOverflow)
				{
					u8TIMA.increment();
				}
				
				//check for overflow
				if (!bOverflow && u8TIMA.getValue() == 0)
				{
					bOverflow = true;
				}
			}
			//if in overflow mode for tima
			if (bOverflow)
			{
				//only request interrupt and set value to tma after 4 cycles
				iOverflowTicks++;
				//only update if tima value hasn't been updated (up until the last cycle)
				if (iOverflowTicks == 3 && u8TIMA.getValue() == 0)
				{
					bExecuteOverflow = true;
				}
				
				if (iOverflowTicks == 4 && bExecuteOverflow)
				{
					u8TIMA.setValue(u8TMA.getValue());
					//request interrupt
					interrupts.requestCPUInterrupt(InterruptType.TIMER);
					iOverflowTicks = 0;
					bOverflow = false;
					bExecuteOverflow = false;
				}
			}
			else 
			{
				bOverflow = false;
				bExecuteOverflow = false;
				iOverflowTicks = 0;
			}
		}
	}
}
