/**
 * @author 18bilkiewiczs
 * Class that contains all the CPU registers and all relevant methods
 */

package com.szymon.gameboy.cpu;

import com.szymon.gameboy.cpu.utils.RegType;
import com.szymon.gameboy.cpu.utils.Register16;
import com.szymon.gameboy.cpu.utils.Register8;
import com.szymon.gameboy.utils.BitOps;
import com.szymon.gameboy.utils.Unsigned;
import com.szymon.gameboy.utils.Unsigned16;
import com.szymon.gameboy.utils.Unsigned8;

public class CPURegisters
{
	// ---------------------------------------------
	// class variables
	// ---------------------------------------------
	private Register8[] reg8; // to store all of the 8 bit registers
	private Register16[] reg16; // to store all of the 16 bit registers

	private final int iNUM_REG8 = 8; // the number of 8 bit registers
	private final int iNUM_REG16 = 2; // the number of 16 bit registers

	// ---------------------------------------------
	// constructors
	// ---------------------------------------------
	// set the appropriate regType to each register
	// the 8 bit registers are: A, F, B, C, D, E, H, L
	// the 16 bit registers are: PC, SP
	public CPURegisters()
	{
		reg8 = new Register8[iNUM_REG8];
		reg16 = new Register16[iNUM_REG16];

		reg8[0] = new Register8(RegType.A);
		reg8[1] = new Register8(RegType.F);
		reg8[2] = new Register8(RegType.B);
		reg8[3] = new Register8(RegType.C);
		reg8[4] = new Register8(RegType.D);
		reg8[5] = new Register8(RegType.E);
		reg8[6] = new Register8(RegType.H);
		reg8[7] = new Register8(RegType.L);

		reg16[0] = new Register16(RegType.PC);
		reg16[0].setValue(0x100);
		reg16[1] = new Register16(RegType.SP);
	}

	// ---------------------------------------------
	// setters
	// ---------------------------------------------
	// set the value of a specific register
	public void setRegValue(RegType regType, Unsigned uValue)
	{
		// ignore the none regType, as there is no corresponding register
		if (regType == RegType.NONE)
		{
			return;
		}

		int iIndex = -1;
		// check if it is an 8 bit or 16 bit register, and proceed accordingly
		if (check8Bit(regType))
		{
			iIndex = getReg8Index(regType);

			reg8[iIndex].setValue(uValue.getValue());
		}
		else
		{
			// check if it is PC/SP, or a combined register
			if (regType == RegType.SP || regType == RegType.PC)
			{
				iIndex = getReg16Index(regType);
				
				reg16[iIndex].setValue(uValue.getValue());
			}
			else
			{
				Unsigned16 u16Num = new Unsigned16(uValue.getValue());
				setRegPairValue(regType, u16Num);
			}
		}
	}

	// sets the value of a pair of 8 bit registers (for use in this class only)
	private void setRegPairValue(RegType regType, Unsigned16 u16Value)
	{
		switch (regType)
		{
		case AF:
			reg8[getReg8Index(RegType.A)].setValue(u16Value.getHighByte());
			reg8[getReg8Index(RegType.F)].setValue(u16Value.getLowByte());
			break;
		case BC:
			reg8[getReg8Index(RegType.B)].setValue(u16Value.getHighByte());
			reg8[getReg8Index(RegType.C)].setValue(u16Value.getLowByte());
			break;
		case DE:
			reg8[getReg8Index(RegType.D)].setValue(u16Value.getHighByte());
			reg8[getReg8Index(RegType.E)].setValue(u16Value.getLowByte());
			break;
		case HL:
			reg8[getReg8Index(RegType.H)].setValue(u16Value.getHighByte());
			reg8[getReg8Index(RegType.L)].setValue(u16Value.getLowByte());
			break;
		default:
			break;
		}
	}

