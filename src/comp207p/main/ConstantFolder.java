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
	
	// optimise comparisons
	void optimizeComparisons (InstructionHandle handle, InstructionList instList, ClassGen cgen, ConstantPoolGen cpgen)
	{
		//optimising float comparisons
		if (handle.getInstruction() instanceof FCMPL || handle.getInstruction() instanceof FCMPG)
		{
			float value1 = getPrevFloat(handle.getPrev(), instList, cpgen);
			float value2 = getPrevFloat(handle.getPrev().getPrev(), instList, cpgen);
			
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
        	if (handle.getInstruction() instanceof FCMPL)
    		{
				if (value1<value2)
					instList.insert(handle, new LDC(1));
				else
					instList.insert(handle, new LDC(0));
    		}
        	if (handle.getInstruction() instanceof FCMPG)
    		{
        		if (value1>value2)
        			instList.insert(handle, new LDC(1));
        		else
        			instList.insert(handle, new LDC(0));
    		}
		}
		
		//optimising double comparisons
		if (handle.getInstruction() instanceof DCMPL || handle.getInstruction() instanceof DCMPG)
		{
			double value1 = getPrevDouble(handle.getPrev(), instList, cpgen);
			double value2 = getPrevDouble(handle.getPrev().getPrev(), instList, cpgen);
			
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
        	if (handle.getInstruction() instanceof DCMPL)
    		{
				if (value1<value2)
					instList.insert(handle, new LDC(1));
				else
					instList.insert(handle, new LDC(0));
    		}
        	if (handle.getInstruction() instanceof DCMPG)
    		{
        		if (value1>value2)
    				instList.insert(handle, new LDC(1));
    			else
    				instList.insert(handle, new LDC(0));
    		}
		}
	}
	
	/** ****************************************************************************************************************** **/
	
	// optimise arithmetic operations
	void optimizeArithmetic (InstructionHandle handle, InstructionList instList, ClassGen cgen, ConstantPoolGen cpgen)
	{
		//optimising arithmetic for integers
		if (handle.getInstruction() instanceof IADD || handle.getInstruction() instanceof ISUB || handle.getInstruction() instanceof IMUL ||handle.getInstruction() instanceof IDIV || handle.getInstruction() instanceof IREM)
		{
			//Searching for the values
			int value1 = getPrevInt(handle.getPrev(), instList, cpgen);
			int value2 = getPrevInt(handle.getPrev().getPrev(), instList, cpgen);
			System.out.println("FOUND VALUE 1 = " + handle.getPrev().getInstruction() + "    " + value1);
			System.out.println("FOUND VALUE 2 = " + handle.getPrev().getPrev().getInstruction() + "    " + value2);
			int val = 0;
			if (handle.getInstruction() instanceof IADD){
				val = value1+value2;
			}
			if (handle.getInstruction() instanceof ISUB){
				val = value2-value1;
			}
			if (handle.getInstruction() instanceof IMUL){
				val = value2*value1;
			}
			if (handle.getInstruction() instanceof IDIV){
				val = value2/value1;
			}
			if (handle.getInstruction() instanceof IREM){
				val = value2%value1;
			}
			System.out.println("VALUE ADDED = " + val);
        	
			handle = handle.getNext();
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
        	
        	//adding the values - LDC pushes the int value onto the stack 
        	/*!! better solution? !!*/
        	instList.insert(handle, new LDC(cgen.getConstantPool().addInteger(val)));
		}
        
		/** ******************************************************************** **/
		//optimising arithmetic for longs
		if (handle.getInstruction() instanceof LADD || handle.getInstruction() instanceof LSUB || handle.getInstruction() instanceof LMUL ||handle.getInstruction() instanceof LDIV || handle.getInstruction() instanceof LREM)
		{
			//Searching for the values
			long value1 = getPrevLong(handle.getPrev(), instList, cpgen);
			long value2 = getPrevLong(handle.getPrev().getPrev(), instList, cpgen);
			System.out.println("FOUND VALUE 1 = " + handle.getPrev().getInstruction() + "    " + value1);
			System.out.println("FOUND VALUE 2 = " + handle.getPrev().getPrev().getInstruction() + "    " + value2);
			long val = 0;
			if (handle.getInstruction() instanceof LADD){
				val = value1+value2;
			}
			if (handle.getInstruction() instanceof LSUB){
				val = value2-value1;
			}
			if (handle.getInstruction() instanceof LMUL){
				val = value2*value1;
			}
			if (handle.getInstruction() instanceof LDIV){
				val = value2/value1;
			}
			if (handle.getInstruction() instanceof LREM){
				val = value2%value1;
			}
			System.out.println("VALUE ADDED = " + val);
		        	
			handle = handle.getNext();
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
		        	
		    instList.insert(handle, new LDC2_W(cgen.getConstantPool().addLong(val)));
		}
		
		/** ******************************************************************** **/
		//optimising arithmetic for Floats
		if (handle.getInstruction() instanceof FADD || handle.getInstruction() instanceof FSUB || handle.getInstruction() instanceof FMUL ||handle.getInstruction() instanceof FDIV || handle.getInstruction() instanceof FREM)
		{
			//Searching for the values
			float value1 = getPrevFloat(handle.getPrev(), instList, cpgen);
			float value2 = getPrevFloat(handle.getPrev().getPrev(), instList, cpgen);
			System.out.println("FOUND VALUE 1 = " + handle.getPrev().getInstruction() + "    " + value1);
			System.out.println("FOUND VALUE 2 = " + handle.getPrev().getPrev().getInstruction() + "    " + value2);
			float val = 0;
			if (handle.getInstruction() instanceof FADD){
				val = value1+value2;
			}
			if (handle.getInstruction() instanceof FSUB){
				val = value2-value1;
			}
			if (handle.getInstruction() instanceof FMUL){
				val = value2*value1;
			}
			if (handle.getInstruction() instanceof FDIV){
				val = value2/value1;
			}
			if (handle.getInstruction() instanceof FREM){
				val = value2%value1;
			}
			System.out.println("VALUE ADDED = " + val);
		        	
			handle = handle.getNext();
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
		
		    instList.insert(handle, new LDC(cgen.getConstantPool().addFloat(val)));
		}
		
		/** ******************************************************************** **/
		//optimising arithmetic for doubles
		if (handle.getInstruction() instanceof DADD || handle.getInstruction() instanceof DSUB || handle.getInstruction() instanceof DMUL ||handle.getInstruction() instanceof DDIV || handle.getInstruction() instanceof DREM)
		{
			//Searching for the values
			double value1 = getPrevDouble(handle.getPrev(), instList, cpgen);
			double value2 = getPrevDouble(handle.getPrev().getPrev(), instList, cpgen);
			System.out.println("FOUND VALUE 1 = " + handle.getPrev().getInstruction() + "    " + value1);
			System.out.println("FOUND VALUE 2 = " + handle.getPrev().getPrev().getInstruction() + "    " + value2);
			double val = 0;
			if (handle.getInstruction() instanceof DADD){
				val = value1+value2;
			}
			if (handle.getInstruction() instanceof DSUB){
				val = value2-value1;
			}
			if (handle.getInstruction() instanceof DMUL){
				val = value2*value1;
			}
			if (handle.getInstruction() instanceof DDIV){
				val = value2/value1;
			}
			if (handle.getInstruction() instanceof DREM){
				val = value2%value1;
			}
			System.out.println("VALUE ADDED = " + val);
		        	
			handle = handle.getNext();
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
		    instList.insert(handle, new LDC2_W(cgen.getConstantPool().addDouble(val)));
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
	        	optimizeComparisons(handle, instList, cgen, cpgen);
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