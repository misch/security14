Use the command line to execute the program as follows:

1. Copy the executable jar-file to the directory in question.
2. Change to the corresponding directory using cd
3. Run

	java -jar integrityCheck.jar indexing
	or
	java -jar integrityCheck.jar analysis

	depending on which mode you want to have.

********************
How to ignore files:
********************
The ignored files have to be listed in a file called .ignore (one ignored file per row). You have to list the files by their absolute path and the .ignore-file should be contained in the same folder as the executable jar-file.