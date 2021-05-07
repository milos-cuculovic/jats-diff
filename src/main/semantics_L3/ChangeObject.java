package main.semantics_L3;

import java.util.ArrayList;

public class ChangeObject {

	public String changement;
	private boolean isFrom=false;
	private boolean isRef=false;
	private boolean isTo=false;
	private String pos;
	private String oldatA;
	private String atA;
	private boolean xref;
	private String firstChildid;
	private boolean isEmpty=false;
	private String op;
	private String oldvalue="";
	private String newvalue="";
	private String nodenumB;
	private String nnBRef;
	private String nnARef;
	private String nodenumA;
	private String original;
	private String modified;
	private ArrayList<TableChange> tablechange;
	private String nodecount;
	private String oldatB;
	private String atB;
	private String labSec;
	private int label;
	private String citable;
	private String move;
	private String textContent;
	private String princNodenum;

	public String getOp() {
		return op;
	}

	public boolean hasNodenumberA() {
		return !(nodenumA == null);
	}
	public boolean hasText() {
		return !(textContent == null);
	}
	public boolean hasMove() {
		return !(move == null);
	}
	public boolean hasFirstChildid() {
		return !(firstChildid == null);
	}
	public boolean hasLabSec() {
		return !(labSec == null);
	}

	public boolean hasPos() {
		return !(pos == null);
	}

	public boolean hasNnAref() {
		return !(nnARef == null);
	}

	public boolean hasNnBref() {
		return !(nnBRef == null);
	}
	public boolean hasTableChange() {
		return !(tablechange == null);
	}

	public boolean hasNodecount() {
		return !(nodecount == null);
	}

	public boolean hasAtA() {
		return !(atA == null);
	}
	public boolean hasOldAtA() {
		return !(oldatA == null);
	}
	public boolean hasOldAtB() {
		return !(oldatB == null);
	}
	public boolean hasAtB() {
		return !(atB == null);
	}

	public boolean hasNodenumB() {
		return !(nodenumB == null);
	}

	public boolean hasOp() {
		return !(op == null);
	}

	public boolean hasOriginal() {
		return !(original == null);
	}

	public void setOp(String op) {
		this.op = op;
	}

	public String getNodenumA() {
		return nodenumA;
	}

	public void setNodenumA(String nodenumA) {
		this.nodenumA = nodenumA;
	}

	public String getChangement() {
		return changement;
	}

	public void setChangement(String changement) {
		this.changement = changement;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getAtA() {
		return atA;
	}

	public void setAtA(String at) {
		this.atA = at;
	}

	public String getNodenumB() {
		return nodenumB;
	}

	public void setNodenumB(String nodenumB) {
		this.nodenumB = nodenumB;
	}

	public String getOriginal() {
		return original;
	}

	public void setOriginal(String original) {
		this.original = original;
	}

	public String getModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}

	public String getNodecount() {
		return nodecount;
	}

	public void setNodecount(String nodecount) {
		this.nodecount = nodecount;
	}

	public String getAtB() {
		return atB;
	}

	public void setAtB(String atB) {
		this.atB = atB;
	}

	public ArrayList<TableChange> getTablechange() {
		return tablechange;
	}

	public void setTablechange(ArrayList<TableChange> tablechange) {
		this.tablechange = tablechange;
	}

	public String getNnBRef() {
		return nnBRef;
	}

	public void setNnBRef(String nnBRef) {
		this.nnBRef = nnBRef;
	}

	public boolean isXref() {
		return xref;
	}

	public void setXref(boolean xref) {
		this.xref = xref;
	}

	public String getNnARef() {
		return nnARef;
	}

	public void setNnARef(String nnARef) {
		this.nnARef = nnARef;
	}

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}

	public String getCitable() {
		return citable;
	}

	public void setCitable(String citable) {
		this.citable = citable;
	}

	public String getOldvalue() {
		return oldvalue;
	}

	public void setOldvalue(String oldvalue) {
		this.oldvalue = oldvalue;
	}

	public String getNewvalue() {
		return newvalue;
	}

	public void setNewvalue(String newvalue) {
		this.newvalue = newvalue;
	}


	public String getLabSec() {
		return labSec;
	}

	public void setLabSec(String labSec) {
		this.labSec = labSec;
	}

	public String getMove() {
		return move;
	}

	public void setMove(String move) {
		this.move = move;
	}

	public String getTextContent() {
		return textContent;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	public String getPrincNodenum() {
		return princNodenum;
	}

	public void setPrincNodenum(String princNodenum) {
		this.princNodenum = princNodenum;
	}

	public String getOldatA() {
		return oldatA;
	}

	public void setOldatA(String oldat) {
		this.oldatA = oldat;
	}

	public boolean isFrom() {
		return isFrom;
	}

	public void setFrom(boolean isFrom) {
		this.isFrom = isFrom;
	}

	public boolean isTo() {
		return isTo;
	}

	public void setTo(boolean isTo) {
		this.isTo = isTo;
	}

	public String getOldatB() {
		return oldatB;
	}

	public void setOldatB(String oldatB) {
		this.oldatB = oldatB;
	}

	public String getFirstChildid() {
		return firstChildid;
	}

	public void setFirstChildid(String firstChildid) {
		this.firstChildid = firstChildid;
	}

	public boolean isRef() {
		return isRef;
	}

	public void setRef(boolean isRef) {
		this.isRef = isRef;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

}
