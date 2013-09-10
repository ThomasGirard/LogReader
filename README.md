LogReader is a GUI application for viewing and filtering text files. It works kind of like an interactive fusion between tail and grep. 

## Basic Installation 

Installation is simple, but depends on whether you have **tail** on your path or not.

Copy the Logreader jar wherever you want.

#### With tail
    
    javaw -jar path/to/logreader.jar -u -f file1.txt file2.log

#### Without tail

LogReader can be used without tail, but files won't be dynamically re-read for changes. This will be fixed later.

The command is the same as above but without the -u flag. 

## Better installation
It's recommended to create an alias like this (again, remove the -u flag if you don't have tail): 

    alias readlogs='javaw -jar path/to/logreader.jar -u -f'

You can now do 
			readlog someFile.txt someOtherFile.log &

## Further info
MANUAL.txt
	Explains how things work.
    
Use the --help flag to see available options.
