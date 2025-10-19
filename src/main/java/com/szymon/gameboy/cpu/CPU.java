/**
 * @author 18bilkiewiczs
 * This class combines all of the relevant classes to emulate the CPU of the Game Boy
 */

package com.szymon.gameboy.cpu;

import com.szymon.gameboy.Cycle;
import com.szymon.gameboy.cpu.utils.Log;
import com.szymon.gameboy.cpu.utils.RegType;
import com.szymon.gameboy.cpu.utils.BlarggConsole;
import com.szymon.gameboy.memory.Memory;
import com.szymon.gameboy.utils.Unsigned16;
import com.szymon.gameboy.utils.Unsigned8;

public class CPU
{
	// ---------------------------------------------
	// class variables
	// ---------------------------------------------
	private Memory memory;	//allows for access to the memory as needed
	private CPURegisters registers;	//contains all of the registers of the CPU
	private CPUProcessing processing;	//handles the main processing to be done by the CPU (fetch, decode and execute)
	private CPUInterrupts interrupts;	//handles any interrupts that are requested
	private Cycle cycle;	//handles the cycling of the system
	
	private boolean bInterruptMasterEnable;	//controls whether any interrupt handlers are called, regardless of the contents of IE
	private boolean bEnablingIME; //indicates whether IME should be enabled in the next CPU cycle, used for EI instruction
	private boolean bHalted; //indicates whether the CPU is halted
	
	//addresses of the Interrupt Flag and Interrupt Enable registers
	//used to check what interrupts have been called, and whether or not they are enabled
	private final Unsigned16 u16IF_ADDRESS = new Unsigned16(0xFF0F);
	private final Unsigned16 u16IE_ADDRESS = new Unsigned16(0xFFFF);
	
	//internal debugging tools
	//BlarggConsole is to run Blargg test ROMs without actual visual output
	//log is used to see the CPU states, and can also be used to compare to Gameboy Doctor
	private BlarggConsole blarggConsole;
	private Log log;
	//if this flag is set, log and blarggConsole are turned on, otherwise they're not used
	private final boolean bDEBUG = false;
	
	// ---------------------------------------------
	// constructors
	// ---------------------------------------------
	//when loading the CPU, it must be loaded with a cart, which is loaded into memory
	public CPU(Memory mem)
	{
		reset(mem);
	}
	
	//loads in the cycle separately to the main constructor, used to allow the same cycle
	//to be used across all classes that require it
	public void loadCycle(Cycle cycleIn)
	{
		cycle = cycleIn;
		processing = new CPUProcessing(this, cycle);
	}
	
	// ---------------------------------------------
	// setters
	// ---------------------------------------------
	//only setters which are required are here
	public void setHalted(boolean bHalt)
	{
		bHalted = bHalt;
	}
	
	public void setInterruptMasterEnable(boolean bEnable)
	{
		bInterruptMasterEnable = bEnable;
	}
	
	public void setEnablingIME(boolean bEnable)
	{
		bEnablingIME = bEnable;
	}
	
	// ---------------------------------------------
	// getters
	// --------------------------------------------
	//only getters that are required are here
	public CPURegisters getCPURegisters()
	{
		return registers;
	}
	
	public Memory getMemory()
	{
		return memory;
	}
	
	public CPUInterrupts getCPUInterrupts()
	{
		return interrupts;
	}
	
	// ---------------------------------------------
	// methods
	// ---------------------------------------------
	//resets the state of the CPU back to a starting state
	public void reset(Memory mem)
	{
		memory = mem;
		registers = new CPURegisters();
		bHalted = false;
		blarggConsole = new BlarggConsole(memory, bDEBUG);
		log = new Log(this, bDEBUG);
		interrupts = new CPUInterrupts(this);
		
		//set initial register values
		registers.setRegValue(RegType.A, new Unsigned8(0x01));
		registers.setRegValue(RegType.F, new Unsigned8(0xB0));
		registers.setRegValue(RegType.B, new Unsigned8(0x00));
		registers.setRegValue(RegType.C, new Unsigned8(0x13));
		registers.setRegValue(RegType.D, new Unsigned8(0x00));
		registers.setRegValue(RegType.E, new Unsigned8(0xD8));
		registers.setRegValue(RegType.H, new Unsigned8(0x01));
		registers.setRegValue(RegType.L, new Unsigned8(0x4D));
		
		registers.setRegValue(RegType.SP, new Unsigned16(0xFFFE));
		registers.setRegValue(RegType.PC, new Unsigned16(0x0100));
	}
	
	//pushes data to the stack
	public void pushStack(Unsigned8 u8Data)
	{
		registers.decrementRegister(RegType.SP);
		Unsigned16 u16SP = (Unsigned16) registers.getRegValue(RegType.SP);
		memory.writeMemory(u16SP, u8Data);
	}
	
	//pushes 2 bytes of data to the stack in on go
	public void pushStack16(Unsigned16 u16Data)
	{
		pushStack(u16Data.getHighByte());
		pushStack(u16Data.getLowByte());
	}
	
	//pops data from the stack
	public Unsigned8 popStack()
	{
		Unsigned8 u8Data = new Unsigned8();
		Unsigned16 u16SP = (Unsigned16) registers.getRegValue(RegType.SP);
		u8Data = memory.readMemory(u16SP);
		registers.incrementRegister(RegType.SP);
		return u8Data;
	}
	
	//pops 2 bytes from the stack in one go
	public Unsigned16 popStack16()
	{
		Unsigned16 u16Data = new Unsigned16();
		u16Data.setLowByte(popStack());
		u16Data.setHighByte(popStack());
		return u16Data;
	}
	
	//this is the main CPU method which controls the CPU's operation
	public boolean stepCPU()
	{
		if (processing == null)
		{
			return true;
		}
		
		//check if in halted mode
		if (!bHalted)
		{	
			//internal debugging
			if (registers.getRegValue(RegType.PC).getValue() == 0x100)
			{
				log.log();
			}
			
			//fetch, decode, execute
			processing.fetchInstruction();
			processing.fetchData();
			processing.execute();
			
			//internal debugging
			log.log();
			blarggConsole.updateDebug();
			blarggConsole.printDebug();
		}
		else 
		{
			cycle.cycleEmu(1);
			//the CPU only exits halt mode if an interrupt is called
			if ((memory.readMemory(u16IF_ADDRESS).getValue() & memory.readMemory(u16IE_ADDRESS).getValue()) != 0)
			{
				bHalted = false;
			}
		}
		
		if (bInterruptMasterEnable)
		{
			interrupts.handleCPUInterrupts();
			bEnablingIME = false;
		}
		//enables the IME for interrupts to be handled on the next cycle
		if (bEnablingIME)
		{
			bInterruptMasterEnable = true;
		}
		
		return true;
	}
}
