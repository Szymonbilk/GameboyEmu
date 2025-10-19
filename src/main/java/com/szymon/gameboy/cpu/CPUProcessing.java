/**
 * @author 18bilkiewiczs
 * Class that handles the main processing of the CPU
 * Including fetching, decoding and executing instructions
 */

package com.szymon.gameboy.cpu;

import com.szymon.gameboy.cpu.utils.RegType;
import com.szymon.gameboy.memory.Memory;
import com.szymon.gameboy.Cycle;
import com.szymon.gameboy.cpu.utils.AddrMode;
import com.szymon.gameboy.cpu.utils.CondType;
import com.szymon.gameboy.utils.BitOps;
import com.szymon.gameboy.utils.Unsigned16;
import com.szymon.gameboy.utils.Unsigned8;

public class CPUProcessing 
{
	// ---------------------------------------------
	// class variables
	// ---------------------------------------------
	private CPU cpu;
	private Memory memory;
	private CPURegisters registers;
	private Cycle cycle;
	
	private int iCurOpcode;	//stores the fetched byte for an instruction
	private Instruction curInstruction;
	
	//u16 used for fetched data as it may be 8 bit or 16 bit
	private Unsigned16 u16FetchedData;
	private Unsigned16 u16MemAddress;
	
	private boolean bDestIsMem;
	
	// ---------------------------------------------
	// constructors
	// ---------------------------------------------
	public CPUProcessing(CPU processor, Cycle cycleIn)
	{
		reset(processor, cycleIn);
	}
	
	// ---------------------------------------------
	// getters
	// ---------------------------------------------
	public int getOpcode()
	{
		return iCurOpcode;
	}
	
	public int getFetchedData()
	{
		return u16FetchedData.getValue();
	}
	
	// ---------------------------------------------
	// methods
	// ---------------------------------------------
	//reset to default state
	public void reset(CPU processor, Cycle cycleIn)
	{
		cpu = processor;
		memory = cpu.getMemory();
		registers = cpu.getCPURegisters();
		
		iCurOpcode = 0;
		curInstruction = new Instruction();
		cycle = cycleIn;
		u16FetchedData = new Unsigned16();
		u16MemAddress = new Unsigned16();
		bDestIsMem = false;
	}
	
	//fetches the next instruction at the PC value, and increments the PC
	//also sets the instruction
	public void fetchInstruction()
	{
		iCurOpcode = memory.readMemory((Unsigned16) registers.getRegValue(RegType.PC)).getValue() & 0xFF;
		registers.incrementRegister(RegType.PC);
		curInstruction.setInstruction(iCurOpcode);
		
		cycle.cycleEmu(1);
	}
	
