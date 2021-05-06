package main.semantics_L3.domxml;

public class TableChange {
	private String name;
	private String jaccard;
	private String similartext;
	private String simitextword;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getJaccard() {
		return jaccard;
	}
	public void setJaccard(String jaccard) {
		this.jaccard = jaccard;
	}

	public String getSimilartext() {
		return similartext;
	}
	public void setSimilartext(String similartext) {
		this.similartext = similartext;
	}
	public String getSimitextword() {
		return simitextword;
	}
	@Override
	public String toString() {
		String retour="";
		retour+=name;
		if(jaccard!=null) {
			retour+="\n            * " + jaccard;
		}
		if(similartext!=null) {
		
			retour+="\n            * " + similartext;
		}
		if(simitextword!=null) {
			retour+="\n            * " + simitextword;
		}
		return retour;
	}
	public void setSimitextword(String simitextword) {
		this.simitextword = simitextword;
	}
}
