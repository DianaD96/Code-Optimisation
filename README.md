# Code Optimisation

This program uses Java and BCEL (Byte Code Engineering Library) to implement the constant folding peephole optimisation as much as possible.
This code consits of:

## Simple Folding
Performs constant folding for the values of type int, long, float, and double, in the bytecode constant pool.
## Constant Variables
Optimises the uses of local variables of type int, long, float, and double, whose value does not change throughout the scope of the method (i.e. after the declaration, the variable is not reassigned). Propagates the initial value throughout the method to achieve constant folding.
## Dynamic Variables
Optimises uses of local variables of type int, long, float, and double, whose value will be reassigned with a different constant number during the scope of the method. Propagates the value of the variable, but for specific intervals: starting from the assignment (or
initialisation) until the next assignment.
