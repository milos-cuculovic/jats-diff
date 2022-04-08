# jats-diff 

#(Beta version, development in progress)

This algorithm is an implementation of jats-diff described in the following scientific article: https://onlinelibrary.wiley.com/doi/full/10.1002/spe.3074

jats-diff is an XML diff algorithm able to detect level 1, 2 and 3 edits while comparing two XML documents.
Although being a generic XML diff algorithm, jats-diff was mainly tested on comparing JATS XML documents (acdemic journal articles).

Beside the basic Level 1 edits detection: text INSERT, text DELETE, tree INSERT, tree DELETE and tree attribute change, 
algorithm also supports Level 2 edits: text UPDATE, text MOVE, tree MOVE, tree UPGRADE, tree DOWNGRADE, tree SPLIT, tree MERGE, style INSERT, style DELETE, style type UPDATE and style content UPDATE.

In addition to the Level 2 edit patterns detection, this tool also supports change semantics detection and calculates the similarity indexes between different XML nodes. We call this being Level 3 edits.

In order to run the tool, you can do it by running the source code in your favourite IDE, or download and run the available jar file: https://github.com/milos-cuculovic/jats-diff/blob/master/jats-diff.jar.



The jar file can be run in the following way:

    java -jar jats-diff.jar -L all orig.xml new.xml delta.xml semantics.xml

* -L can have the following values:

  * diff: for Level 1 and Level 2 edits
  * semantics: for Level 3 edits
  * all: do both, diff and semantics
* orig.xml being your original XML file
* new.xml its modified version;
* delta.xml the output file for the Level 1 and Level 2 edits
* semantics.xml the output file for the Level 3 edits


Some examples of the JATS file pairs can be found in examples directory in the project: https://github.com/milos-cuculovic/jats-diff/tree/master/examples


The following schema shows the simplified workflow of the jats-diff algorithm that is divided into 5 parts:
- Conversion of the two XML files as VTree objects
- Detection of the Level 1 Insert, Delete and Attribute Edit changes
- Refinment of the Level 1 and detection of Level 2 changes by identifiying specific Insert-Delete sequences
- Building of the delta output file
- Semantic analyses and refinment of the delta output + calculation of the similarity indexes

<p align="center">
  <img src="https://github.com/milos-cuculovic/jats-diff/blob/master/jats-diff_workflow.png?raw=true" />
</p>
