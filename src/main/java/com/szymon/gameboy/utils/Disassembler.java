package com.szymon.gameboy.utils;

import com.szymon.gameboy.cpu.Instruction;
import com.szymon.gameboy.cpu.utils.AddrMode;
import com.szymon.gameboy.cpu.utils.CondType;

public class Disassembler 
{
	private Instruction instruction;
	private String fetchedData;
	
	public Disassembler()
	{
		instruction = new Instruction();
	}
	
	public void setInstruction(int opcode, int data)
	{
		instruction.setInstruction(opcode);
		//convert the fetched data into hex
		//convert it to 16 bit (4 hex digits), convert later if necessary
		fetchedData = String.format("%04x", data);
	}
	
	//changes fetched data to 8 bit (2 hex digit)
	public String convert8Bit(String data)
	{
		String szConverted = data.substring(2);
		return szConverted;
	}
	
	//disassemble from the set instruction
	//this is done by working through the appropriate fields, and adding the values
	
	//TODO - currently doesn't disassemble CB-prefix instructions to their relevant instructions
	//currently just output CB followed by the next byte in memory
	//additionally, due to how instructions are stored internally, instructions which implicitly store the result in 
	//A (accumulator) also output A as part of the instruction when not needed (e.g. ADD A, B could just be ADD B)
	public String disassemble()
	{
		String szInstr = "";
		
		//first add the instruction type
		szInstr += instruction.getInsType().getValue() + " ";
		
		//add the flag condition if there is any
		if (instruction.getCond() != CondType.NONE)
		{
			szInstr += instruction.getCond().getValue();
			//if it has an addressing mode, add a comma
			if (instruction.getAddrMode() != AddrMode.NONE && instruction.getAddrMode() != AddrMode.IMP)
			{
				szInstr += ", ";
			}
		}
		
		//add the parameter if there is any
		if (instruction.getParam() != -1)
		{
			//the parameter actually specifies the instruction's jump address
			//but when disassembled, only the numbers from 0-7 need to be used
			/* Map:
			 * 0: 0x00
			 * 1: 0x08
			 * 2: 0x10
			 * 3: 0x18
			 * 4: 0x20
			 * 5: 0x28
			 * 6: 0x30
			 * 7: 0x38
			 */
			int iParam = instruction.getParam();
			int iValue = 0;
			
			if (iParam == 0)
			{
				iValue = 0;
			}
			else if (iParam % 0x10 == 0)
			{
				iValue = (iParam / 0x10) * 2;
			}
			else if ((iParam - 0x08) % 0x10 == 0)
			{
				iValue = ((iParam - 0x08) / 0x10) * 2 + 1;
			}
			
			szInstr += iValue;
		}
		
		//check the addressing mode to add the appropriate data or register names
		//need to consider the special cases (e.g. s8 or a8 values, hl+ or hl- etc.)
		switch (instruction.getAddrMode())
		{
		case A16_R:
			szInstr += "$" + fetchedData + ", " + instruction.getReg2().getValue();
			break;
		case A8_R:
			szInstr += "$" + convert8Bit(fetchedData) + ", " + instruction.getReg2().getValue();
			break;
		case D16:
			szInstr += fetchedData;
			break;
		case D8:
			szInstr += convert8Bit(fetchedData);
			break;
		case HLD_R:
			szInstr += "(HL-), A";
			break;
		case HLI_R:
			szInstr += "(HL+), A";
			break;
		case HL_SPR:
			szInstr += "HL, SP+" + convert8Bit(szInstr);
			break;
		case IMP:
			break;
		case MR:
			szInstr += "(" + instruction.getReg1().getValue() + ")";
			break;
		case MR_D8:
			szInstr += "(" + instruction.getReg1().getValue() + "), " + convert8Bit(fetchedData);
			break;
		case MR_R:
			szInstr += "(" + instruction.getReg1().getValue() + "), " + instruction.getReg2().getValue();
			break;
		case NONE:
			break;
		case R:
			szInstr += instruction.getReg1().getValue();
			break;
		case R_A16:
			szInstr += instruction.getReg1().getValue() + ", $" + fetchedData;
			break;
		case R_A8:
			szInstr += instruction.getReg1().getValue() + ", $" + convert8Bit(fetchedData);
			break;
		case R_D16:
			szInstr += instruction.getReg1().getValue() + ", " + fetchedData;
			break;
		case R_D8:
			szInstr += instruction.getReg1().getValue() + ", " + convert8Bit(fetchedData);
			break;
		case R_HLD:
			szInstr += instruction.getReg1().getValue() + ", (HL-)";
			break;
		case R_HLI:
			szInstr += instruction.getReg1().getValue() + ", (HL+)";
			break;
		case R_MR:
			szInstr += instruction.getReg1().getValue() + ", (" + instruction.getReg2().getValue() + ")";
			break;
		case R_R:
			szInstr += instruction.getReg1().getValue() + ", " + instruction.getReg2().getValue();
			break;
		default:
			break;
		}
		
		return szInstr;
	}
	
	//test for all opcodes
	public static void main(String[] args)
	{
		int opcode = 0;
		int value = 0x12;
		String out = "";
		
		Disassembler disassembler = new Disassembler();
		
		for (opcode = 0; opcode <= 0xFF; opcode++)
		{
			disassembler.setInstruction(opcode, value);
			out = disassembler.disassemble();
			
			System.out.printf("%02x: %-10s%n", opcode, out);
		}
	}
}
