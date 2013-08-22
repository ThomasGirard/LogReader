## Basic Installation
	1. Copy the Logreader jar wherever you want.
	2. Usage
		java -jar path/to/logreader.jar -u -f someFile.txt someOtherFile.log
	(you can use -h to view what options are available and what they do).
		
## Better installation
	1. Create an alias for starting the program (e.g. in ~/.bashrc):
		alias readlog='java -jar path/to/logreader.jar -u -f'
	
	2. You can now do 
			readlog someFile.txt someOtherFile.log &

## MANUAL.txt
	Explains how things work.