	//dependent on the addressing mode, fetch the appropriate data
	public void fetchData()
	{
		Unsigned16 u16TempAddress = new Unsigned16();
		u16MemAddress.setValue(0);
		bDestIsMem = false;
		
		switch(curInstruction.getAddrMode())
		{
		case IMP: return;
		case R:	
			u16FetchedData.setValue(registers.getRegValue(curInstruction.getReg1()));
			return;
		case R_R:
			u16FetchedData.setValue(registers.getRegValue(curInstruction.getReg2()));
			return;
		case R_D8:
			u16FetchedData.setValue(memory.readMemory((Unsigned16) registers.getRegValue(RegType.PC)));
			registers.incrementRegister(RegType.PC);
			cycle.cycleEmu(1);
			return;
		case R_D16:
		case D16:
			u16FetchedData.setLowByte(memory.readMemory((Unsigned16) registers.getRegValue(RegType.PC)));
			registers.incrementRegister(RegType.PC);
			cycle.cycleEmu(1);
			u16FetchedData.setHighByte(memory.readMemory((Unsigned16) registers.getRegValue(RegType.PC)));
			registers.incrementRegister(RegType.PC);
			cycle.cycleEmu(1);
			return;
		case MR_R:
			u16FetchedData.setValue(registers.getRegValue(curInstruction.getReg2()));
			u16MemAddress.setValue(registers.getRegValue(curInstruction.getReg1()));
			bDestIsMem = true;
			
			if (curInstruction.getReg1() == RegType.C)
			{
				u16MemAddress.setValue(u16MemAddress.getValue() | 0xFF00);
			}
			return;
		case R_MR:
			u16TempAddress.setValue(registers.getRegValue(curInstruction.getReg2()));
			
			if (curInstruction.getReg2() == RegType.C)
			{
				u16TempAddress.setValue(u16TempAddress.getValue() | 0xFF00);
			}
			
			u16FetchedData.setValue(memory.readMemory(u16TempAddress));
			cycle.cycleEmu(1);
			return;
		case R_HLI:
			//get data from hl, increment it
			u16FetchedData.setValue(memory.readMemory((Unsigned16) registers.getRegValue(RegType.HL)));
			cycle.cycleEmu(1);
			registers.incrementRegister(RegType.HL);
			return;
		case R_HLD:
			//get data from hl, increment it
			u16FetchedData.setValue(memory.readMemory((Unsigned16) registers.getRegValue(RegType.HL)));
			cycle.cycleEmu(1);
			registers.decrementRegister(RegType.HL);
			return;
		case HLI_R:
			u16FetchedData.setValue(registers.getRegValue(curInstruction.getReg2()));
			u16MemAddress.setValue(registers.getRegValue(curInstruction.getReg1()));
			bDestIsMem = true;
			registers.incrementRegister(RegType.HL);
			return;
		case HLD_R:
			u16FetchedData.setValue(registers.getRegValue(curInstruction.getReg2()));
			u16MemAddress.setValue(registers.getRegValue(curInstruction.getReg1()));
			bDestIsMem = true;
			registers.decrementRegister(RegType.HL);
			return;
		case R_A8:
			u16FetchedData.setValue(memory.readMemory((Unsigned16) registers.getRegValue(RegType.PC)));
			cycle.cycleEmu(1);
			registers.incrementRegister(RegType.PC);
			return;
		case A8_R:
			u16MemAddress.setValue(memory.readMemory((Unsigned16) registers.getRegValue(RegType.PC)).getValue() | 0xFF00);
			bDestIsMem = true;
			cycle.cycleEmu(1);
			registers.incrementRegister(RegType.PC);
			return;
		case HL_SPR:
			//special case, instruction F8
			u16FetchedData.setValue(memory.readMemory((Unsigned16) registers.getRegValue(RegType.PC)));
			cycle.cycleEmu(1);
			registers.incrementRegister(RegType.PC);
			return;
		case D8:
			u16FetchedData.setValue(memory.readMemory((Unsigned16) registers.getRegValue(RegType.PC)));
			cycle.cycleEmu(1);
			registers.incrementRegister(RegType.PC);
			return;
		case A16_R:
			u16MemAddress.setLowByte(memory.readMemory((Unsigned16) registers.getRegValue(RegType.PC)));
			registers.incrementRegister(RegType.PC);
			cycle.cycleEmu(1);
			u16MemAddress.setHighByte(memory.readMemory((Unsigned16) registers.getRegValue(RegType.PC)));
			registers.incrementRegister(RegType.PC);
			cycle.cycleEmu(1);
			bDestIsMem = true;
			u16FetchedData.setValue(registers.getRegValue(curInstruction.getReg2()));
			return;
		case MR_D8:
			u16FetchedData.setValue(memory.readMemory((Unsigned16) registers.getRegValue(RegType.PC)));
			cycle.cycleEmu(1);
			registers.incrementRegister(RegType.PC);
			u16MemAddress.setValue(registers.getRegValue(curInstruction.getReg1()));
			bDestIsMem = true;
			return;
		case MR:
			u16MemAddress.setValue(registers.getRegValue(curInstruction.getReg1()));
			bDestIsMem = true;
			u16FetchedData.setValue(registers.getRegValue(curInstruction.getReg1()));
			cycle.cycleEmu(1);
			return;
		case R_A16:
			u16TempAddress.setLowByte(memory.readMemory((Unsigned16) registers.getRegValue(RegType.PC)));
			registers.incrementRegister(RegType.PC);
			cycle.cycleEmu(1);
			u16TempAddress.setHighByte(memory.readMemory((Unsigned16) registers.getRegValue(RegType.PC)));
			registers.incrementRegister(RegType.PC);
			cycle.cycleEmu(1);
			u16FetchedData.setValue(memory.readMemory(u16TempAddress));
			cycle.cycleEmu(1);
			return;
		default:
			System.out.println(curInstruction.getAddrMode());
			System.err.println("Unkown Addressing Mode!");
			System.exit(0);
			return;
		}
	}
	
