import java.util.*;
import java.io.*;

public class CPU {
	// Read and write from memory
	private Scanner memRead;
	private PrintWriter memWrite;
	
	/* Registers: 
	1. PC = program counter
	2. SP = stack pointer (0-999 user, 1000-1999 stack)
	3. IR = instruction register
	4. AC = accumulator
	5. X is a variable
	6. Y is also a variable
	*/
	private int PC = 0;
	private int SP = 1000;
	private int IR = 0;
	private int AC = 0;
	private int X = 0;
	private int Y = 0;
	
	// For mode and interrupt
	private int mode;
	private boolean interrupt;
	
	public CPU(Scanner memRead, PrintWriter memWrite) {
		// Initialize memory read and write and timer
		this.memRead = memRead;
		this.memWrite = memWrite;
		
		// Initialize registers
		this.PC = 0;
		this.SP = 1000;
		this.IR = 0;
		this.AC = 0;
		this.X = 0;
		this.Y = 0;
		
		// At start, user mode and no interrupt
		this.mode = 0;
		this.interrupt = false;
	}
	
	// Get the interrupt existance
	public boolean getInterrupt() {
		return interrupt;
	}
	
	// Cannot access system address 1000 in user mode
	private boolean accessSysUserMode(int ind) {
		if (mode == 0 && ind >= 1000) 
			return true;
		else
			return false;
	}
	
	private int readVal(int ind) {
		if (accessSysUserMode(ind))			
			throw new RuntimeException("Memory violation: accessing system address 1000 in user mode ");
		
		// Write command to memory to ask for reading value
		String readFromMem = "Read " + ind + " -1";
		memWrite.printf(readFromMem + "\n");
		memWrite.flush();
		
		// Read in command from memory
		int val = -1;
		if (memRead.hasNext()) {
			String valStr = memRead.nextLine();
			
			// Parse valid num string to int
			if (valStr.length() > 0)  
				val = Integer.parseInt(valStr);
		}		
		return val;
	}
	
	private void writeVal(int ind, int val) {
		if (accessSysUserMode(ind))			
			throw new RuntimeException("Memory violation: accessing system address 1000 in user mode ");
		
		// Write command to memory to ask for writing value
		String writeToMem = "Write " + ind + " " + val;
		memWrite.printf(writeToMem + "\n");
		memWrite.flush();
	}
	
	public int fetch() {
		// Fetch instruction, then increment PC
		int ins = readVal(PC);
		++PC;
		return ins;
	}
	
	private void stackPush(int val) {
		// Goes into stack at SP - 1 because pushed on top of previous stack space head
		--SP;
		writeVal(SP, val);
	}
	
	private int stackPop() {
		/* Read value at top of stack and increment stack 
		because pointer goes one index deeper since top istaken out */
		int pop = readVal(SP);
		IR = pop;
		
		// Increment stack and return popped value;
		++SP;
		return pop;
	}
	
	private void switchMode(int flag) {
		// User mode
		if (flag == 0) {
			mode = 0;
			interrupt = false;
		}
		// Kernel mode
		else if (flag == 1) {
			mode = 1;
			interrupt = true;
		}
	}
	
	private int rand() {
		Random random = new Random();
		int rand = (1 + random.nextInt(100));
		return rand;
	}
	
	public void systemCall(boolean interrupt) {
		// At interrupt, decrement PC b/c we re-fetch unrun instruction
		if (interrupt)
			--PC;
		
		// In Kernel mode
		switchMode(1);
		
		// Save current SP and update SP to that in system
		int currSP = SP;
		SP = 2000;
		
		// Push user mode SP and PC onto stack since Kernel 
		stackPush(currSP);
		stackPush(PC);
		
		// Program counter (1000 at interrupt)
		if (interrupt)
			PC = 1000;
		else
			PC = 1500;
	}
	
	private void systemCallReturn() {
		// Pop user mode PC and SP to resume user mode
		PC = stackPop();
        SP = stackPop();
		
		// User mode
		switchMode(0);
	}
	
