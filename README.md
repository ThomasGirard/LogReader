LogReader is a GUI application for viewing and filtering text files. 
It works kind of like an interactive fusion between tail and grep. 

## Basic Installation 

Installation is simple:

1. Copy the Logreader jar wherever you want.
2. javaw -jar path/to/logreader.jar -f file1.txt file2.log

## Better installation for CLI
It's recommended to create an alias like this : 

    alias readlogs='javaw -jar path/to/logreader.jar -f'

You can now do 
			readlog someFile.txt someOtherFile.log &

## Further info
MANUAL.txt
	Explains how things work.
    
Use the --help flag to see available options.
