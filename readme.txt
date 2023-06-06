1. Description of Files
	1. CPU.java - Class simulating CPU of simulated computer system (parent class)
	2. Memory.java - Class simulating memory of simulated computer system (child class)
	3. sample5.txt - One of the text files that is set to print my full name
	4. Project1Summary.docx - Summary of Project 1
	5. readme.txt - Description of files and compilation instructions for Windows and CS1 Linux
	
2. How to Compile Project (all done in Windows terminal, but can be done in CS1 Linux server also)
	- If using Windows terminal
		1. Open Windows terminal
		2. Make sure all files listed in "Description of Files" above and all test files are in same file location and in the same folder (if not, create folder to put all files into)
		3. Compile Memory - javac Memory.java
		4. Compile CPU - javac CPU.java
		5. Execute CPU - java CPU filename interrupt (e.g., java CPU sample1.txt 30)
	- If using CS1 Linux Server
		1. Connect using GlobalProtect
		2. Open an SSH client (I used XShell)
		3. Open session and establish connection 
		4. Make sure all files listed in "Description of Files" above and all test files are in the same directory (if not, create a directory where all files can be put in)
		5. Compile Memory - javac Memory.java
		6. Compile CPU - javac CPU.java
		7. Execute CPU - java CPU filename interrupt (e.g., java CPU sample1.txt 30)