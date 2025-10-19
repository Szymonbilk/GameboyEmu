/**
 * @author 18bilkiewiczs
 * Class that sets all instruction parameters based on an opcode
 */

package com.szymon.gameboy.cpu;

import com.szymon.gameboy.cpu.utils.AddrMode;
import com.szymon.gameboy.cpu.utils.CondType;
import com.szymon.gameboy.cpu.utils.InsType;
import com.szymon.gameboy.cpu.utils.RegType;

public class Instruction 
{
	// ---------------------------------------------
	// class variables
	// ---------------------------------------------
	private InsType type;	//the type of instruction (e.g. ADD, LD etc.)
	private AddrMode mode;	//the addressing mode used by the instruction (e.g. get value from A reg, store in B reg, etc.)
	private RegType reg1;	//the first register in the addressing (if applicable)
	private RegType reg2;	//the second register in the addressing (if applicable)
	private CondType cond;	//the condition type (if applicable, only used for a few instructions, e.g. JP NZ, where NZ (Not Zero) is the condition)
	private int iParam;		//only used by the RST instructions, where the parameter is an address to jump to
	
	// ---------------------------------------------
	// constructors
	// ---------------------------------------------
	public Instruction() 
	{
		resetInstruction();
	}
	
	public Instruction(int iOpcode)
	{
		setInstruction(iOpcode);
	}
	
	//setters not necessary as the setInstruction method is the only way to set the values
	
	// ---------------------------------------------
	// getters
	// ---------------------------------------------
	public InsType getInsType()
	{
		return type;
	}
	
	public AddrMode getAddrMode()
	{
		return mode;
	}
	
	public RegType getReg1()
	{
		return reg1;
	}
	
	public RegType getReg2()
	{
		return reg2;
	}
	
	public CondType getCond()
	{
		return cond;
	}
	
	public int getParam()
	{
		return iParam;
	}
	
	// ---------------------------------------------
	// methods
	// ---------------------------------------------
	//set an instruction to default values
	private void resetInstruction()
	{
		type = InsType.NONE;
		mode = AddrMode.IMP;
		reg1 = RegType.NONE;
		reg2 = RegType.NONE;
		cond = CondType.NONE;
		iParam = -1;
	}
	
