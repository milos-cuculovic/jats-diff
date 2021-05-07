package main.semantics_L3;


import main.diff_L1_L2.exceptions.InputFileException;

import java.util.ArrayList;

public class AttributeParse {
	private ArrayList<NodeChanged> modif = null;
	private ChangeList cl=new ChangeList();
	private NodeParents np=new NodeParents();
	private TabFigure tf=new TabFigure();
	private XmlFileAttributes xfa=new XmlFileAttributes();
	private Citables ct=new Citables();
	private References ref=new References();
	private TextChange tc=new TextChange();
	public ArrayList<NodeChanged> toolChange(BrowseDelta bD,boolean jaccard, boolean simtext,boolean simtextW) throws InputFileException {

	modif = cl.changetoList(bD);
	modif = ct.setcitable(modif);
	modif = ct.labSecfilt(modif);
	modif = ct.parasiteWord(modif);
	modif = ct.moveCitable(modif,bD);
	modif = ct.negQuote(modif);
	modif = np.specFather(modif,bD);
	modif = np.specFather1(modif,bD);
	modif = ref.labelCondi(modif);
	modif = ref.refNeg(modif,bD);
	modif = ref.findRef(modif,bD, jaccard, simtext, simtextW);
	modif = tf.findTabFig(modif, bD, jaccard, simtext, simtextW);
	modif = tc.movetext1( modif, bD);
	modif = tc.objupdate(modif);
	modif = np.findNoeudPar(modif, bD, jaccard, simtext, simtextW);
	modif = xfa.addChanging(modif, bD, jaccard, simtext, simtextW);
	modif = xfa.propSimilarity(modif, bD, jaccard, simtext, simtextW);
	modif = xfa.affichageChanging(modif, bD);
		return modif;





}}