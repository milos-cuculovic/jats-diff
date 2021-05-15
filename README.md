# jats-diff 

#(Beta version, development in progress)


A high level (1, 2 and 3) XML diff algorithm for (JATS) XML comparison.
Beside the basic Level 1 edits detection: text INSERT, text DELETE, tree INSERT, tree DELETE and tree attribute change, 
this tool supports also Level 2 edits: text UPDATE, text MOVE, tree MOVE, tree UPGRADE, tree DOWNGRADE, tree SPLIT, tree MERGE, style INSERT, style DELETE style type UPDATE and style content UPDATE.

In addition to the Level 2 patterns detection, this tool supports also change semantics extraction, called Level 3.

In order to run the tool, you can do it by running the source code in your favourite IDE, or download and run the available jar file.
The jar file can be run in the following way:

    java -jar jats-diff.jar -L all orig.xml new.xml delta.xml semantics.xml

* -L can have the following values:

  * diff: do only JATS diffing
  * semantics: do only change semantics
  * all: do both, diff and semantics
* orig.xml being your original file
* new.xml its modified version;
* delta.xml the output file for the Level1 and Level2 edits
* semantics.xml for the Level 3 change semantics

Some examples of the JATS file pairs can be found in examples directory in the project: https://github.com/milos-cuculovic/jats-diff/tree/master/examples