	public void execute(int ins) {
		switch (ins) {
			case 1:
				// Load value into the AC
				IR = fetch();
				AC = IR;
				break;
				
			case 2:
				// Load value at address into the AC
				int addressA = fetch();
				IR = readVal(addressA);
				AC = IR;
				break;
				
			case 3:
				/* Load the value from the address found in the given 
				address into the AC (for example, if LoadInd 500, and 500 
				contains 100, then load from 100). */
				int address1 = fetch();
				int address2 = readVal(address1);
				IR = readVal(address2);
				AC = IR;
				break;
				
			case 4:
				// Load the value at (address+X) into the AC 
				int addressB = fetch();
				IR = readVal(addressB + X);
				AC = IR;
				break;
				
			case 5:
				// Load the value at (address+Y) into the AC 
				int addressC = fetch();
				IR = readVal(addressC + Y);
				AC = IR;
				break;
				
			case 6:
				/* Load from (Sp+X) into the AC 
				(if SP is 990, and X is 1, load from 991)*/
                IR = readVal(SP + X);
                AC = IR;
                break;
				
			case 7:
				// Store the value in the AC into the address 
				IR = fetch();
				writeVal(IR, AC);
				break;
				
			case 8:
				// Gets a random int from 1 to 100 into the AC 
				AC = rand();
				break;
				
			case 9:
				/*If port=1, writes AC as an int to the screen 
				If port=2, writes AC as a char to the screen */
				IR = fetch();
				int port = IR;	
				
				if (port == 1) 
					System.out.print(AC);
				else if (port == 2) {
					char ascii = (char) AC;
					System.out.print(ascii);
				}
				break;
				
			case 10:
				// Add the value in X to the AC 
				AC += X;
				break;
				
			case 11:
				// Add the value in Y to the AC 
				AC += Y;
				break;
				
			case 12:
				// Subtract the value in X from the AC 
				AC -= X;
				break;
				
			case 13:
				// Subtract the value in Y from the AC 
				AC -= Y;
				break;
				
			case 14:
				// Copy the value in the AC to X 
				X = AC;
				break;
				
			case 15:
				// Copy the value in X to the AC 
				AC = X;
				break;
				
			case 16:
				// Copy the value in the AC to Y
				Y = AC;
				break;
				
			case 17:
				// Copy the value in X to the AC 
				AC = Y;
				break;
				
			case 18:
				// Copy the value in the AC to SP
				SP = AC;
				break;
				
			case 19:
				// Copy the value in SP to the AC 
				AC = SP;
				break;
				
			case 20:
				// Jump to the address
				IR = fetch();
				PC = IR;
				break;
				
			case 21:
				// Jump to the address only if the value in the AC is zero 
				IR = fetch();
				
				if (AC == 0)
					PC = IR;
				break;
				
			case 22:
				// Jump to the address only if the value in the AC is not zero 
				IR = fetch();
				
				if (AC != 0)
					PC = IR;
				break;
				
			case 23:
				// Push return address onto stack, jump to the address 
				stackPush(PC + 1);
				
				IR = fetch();
				PC = IR;
				break;
				
			case 24:
				// Pop return address from the stack, jump to the address 
				IR = stackPop();

				PC = IR;
				break;
				
			case 25:
				// Increment the value in X 
				++X;
				break;
				
			case 26:
				// Decrement the value in X 
				--X;
				break;
				
			case 27:
				// Push AC onto stack 
				stackPush(AC);
				break;
				
			case 28:
				// Pop from stack into AC 
				AC = stackPop();
				break;
				
			case 29:
				// Perform system call 
				systemCall(false);
				break;
				
			case 30:
				// Return from system call 
				systemCallReturn();
				break;
			
			case 50:
				// End execution
				break;
			
			default:
				break;
		}
	}
	
	public static void main(String[] args) {
		// Input file or timer is missing
		if (args.length != 2) {
			System.out.println("Invalid number of arguments");
			System.exit(1);
		}
		// Store filename and a timer
		String filename = args[0]; 
		int timer = Integer.parseInt(args[1]);
		
		// Check if timer counter is valid
		if (timer <= 0) {
			System.out.println("Invalid runtime!");
			System.exit(1);
		}
		try {
			// Execute Memory class
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec("java Memory " + filename);
			
			// Input and output streams from other program running
			InputStream is = proc.getInputStream();
			OutputStream os = proc.getOutputStream();
			
			// Get input and output from programs
			Scanner memRead = new Scanner(is);
			PrintWriter memWrite = new PrintWriter(os);
			
			// Execute CPU with set time
			CPU cpu = new CPU(memRead, memWrite);
			int time = timer;
			
			// Run the logic and fetch instruction and process runs
			int val = cpu.fetch();
			boolean interrupt = cpu.getInterrupt();

			while (true) {	
				// Terminate at 50
				if (val == 50)
					break;
				
				if (time == 0) {
					if (!interrupt) {
						// Execute system call and fetch instruction
						cpu.systemCall(true);
						val = cpu.fetch();
					}
					// Reset timer
					time = timer;
					continue;
				}
				else {
					// Execute instruction, get PC, and get interrupt
					cpu.execute(val);
					interrupt = cpu.getInterrupt();
					
					// Decrement timer and fetch new instruction
					val = cpu.fetch();
					--time;
				}
			}
			// Send the exit
			memWrite.printf("End" + "\n");
			memWrite.flush();
			
			// Wait for memory process to finish
			proc.waitFor();
			
			// Print process exit code
			int exitVal = proc.exitValue();
			System.out.println("\nProcess exited: " + exitVal);
		}
		// Terminate under any error
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
}