	//based on an opcode, set all the instruction parameters
	//just a very long switch case
	public void setInstruction(int iOpcode)
	{
		resetInstruction();
		
		switch(iOpcode)
		{
		//0x0X
		case 0x00:
			type = InsType.NOP;
			break;
		case 0x01:
			type = InsType.LD;
			mode = AddrMode.R_D16;
			reg1 = RegType.BC;
			break;
		case 0x02:
			type = InsType.LD;
			mode = AddrMode.MR_R;
			reg1 = RegType.BC;
			reg2 = RegType.A;
			break;
		case 0x03:
			type = InsType.INC;
			mode = AddrMode.R;
			reg1 = RegType.BC;
			break;
		case 0x04:
			type = InsType.INC;
			mode = AddrMode.R;
			reg1 = RegType.B;
			break;
		case 0x05:
			type = InsType.DEC;
			mode = AddrMode.R;
			reg1 = RegType.B;
			break;
		case 0x06:
			type = InsType.LD;
			mode = AddrMode.R_D8;
			reg1 = RegType.B;
			break;
		case 0x07:
			type = InsType.RLCA;
			break;
		case 0x08:
			type = InsType.LD;
			mode = AddrMode.A16_R;
			reg2 = RegType.SP;
			break;
		case 0x09:
			type = InsType.ADD;
			mode = AddrMode.R_R;
			reg1 = RegType.HL;
			reg2 = RegType.BC;
			break;
		case 0x0A:
			type = InsType.LD;
			mode = AddrMode.R_MR;
			reg1 = RegType.A;
			reg2 = RegType.BC;
			break;
		case 0x0B:
			type = InsType.DEC;
			mode = AddrMode.R;
			reg1 = RegType.BC;
			break;
		case 0x0C:
			type = InsType.INC;
			mode = AddrMode.R;
			reg1 = RegType.C;
			break;
		case 0x0D:
			type = InsType.DEC;
			mode = AddrMode.R;
			reg1 = RegType.C;
			break;
		case 0x0E:
			type = InsType.LD;
			mode = AddrMode.R_D8;
			reg1 = RegType.C;
			break;
		case 0x0F:
			type = InsType.RRCA;
			break;
		
		//0x1X
		case 0x10:
			type = InsType.STOP;
			break;
		case 0x11:
			type = InsType.LD;
			mode = AddrMode.R_D16;
			reg1 = RegType.DE;
			break;
		case 0x12:
			type = InsType.LD;
			mode = AddrMode.MR_R;
			reg1 = RegType.DE;
			reg2 = RegType.A;
			break;
		case 0x13:
			type = InsType.INC;
			mode = AddrMode.R;
			reg1 = RegType.DE;
			break;
		case 0x14:
			type = InsType.INC;
			mode = AddrMode.R;
			reg1 = RegType.D;
			break;
		case 0x15:
			type = InsType.DEC;
			mode = AddrMode.R;
			reg1 = RegType.D;
			break;
		case 0x16:
			type = InsType.LD;
			mode = AddrMode.R_D8;
			reg1 = RegType.D;
			break;
		case 0x17:
			type = InsType.RLA;
			break;
		case 0x18:
			type = InsType.JR;
			mode = AddrMode.D8;
			break;
		case 0x19:
			type = InsType.ADD;
			mode = AddrMode.R_R;
			reg1 = RegType.HL;
			reg2 = RegType.DE;
			break;
		case 0x1A:
			type = InsType.LD;
			mode = AddrMode.R_MR;
			reg1 = RegType.A;
			reg2 = RegType.DE;
			break;
		case 0x1B:
			type = InsType.DEC;
			mode = AddrMode.R;
			reg1 = RegType.DE;
			break;
		case 0x1C:
			type = InsType.INC;
			mode = AddrMode.R;
			reg1 = RegType.E;
			break;
		case 0x1D:
			type = InsType.DEC;
			mode = AddrMode.R;
			reg1 = RegType.E;
			break;
		case 0x1E:
			type = InsType.LD;
			mode = AddrMode.R_D8;
			reg1 = RegType.E;
			break;
		case 0x1F:
			type = InsType.RRA;
			break;
			
		//0x2X
		case 0x20:
			type = InsType.JR;
			mode = AddrMode.D8;
			cond = CondType.NZ;
			break;
		case 0x21:
			type = InsType.LD;
			mode = AddrMode.R_D16;
			reg1 = RegType.HL;
			break;
		case 0x22:
			type = InsType.LD;
			mode = AddrMode.HLI_R;
			reg1 = RegType.HL;
			reg2 = RegType.A;
			break;
		case 0x23:
			type = InsType.INC;
			mode = AddrMode.R;
			reg1 = RegType.HL;
			break;
		case 0x24:
			type = InsType.INC;
			mode = AddrMode.R;
			reg1 = RegType.H;
			break;
		case 0x25:
			type = InsType.DEC;
			mode = AddrMode.R;
			reg1 = RegType.H;
			break;
		case 0x26:
			type = InsType.LD;
			mode = AddrMode.R_D8;
			reg1 = RegType.H;
			break;
		case 0x27:
			type = InsType.DAA;
			break;
		case 0x28:
			type = InsType.JR;
			mode = AddrMode.D8;
			cond = CondType.Z;
			break;
		case 0x29:
			type = InsType.ADD;
			mode = AddrMode.R_R;
			reg1 = RegType.HL;
			reg2 = RegType.HL;
			break;
		case 0x2A:
			type = InsType.LD;
			mode = AddrMode.R_HLI;
			reg1 = RegType.A;
			reg2 = RegType.HL;
			break;
		case 0x2B:
			type = InsType.DEC;
			mode = AddrMode.R;
			reg1 = RegType.HL;
			break;
		case 0x2C:
			type = InsType.INC;
			mode = AddrMode.R;
			reg1 = RegType.L;
			break;
		case 0x2D:
			type = InsType.DEC;
			mode = AddrMode.R;
			reg1 = RegType.L;
			break;
		case 0x2E:
			type = InsType.LD;
			mode = AddrMode.R_D8;
			reg1 = RegType.L;
			break;
		case 0x2F:
			type = InsType.CPL;
			break;
			
		//0x3X
		case 0x30:
			type = InsType.JR;
			mode = AddrMode.D8;
			cond = CondType.NC;
			break;
		case 0x31:
			type = InsType.LD;
			mode = AddrMode.R_D16;
			reg1 = RegType.SP;
			break;
		case 0x32:
			type = InsType.LD;
			mode = AddrMode.HLD_R;
			reg1 = RegType.HL;
			reg2 = RegType.A;
			break;
		case 0x33:
			type = InsType.INC;
			mode = AddrMode.R;
			reg1 = RegType.SP;
			break;
		case 0x34:
			type = InsType.INC;
			mode = AddrMode.MR;
			reg1 = RegType.HL;
			break;
		case 0x35:
			type = InsType.DEC;
			mode = AddrMode.MR;
			reg1 = RegType.HL;
			break;
		case 0x36:
			type = InsType.LD;
			mode = AddrMode.MR_D8;
			reg1 = RegType.HL;
			break;
		case 0x37:
			type = InsType.SCF;
			break;
		case 0x38:
			type = InsType.JR;
			mode = AddrMode.D8;
			cond = CondType.C;
			break;
		case 0x39:
			type = InsType.ADD;
			mode = AddrMode.R_R;
			reg1 = RegType.HL;
			reg2 = RegType.SP;
			break;
		case 0x3A:
			type = InsType.LD;
			mode = AddrMode.R_HLD;
			reg1 = RegType.A;
			reg2 = RegType.HL;
			break;
		case 0x3B:
			type = InsType.DEC;
			mode = AddrMode.R;
			reg1 = RegType.SP;
			break;
		case 0x3C:
			type = InsType.INC;
			mode = AddrMode.R;
			reg1 = RegType.A;
			break;
		case 0x3D:
			type = InsType.DEC;
			mode = AddrMode.R;
			reg1 = RegType.A;
			break;
		case 0x3E:
			type = InsType.LD;
			mode = AddrMode.R_D8;
			reg1 = RegType.A;
			break;
		case 0x3F:
			type = InsType.CCF;
			break;
			
		//0x4X
		case 0x40:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.B;
			reg2 = RegType.B;
			break;
		case 0x41:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.B;
			reg2 = RegType.C;
			break;
		case 0x42:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.B;
			reg2 = RegType.D;
			break;
		case 0x43:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.B;
			reg2 = RegType.E;
			break;
		case 0x44:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.B;
			reg2 = RegType.H;
			break;
		case 0x45:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.B;
			reg2 = RegType.L;
			break;
		case 0x46:
			type = InsType.LD;
			mode = AddrMode.R_MR;
			reg1 = RegType.B;
			reg2 = RegType.HL;
			break;
		case 0x47:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.B;
			reg2 = RegType.A;
			break;
		case 0x48:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.C;
			reg2 = RegType.B;
			break;
		case 0x49:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.C;
			reg2 = RegType.C;
			break;
		case 0x4A:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.C;
			reg2 = RegType.D;
			break;
		case 0x4B:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.C;
			reg2 = RegType.E;
			break;
		case 0x4C:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.C;
			reg2 = RegType.H;
			break;
		case 0x4D:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.C;
			reg2 = RegType.L;
			break;
		case 0x4E:
			type = InsType.LD;
			mode = AddrMode.R_MR;
			reg1 = RegType.C;
			reg2 = RegType.HL;
			break;
		case 0x4F:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.C;
			reg2 = RegType.A;
			break;
			
		//0x5X
		case 0x50:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.D;
			reg2 = RegType.B;
			break;
		case 0x51:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.D;
			reg2 = RegType.C;
			break;
		case 0x52:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.D;
			reg2 = RegType.D;
			break;
		case 0x53:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.D;
			reg2 = RegType.E;
			break;
		case 0x54:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.D;
			reg2 = RegType.H;
			break;
		case 0x55:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.D;
			reg2 = RegType.L;
			break;
		case 0x56:
			type = InsType.LD;
			mode = AddrMode.R_MR;
			reg1 = RegType.D;
			reg2 = RegType.HL;
			break;
		case 0x57:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.D;
			reg2 = RegType.A;
			break;
		case 0x58:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.E;
			reg2 = RegType.B;
			break;
		case 0x59:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.E;
			reg2 = RegType.C;
			break;
		case 0x5A:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.E;
			reg2 = RegType.D;
			break;
		case 0x5B:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.E;
			reg2 = RegType.E;
			break;
		case 0x5C:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.E;
			reg2 = RegType.H;
			break;
		case 0x5D:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.E;
			reg2 = RegType.L;
			break;
		case 0x5E:
			type = InsType.LD;
			mode = AddrMode.R_MR;
			reg1 = RegType.E;
			reg2 = RegType.HL;
			break;
		case 0x5F:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.E;
			reg2 = RegType.A;
			break;
			
		//0x6X
		case 0x60:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.H;
			reg2 = RegType.B;
			break;
		case 0x61:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.H;
			reg2 = RegType.C;
			break;
		case 0x62:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.H;
			reg2 = RegType.D;
			break;
		case 0x63:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.H;
			reg2 = RegType.E;
			break;
		case 0x64:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.H;
			reg2 = RegType.H;
			break;
		case 0x65:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.H;
			reg2 = RegType.L;
			break;
		case 0x66:
			type = InsType.LD;
			mode = AddrMode.R_MR;
			reg1 = RegType.H;
			reg2 = RegType.HL;
			break;
		case 0x67:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.H;
			reg2 = RegType.A;
			break;
		case 0x68:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.L;
			reg2 = RegType.B;
			break;
		case 0x69:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.L;
			reg2 = RegType.C;
			break;
		case 0x6A:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.L;
			reg2 = RegType.D;
			break;
		case 0x6B:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.L;
			reg2 = RegType.E;
			break;
		case 0x6C:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.L;
			reg2 = RegType.H;
			break;
		case 0x6D:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.L;
			reg2 = RegType.L;
			break;
		case 0x6E:
			type = InsType.LD;
			mode = AddrMode.R_MR;
			reg1 = RegType.L;
			reg2 = RegType.HL;
			break;
		case 0x6F:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.L;
			reg2 = RegType.A;
			break;
			
		//0x7X
		case 0x70:
			type = InsType.LD;
			mode = AddrMode.MR_R;
			reg1 = RegType.HL;
			reg2 = RegType.B;
			break;
		case 0x71:
			type = InsType.LD;
			mode = AddrMode.MR_R;
			reg1 = RegType.HL;
			reg2 = RegType.C;
			break;
		case 0x72:
			type = InsType.LD;
			mode = AddrMode.MR_R;
			reg1 = RegType.HL;
			reg2 = RegType.D;
			break;
		case 0x73:
			type = InsType.LD;
			mode = AddrMode.MR_R;
			reg1 = RegType.HL;
			reg2 = RegType.E;
			break;
		case 0x74:
			type = InsType.LD;
			mode = AddrMode.MR_R;
			reg1 = RegType.HL;
			reg2 = RegType.H;
			break;
		case 0x75:
			type = InsType.LD;
			mode = AddrMode.MR_R;
			reg1 = RegType.HL;
			reg2 = RegType.L;
			break;
		case 0x76:
			type = InsType.HALT;
			break;
		case 0x77:
			type = InsType.LD;
			mode = AddrMode.MR_R;
			reg1 = RegType.HL;
			reg2 = RegType.A;
			break;
		case 0x78:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.B;
			break;
		case 0x79:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.C;
			break;
		case 0x7A:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.D;
			break;
		case 0x7B:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.E;
			break;
		case 0x7C:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.H;
			break;
		case 0x7D:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.L;
			break;
		case 0x7E:
			type = InsType.LD;
			mode = AddrMode.R_MR;
			reg1 = RegType.A;
			reg2 = RegType.HL;
			break;
		case 0x7F:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.A;
			break;
			
		//0x8X
		case 0x80:
			type = InsType.ADD;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.B;
			break;
		case 0x81:
			type = InsType.ADD;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.C;
			break;
		case 0x82:
			type = InsType.ADD;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.D;
			break;
		case 0x83:
			type = InsType.ADD;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.E;
			break;
		case 0x84:
			type = InsType.ADD;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.H;
			break;
		case 0x85:
			type = InsType.ADD;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.L;
			break;
		case 0x86:
			type = InsType.ADD;
			mode = AddrMode.R_MR;
			reg1 = RegType.A;
			reg2 = RegType.HL;
			break;
		case 0x87:
			type = InsType.ADD;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.A;
			break;
		case 0x88:
			type = InsType.ADC;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.B;
			break;
		case 0x89:
			type = InsType.ADC;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.C;
			break;
		case 0x8A:
			type = InsType.ADC;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.D;
			break;
		case 0x8B:
			type = InsType.ADC;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.E;
			break;
		case 0x8C:
			type = InsType.ADC;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.H;
			break;
		case 0x8D:
			type = InsType.ADC;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.L;
			break;
		case 0x8E:
			type = InsType.ADC;
			mode = AddrMode.R_MR;
			reg1 = RegType.A;
			reg2 = RegType.HL;
			break;
		case 0x8F:
			type = InsType.ADC;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.A;
			break;
			
		//0x9X
		case 0x90:
			type = InsType.SUB;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.B;
			break;
		case 0x91:
			type = InsType.SUB;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.C;
			break;
		case 0x92:
			type = InsType.SUB;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.D;
			break;
		case 0x93:
			type = InsType.SUB;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.E;
			break;
		case 0x94:
			type = InsType.SUB;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.H;
			break;
		case 0x95:
			type = InsType.SUB;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.L;
			break;
		case 0x96:
			type = InsType.SUB;
			mode = AddrMode.R_MR;
			reg1 = RegType.A;
			reg2 = RegType.HL;
			break;
		case 0x97:
			type = InsType.SUB;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.A;
			break;
		case 0x98:
			type = InsType.SBC;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.B;
			break;
		case 0x99:
			type = InsType.SBC;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.C;
			break;
		case 0x9A:
			type = InsType.SBC;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.D;
			break;
		case 0x9B:
			type = InsType.SBC;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.E;
			break;
		case 0x9C:
			type = InsType.SBC;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.H;
			break;
		case 0x9D:
			type = InsType.SBC;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.L;
			break;
		case 0x9E:
			type = InsType.SBC;
			mode = AddrMode.R_MR;
			reg1 = RegType.A;
			reg2 = RegType.HL;
			break;
		case 0x9F:
			type = InsType.SBC;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.A;
			break;
			
		//0xAX
		case 0xA0:
			type = InsType.AND;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.B;
			break;
		case 0xA1:
			type = InsType.AND;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.C;
			break;
		case 0xA2:
			type = InsType.AND;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.D;
			break;
		case 0xA3:
			type = InsType.AND;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.E;
			break;
		case 0xA4:
			type = InsType.AND;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.H;
			break;
		case 0xA5:
			type = InsType.AND;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.L;
			break;
		case 0xA6:
			type = InsType.AND;
			mode = AddrMode.R_MR;
			reg1 = RegType.A;
			reg2 = RegType.HL;
			break;
		case 0xA7:
			type = InsType.AND;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.A;
			break;
		case 0xA8:
			type = InsType.XOR;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.B;
			break;
		case 0xA9:
			type = InsType.XOR;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.C;
			break;
		case 0xAA:
			type = InsType.XOR;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.D;
			break;
		case 0xAB:
			type = InsType.XOR;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.E;
			break;
		case 0xAC:
			type = InsType.XOR;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.H;
			break;
		case 0xAD:
			type = InsType.XOR;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.L;
			break;
		case 0xAE:
			type = InsType.XOR;
			mode = AddrMode.R_MR;
			reg1 = RegType.A;
			reg2 = RegType.HL;
			break;
		case 0xAF:
			type = InsType.XOR;
			mode = AddrMode.R;
			reg1 = RegType.A;
			reg2 = RegType.A;
			break;
			
		//0xBX
		case 0xB0:
			type = InsType.OR;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.B;
			break;
		case 0xB1:
			type = InsType.OR;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.C;
			break;
		case 0xB2:
			type = InsType.OR;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.D;
			break;
		case 0xB3:
			type = InsType.OR;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.E;
			break;
		case 0xB4:
			type = InsType.OR;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.H;
			break;
		case 0xB5:
			type = InsType.OR;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.L;
			break;
		case 0xB6:
			type = InsType.OR;
			mode = AddrMode.R_MR;
			reg1 = RegType.A;
			reg2 = RegType.HL;
			break;
		case 0xB7:
			type = InsType.OR;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.A;
			break;
		case 0xB8:
			type = InsType.CP;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.B;
			break;
		case 0xB9:
			type = InsType.CP;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.C;
			break;
		case 0xBA:
			type = InsType.CP;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.D;
			break;
		case 0xBB:
			type = InsType.CP;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.E;
			break;
		case 0xBC:
			type = InsType.CP;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.H;
			break;
		case 0xBD:
			type = InsType.CP;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.L;
			break;
		case 0xBE:
			type = InsType.CP;
			mode = AddrMode.R_MR;
			reg1 = RegType.A;
			reg2 = RegType.HL;
			break;
		case 0xBF:
			type = InsType.CP;
			mode = AddrMode.R_R;
			reg1 = RegType.A;
			reg2 = RegType.A;
			break;
			
		//0xCX
		case 0xC0:
			type = InsType.RET;
			cond = CondType.NZ;
			break;
		case 0xC1:
			type = InsType.POP;
			mode = AddrMode.R;
			reg1 = RegType.BC;
			break;
		case 0xC2:
			type = InsType.JP;
			mode = AddrMode.D16;
			cond = CondType.NZ;
			break;
		case 0xC3:
			type = InsType.JP;
			mode = AddrMode.D16;
			break;
		case 0xC4:
			type = InsType.CALL;
			mode = AddrMode.D16;
			cond = CondType.NZ;
			break;
		case 0xC5:
			type = InsType.PUSH;
			mode = AddrMode.R;
			reg1 = RegType.BC;
			break;
		case 0xC6:
			type = InsType.ADD;
			mode = AddrMode.R_D8;
			reg1 = RegType.A;
			break;
		case 0xC7:
			type = InsType.RST;
			iParam = 0x00;
			break;
		case 0xC8:
			type = InsType.RET;
			cond = CondType.Z;
			break;
		case 0xC9:
			type = InsType.RET;
			break;
		case 0xCA:
			type = InsType.JP;
			mode = AddrMode.D16;
			cond = CondType.Z;
			break;
		case 0xCB:
			type = InsType.CB;
			mode = AddrMode.D8;
			break;
		case 0xCC:
			type = InsType.CALL;
			mode = AddrMode.D16;
			cond = CondType.Z;
			break;
		case 0xCD:
			type = InsType.CALL;
			mode = AddrMode.D16;
			break;
		case 0xCE:
			type = InsType.ADC;
			mode = AddrMode.R_D8;
			reg1 = RegType.A;
			break;
		case 0xCF:
			type = InsType.RST;
			iParam = 0x08;
			break;
			
		//0xDX
		case 0xD0:
			type = InsType.RET;
			mode = AddrMode.IMP;
			cond = CondType.NC;
			break;
		case 0xD1:
			type = InsType.POP;
			mode = AddrMode.R;
			reg1 = RegType.DE;
			break;
		case 0xD2:
			type = InsType.JP;
			mode = AddrMode.D16;
			cond = CondType.NC;
			break;
		case 0xD4:
			type = InsType.CALL;
			mode = AddrMode.D16;
			cond = CondType.NC;
			break;
		case 0xD5:
			type = InsType.PUSH;
			mode = AddrMode.R;
			reg1 = RegType.DE;
			break;
		case 0xD6:
			type = InsType.SUB;
			mode = AddrMode.R_D8;
			reg1 = RegType.A;
			break;
		case 0xD7:
			type = InsType.RST;
			iParam = 0x10;
			break;
		case 0xD8:
			type = InsType.RET;
			cond = CondType.C;
			break;
		case 0xD9:
			type = InsType.RETI;
			break;
		case 0xDA:
			type = InsType.JP;
			mode = AddrMode.D16;
			cond = CondType.C;
			break;
		case 0xDC:
			type = InsType.CALL;
			mode = AddrMode.D16;
			cond = CondType.C;
			break;
		case 0xDE:
			type = InsType.SBC;
			mode = AddrMode.R_D8;
			reg1 = RegType.A;
			break;
		case 0xDF:
			type = InsType.RST;
			iParam = 0x18;
			break;
			
		//0xEX
		case 0xE0:
			type = InsType.LDH;
			mode = AddrMode.A8_R;
			reg2 = RegType.A;
			break;
		case 0xE1:
			type = InsType.POP;
			mode = AddrMode.R;
			reg1 = RegType.HL;
			break;
		case 0xE2:
			type = InsType.LD;
			mode = AddrMode.MR_R;
			reg1 = RegType.C;
			reg2 = RegType.A;
			break;
		case 0xE5:
			type = InsType.PUSH;
			mode = AddrMode.R;
			reg1 = RegType.HL;
			break;
		case 0xE6:
			type = InsType.AND;
			mode = AddrMode.R_D8;
			reg1 = RegType.A;
			break;
		case 0xE7:
			type = InsType.RST;
			iParam = 0x20;
			break;
		case 0xE8:
			type = InsType.ADD;
			mode = AddrMode.R_D8;
			reg1 = RegType.SP;
			break;
		case 0xE9:
			type = InsType.JP;
			mode = AddrMode.R;
			reg1 = RegType.HL;
			break;
		case 0xEA:
			type = InsType.LD;
			mode = AddrMode.A16_R;
			reg2 = RegType.A;
			break;
		case 0xEE:
			type = InsType.XOR;
			mode = AddrMode.R_D8;
			reg1 = RegType.A;
			break;
		case 0xEF:
			type = InsType.RST;
			iParam = 0x28;
			break;
			
		//0xFX
		case 0xF0:
			type = InsType.LDH;
			mode = AddrMode.R_A8;
			reg1 = RegType.A;
			break;
		case 0xF1:
			type = InsType.POP;
			mode = AddrMode.R;
			reg1 = RegType.AF;
			break;
		case 0xF2:
			type = InsType.LD;
			mode = AddrMode.R_MR;
			reg1 = RegType.A;
			reg2 = RegType.C;
			break;
		case 0xF3:
			type = InsType.DI;
			break;
		case 0xF5:
			type = InsType.PUSH;
			mode = AddrMode.R;
			reg1 = RegType.AF;
			break;
		case 0xF6:
			type = InsType.OR;
			mode = AddrMode.R_D8;
			reg1 = RegType.A;
			break;
		case 0xF7:
			type = InsType.RST;
			iParam = 0x30;
			break;
		case 0xF8:
			type = InsType.LD;
			mode = AddrMode.HL_SPR;
			reg1 = RegType.HL;
			reg2 = RegType.SP;
			break;
		case 0xF9:
			type = InsType.LD;
			mode = AddrMode.R_R;
			reg1 = RegType.SP;
			reg2 = RegType.HL;
			break;
		case 0xFA:
			type = InsType.LD;
			mode = AddrMode.R_A16;
			reg1 = RegType.A;
			break;
		case 0xFB:
			type = InsType.EI;
			break;
		case 0xFE:
			type = InsType.CP;
			mode = AddrMode.R_D8;
			reg1 = RegType.A;
			break;
		case 0xFF:
			type = InsType.RST;
			iParam = 0x38;
			break;
			
		default:
			mode = AddrMode.NONE;
			break;
		}
	}
}