	// ---------------------------------------------
	// getters
	// ---------------------------------------------
	// get the value of a specific register
	public Unsigned getRegValue(RegType regType)
	{
		Unsigned uNum;

		// ignore the none regType, as there is no corresponding register
		if (regType == RegType.NONE)
		{
			uNum = new Unsigned8();
			return uNum;
		}

		int iIndex = -1;
		// check if it is an 8 bit or 16 bit register, and proceed accordingly
		if (check8Bit(regType))
		{
			iIndex = getReg8Index(regType);
			uNum = new Unsigned8(reg8[iIndex]);
		}
		else
		{
			// check if it is PC/SP, or a combined register
			if (regType == RegType.SP || regType == RegType.PC)
			{
				iIndex = getReg16Index(regType);
				uNum = new Unsigned16(reg16[iIndex]);
			}
			else
			{
				uNum = getRegPairValue(regType);
			}
		}

		return uNum;
	}

	// gets the value of a pair of 8 bit registers (for use in this class only)
	private Unsigned16 getRegPairValue(RegType regType)
	{
		Unsigned16 u16Num = new Unsigned16();

		switch (regType)
		{
		case AF:
			u16Num.setHighByte(reg8[getReg8Index(RegType.A)]);
			u16Num.setLowByte(reg8[getReg8Index(RegType.F)]);
			break;
		case BC:
			u16Num.setHighByte(reg8[getReg8Index(RegType.B)]);
			u16Num.setLowByte(reg8[getReg8Index(RegType.C)]);
			break;
		case DE:
			u16Num.setHighByte(reg8[getReg8Index(RegType.D)]);
			u16Num.setLowByte(reg8[getReg8Index(RegType.E)]);
			break;
		case HL:
			u16Num.setHighByte(reg8[getReg8Index(RegType.H)]);
			u16Num.setLowByte(reg8[getReg8Index(RegType.L)]);
			break;
		default:
			break;
		}

		return u16Num;
	}

	// ---------------------------------------------
	// methods
	// ---------------------------------------------
	//increment a specific register
	public void incrementRegister(RegType regType)
	{
		if (check8Bit(regType))
		{
			Unsigned8 u8Reg = (Unsigned8) getRegValue(regType);
			u8Reg.increment();
			setRegValue(regType, u8Reg);
		}
		else 
		{
			Unsigned16 u16Reg = (Unsigned16) getRegValue(regType);
			u16Reg.increment();
			setRegValue(regType, u16Reg);
		}
	}
	
	//decrement a specific register
	public void decrementRegister(RegType regType)
	{
		if (check8Bit(regType))
		{
			Unsigned8 u8Reg = (Unsigned8) getRegValue(regType);
			u8Reg.decrement();
			setRegValue(regType, u8Reg);
		}
		else 
		{
			Unsigned16 u16Reg = (Unsigned16) getRegValue(regType);
			u16Reg.decrement();
			setRegValue(regType, u16Reg);
		}
	}
	
	// decode 8 bit reg from int value, for CB prefix instructions only
	public RegType decodeRegForCB(int iValue)
	{
		switch (iValue)
		{
		case 0:
			return RegType.B;
		case 1:
			return RegType.C;
		case 2:
			return RegType.D;
		case 3:
			return RegType.E;
		case 4:
			return RegType.H;
		case 5:
			return RegType.L;
		case 6:
			return RegType.HL;
		case 7:
			return RegType.A;
		default:
			return RegType.NONE;
		}
	}

	// checks if a register type is 8 bit or 16 bit
	// return true for 8 bit, false for 16 bit
	public boolean check8Bit(RegType regType)
	{
		boolean b8Bit = false;

		switch (regType)
		{
		// 8 bit registers
		case A:
		case F:
		case B:
		case C:
		case D:
		case E:
		case H:
		case L:
			b8Bit = true;
			break;
		// 16 bit registers
		case AF:
		case BC:
		case DE:
		case HL:
		case PC:
		case SP:
			b8Bit = false;
			break;
		default:
			b8Bit = false;
			break;
		}

		return b8Bit;
	}

	// return the index of an 8 bit register in the given array
	private int getReg8Index(RegType regType)
	{
		int iIndex = 0;

		switch (regType)
		{
		case A:
			iIndex = 0;
			break;
		case F:
			iIndex = 1;
			break;
		case B:
			iIndex = 2;
			break;
		case C:
			iIndex = 3;
			break;
		case D:
			iIndex = 4;
			break;
		case E:
			iIndex = 5;
			break;
		case H:
			iIndex = 6;
			break;
		case L:
			iIndex = 7;
			break;
		default:
			iIndex = -1;
			break;
		}

		return iIndex;
	}

