package comp207p.main;
import java.io.File;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.util.InstructionFinder;


public class ConstantFolder
{
	ClassParser parser = null;
	ClassGen gen = null;

	JavaClass original = null;
	JavaClass optimized = null;

	public ConstantFolder(String classFilePath)
	{
		try{
			this.parser = new ClassParser(classFilePath);
			this.original = this.parser.parse();
			this.gen = new ClassGen(this.original);
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * GET PREVIOUS VALUES 
	**/
	/** ****************************************************************************************************************** **/
	private int getPrevInt(InstructionHandle handle, InstructionList instList, ConstantPoolGen cpgen)
	{
		//System.out.println("instruction = " + handle.getInstruction());
		if (handle.getInstruction() instanceof ICONST)
		{
			//System.out.println("FROM ICONST = " + ((ICONST)(handle.getInstruction())).getValue());
			//System.out.println();
			return (int) ((ICONST)(handle.getInstruction())).getValue();
		} else
		if (handle.getInstruction() instanceof BIPUSH)
		{
			//System.out.println("FROM BIPUSH = " + ((BIPUSH)(handle.getInstruction())).getValue());
			//System.out.println();
			return (int) ((BIPUSH)(handle.getInstruction())).getValue();
		}else
		if (handle.getInstruction() instanceof SIPUSH)
		{
			//System.out.println("FROM SIPUSH = " + ((SIPUSH)(handle.getInstruction())).getValue());
			//System.out.println();
			return (int) ((SIPUSH)(handle.getInstruction())).getValue();
		}else
		if (handle.getInstruction() instanceof LDC)
		{
			//System.out.println("FROM LDC = " + ((LDC)(handle.getInstruction())).getValue(cpgen));
			//System.out.println();
			return (int) ((LDC)(handle.getInstruction())).getValue(cpgen);
		}else
		if (handle.getInstruction() instanceof ILOAD)
		{
			int load_index = (int) ((ILOAD)(handle.getInstruction())).getIndex();
        	int value = getLoadIntValue(handle, instList,cpgen,load_index);
        	return value;
		}else
			return 0;
	}
	
	private long getPrevLong(InstructionHandle handle, InstructionList instList, ConstantPoolGen cpgen)
	{
		//System.out.println("instruction = " + handle.getInstruction());
		if (handle.getInstruction() instanceof LCONST)
		{
			//System.out.println("FROM LCONST = " + ((LCONST)(handle.getInstruction())).getValue());
			//System.out.println();
			return (long) ((LCONST)(handle.getInstruction())).getValue();
		}else 
		if (handle.getInstruction() instanceof LDC2_W)
		{
			//System.out.println("FROM LDC2_W = " + ((LDC2_W)(handle.getInstruction())).getValue(cpgen));
			//System.out.println();
			return (long) ((LDC2_W)(handle.getInstruction())).getValue(cpgen);
		}else
		if (handle.getInstruction() instanceof LLOAD)
		{
			int load_index = (int) ((LLOAD)(handle.getInstruction())).getIndex();
        	long value = getLoadLongValue(handle, instList,cpgen,load_index);
        	return value;
		}else
			return 0;
	}
	
	private double getPrevDouble(InstructionHandle handle, InstructionList instList, ConstantPoolGen cpgen)
	{
		//System.out.println("instruction = " + handle.getInstruction());
		if (handle.getInstruction() instanceof DCONST)
		{
			//System.out.println("FROM DCONST = " + ((DCONST)(handle.getInstruction())).getValue());
			//System.out.println();
			return (double) ((DCONST)(handle.getInstruction())).getValue();
		}else
		if (handle.getInstruction() instanceof LDC2_W)
		{
			//System.out.println("FROM LDC2_W = " + ((LDC2_W)(handle.getInstruction())).getValue(cpgen));
			//System.out.println();
			return (double) ((LDC2_W)(handle.getInstruction())).getValue(cpgen);
		}else 
		if (handle.getInstruction() instanceof DLOAD)
		{
			int load_index = (int) ((DLOAD)(handle.getInstruction())).getIndex();
        	double value = getLoadDoubleValue(handle, instList,cpgen,load_index);
        	return value;
		}else
			return 0;
	}
	
	private float getPrevFloat(InstructionHandle handle, InstructionList instList, ConstantPoolGen cpgen)
	{
		//System.out.println("instruction = " + handle.getInstruction());
		if (handle.getInstruction() instanceof FCONST)
		{
			//System.out.println("FROM FCONST = " + ((FCONST)(handle.getInstruction())).getValue());
			//System.out.println();
			return (float) ((FCONST)(handle.getInstruction())).getValue();
		} else if (handle.getInstruction() instanceof LDC)
		{
			//System.out.println("FROM LDC = " + ((LDC)(handle.getInstruction())).getValue(cpgen));
			//System.out.println();
			return (float) ((LDC)(handle.getInstruction())).getValue(cpgen);
		} else if (handle.getInstruction() instanceof FLOAD)
		{
			int load_index = (int) ((FLOAD)(handle.getInstruction())).getIndex();
        	float value = getLoadFloatValue(handle, instList,cpgen,load_index);
        	return value;
		}else
			return 0;
	}
	/** ****************************************************************************************************************** **/

	
	/**
	 * GETTING THE LOADVALUES
	 **/
	/** ****************************************************************************************************************** **/
	private int getLoadIntValue (InstructionHandle handle, InstructionList instList, ConstantPoolGen  cpgen, int load_index)
	{
		// get the int value
		// iterate back until the value is found (where the instruction index for the ISTORE is the same as ILOAD)
		
		// start from the current handle
		InstructionHandle newHandle = handle;
		
		//iterate back
		while (newHandle.getPrev()!=null)
		{
			if (newHandle.getInstruction() instanceof ISTORE)
			{
				if ((load_index == (int)((ISTORE)(newHandle.getInstruction())).getIndex())) 
				{
					return getPrevInt(newHandle.getPrev(), instList, cpgen);
				}
			}		
			newHandle = newHandle.getPrev();
		}
		return 0;
	}
	
	private long getLoadLongValue (InstructionHandle handle, InstructionList instList, ConstantPoolGen  cpgen, int load_index)
	{
		// get the long value
		// iterate back until the value is found (where the instruction index for the LSTORE is the same as LLOAD)
		
		// start from the current handle
		InstructionHandle newHandle = handle;
		
		//iterate back
		while (newHandle.getPrev()!=null)
		{
			if (newHandle.getInstruction() instanceof LSTORE)
			{
				if ((load_index == (int)((LSTORE)(newHandle.getInstruction())).getIndex())) 
				{
					return getPrevLong(newHandle.getPrev(), instList, cpgen);
				}
			}		
			newHandle = newHandle.getPrev();
		}
		return 0;
	}
	
	private double getLoadDoubleValue (InstructionHandle handle, InstructionList instList, ConstantPoolGen  cpgen, int load_index)
	{
		// get the double value
		// iterate back until the value is found (where the instruction index for the DSTORE is the same as DLOAD)
		
		// start from the current handle
		InstructionHandle newHandle = handle;
		
		//iterate back
		while (newHandle.getPrev()!=null)
		{
			if (newHandle.getInstruction() instanceof DSTORE)
			{
				if ((load_index == (int)((DSTORE)(newHandle.getInstruction())).getIndex())) 
				{
					return getPrevDouble(newHandle.getPrev(), instList, cpgen);
				}
			}		
			newHandle = newHandle.getPrev();
		}
		return 0;
	}
	
	private float getLoadFloatValue (InstructionHandle handle, InstructionList instList, ConstantPoolGen  cpgen, int load_index)
	{
		// get the flaot value
		// iterate back until the value is found (where the instruction index for the FSTORE is the same as FLOAD)
		
		// start from the current handle
		InstructionHandle newHandle = handle;
		
		//iterate back
		while (newHandle.getPrev()!=null)
		{
			if (newHandle.getInstruction() instanceof FSTORE)
			{
				if ((load_index == (int)((FSTORE)(newHandle.getInstruction())).getIndex())) 
				{
					return getPrevFloat(newHandle.getPrev(), instList, cpgen);
				}
			}		
			newHandle = newHandle.getPrev();
		}
		return 0;
	}
	/** ****************************************************************************************************************** **/
	
	void optimizeComparaisons (InstructionHandle handle, InstructionList instList, ClassGen cgen, ConstantPoolGen cpgen)
	{
		
	}
	
	void optimizeArithmetic (InstructionHandle handle, InstructionList instList, ClassGen cgen, ConstantPoolGen cpgen)
	{
		//optimising addition for integers
		if (handle.getInstruction() instanceof IADD)
        {
			// searching the values we have to add
			int value1 = getPrevInt(handle.getPrev(), instList, cpgen);
			int value2 = getPrevInt(handle.getPrev().getPrev(), instList, cpgen);
        	System.out.println("FOUND VALUE 1 = " + handle.getPrev().getInstruction() + "    " + value1);
        	System.out.println("FOUND VALUE 2 = " + handle.getPrev().getPrev().getInstruction() + "    " + value2);
        	
        	Integer intObj = new Integer(value1+value2);
        	byte new_value = intObj.byteValue();
        	System.out.println("VALUE ADDED IN BYTES = " + new_value);
        	
        	
        	handle= handle.getNext();
        	try {
            	 System.out.println("DELETING HANDLES = " + handle.getPrev().getInstruction() + "   " + handle.getPrev().getPrev().getInstruction() + "   " + handle.getPrev().getPrev().getPrev().getInstruction());
                // delete the old values
        		instList.delete(handle.getPrev());
                instList.delete(handle.getPrev().getPrev());
                instList.delete(handle.getPrev().getPrev().getPrev());
            } catch (TargetLostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        	
        	//adding the values - BIPUSH pushes the byte value onto the stack as an integer value
        	instList.insert(handle, new BIPUSH(new_value));
        }
		//optimising subtraction for integers
		else if (handle.getInstruction() instanceof ISUB)
		{
			// searching the values we have to subtract
			int value1 = getPrevInt(handle.getPrev(), instList, cpgen);
			int value2 = getPrevInt(handle.getPrev().getPrev(), instList, cpgen);
        	System.out.println("FOUND VALUE 1 = " + value1);
        	System.out.println("FOUND VALUE 2 = " + value2);
        	
        	Integer intObj = new Integer(value2-value1);
        	byte new_value = intObj.byteValue();
        	System.out.println("VALUE ADDED IN BYTES = " + new_value);
        	//adding the values - BIPUSH pushes the byte value onto the stack as an integer value
        	instList.insert(handle, new BIPUSH(new_value));
        	
        	try {
                // delete the old values
                instList.delete(handle.getPrev());
                instList.delete(handle.getPrev().getPrev());
            } catch (TargetLostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
		}
		//optimising multiplication for integers
		else if (handle.getInstruction() instanceof IMUL)
        {
			// searching the values we have to multiply
			int value1 = getPrevInt(handle.getPrev(), instList, cpgen);
			int value2 = getPrevInt(handle.getPrev().getPrev(), instList, cpgen);
        	System.out.println("FOUND VALUE 1 = " + value1);
        	System.out.println("FOUND VALUE 2 = " + value2);
        	
        	Integer intObj = new Integer(value1*value2);
        	byte new_value = intObj.byteValue();
        	System.out.println("VALUE ADDED IN BYTES = " + new_value);
        	//adding the values - BIPUSH pushes the byte value onto the stack as an integer value
        	instList.insert(handle, new BIPUSH(new_value));
        	
        	try {
                // delete the old values
                instList.delete(handle.getPrev());
                instList.delete(handle.getPrev().getPrev());
            } catch (TargetLostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
		//optimising division for integers
		else if (handle.getInstruction() instanceof IDIV)
		{
			// searching the values we have to divide
			int value1 = getPrevInt(handle.getPrev(), instList, cpgen);
			int value2 = getPrevInt(handle.getPrev().getPrev(), instList, cpgen);
        	System.out.println("FOUND VALUE 1 = " + value1);
        	System.out.println("FOUND VALUE 2 = " + value2);
 
        	Integer intObj = new Integer(value2/value1);
        	byte new_value = intObj.byteValue();
        	System.out.println("VALUE ADDED IN BYTES = " + new_value);
        	//adding the values - BIPUSH pushes the byte value onto the stack as an integer value
        	instList.insert(handle, new BIPUSH(new_value));
        	
        	try {
                // delete the old values
                instList.delete(handle.getPrev());
                instList.delete(handle.getPrev().getPrev());
            } catch (TargetLostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
		}
		//optimising remainder for integers
		else if (handle.getInstruction() instanceof IREM)
		{
			// searching the values we have to get the remainder of
			int value1 = getPrevInt(handle.getPrev(), instList, cpgen);
			int value2 = getPrevInt(handle.getPrev().getPrev(), instList, cpgen);
        	System.out.println("FOUND VALUE 1 = " + value1);
        	System.out.println("FOUND VALUE 2 = " + value2);
        	
        	Integer intObj = new Integer(value2%value1);
        	byte new_value = intObj.byteValue();
        	System.out.println("VALUE ADDED IN BYTES = " + new_value);
        	//adding the values - BIPUSH pushes the byte value onto the stack as an integer value
        	instList.insert(handle, new BIPUSH(new_value));
        	
        	try {
                // delete the old values
                instList.delete(handle.getPrev());
                instList.delete(handle.getPrev().getPrev());
            } catch (TargetLostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
		}
		
		/** ******************************************************************** **/
		//optimising addition for longs
		else if (handle.getInstruction() instanceof LADD)
	    {
			// searching the values we have to add
			long value1 = getPrevLong(handle.getPrev(), instList, cpgen);
			long value2 = getPrevLong(handle.getPrev().getPrev(), instList, cpgen);
			System.out.println("FOUND VALUE 1 = " + value1);
			System.out.println("FOUND VALUE 2 = " + value2);
			        	
			Long longObj = new Long(value1+value2);		
			byte new_value = longObj.byteValue();
			System.out.println("VALUE ADDED IN BYTES = " + new_value);
			//adding the values - BIPUSH pushes the byte value onto the stack as an Long value
			instList.insert(handle, new BIPUSH(new_value));
			        	
			try {
				// delete the old values
			    instList.delete(handle.getPrev());
			    instList.delete(handle.getPrev().getPrev());
			} catch (TargetLostException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
	    }
		//optimising subtraction for longs
		else if (handle.getInstruction() instanceof LSUB)
		{
			// searching the values we have to subtract
			long value1 = getPrevLong(handle.getPrev(), instList, cpgen);
			long value2 = getPrevLong(handle.getPrev().getPrev(), instList, cpgen);
			System.out.println("FOUND VALUE 1 = " + value1);
			System.out.println("FOUND VALUE 2 = " + value2);
						        	
			Long longObj = new Long(value2-value1);	
			byte new_value = longObj.byteValue();
			System.out.println("VALUE ADDED IN BYTES = " + new_value);
			//adding the values - BIPUSH pushes the byte value onto the stack as an Long value
			instList.insert(handle, new BIPUSH(new_value));
						        	
			try {
				// delete the old values
				instList.delete(handle.getPrev());
				instList.delete(handle.getPrev().getPrev());
			} catch (TargetLostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//optimising multiplication for longs
		else if (handle.getInstruction() instanceof LMUL)
		{
			// searching the values we have to multiply
			long value1 = getPrevLong(handle.getPrev(), instList, cpgen);
			long value2 = getPrevLong(handle.getPrev().getPrev(), instList, cpgen);
        	System.out.println("FOUND VALUE 1 = " + value1);
        	System.out.println("FOUND VALUE 2 = " + value2);
        	
        	Long longObj = new Long(value1*value2);
        	byte new_value = longObj.byteValue();
        	System.out.println("VALUE ADDED IN BYTES = " + new_value);
        	//adding the values - BIPUSH pushes the byte value onto the stack as an integer value
        	instList.insert(handle, new BIPUSH(new_value));
        	
        	try {
                // delete the old values
                instList.delete(handle.getPrev());
                instList.delete(handle.getPrev().getPrev());
            } catch (TargetLostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
		}
		//optimising division for longs
		else if (handle.getInstruction() instanceof LDIV)
		{
			// searching the values we have to divide
			long value1 = getPrevLong(handle.getPrev(), instList, cpgen);
			long value2 = getPrevLong(handle.getPrev().getPrev(), instList, cpgen);
        	System.out.println("FOUND VALUE 1 = " + value1);
        	System.out.println("FOUND VALUE 2 = " + value2);
 
        	Long longObj = new Long(value2/value1);
        	byte new_value = longObj.byteValue();
        	System.out.println("VALUE ADDED IN BYTES = " + new_value);
        	//adding the values - BIPUSH pushes the byte value onto the stack as an integer value
        	instList.insert(handle, new BIPUSH(new_value));
        	
        	try {
                // delete the old values
                instList.delete(handle.getPrev());
                instList.delete(handle.getPrev().getPrev());
            } catch (TargetLostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }				
		}
		//optimising remainder for longs
		else if (handle.getInstruction() instanceof LREM)
		{
			// searching the values we have to get the remainder of
			long value1 = getPrevLong(handle.getPrev(), instList, cpgen);
			long value2 = getPrevLong(handle.getPrev().getPrev(), instList, cpgen);
        	System.out.println("FOUND VALUE 1 = " + value1);
        	System.out.println("FOUND VALUE 2 = " + value2);
 
        	Long longObj = new Long(value2%value1);
        	byte new_value = longObj.byteValue();
        	System.out.println("VALUE ADDED IN BYTES = " + new_value);
        	//adding the values - BIPUSH pushes the byte value onto the stack as an integer value
        	instList.insert(handle, new BIPUSH(new_value));
        	
        	try {
                // delete the old values
                instList.delete(handle.getPrev());
                instList.delete(handle.getPrev().getPrev());
            } catch (TargetLostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }					
		}
		
		/** ******************************************************************** **/
		//optimising addition for floats
		else if (handle.getInstruction() instanceof FADD)
		{
			// searching the values we have to add
			float value1 = getPrevFloat(handle.getPrev(), instList, cpgen);
			float value2 = getPrevFloat(handle.getPrev().getPrev(), instList, cpgen);
        	System.out.println("FOUND VALUE 1 = " + value1);
        	System.out.println("FOUND VALUE 2 = " + value2);
        	
        	Float floatObj = new Float(value1+value2);
        	byte new_value = floatObj.byteValue();
        	System.out.println("VALUE ADDED IN BYTES = " + new_value);
        	//adding the values - BIPUSH pushes the byte value onto the stack as an integer value
        	instList.insert(handle, new BIPUSH(new_value));
        	
        	try {
                // delete the old values
                instList.delete(handle.getPrev());
                instList.delete(handle.getPrev().getPrev());
            } catch (TargetLostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
		}
		//optimising subtraction for floats
		else if (handle.getInstruction() instanceof FSUB)
		{
			// searching the values we have to subtract
			float value1 = getPrevFloat(handle.getPrev(), instList, cpgen);
			float value2 = getPrevFloat(handle.getPrev().getPrev(), instList, cpgen);
        	System.out.println("FOUND VALUE 1 = " + value1);
        	System.out.println("FOUND VALUE 2 = " + value2);
        	
        	Float floatObj = new Float(value2-value1);
        	byte new_value = floatObj.byteValue();
        	System.out.println("VALUE ADDED IN BYTES = " + new_value);
        	//adding the values - BIPUSH pushes the byte value onto the stack as an integer value
        	instList.insert(handle, new BIPUSH(new_value));
        	
        	try {
                // delete the old values
                instList.delete(handle.getPrev());
                instList.delete(handle.getPrev().getPrev());
            } catch (TargetLostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }					
		}
		//optimising multiplication for floats
		else if (handle.getInstruction() instanceof FMUL)
		{
			// searching the values we have to multiply
			float value1 = getPrevFloat(handle.getPrev(), instList, cpgen);
			float value2 = getPrevFloat(handle.getPrev().getPrev(), instList, cpgen);
        	System.out.println("FOUND VALUE 1 = " + value1);
        	System.out.println("FOUND VALUE 2 = " + value2);
        	
        	Float floatObj = new Float(value1*value2);
        	byte new_value = floatObj.byteValue();
        	System.out.println("VALUE ADDED IN BYTES = " + new_value);
        	//adding the values - BIPUSH pushes the byte value onto the stack as an integer value
        	instList.insert(handle, new BIPUSH(new_value));
        	
        	try {
                // delete the old values
                instList.delete(handle.getPrev());
                instList.delete(handle.getPrev().getPrev());
            } catch (TargetLostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }		        	
		}
		//optimising division for floats
		else if (handle.getInstruction() instanceof FDIV)
		{
			// searching the values we have to divide
			float value1 = getPrevFloat(handle.getPrev(), instList, cpgen);
			float value2 = getPrevFloat(handle.getPrev().getPrev(), instList, cpgen);
        	System.out.println("FOUND VALUE 1 = " + value1);
        	System.out.println("FOUND VALUE 2 = " + value2);
        	
        	Float floatObj = new Float(value2/value1);
        	byte new_value = floatObj.byteValue();
        	System.out.println("VALUE ADDED IN BYTES = " + new_value);
        	//adding the values - BIPUSH pushes the byte value onto the stack as an integer value
        	instList.insert(handle, new BIPUSH(new_value));
        	
        	try {
                // delete the old values
                instList.delete(handle.getPrev());
                instList.delete(handle.getPrev().getPrev());
            } catch (TargetLostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }					
		}
		//optimising remainder for floats
		else if (handle.getInstruction() instanceof FREM)
		{
			// searching the values we have to get the remainder of
			float value1 = getPrevFloat(handle.getPrev(), instList, cpgen);
			float value2 = getPrevFloat(handle.getPrev().getPrev(), instList, cpgen);
        	System.out.println("FOUND VALUE 1 = " + value1);
        	System.out.println("FOUND VALUE 2 = " + value2);
        	
        	Float floatObj = new Float(value2%value1);
        	byte new_value = floatObj.byteValue();
        	System.out.println("VALUE ADDED IN BYTES = " + new_value);
        	//adding the values - BIPUSH pushes the byte value onto the stack as an integer value
        	instList.insert(handle, new BIPUSH(new_value));
        	
        	try {
                // delete the old values
                instList.delete(handle.getPrev());
                instList.delete(handle.getPrev().getPrev());
            } catch (TargetLostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }				
		}
		
		/** ******************************************************************** **/
		//optimising addition for doubles
		else if (handle.getInstruction() instanceof DADD)
		{
			// searching the values we have to add
			double value1 = getPrevDouble(handle.getPrev(), instList, cpgen);
			double value2 = getPrevDouble(handle.getPrev().getPrev(), instList, cpgen);
        	System.out.println("FOUND VALUE 1 = " + value1);
        	System.out.println("FOUND VALUE 2 = " + value2);
        	
        	Double doubleObj = new Double(value1+value2);
        	byte new_value = doubleObj.byteValue();
        	System.out.println("VALUE ADDED IN BYTES = " + new_value);
        	//adding the values - BIPUSH pushes the byte value onto the stack as an integer value
        	instList.insert(handle, new BIPUSH(new_value));
        	
        	try {
                // delete the old values
                instList.delete(handle.getPrev());
                instList.delete(handle.getPrev().getPrev());
            } catch (TargetLostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
		}
		//optimising subtraction for doubles
		else if (handle.getInstruction() instanceof DSUB)
		{
			// searching the values we have to subtract
			double value1 = getPrevDouble(handle.getPrev(), instList, cpgen);
			double value2 = getPrevDouble(handle.getPrev().getPrev(), instList, cpgen);
        	System.out.println("FOUND VALUE 1 = " + value1);
        	System.out.println("FOUND VALUE 2 = " + value2);
        	
        	Double doubleObj = new Double(value2-value1);
        	byte new_value = doubleObj.byteValue();
        	System.out.println("VALUE ADDED IN BYTES = " + new_value);
        	//adding the values - BIPUSH pushes the byte value onto the stack as an integer value
        	instList.insert(handle, new BIPUSH(new_value));
        	
        	try {
                // delete the old values
                instList.delete(handle.getPrev());
                instList.delete(handle.getPrev().getPrev());
            } catch (TargetLostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }						
		}
		//optimising multiplication for doubles
		else if (handle.getInstruction() instanceof DMUL)
		{
			// searching the values we have to multiply
			double value1 = getPrevDouble(handle.getPrev(), instList, cpgen);
			double value2 = getPrevDouble(handle.getPrev().getPrev(), instList, cpgen);
        	System.out.println("FOUND VALUE 1 = " + value1);
        	System.out.println("FOUND VALUE 2 = " + value2);
        	
        	Double doubleObj = new Double(value1*value2);
        	byte new_value = doubleObj.byteValue();
        	System.out.println("VALUE ADDED IN BYTES = " + new_value);
        	//adding the values - BIPUSH pushes the byte value onto the stack as an integer value
        	instList.insert(handle, new BIPUSH(new_value));
        	
        	try {
                // delete the old values
                instList.delete(handle.getPrev());
                instList.delete(handle.getPrev().getPrev());
            } catch (TargetLostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }		        	
		}
		//optimising division for doubles
		else if (handle.getInstruction() instanceof DDIV)
		{// searching the values we have to divide
			double value1 = getPrevDouble(handle.getPrev(), instList, cpgen);
			double value2 = getPrevDouble(handle.getPrev().getPrev(), instList, cpgen);
        	System.out.println("FOUND VALUE 1 = " + value1);
        	System.out.println("FOUND VALUE 2 = " + value2);
        	
        	Double doubleObj = new Double(value2/value1);
        	byte new_value = doubleObj.byteValue();
        	System.out.println("VALUE ADDED IN BYTES = " + new_value);
        	//adding the values - BIPUSH pushes the byte value onto the stack as an integer value
        	instList.insert(handle, new BIPUSH(new_value));
        	
        	try {
                // delete the old values
                instList.delete(handle.getPrev());
                instList.delete(handle.getPrev().getPrev());
            } catch (TargetLostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }				
		}
		//optimising remainder for doubles
		else if (handle.getInstruction() instanceof DREM)
		{
			// searching the values we have to get the remainder of
			double value1 = getPrevDouble(handle.getPrev(), instList, cpgen);
			double value2 = getPrevDouble(handle.getPrev().getPrev(), instList, cpgen);
        	System.out.println("FOUND VALUE 1 = " + value1);
        	System.out.println("FOUND VALUE 2 = " + value2);
        	
        	Double doubleObj = new Double(value2%value1);
        	byte new_value = doubleObj.byteValue();
        	System.out.println("VALUE ADDED IN BYTES = " + new_value);
        	//adding the values - BIPUSH pushes the byte value onto the stack as an integer value
        	instList.insert(handle, new BIPUSH(new_value));
        	
        	try {
                // delete the old values
                instList.delete(handle.getPrev());
                instList.delete(handle.getPrev().getPrev());
            } catch (TargetLostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }					
		}
	}
	
	 private void optimizeMethod(ClassGen cgen, ConstantPoolGen cpgen, Method method) {
	        // Get the Code of the method, which is a collection of bytecode instructions
	        Code methodCode = method.getCode();

			//System.out.println("cpgen= " + cpgen);

	        // Now get the actualy bytecode data in byte array,
	        // and use it to initialise an InstructionList
	        InstructionList instList = new InstructionList(methodCode.getCode());

			//System.out.println("instList= " + instList);

	        // Initialise a method generator with the original method as the baseline
	        //MethodGen methodGen = new MethodGen(method.getAccessFlags(), method.getReturnType(), method.getArgumentTypes(),
	        //        null, method.getName(), cgen.getClassName(), instList, cpgen);
	        MethodGen methodGen = new MethodGen(method, cgen.getClassName(), cpgen);

	        // InstructionHandle is a wrapper for actual Instructions
	        for (InstructionHandle handle : instList.getInstructionHandles()) {
				System.out.println("instHandle= " + handle.getInstruction());
	        	optimizeArithmetic(handle, instList, cgen, cpgen);	            
	         
                
			        // set max stack/local
			        methodGen.setMaxStack();
			        methodGen.setMaxLocals();
		
			        // remove local variable table
			        methodGen.removeLocalVariables();
		
			        //methodGen.removeCodeAttributes();
		
			        // generate the new method with replaced instList
			        Method newMethod = methodGen.getMethod();
			        // replace the method in the original class
			        cgen.replaceMethod(method, newMethod);
		    }
	}
	 
	public void optimize()
	{
		ClassGen cgen = new ClassGen(original);
		ConstantPoolGen cpgen = cgen.getConstantPool();

		//System.out.println("cgen= " + cgen);
		//System.out.println("cpgen= " + cpgen);

		// Implement your optimization here
		 Method[] methods = cgen.getMethods();
	        for (Method m : methods) {
	           // System.out.println("Method: " + m.getName());
	            optimizeMethod(cgen, cpgen, m);
	            System.out.println("Method End!");
	        }
	        gen = cgen;

		this.optimized = gen.getJavaClass();
	}

	
	public void write(String optimisedFilePath)
	{
		this.optimize();

		try {
			FileOutputStream out = new FileOutputStream(new File(optimisedFilePath));
			this.optimized.dump(out);
		} catch (FileNotFoundException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}
}