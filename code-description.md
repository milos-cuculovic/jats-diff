# jats-diff code workflow description

The main class is located at main.semantics_L3.Main
In there, the parameters are parsed and the right actions are executed.
The two main actions are **diff** and **semantics**. 
The diff action is calling the main.diff_L1_L2.ui.Main and the semantics action is calling the
code locally from main.semantics_L3.Main.

The code workflow for the diff is as followd (main.diff_L1_L2.ui.Main):
 * The main class is getting the default params and calls the *OperationsHandler.doOperation()*
 * The OperationsHandler class is calling the *Ndiff.getDOMDocument()*
 * The Ndiff class is looping through all the phases with *switch config.phasesOrder*
   * FindTextChangeStyle
   * FindTextMove
   * FindUpgrade
   * FindDowngrade
   * FindMerge
   * FindSplit
   * FindMove
   * FindUpdate
   * Propagation - for attribute update detection
 
Each of those phases has specific code that parses the XML tree and tries to identify Level 2 patterns