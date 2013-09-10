## Keyboard shortcuts
Most shortcuts are visible in the menu, here are some more hidden ones:

* alt-1 to alt-9 will switch to tabs 1 to 9
* Insert or Enter will create a new filter
* Delete will delete a filter

##	Config
Inside the jar there's a ch\arrg\logreader\base-conf.txt file. This file contains defaults for all properties in the application.

You can either change this file directly (bad) or create a copy it somewhere. You can then read your conf file by adding the argument

	--props path/to/your-conf.txt

All properties you set will override the defaults. You don't need to keep properties you don't want to override in your user file.
	
##	Filtering
		
### Filter chain

Inside each tab, above the text area are filters.
	
The idea is the following:

1. Each line passes through the filters, in order.
2. Filters either accept or reject the line.
3. Filters have 2 modes: "Print on accept" and "Discard on reject".
	1. If the line is accepted and the filter prints on accept, the line goes to the console.
	2. If the line is rejected and the filter discards on reject, the line is erased.
	3. Otherwise, the line goes to the next filter.
4. Finally there's an option to print or discard all lines that pass through all filters.
	
By using a combination of filters it is possible to achieve fine-grained control on the output.
	
### Basic filter
Basic filters accept a series of words and matches lines when all the words in the filter are matched.
They are case insensitive.

	[foo bar] accepts all lines where both foo and bar are present.

They accept negations: words that must not be in the line to match.

    [foo -bar] accepts lines where foo is present and bar isn't.

### Block filters

Block filters use two basic filters in conjuction. The first is the "from" filter, and the second is the "to" filter.
	
When the from filter accepts a line, all future lines are also accepted.
When the to filter accepts a line, then future lines aren't accepted anymore.
	
This is useful, for example, to remove output from certain exceptions. Assume you aren't interested in BananaException. Also suppose that all new log lines start with the current year (2013-). then use a block filter that starts at [BananaException] and ends at [2013-]. It will suppress all output from the first occurence of BananaException to the next log entry.
	
### Escaping -

If you wish to match the word "-foo" you cannot use the filter [-foo] because it means "exclude foo" instead.
	
You must escape the minus sign by using a +.
	
    [+-foo] means match all lines where "-foo" is present.
	[++foo] matches all the lines where "+foo" is present.
	[+foo] is the same as [foo].
	
The reverse also works:
	
    [-+foo] excludes all lines where "+foo" is present.
	[--foo] ecludes all lines where "-foo" is present.