	//processes the instruction based on the type of instruction
	public void execute()
	{
		//flag setting
		int iZ = -1;
		int iH = -1;
		int iN = -1;
		int iC = -1;
		//general values to be used
		Unsigned8 u8Num = new Unsigned8();
		Unsigned16 u16Num = new Unsigned16();
		
		int iTemp = 0;
		
		switch (curInstruction.getInsType())
		{
		case NONE:
			System.err.println("INVALID INSTRUCTION");
			System.exit(0);
			break;
		case NOP:
			break;
		case LD:
			//LD (BC), A for instance
			if (bDestIsMem)
			{
				//if 16 bit register
				if (!registers.check8Bit(curInstruction.getReg2()) && curInstruction.getReg2() != RegType.NONE)
				{
					cycle.cycleEmu(1);
					memory.writeMemory16(u16MemAddress, u16FetchedData);
				}
				else 
				{
					memory.writeMemory(u16MemAddress, u16FetchedData.getLowByte());
				}
				
				break;
			}
			
			//special case
			if (curInstruction.getAddrMode() == AddrMode.HL_SPR)
			{
				Unsigned16 u16Reg = new Unsigned16();
				u16Reg.setValue(registers.getRegValue(curInstruction.getReg2()));
				byte s8 = (byte) u16FetchedData.getValue();	//byte so it is signed
				u16Num.setValue(u16Reg);
				u16Num.addAsSigned8(s8);
				
				//consider seperately when negative and when positive when setting h and c flags
				if (s8 >= 0)
				{
					iC = BitOps.convertBoolToInt(((u16Reg.getValue() & 0xFF) + s8) > 0xFF);
					iH = BitOps.convertBoolToInt(((u16Reg.getValue() & 0xF) + (s8 & 0xF)) > 0xF);
				}
				else
				{
					iC = BitOps.convertBoolToInt((u16Num.getValue() & 0xFF) <= (u16Reg.getValue() & 0xFF));
					iH = BitOps.convertBoolToInt((u16Num.getValue() & 0xF) <= (u16Reg.getValue() & 0xF));
				}
				
				iZ = 0;
				iN = 0;
				
				registers.setRegValue(curInstruction.getReg1(), u16Num);
			
				break;
			}
			
			registers.setRegValue(curInstruction.getReg1(), u16FetchedData);
			break;
		case LDH:
			cycle.cycleEmu(1);
			
			if (curInstruction.getReg1() == RegType.A)
			{
				u16Num.setValue(u16FetchedData.getValue() | 0xFF00);
				registers.setRegValue(curInstruction.getReg1(), memory.readMemory(u16Num));
			}
			else 
			{
				memory.writeMemory(u16MemAddress, (Unsigned8) registers.getRegValue(curInstruction.getReg2()));
			}
			break;
		case INC:
			u16Num.setValue(registers.getRegValue(curInstruction.getReg1()));
			u16Num.increment();
			
			//special behaviour when 16 bit
			if (!registers.check8Bit(curInstruction.getReg1()))
			{
				cycle.cycleEmu(1);
			}
			
			if (curInstruction.getReg1() == RegType.HL && curInstruction.getAddrMode() == AddrMode.MR)
			{
				u16Num.setValue(memory.readMemory((Unsigned16) registers.getRegValue(RegType.HL)));
				u16Num.increment();
				memory.writeMemory((Unsigned16) registers.getRegValue(RegType.HL), u16Num.getLowByte());
				
				iZ = BitOps.convertBoolToInt(u16Num.getLowByte().getValue() == 0);
			}
			else 
			{
				registers.setRegValue(curInstruction.getReg1(), u16Num);
				
				if ((iCurOpcode & 0x03) != 0x03)
				{
					iZ = BitOps.convertBoolToInt(u16Num.getValue() == 0);
				}
			}
			
			//opcodes that end in 0xX3 don't set any flags
			if ((iCurOpcode & 0x03) != 0x03)
			{
				if (registers.check8Bit(curInstruction.getReg1()))
				{
					iZ = BitOps.convertBoolToInt(u16Num.getLowByte().getValue() == 0);
				}
				iN = 0;
				iH = BitOps.convertBoolToInt((u16Num.getValue() & 0x0F) == 0);
			}
			break;
		case ADC:
			int u = u16FetchedData.getValue();
			int a = registers.getRegValue(RegType.A).getValue();
			int flag = BitOps.convertBoolToInt(registers.getCFlag());
			
			iTemp = (u + a + flag) & 0xFF;
			registers.setRegValue(RegType.A, new Unsigned8(iTemp));
			
			iZ = BitOps.convertBoolToInt(iTemp == 0);
			iN = 0;
			iH = BitOps.convertBoolToInt((u & 0x0F) + (a & 0x0F) + flag > 0xF);
			iC = BitOps.convertBoolToInt(u + a + flag > 0xFF);
			break;
		case ADD:
			u16Num.setValue(registers.getRegValue(curInstruction.getReg1()));
			u16Num.add(u16FetchedData);
			iN = 0;
			
			if (!registers.check8Bit(curInstruction.getReg1()))
			{
				cycle.cycleEmu(1);
				iH = BitOps.convertBoolToInt((registers.getRegValue(curInstruction.getReg1()).getValue() & 0x0FFF) + (u16FetchedData.getValue() & 0x0FFF) >= 0x1000);
				iC = BitOps.convertBoolToInt((registers.getRegValue(curInstruction.getReg1()).getValue() & 0xFFFF) + (u16FetchedData.getValue() & 0xFFFF) >= 0x10000);
				
				if (curInstruction.getReg1() == RegType.SP)
				{
					u16Num.setValue(registers.getRegValue(curInstruction.getReg1()));
					u16Num.addAsSigned8(u16FetchedData);
					
					iZ = 0;
					iH = BitOps.convertBoolToInt((registers.getRegValue(curInstruction.getReg1()).getValue() & 0x0F) + (u16FetchedData.getValue() & 0x0F) >= 0x10);
					iC = BitOps.convertBoolToInt((registers.getRegValue(curInstruction.getReg1()).getValue() & 0xFF) + (u16FetchedData.getValue() & 0xFF) >= 0x100);
				}
			}
			else 
			{
				u8Num.setValue(u16Num.getLowByte());
				iZ = BitOps.convertBoolToInt(u8Num.getValue() == 0);
				iH = BitOps.convertBoolToInt((registers.getRegValue(curInstruction.getReg1()).getValue() & 0x0F) + (u16FetchedData.getValue() & 0x0F) >= 0x10);
				iC = BitOps.convertBoolToInt((registers.getRegValue(curInstruction.getReg1()).getValue() & 0xFF) + (u16FetchedData.getValue() & 0xFF) >= 0x100);
			}
			
			registers.setRegValue(curInstruction.getReg1(), u16Num);
			break;
		case AND:
			u8Num.setValue(registers.getRegValue(RegType.A).getValue() & u16FetchedData.getValue());
			registers.setRegValue(RegType.A, u8Num);
			iZ = BitOps.convertBoolToInt(u8Num.getValue() == 0);
			iN = 0;
			iH = 1;
			iC = 0;
			break;
		case CALL:
			goToAddr(u16FetchedData, true);
			break;
		case CB:
			int op = u16FetchedData.getValue();
			RegType reg = registers.decodeRegForCB(op & 0b111);
			int bit = (op >>> 3) & 0b111;
			int bitOp = (op >>> 6) & 0b11;
			int regVal = getCPUReg8(reg).getValue();
			
			cycle.cycleEmu(1);
			
			if (reg == RegType.HL)
			{
				cycle.cycleEmu(2);
			}
			
			switch (bitOp) 
			{
			case 1:
				//BIT
				iZ = BitOps.convertBoolToInt(!BitOps.getBit(regVal, bit));
				iN = 0;
				iH = 1;
				break;
			case 2:
				//RES
				regVal = BitOps.clearBit(regVal, bit);
				setCPUReg8(reg, new Unsigned8(regVal));
				break;
			case 3:
				//SET
				regVal = BitOps.setBit(regVal, bit);
				setCPUReg8(reg, new Unsigned8(regVal));
				break;
			default:
				boolean flagC = registers.getCFlag();
				switch (bit)
				{
				case 0:
					//RLC
					iTemp = regVal << 1;
					iTemp |= BitOps.convertBoolToInt(BitOps.getBit(regVal, 7));
					u8Num.setValue(iTemp);
					
					setCPUReg8(reg, u8Num);
					iZ = BitOps.convertBoolToInt(u8Num.getValue() == 0);
					iN = 0;
					iH = 0;
					iC = BitOps.convertBoolToInt(BitOps.getBit(regVal, 7));
					break;
				case 1:
					//RRC
					iTemp = regVal >>> 1;
					iTemp |= (regVal << 7);
					u8Num.setValue(iTemp);
					
					setCPUReg8(reg, u8Num);
					iZ = BitOps.convertBoolToInt(u8Num.getValue() == 0);
					iN = 0;
					iH = 0;
					iC = BitOps.convertBoolToInt(BitOps.getBit(regVal, 0));
					break;
				case 2:
					//RL
					iTemp = regVal << 1;
					iTemp |= BitOps.convertBoolToInt(flagC);
					u8Num.setValue(iTemp);
					
					setCPUReg8(reg, u8Num);
					iZ = BitOps.convertBoolToInt(u8Num.getValue() == 0);
					iN = 0;
					iH = 0;
					iC = BitOps.convertBoolToInt(BitOps.getBit(regVal, 7));
					break;
				case 3:
					//RR
					iTemp = regVal >>> 1;
					iTemp |= (BitOps.convertBoolToInt(flagC) << 7);
					u8Num.setValue(iTemp);
					
					setCPUReg8(reg, u8Num);
					iZ = BitOps.convertBoolToInt(u8Num.getValue() == 0);
					iN = 0;
					iH = 0;
					iC = BitOps.convertBoolToInt(BitOps.getBit(regVal, 0));
					break;
				case 4:
					//SLA
					iTemp = regVal << 1;
					u8Num.setValue(iTemp);
					
					setCPUReg8(reg, u8Num);
					iZ = BitOps.convertBoolToInt(u8Num.getValue() == 0);
					iN = 0;
					iH = 0;
					iC = BitOps.convertBoolToInt(BitOps.getBit(regVal, 7));
					break;
				case 5:
					//SRA
					//bit 7 remains the same
					iTemp = (regVal >>> 1) | (regVal & 0x80);
					u8Num.setValue(iTemp);
					
					setCPUReg8(reg, u8Num);
					iZ = BitOps.convertBoolToInt(u8Num.getValue() == 0);
					iN = 0;
					iH = 0;
					iC = BitOps.convertBoolToInt(BitOps.getBit(regVal, 0));
					break;
				case 6:
					//SWAP
					int highNibble = (regVal & 0xF0) >>> 4;
					int lowNibble = regVal & 0xF;
					iTemp = (lowNibble << 4) | highNibble;
					u8Num.setValue(iTemp);
					
					setCPUReg8(reg, u8Num);
					iZ = BitOps.convertBoolToInt(u8Num.getValue() == 0);
					iN = 0;
					iH = 0;
					iC = 0;
					break;
				case 7:
					//SRL
					//bit 7 becomes 0
					iTemp = (regVal & 0xFF) >>> 1;
					u8Num.setValue(iTemp);
					
					setCPUReg8(reg, u8Num);
					iZ = BitOps.convertBoolToInt(u8Num.getValue() == 0);
					iN = 0;
					iH = 0;
					iC = BitOps.convertBoolToInt(BitOps.getBit(regVal, 0));
					break;
				default:
					System.err.println("INVALID CB PREFIX INSTRUCTION");
					System.exit(0);
				}
				break;
			}
			break;
		case CCF:
			iN = 0;
			iH = 0;
			iC = BitOps.convertBoolToInt(!registers.getCFlag());
			break;
		case CP:
			iTemp = registers.getRegValue(RegType.A).getValue() - u16FetchedData.getValue();
			iZ = BitOps.convertBoolToInt(iTemp == 0);
			iN = 1;
			iH = BitOps.convertBoolToInt((registers.getRegValue(RegType.A).getValue() & 0xF) - (u16FetchedData.getValue() & 0xF) < 0);
			iC = BitOps.convertBoolToInt(iTemp < 0);
			break;
		case CPL:
			u8Num.setValue(~registers.getRegValue(RegType.A).getValue());
			registers.setRegValue(RegType.A, u8Num);
			
			iN = 1;
			iH = 1;
			break;
		case DAA:
			iC = 0;
			if (registers.getHFlag() || (!registers.getNFlag() && (registers.getRegValue(RegType.A).getValue() & 0xF) > 9))
			{
				iTemp = 6;
			}
			
			if (registers.getCFlag() || (!registers.getNFlag() && (registers.getRegValue(RegType.A).getValue() > 0x99)))
			{
				iTemp |= 0x60;
				iC = 1;
			}
			
			if (registers.getNFlag())
			{
				iTemp *= -1;
			}
			
			iTemp += registers.getRegValue(RegType.A).getValue();
			iTemp &= 0xFF;
			u8Num.setValue(iTemp);
			
			registers.setRegValue(RegType.A, u8Num);
			iZ = BitOps.convertBoolToInt(iTemp == 0);
			iH = 0;
			break;
		case DEC:
			u16Num.setValue(registers.getRegValue(curInstruction.getReg1()));
			u16Num.decrement();
			
			//special behaviour when 16 bit
			if (!registers.check8Bit(curInstruction.getReg1()))
			{
				cycle.cycleEmu(1);
			}
			
			if (curInstruction.getReg1() == RegType.HL && curInstruction.getAddrMode() == AddrMode.MR)
			{
				u16Num.setValue(memory.readMemory((Unsigned16) registers.getRegValue(RegType.HL)));
				u16Num.decrement();
				memory.writeMemory((Unsigned16) registers.getRegValue(RegType.HL), u16Num.getLowByte());
			}
			else 
			{
				registers.setRegValue(curInstruction.getReg1(), u16Num);
				u16Num.setValue(registers.getRegValue(curInstruction.getReg1()));
			}
			
			//opcodes that end in 0xXB don't set any flags
			if ((iCurOpcode & 0x0B) != 0x0B)
			{
				iZ = BitOps.convertBoolToInt(u16Num.getValue() == 0);
				iN = 1;
				iH = BitOps.convertBoolToInt((u16Num.getValue() & 0x0F) == 0x0F);
			}
			break;
		case DI:
			cpu.setInterruptMasterEnable(false);
			break;
		case EI:
			cpu.setEnablingIME(true);
			break;
		case HALT:
			cpu.setHalted(true);
			break;
		case JP:
			goToAddr(u16FetchedData, false);
			break;
		case JPHL:
			break;
		case JR:
			u16Num.setValue(registers.getRegValue(RegType.PC));
			u16Num.addAsSigned8(u16FetchedData);
			goToAddr(u16Num, false);
			break;
		case OR:
			u8Num.setValue(registers.getRegValue(RegType.A).getValue() | u16FetchedData.getValue());
			registers.setRegValue(RegType.A, u8Num);
			iZ = BitOps.convertBoolToInt(u8Num.getValue() == 0);
			iN = 0;
			iH = 0;
			iC = 0;
			break;
		case POP:
			u16Num.setLowByte(cpu.popStack());
			cycle.cycleEmu(1);
			u16Num.setHighByte(cpu.popStack());
			cycle.cycleEmu(1);
			registers.setRegValue(curInstruction.getReg1(), u16Num);
			
			if (curInstruction.getReg1() == RegType.AF)
			{
				u16Num.setValue(u16Num.getValue() & 0xFFF0);
				registers.setRegValue(curInstruction.getReg1(), u16Num);
			}
			break;
		case PUSH:
			u16Num.setValue(registers.getRegValue(curInstruction.getReg1()));
			u8Num.setValue(u16Num.getHighByte());
			cycle.cycleEmu(1);
			cpu.pushStack(u8Num);
			u16Num.setValue(registers.getRegValue(curInstruction.getReg1()));
			u8Num.setValue(u16Num.getLowByte());
			cycle.cycleEmu(1);
			cpu.pushStack(u8Num);
			
			cycle.cycleEmu(1);
			break;
		case RETI:
			cpu.setInterruptMasterEnable(true);
			//after, do same code as RET
		case RET:
			if (curInstruction.getCond() != CondType.NONE)
			{
				cycle.cycleEmu(1);
			}
			
			if (checkCond())
			{
				//to keep cycle accurate, must do separate pops
				u16Num.setLowByte(cpu.popStack());
				cycle.cycleEmu(1);
				u16Num.setHighByte(cpu.popStack());
				cycle.cycleEmu(1);
				
				registers.setRegValue(RegType.PC, u16Num);
				cycle.cycleEmu(1);
			}
			break;
		case RLA:
			iTemp = registers.getRegValue(RegType.A).getValue();
			iC = (iTemp >>> 7) & 1;
			iTemp = (iTemp << 1) | BitOps.convertBoolToInt(registers.getCFlag());
			u8Num.setValue(iTemp);
			
			registers.setRegValue(RegType.A, u8Num);
			iZ = 0;
			iN = 0;
			iH = 0;
			break;
		case RLCA:
			iTemp = registers.getRegValue(RegType.A).getValue();
			iC = BitOps.convertBoolToInt(BitOps.getBit(registers.getRegValue(RegType.A).getValue(), 7)); 
			
			iTemp <<= 1;
			iTemp |= iC;
			u8Num.setValue(iTemp);
			
			registers.setRegValue(RegType.A, u8Num);
			iZ = 0;
			iN = 0;
			iH = 0;
			break;
		case RRA:
			iTemp = registers.getRegValue(RegType.A).getValue();
			iC = registers.getRegValue(RegType.A).getValue() & 1;
			
			iTemp = (iTemp >>> 1) | (BitOps.convertBoolToInt(registers.getCFlag()) << 7);
			u8Num.setValue(iTemp);
			
			registers.setRegValue(RegType.A, u8Num);
			iZ = 0;
			iN = 0;
			iH = 0;
			break;
		case RRCA:
			iTemp = registers.getRegValue(RegType.A).getValue();
			iC = BitOps.convertBoolToInt(registers.getRegValue(RegType.A).getBit(0));
			iTemp >>>= 1;
			iTemp |= iC << 7;
			u8Num.setValue(iTemp);
			
			registers.setRegValue(RegType.A, u8Num);
			iZ = 0;
			iN = 0;
			iH = 0;
			break;
		case RST:
			u16Num.setValue(curInstruction.getParam());
			goToAddr(u16Num, true);
			break;
		case STOP:
			System.out.println("STOPPING!");
			System.exit(0);
			break;
		case SUB:
			iN = 1;
			
			u16Num.setValue(registers.getRegValue(curInstruction.getReg1()));
			u16Num.sub(u16FetchedData);
			
			iZ = BitOps.convertBoolToInt((u16Num.getValue() & 0xFF) == 0);
			iH = BitOps.convertBoolToInt((registers.getRegValue(curInstruction.getReg1()).getValue() & 0xF) - (u16FetchedData.getValue() & 0xF) < 0);
			iC = BitOps.convertBoolToInt(registers.getRegValue(curInstruction.getReg1()).getValue() - u16FetchedData.getValue() < 0);
			
			registers.setRegValue(curInstruction.getReg1(), u16Num);
			break;
		case SBC:
			iN = 1;
			
			u16Num.setValue(registers.getRegValue(curInstruction.getReg1()).getValue());
			u16Num.sub(u16FetchedData);
			u16Num.sub(BitOps.convertBoolToInt(registers.getCFlag()));
			
			iZ = BitOps.convertBoolToInt((u16Num.getValue() & 0xFF) == 0);
			iH = BitOps.convertBoolToInt((registers.getRegValue(curInstruction.getReg1()).getValue() & 0xF) - (u16FetchedData.getValue() & 0xF) - BitOps.convertBoolToInt(registers.getCFlag()) < 0);
			iC = BitOps.convertBoolToInt(registers.getRegValue(curInstruction.getReg1()).getValue() - u16FetchedData.getValue() - BitOps.convertBoolToInt(registers.getCFlag()) < 0);
			
			registers.setRegValue(curInstruction.getReg1(), u16Num);
			break;
		case SCF:
			iN = 0;
			iH = 0;
			iC = 1;
			break;
		case XOR:
			u8Num.setValue(registers.getRegValue(curInstruction.getReg1()).getValue() ^ u16FetchedData.getValue());
			registers.setRegValue(RegType.A, u8Num);
			iZ = BitOps.convertBoolToInt(u8Num.getValue() == 0);
			iN = 0;
			iH = 0;
			iC = 0;
			break;
		/*
		default:
			System.err.println("UNKOWN INSTRUCTION!");
			System.exit(0);
			break;*/
		}
		
		registers.setCPUFlags(iZ, iN, iH, iC);
	}
	
