package main.semantics_L3;

public class ObjectChange<ELEMENT> {
    private ELEMENT element;
    public void add(ELEMENT element){
        this.element=element;
    }
    public void setJaccard(Double jaccard){
        if (element instanceof NodeChanged){
            ((NodeChanged) element).setJaccard(jaccard);
        }
        else if (element instanceof TableChange) {
            ((TableChange) element).setJaccard(jaccard);
        }
    }
    public void setSimtext(Double simtext){
        if (element instanceof NodeChanged){
            ((NodeChanged) element).setSimilartext(simtext);
        }
        else if (element instanceof TableChange) {
            ((TableChange) element).setSimilartext(simtext);
        }
    }
    public void setSimtextWord(Double simtextword){
        if (element instanceof NodeChanged){
            ((NodeChanged) element).setSimitextword(simtextword);
        }
        else if (element instanceof TableChange) {
            ((TableChange) element).setSimitextword(simtextword);
        }
    }
    public void setTopicModel(Double topicModel){
        if (element instanceof NodeChanged){
            ((NodeChanged) element).setTopicModel(topicModel);
        }
        else if (element instanceof TableChange) {
            ((TableChange) element).setTopicModel(topicModel);
        }
    }
    public void setTF(Double topicModel){
        if (element instanceof NodeChanged){
            ((NodeChanged) element).setTf(topicModel);
        }
        else if (element instanceof TableChange) {
            ((TableChange) element).setTf(topicModel);
        }
    }
}

