import java.util.*;
import java.io.*;

public class Memory {
	// Memory array and filename
	private int[] memoryArr;
	private String filename;
	
	// Read string from CPU
	private String readFromCPU;
	
	public Memory(String filename) {
		// Initialize filename and empty array of size 2000
		this.filename = filename;
		this.memoryArr = new int[2000];
		
		// Initialize empty string from CPU
		this.readFromCPU = "";
	}
	
	public void readFromFile() {
		try {	
			// Iterate all lines
			Scanner fileRead = new Scanner(new File(filename));
			String line = "";
			
			// Place where in array
			int fromIndex = 0;
			int cntIndex = 0;
			
			// Loop iteration of file
			while (fileRead.hasNextLine()) {
				line = fileRead.nextLine();
				int len = line.length();
				
				// If empty line
				if (len == 0)
					continue;
				
				// Change memory location
				else if (line.charAt(0) == '.') {
					fromIndex = Integer.parseInt(line.substring(1, len));
					cntIndex = fromIndex;
				}
				
				// Put number in memory
				else if (line.charAt(0) != ' ' && line.charAt(0) != '.') {
					String[] lineSplit = line.split(" ", 2);
					int n = Integer.parseInt(lineSplit[0]);
					
					// Store number and increment
					memoryArr[cntIndex] = n;
					++cntIndex;
				}
			}
		}
		// Terminate under any error
		catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	public boolean inBounds(int index) {
		if (index >= 0 && index <= 1999)
			return true;
		else
			return false;
	}
	
	public void writeToMemory(int index, int val) {
		// If in bounds, write to address, else throw exception
		if (inBounds(index))
			memoryArr[index] = val;
		else
			throw new IndexOutOfBoundsException("Out of bounds!");
	}
	
	public int readFromMemory(int index) {
		// If in bounds, return value at address, else throw exception
		if (inBounds(index))
			return memoryArr[index];
		else
			throw new IndexOutOfBoundsException("Out of bounds!");
	}
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Input file is missing!");
			System.exit(1);
		}
		// Store filename
		String filename = args[0];
		
		// Read memory with filename
		Memory mem = new Memory(filename);
		mem.readFromFile();
		
		// Read command from CPU
		Scanner commandInput = new Scanner(System.in);
		String data = null;
		
		while (commandInput.hasNext()) {
			data = commandInput.nextLine();
			
			// End memory process if necessary
			if (data.equals("End"))
				break;
			
			else {
				// Split string into 3 parts
				String[] dataCommands = data.split(" ", 3);
				
				// Command, index, and value (read only)
				String readWrite = dataCommands[0];
				int index = Integer.parseInt(dataCommands[1]);
				int value = Integer.parseInt(dataCommands[2]);
				
				// Read from memory if read, else write to memory
				if (readWrite.equals("Read")) {
					int readVal = mem.readFromMemory(index);
					System.out.println(readVal);
				}
				else 
					mem.writeToMemory(index, value);
			}
		}
	}
}