	//for the CB instructions
	//allows for an 8 bit register value to be set
	//but if HL is used, it writes to the HL memory location
	private void setCPUReg8(RegType regType, Unsigned8 u8Num)
	{
		switch (regType)
		{
		case A:
		case B:
		case C:
		case D:
		case E:
		case F:
		case H:
		case L:
			registers.setRegValue(regType, u8Num);
			break;
		case HL:
			memory.writeMemory((Unsigned16) registers.getRegValue(RegType.HL), u8Num);
			break;
		default:
			System.err.println("INVALID REG8: " + regType);
			System.exit(0);
			break;
		}
	}
	
	//for the CB instructions
	//allows for an 8 bit register value to be read
	//but if HL is used, it reads from the HL memory location
	private Unsigned8 getCPUReg8(RegType regType)
	{
		Unsigned8 u8Data = new Unsigned8();
		
		switch(regType)
		{
		case A:
		case B:
		case C:
		case D:
		case E:
		case F:
		case H:
		case L:
			u8Data.setValue(registers.getRegValue(regType));
			break;
		case HL:
			u8Data.setValue(memory.readMemory((Unsigned16) registers.getRegValue(RegType.HL)));
			break;
		default:
			System.err.println("INVALID REG8: " + regType);
			System.exit(0);
			break;
		}
		
		return u8Data;
	}
	
	//used to return whether an instruction should execute based on its condition type
	private boolean checkCond()
	{
		boolean z = registers.getZFlag();
		boolean c = registers.getCFlag();
		
		switch (curInstruction.getCond())
		{
		case NONE: return true;
		case C: return c;
		case NC: return !c;
		case Z: return z;
		case NZ: return !z;
		}
		
		return false;
	}
	
	//used to jump to a specific address
	private void goToAddr(Unsigned16 u16Address, boolean bPushPC)
	{
		if (checkCond())
		{
			if (bPushPC)
			{
				cycle.cycleEmu(2);
				cpu.pushStack16((Unsigned16) registers.getRegValue(RegType.PC));
			}
			
			registers.setRegValue(RegType.PC, u16Address);
			cycle.cycleEmu(1);
		}
	}
}
