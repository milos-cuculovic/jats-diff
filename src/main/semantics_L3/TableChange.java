package main.semantics_L3;

public class TableChange {
	private String name;
	private Double jaccard;
	private Double similartext;
	private Double simitextword;

	public Double getTf() {
		return tf;
	}

	public void setTf(Double tf) {
		this.tf = tf;
	}

	private Double tf;

	public TableChange() {
	}

	public Double getTopicModel() {
		return topicModel;
	}

	public void setTopicModel(Double topicModel) {
		this.topicModel = topicModel;
	}

	private Double topicModel;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getJaccard() {
		return jaccard;
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
	public void setSimitextword(Double simitextword) {
		this.simitextword = simitextword;
	}

}
