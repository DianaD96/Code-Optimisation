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
	
	private int loadIntValue(InstructionHandle handle, InstructionList instList, ConstantPoolGen cpgen) {
        if (handle.getInstruction() instanceof ILOAD) {
            int index = ((ILOAD) handle.getInstruction()).getIndex();
            int increments = 0;

            InstructionHandle handle1 = handle;
            while (handle1.getPrev() != null) {
                if (handle1.getInstruction() instanceof ISTORE && index == ((ISTORE) handle1.getInstruction()).getIndex()) {
                    return getIntValue(handle1.getPrev(), instList, cpgen) + increments;
                }
                if (handle1.getInstruction() instanceof IINC && ((IINC) handle1.getInstruction()).getIndex() == index) {
                    increments += ((IINC) handle1.getInstruction()).getIncrement();
                }
                handle1 = handle1.getPrev();
            }
        }

        System.out.println("Error loadIntValue()");
        return 0;
    }

	   private int getIntValue(InstructionHandle handle, InstructionList instList, ConstantPoolGen cpgen) {
	        if (handle.getInstruction() instanceof LDC) {
	            return (int) ((LDC) handle.getInstruction()).getValue(cpgen);
	        } else if (handle.getInstruction() instanceof ICONST) {
	            return (int) ((ICONST) handle.getInstruction()).getValue();
	        } else if (handle.getInstruction() instanceof BIPUSH) {
	            return (int) ((BIPUSH) handle.getInstruction()).getValue();
	        } else if (handle.getInstruction() instanceof SIPUSH) {
	            return (int) ((SIPUSH) handle.getInstruction()).getValue();
	        } else if (handle.getInstruction() instanceof ILOAD) {
	            return loadIntValue(handle, instList, cpgen);
	        //else convert other types to int
	        }
	        System.out.println("Error getIntValue()");
	        return 0;
	    }
	   
	   
	 private void optimizeMethod(ClassGen cgen, ConstantPoolGen cpgen, Method method) {
	        // Get the Code of the method, which is a collection of bytecode instructions
	        Code methodCode = method.getCode();

	        // Now get the actualy bytecode data in byte array,
	        // and use it to initialise an InstructionList
	        InstructionList instList = new InstructionList(methodCode.getCode());

	        // Initialise a method generator with the original method as the baseline
	        //MethodGen methodGen = new MethodGen(method.getAccessFlags(), method.getReturnType(), method.getArgumentTypes(),
	        //        null, method.getName(), cgen.getClassName(), instList, cpgen);
	        MethodGen methodGen = new MethodGen(method, cgen.getClassName(), cpgen);

	        // InstructionHandle is a wrapper for actual Instructions
	        for (InstructionHandle handle : instList.getInstructionHandles()) {

	        	//folding addition
	            if (handle.getInstruction() instanceof IADD) {
	                InstructionHandle prev = handle.getPrev();
	                //if(checkLoopModification(prev)) { continue; }
	                int prevVal = getIntValue(prev, instList, cpgen);

	                InstructionHandle prev2 = prev.getPrev();
	                //if(checkLoopModification(prev2)) { continue; }
	                int prevVal2 = getIntValue(prev2, instList, cpgen);

	                instList.insert(handle, new LDC(cgen.getConstantPool().addInteger(prevVal2 + prevVal)));
	              //  removeInstructions(instList, handle, prev, prev2);
	            }
                
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

		// Implement your optimization here
		 Method[] methods = cgen.getMethods();
	        for (Method m : methods) {
	            System.out.println("Method: " + m.getName());
	            optimizeMethod(cgen, cpgen, m);
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