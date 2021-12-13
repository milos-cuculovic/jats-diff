package main.semantics_L3;

import java.util.ArrayList;

public class NodeChanged {

	private int nodenumberA;
	private Integer nodenumberB;
	private boolean isA=true;
	private String valueCitable;
	private ArrayList<ChangeObject> changelist;
	private Double jaccard;
	private Double tf;
	private Double topicModel;
	private Integer atB;
	private Integer atA;
	private Double similartext;
	private Double simitextword;
	private String nodetype;
	private String id;
	private String title;
	private String depth;
	private String init;
	private String modified;
	private String finall;
	private String labelRef;
	private String labelObj;
	private String labelSec;

	public Double getTopicModel() {
		return topicModel;
	}

	public void setTopicModel(Double topicModel) {
		this.topicModel = topicModel;
	}

	public String getModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}

	public String getFinall() {
		return finall;
	}

	public void setFinall(String finall) {
		this.finall = finall;
	}

	public void setNodenumberB(Integer nodenumberB) {
		this.nodenumberB = nodenumberB;
	}

	public NodeChanged(int nodenumberA) {
		this.nodenumberA = nodenumberA;
	}

	public boolean hasTitle() {
		return !(title == null);
	}
	public boolean hasCitVal() {
		return !(valueCitable == null);
	}

	public boolean hasNodenumberB() {
		return !(nodenumberB == null);
	}

	public boolean hasDepth() {
		return !(depth == null);
	}

	public boolean hasInit() {
		return !(init == null);
	}


	public boolean hasLabelRef() {
		return !(labelRef == null);
	}
	public boolean hasLabelsec() {
		return !(labelSec == null);
	}
	public boolean hasLabelObj() {
		return !(labelObj == null);
	}
	public boolean hasTopicmodel() {
		return !(topicModel == null);
	}

	public boolean hasid() {
		return !(id == null);
	}

	public boolean hasNodeT() {
		return !(nodetype == null);
	}

	public boolean hasChangelist() {
		return !(changelist == null);
	}

	public boolean hasPourcentage() {
		return (!(similartext == null) || !(simitextword == null) || !(jaccard == null)|| !(tf == null));
	}

	public boolean hasJaccard() {
		return !(jaccard == null);
	}

	public boolean hasTF() {
		return !(tf == null);
	}
	public boolean hasSimilartext() {
		return !(similartext == null);
	}

	public boolean hasAtB() {
		return !(atB == null);
	}

	public boolean hasSimilartextW() {
		return !(simitextword == null);
	}

	public int getNodenumberA() {
		return nodenumberA;
	}

	public void setNodenumberA(int nodenumberA) {
		this.nodenumberA = nodenumberA;
	}

	public ArrayList<ChangeObject> getChangelist() {
		return changelist;
	}

	public void setChangelist(ArrayList<ChangeObject> changelist) {
		this.changelist = changelist;
	}

	public Double getJaccard() {
		return jaccard;
	}

	public Double getTf() {
		return tf;
	}

	public void setTf(Double tf) {
		this.tf = tf;
	}

	public void setJaccard(Double jaccard) {
		this.jaccard = jaccard;
	}

	public Double getSimilartext() {
		return similartext;
	}

	public void setSimilartext(Double similartext) {
		this.similartext = similartext;
	}

	public Double getSimitextword() {
		return simitextword;
	}

	public void setSimitextword(Double simitextword) {
		this.simitextword = simitextword;
	}

	public String getNodetype() {
		return nodetype;
	}

	public void setNodetype(String nodetype) {
		this.nodetype = nodetype;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDepth() {
		return depth;
	}

	public void setDepth(String depth) {
		this.depth = depth;
	}

	public int getNodenumberB() {
		return nodenumberB;
	}

	public void setNodenumberB(int nodenumberB) {
		this.nodenumberB = nodenumberB;
	}



	public String getInit() {
		return init;
	}

	public void setInit(String init) {
		this.init = init;
	}

	public Integer getAtB() {
		return atB;
	}

	public void setAtB(Integer atB) {
		this.atB = atB;
	}

	public String getLabelRef() {
		return labelRef;
	}

	public void setLabelRef(String labelRef) {
		this.labelRef = labelRef;
	}

	public String getValueCitable() {
		return valueCitable;
	}

	public void setValueCitable(String valueCitable) {
		this.valueCitable = valueCitable;
	}

	public String getLabelSec() {
		return labelSec;
	}

	public void setLabelSec(String labelSec) {
		this.labelSec = labelSec;
	}

	public boolean isA() {
		return isA;
	}

	public void setA(boolean isA) {
		this.isA = isA;
	}

	public Integer getAtA() {
		return atA;
	}

	public void setAtA(Integer atA) {
		this.atA = atA;
	}

	public String getLabelObj() {
		return labelObj;
	}

	public void setLabelObj(String labelObj) {
		this.labelObj = labelObj;
	}

}
