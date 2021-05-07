# jats-diff 

#(Beta version, development in progress)


A high level (1, 2 and 3) XML diff algorithm for (JATS) XML comparison.
Beside the basic Level 1 edits detection: text INSERT, text DELETE, tree INSERT, tree DELETE and tree attribute change, 
this tool supports also Level 2 edits: text UPDATE, text MOVE, tree MOVE, tree UPGRADE, tree DOWNGRADE, tree SPLIT, tree MERGE, style INSERT, style DELETE style type UPDATE and style content UPDATE.

In addition to the Level 2 patterns detection, this tool supports also change semantics extraction, called Level 3.

In order to run the tool, you can do it by running the source code in your favourite IDE, or download and run the available jar file.
The jar file can be run in the following way:

    java -jar jats-diff.jar

Without any parameter, the tool will compare a default JATS XML file pair from it's example dataset.
In order to compar two custom JATS XML files, use the following command:

    java -jar jats-diff.jar orig.xml new.xml delta.xml semantics.xml

orig.xml being your original file; new.xml its modified version; delta.xml the output file for the Level1 and Level2 edits; semantics.xml for the Level 3 change semantics.

For more mass testing, you can use:

    java -jar jats-diff.jar -l list.txt

list.txt being a text file containing the previously mentioned file pairs per line.



