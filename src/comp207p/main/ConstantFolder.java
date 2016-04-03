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

	private int getPrevInt(InstructionHandle handle, InstructionList instList, ConstantPoolGen cpgen)
	{
		System.out.println("instruction = " + handle.getInstruction());
		if (handle.getInstruction() instanceof ICONST)
		{
			System.out.println("FROM ICONST = " + ((ICONST)(handle.getInstruction())).getValue());
			System.out.println();
		}
		if (handle.getInstruction() instanceof BIPUSH)
		{
			System.out.println("FROM BIPUSH = " + ((BIPUSH)(handle.getInstruction())).getValue());
			System.out.println();
		}
		if (handle.getInstruction() instanceof SIPUSH)
		{
			System.out.println("FROM SIPUSH = " + ((SIPUSH)(handle.getInstruction())).getValue());
			System.out.println();
		}
		if (handle.getInstruction() instanceof LDC)
		{
			System.out.println("FROM LDC = " + ((LDC)(handle.getInstruction())).getValue(cpgen));
			System.out.println();
		}
		if (handle.getInstruction() instanceof LDC2_W)
		{
			System.out.println("FROM LDC2_W = " + ((LDC2_W)(handle.getInstruction())).getValue(cpgen));
			System.out.println();
		}
		return 0;
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

				//System.out.println("instHandle= " + handle.getInstruction());
	            
	        	
	        	if ((handle.getInstruction() instanceof IF_ICMPLE)){
	        		return;
	        	}
				getPrevInt(handle, instList, cpgen);
	            	
                
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