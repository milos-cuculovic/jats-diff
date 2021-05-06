package main.semantics_L3.display;

import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.vdom.diffing.Dtree;
import main.diff_L1_L2.exceptions.InputFileException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;

public class NodeDev {
	private String path ; 
	private int nodeNumber ; 
	private Node node ; 
	private String change ; 
	private int profondeur ; 
	private ArrayList<Node> hierarchy = new ArrayList<Node>() ; 
	private ArrayList<Node> hierarchyfacto  ;
	
	private ArrayList<NodeDev> hierarchyDev = new ArrayList<NodeDev>() ;
	private ArrayList<NodeDev> hierarchyfactoDev = new ArrayList<NodeDev>() ;
	private boolean orig = true ; 
	
	
	
	public NodeDev( String path , int nodeNumber) throws InputFileException{
		this.nodeNumber = nodeNumber ; 
		this.setPath(path) ; 
		Dtree tree = new Dtree(path, true, true, true, true, true);
		Dnode dnode = tree.getNode(nodeNumber);
		
		
		this.setNode(dnode.getRefDomNode());
		
		//NodeEval eval = new NodeEval() ; 
		//ArrayList<Node> hierarchy = eval.getHierarchy(nodeNumber, path) ;
		this.setHierarchy(getStaticHierarchy(nodeNumber, path)) ; 
		//this.setHierarchyDev(getStaticHierarchyDev(nodeNumber, path));
		//this.setHierarchy(hierarchy);
		this.setProfondeur(hierarchy.size()) ; 
		
		
		ArrayList<Node> facto = new ArrayList<Node>() ; 
		facto.add(this.getNode()) ; 
		this.setHierarchyfacto(facto);
		
		
	}
	
	private static ArrayList<Node> getStaticHierarchy(int index , String path) throws InputFileException{
		
		ArrayList<Node> listHierarchy = new ArrayList<Node>() ; 
		Dtree tree = new Dtree(path, true, true, true, true, true);
		Dnode dnode = tree.getNode(index);
		Node node = dnode.getRefDomNode();
	
	
		while ( !node.isSameNode(tree.getNode(0).getRefDomNode())) {

				listHierarchy.add(node);
				node = node.getParentNode() ; 
	
		}
		
		return listHierarchy; 
		
		
	}
	
	
	private static ArrayList<NodeDev> getStaticHierarchyDev(int index , String path) throws InputFileException{
		
		ArrayList<Node> listHierarchy = new ArrayList<Node>() ; 
		ArrayList<NodeDev> listHierarchyDev = new ArrayList<NodeDev>() ; 
		Dtree tree = new Dtree(path, true, true, true, true, true);
		Dnode dnode = tree.getNode(index);
		int nodenum = index ; 
		
		Node node = dnode.getRefDomNode();
	
	
		while ( !node.isSameNode(tree.getNode(0).getRefDomNode())) {
				
				
				listHierarchy.add(node);
				listHierarchyDev.add(new NodeDev(path , nodenum )) ; 
				node = node.getParentNode() ; 
				nodenum = dnode.getPosFather() ; 
				dnode = tree.getNode(nodenum) ; 
				
	
		}
		
		return listHierarchyDev; 
		
		
	}
	
	
	public Dnode getDnodeElementDepthFather(int depth) throws InputFileException {
		Dtree tree = new Dtree(path, true, true, true, true, true);
		Dnode dnode = tree.getNode(nodeNumber);
		for(int i =0 ; i< profondeur - depth ; i++) {
			dnode = tree.getNode(dnode.getPosFather()) ;
		}
		return dnode  ; 

	}
	
	public Dnode getDnode( )throws InputFileException {
		Dtree tree = new Dtree(path, true, true, true, true, true);
		Dnode dnode = tree.getNode(nodeNumber) ; 
		return dnode ; 
	}



	public String getPath() {
		return path;
	}



	public void setPath(String path) {
		this.path = path;
	}



	public Node getNode() {
		return node;
	}



	public void setNode(Node node) {
		this.node = node;
	}



	public int getNodeNumber() {
		return nodeNumber;
	}



	public void setNodeNumber(int nodeNumber) {
		this.nodeNumber = nodeNumber;
	}



	public int getProfondeur() {
		return profondeur;
	}



	public void setProfondeur(int profondeur) {
		this.profondeur = profondeur;
	}



	public String getChange() {
		return change;
	}



	public void setChange(String change) {
		this.change = change;
	}



	public ArrayList<Node> getHierarchy() {
		return hierarchy;
	}



	public void setHierarchy(ArrayList<Node> hierarchy) {
		this.hierarchy = hierarchy;
	}



	public ArrayList<Node> getHierarchyfacto() {
		return hierarchyfacto;
	}



	public void setHierarchyfacto(ArrayList<Node> hierarchyfacto) {
		this.hierarchyfacto = hierarchyfacto;
	}

	public static String applyMainInformations(Node node) {
		String info  =""; 
		switch (node.getNodeName()) {
			case "p" : return "" ; 
			case "#text" : return "" ; 
		}
		info  += node.getNodeName(); 
		Element element = (Element) node ; 
		
		if( element.hasAttributes() ) {
			if(element.getAttributes().item(0).getNodeName().contains("id"))
				info  += " { " +element.getAttributes().item(0).getNodeName()  + " : " + element.getAttributes().item(0).getTextContent()   +"}" ;	
			if( node.getChildNodes().item(0).getNodeName().contains("title"))
				info += " { " + element.getChildNodes().item(0).getNodeName() + " : " +  node.getChildNodes().item(0).getTextContent() +"}"; 
			if( element.getAttributes().item(0).getNodeName().contains("type"))
				info += " { " +element.getAttributes().item(0).getNodeName() + " : " + element.getAttributes().item(0).getTextContent() +"}" ; 
			
			
			
		}
//		if ( element.hasAttribute("id"))
//			info += " *id : " + element.getAttribute("id") ; 
		
		
		return info ; 
	}

	public boolean isOrig() {
		return orig;
	}

	public void setOrig(boolean orig) {
		this.orig = orig;
	}
	
	public boolean isEqual(NodeDev node) {
		if( this.isOrig() == node.isOrig())
		return this.getNode().isEqualNode(node.getNode()) ; 
		else {
			if( applyMainInformations(getNode()).equals(applyMainInformations(node.getNode()))) return true ; 
			else return false ; 
		}
	}

	public ArrayList<NodeDev> getHierarchyDev() {
		return hierarchyDev;
	}

	public void setHierarchyDev(ArrayList<NodeDev> hierarchyDev) {
		this.hierarchyDev = hierarchyDev;
	}

	public ArrayList<NodeDev> getHierarchyfactoDev() {
		return hierarchyfactoDev;
	}

	public void setHierarchyfactoDev(ArrayList<NodeDev> hierarchyfactoDev) {
		this.hierarchyfactoDev = hierarchyfactoDev;
	}
	
}