	// return the index of a 16 bit register in the given array
	private int getReg16Index(RegType regType)
	{
		int iIndex = 0;

		switch (regType)
		{
		case PC:
			iIndex = 0;
			break;
		case SP:
			iIndex = 1;
			break;
		default:
			iIndex = -1;
			break;
		}

		return iIndex;
	}

	//the following methods relate specifically to the flag register (F)
	//so they are laid out as their own class
	// ---------------------------------------------
	// class variables
	// ---------------------------------------------
	final private int iZFLAG_BIT = 7;
	final private int iNFLAG_BIT = 6;
	final private int iHFLAG_BIT = 5;
	final private int iCFLAG_BIT = 4;

	// ---------------------------------------------
	// setters
	// ---------------------------------------------
	// get and set flags in the flags register
	// z flag - bit 7 (zero)
	// n flag - bit 6 (subtraction)
	// h flag - bit 5 (half carry)
	// c flag - bit 4 (carry)
	public void setZFlag(boolean bFlag)
	{
		if (bFlag)
		{
			reg8[getReg8Index(RegType.F)].setBit(iZFLAG_BIT);
		}
		else
		{
			reg8[getReg8Index(RegType.F)].clearBit(iZFLAG_BIT);
		}
	}

	public void setNFlag(boolean bFlag)
	{
		if (bFlag)
		{
			reg8[getReg8Index(RegType.F)].setBit(iNFLAG_BIT);
		}
		else
		{
			reg8[getReg8Index(RegType.F)].clearBit(iNFLAG_BIT);
		}
	}

	public void setHFlag(boolean bFlag)
	{
		if (bFlag)
		{
			reg8[getReg8Index(RegType.F)].setBit(iHFLAG_BIT);
		}
		else
		{
			reg8[getReg8Index(RegType.F)].clearBit(iHFLAG_BIT);
		}
	}

	public void setCFlag(boolean bFlag)
	{
		if (bFlag)
		{
			reg8[getReg8Index(RegType.F)].setBit(iCFLAG_BIT);
		}
		else
		{
			reg8[getReg8Index(RegType.F)].clearBit(iCFLAG_BIT);
		}
	}

	// ---------------------------------------------
	// getters
	// ---------------------------------------------
	public boolean getZFlag()
	{
		return reg8[getReg8Index(RegType.F)].getBit(iZFLAG_BIT);
	}

	public boolean getNFlag()
	{
		return reg8[getReg8Index(RegType.F)].getBit(iNFLAG_BIT);
	}

	public boolean getHFlag()
	{
		return reg8[getReg8Index(RegType.F)].getBit(iHFLAG_BIT);
	}

	public boolean getCFlag()
	{
		return reg8[getReg8Index(RegType.F)].getBit(iCFLAG_BIT);
	}

	// ---------------------------------------------
	// methods
	// ---------------------------------------------
	// allows for setting of all flags based on integer values
	// -1 means don't change the flag, 0 means clear the bit, 1 means set the bit
	public void setCPUFlags(int iZFlag, int iNFlag, int iHFlag, int iCFlag)
	{
		if (iZFlag != -1)
		{
			setZFlag(BitOps.convertIntToBool(iZFlag));
		}

		if (iNFlag != -1)
		{
			setNFlag(BitOps.convertIntToBool(iNFlag));
		}

		if (iHFlag != -1)
		{
			setHFlag(BitOps.convertIntToBool(iHFlag));
		}

		if (iCFlag != -1)
		{
			setCFlag(BitOps.convertIntToBool(iCFlag));
		}
	}
	
	
	public static void main(String[] args)
	{
		CPURegisters regs = new CPURegisters();
		
		regs.setRegValue(RegType.A, new Unsigned8(0xF0));
		System.out.println(regs.getRegValue(RegType.A).getValue());
		regs.incrementRegister(RegType.A);
		System.out.println(regs.getRegValue(RegType.A).getValue());
	}
}
