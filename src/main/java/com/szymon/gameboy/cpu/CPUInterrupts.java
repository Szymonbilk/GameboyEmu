/**
 * @author 18bilkiewiczs
 * Class that deals with interrupts specifically
 * Allows for other classes that request interrupts to deal with this class specifically
 * Interrupts in the Game Boy simply push the PC to the stack, and jump to a specific address
 * It is the game developer's responsibility to code what they want to happen for a certain interrupt
 */

package com.szymon.gameboy.cpu;

import com.szymon.gameboy.cpu.utils.InterruptType;
import com.szymon.gameboy.cpu.utils.RegType;
import com.szymon.gameboy.memory.Memory;
import com.szymon.gameboy.utils.Unsigned16;
import com.szymon.gameboy.utils.Unsigned8;

public class CPUInterrupts 
{
	// ---------------------------------------------
	// class variables
	// ---------------------------------------------
	private CPU cpu;
	private CPURegisters registers;
	private Memory memory;
	
	//addresses of the Interrupt Flag and Interrupt Enable registers
	//used to check what interrupts have been called, and whether or not they are enabled
	private final Unsigned16 u16IF_ADDRESS = new Unsigned16(0xFF0F);
	private final Unsigned16 u16IE_ADDRESS = new Unsigned16(0xFFFF);
	
	//addresses that are jumped to depending on the interrupt that is requested
	private final Unsigned16 u16VBLANK_ADDRESS = new Unsigned16(0x0040);
	private final Unsigned16 u16LCD_STAT_ADDRESS = new Unsigned16(0x0048);
	private final Unsigned16 u16TIMER_ADDRESS = new Unsigned16(0x0050);
	private final Unsigned16 u16SERIAL_ADDRESS = new Unsigned16(0x0058);
	private final Unsigned16 u16JOYPAD_ADDRESS = new Unsigned16(0x0060);
	
	// ---------------------------------------------
	// constructors
	// ---------------------------------------------
	public CPUInterrupts(CPU processor)
	{
		reset(processor);
	}
	
	// ---------------------------------------------
	// methods
	// ---------------------------------------------
	//reset back to original state
	private void reset(CPU processor)
	{
		cpu = processor;
		registers = cpu.getCPURegisters();
		memory = cpu.getMemory();
	}
	
	//handles an interrupt by pushing the value of the PC to the stack, and jumping to the specified address
	private void handleInterrupt(Unsigned16 u16Address)
	{
		cpu.pushStack16((Unsigned16) registers.getRegValue(RegType.PC));
		registers.setRegValue(RegType.PC, u16Address);
	}
	
	//checks if an interrupt is to be handled, by checking whether it has been enabled in IE, and is being requested in IF
	private boolean checkInterrupt(Unsigned16 u16Address, InterruptType intType)
	{
		if ((memory.readMemory(u16IF_ADDRESS).getValue() & intType.value) != 0 && (memory.readMemory(u16IE_ADDRESS).getValue() & intType.value) != 0)
		{
			handleInterrupt(u16Address);
			//turn the bit off which correlates to that interrupt in IF to show it has been dealt with
			Unsigned8 u8Data = new Unsigned8(memory.readMemory(u16IF_ADDRESS).getValue() & (~intType.value));
			memory.writeMemory(u16IF_ADDRESS, u8Data);
			//once the interrupt has been handled, the CPU can exit halted mode
			cpu.setHalted(false);
			//IME is disabled once an interrupt has been handled
			cpu.setInterruptMasterEnable(false);
			
			return true;
		}
		
		return false;
	}
	
	//sets the bit in the IF flag pertaining to the specific interrupt
	public void requestCPUInterrupt(InterruptType type)
	{
		Unsigned8 u8Data = new Unsigned8(memory.readMemory(u16IF_ADDRESS).getValue() | type.value);
		memory.writeMemory(u16IF_ADDRESS, u8Data);
	}
	
	//checks all interrupts in the appropriate priority order
	//done with an if statement to ensure that if one interrupt is performed, that no others are performed
	public void handleCPUInterrupts()
	{
		if (checkInterrupt(u16VBLANK_ADDRESS, InterruptType.VBLANK));
		else if (checkInterrupt(u16LCD_STAT_ADDRESS, InterruptType.LCD_STAT));
		else if (checkInterrupt(u16TIMER_ADDRESS, InterruptType.TIMER));
		else if (checkInterrupt(u16SERIAL_ADDRESS, InterruptType.SERIAL));
		else if (checkInterrupt(u16JOYPAD_ADDRESS, InterruptType.JOYPAD));
	}
}
