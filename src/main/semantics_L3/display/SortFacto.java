package main.semantics_L3.display;

import main.diff_L1_L2.vdom.diffing.Dnode;
import main.diff_L1_L2.vdom.diffing.Dtree;
import main.diff_L1_L2.exceptions.InputFileException;
import org.w3c.dom.Node;
import main.semantics_L3.domxml.Protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SortFacto {
	private String pathOriginal ; 
	private String pathModified ; 
	private ArrayList<Dnode> listNode = new ArrayList<Dnode>() ;
	private ArrayList<Integer> listNodeNumbers = new ArrayList<Integer>() ; 
	private ArrayList<NodeDev> listNodeDev = new ArrayList<NodeDev>() ; 
	private HashMap<Integer,ArrayList<ArrayList<String>>> hash = new HashMap<Integer, ArrayList<ArrayList<String>>>() ; 
	private Dtree treeOriginal ; 
	private Dtree treeModified ; 

	public String getPathOriginal() {
		return pathOriginal;
	}
	public void setPathOriginal(String path) {
		this.pathOriginal = path;
	}
	public String getPathModified() {
		return pathModified;
	}
	public void setPathModified(String path) {
		this.pathModified = path;
	}
	public ArrayList<Dnode> getListNode() {
		return listNode;
	}
	public void setListNode(ArrayList<Dnode> listNode) {
		this.listNode = listNode;
	}
	public ArrayList<Integer> getListNodeNumbers() {
		return listNodeNumbers;
	}
	public void setListNodeNumbers(ArrayList<Integer> listNodeNumbers) {
		this.listNodeNumbers = listNodeNumbers;
	}
	public ArrayList<NodeDev> getListNodeDev() {
		return listNodeDev;
	}
	public void setListNodeDev(ArrayList<NodeDev> listNodeDev) {
		this.listNodeDev = listNodeDev;
	}
	

	//constructeur a partir de la hashmap
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SortFacto(HashMap<String,ArrayList<ArrayList<String>>> hash , String pathOriginal , String pathModified) throws InputFileException {
		HashMap<Integer , ArrayList<ArrayList<String>>> hash1 = new HashMap<Integer, ArrayList<ArrayList<String>>>(); 
		// this.hash = hash1 ; 
		 this.pathOriginal = pathOriginal ; 
		 this.pathModified = pathModified ; 
		 int nodeNum ; 
		 try {
			 Dtree treeOriginal = new Dtree(pathOriginal, true, true, true, true, true);
			 this.treeOriginal = treeOriginal ; 
			 Dtree treeModified = new Dtree(pathOriginal, true, true, true, true, true);
			 this.treeModified = treeModified ; 
			 Iterator it = hash.entrySet().iterator();
			 while (it.hasNext()) {
				 
		        Map.Entry pair = (Map.Entry)it.next();
		        if ( pair.getKey().equals("")) continue ; 
		        // partie modif 
		        for (int index = 0 ; index < ((ArrayList<ArrayList<String>>) pair.getValue()).size()-1 ; index++) {
		        	ArrayList<String> change = new ArrayList<String>(((ArrayList<ArrayList<String>>) pair.getValue()).get(index)) ; 
		        	if( !change.get(2).equals("none")&& !change.get(3).equals("none")) { // at et pos 
		        		Dnode dnNewParent = treeOriginal.getNode(Integer.parseInt(change.get(4))) ; 
		        		//Dnode dn = treeOriginal.getNode(Integer.parseInt((String)pair.getKey())) ; 
		        		NodeDev ndev = new NodeDev(pathOriginal,Integer.parseInt(change.get(Protocol.AT)));  
		        		
		        	}
		        }
		        
		        nodeNum = Integer.parseInt((String) pair.getKey()) ; 
		         
				// insert ***
				ArrayList<ArrayList<String>> insert = new ArrayList<ArrayList<String>>((ArrayList<ArrayList<String>>)pair.getValue()) ;
				boolean checkinsert = false ;
				boolean posat =false ;
				boolean notput = false ;
				int indexChangeposAt =-1 ; 
				for (ArrayList<String> arrayList : insert) {
					if(arrayList.get(0).equals("insert")) checkinsert = true ; 
					if(!arrayList.get(2).equals("none") && !arrayList.get(3).equals("none") && !arrayList.get(4).contains("%")) {
						
						posat = true ; 
						indexChangeposAt = insert.indexOf(arrayList) ;
					}
				}
				if(checkinsert) {
					checkinsert = false ; 
					NodeDev ndevtmp = new NodeDev(pathModified , nodeNum) ;
					ndevtmp.setOrig(false);
					Dnode dnode = treeModified.getNode(nodeNum) ;
					this.listNode.add(dnode) ;
					this.listNodeNumbers.add(nodeNum) ;
					this.listNodeDev.add(ndevtmp) ; 
				}else {
					if(posat) {
						posat = false ; 
						ArrayList<ArrayList<String>> tmp = (ArrayList<ArrayList<String>>) pair.getValue() ;
						
						ArrayList<String> posatChange = tmp.get(indexChangeposAt) ; 
						//System.out.println(posatChange +"KKKKKKKKKKKKKKKKKKKKKKKK");
						int nodenumposat = Integer.parseInt(posatChange.get(3)) ; 
						Dnode dnode = treeOriginal.getNode(nodenumposat) ;
						Dnode dnodeTo = treeOriginal.getNode(Integer.parseInt(posatChange.get(4))) ; 
						
						this.listNode.add(dnode) ;
						this.listNodeNumbers.add(nodenumposat) ;
						this.listNodeDev.add(new NodeDev(pathOriginal,nodenumposat));
						notput = true ; 
						//Dnode dnodeposat = treeOriginal.getNode(Integer.parseInt(posatChange.get(4))) ;
						String nodeinfos = NodeDev.applyMainInformations(dnodeTo.refDomNode) ; 
						String ch = posatChange.get(0) ; 
						String type = posatChange.get(1) ; 
						String pos = posatChange.get(2) ; 
						String at = "--" ; 
						
						posatChange.clear(); 
						posatChange.add(ch) ;
						posatChange.add(type) ;
						posatChange.add(pos) ; 
						posatChange.add(at) ; 
						posatChange.add(nodeinfos ) ; 
						tmp.remove(indexChangeposAt) ; 
						tmp.add(indexChangeposAt, posatChange);
						//hash1.remove(nodeNum) ; 
						hash1.put(nodenumposat, tmp) ; 

						//(ArrayList<ArrayList<String>>)pair.getValue().
					}
					else {
					Dnode dnode = treeOriginal.getNode(nodeNum) ;
					this.listNode.add(dnode) ;
					this.listNodeNumbers.add(nodeNum) ;
					this.listNodeDev.add(new NodeDev(pathOriginal,nodeNum));
					}
				}
				if(! notput) {
				hash1.put(nodeNum, (ArrayList<ArrayList<String>>) pair.getValue()) ; 
				notput = false ; 
				}
				
		        //System.out.println(pair.getKey() + " = " + pair.getValue());
		       // it.remove(); // avoids a ConcurrentModificationException
			 	}
			 this.hash = hash1 ; 
		 	} catch (InputFileException e) {
				e.printStackTrace();
		}
		 
		
		 
		 
		 
	}	
	private int minsize(int a , int b ) {
		if(a>b) return b ;
		else return a ; 
	}
	
	public boolean leftBeforRight(NodeDev left , NodeDev right ) throws InputFileException {
		//if(left.getNodeNumber() == 0) return true ; 
		//if(right.getNodeNumber() == 0) return false ; 
//		System.out.println("name ::::" + NodeDev.applyMainInformations(left.getNode()));
//		System.out.println("name ::::" + NodeDev.applyMainInformations(right.getNode()));
		
		
		// meme nodenum pas le meme fichier ( qqun doit prendre la place de l'autre ) 
		if(left.getNodeNumber() == right.getNodeNumber()) {
			return !left.isOrig() ; 
		}
		//
		if(left.isOrig() != right.isOrig()) return left.getNodeNumber()<right.getNodeNumber() ;
		//
			
		int minDepth = minsize(left.getProfondeur() , right.getProfondeur()) ; 
	

//		System.out.println( "minDepth" + left.getProfondeur());
//		System.out.println( "minDepth" + right.getProfondeur());

		Dnode Dleft = left.getDnodeElementDepthFather(minDepth);

//		System.out.println("leftfirst " + (NodeDev.applyMainInformations(Dleft.getRefDomNode())));
		Dnode Dright = right.getDnodeElementDepthFather(minDepth);
//		System.out.println("rightfirs " +(NodeDev.applyMainInformations(Dright.getRefDomNode())));

		//Dtree tree = new Dtree(this.path, true, true, true, true, true);
		int count  = minDepth ; 
		// ********
//		if(Dleft.getRefDomNode().isSameNode(Dright.getRefDomNode() )) {
//			return left.getDnode().getPosLikeChild()< right.getDnode().getPosLikeChild() ; 
//		}
			
		
		
		Dnode tmpLeft = Dleft ; 
		Dnode tmpRight = Dright ; 
		while(!Dleft.getRefDomNode().isSameNode(Dright.getRefDomNode())) {
			tmpLeft = Dleft ; 
			tmpRight = Dright ; 
			Dleft = treeOriginal.getNode(Dleft.getPosFather())  ; 
			Dright = treeOriginal.getNode(Dright.getPosFather()) ;  
			count --  ; 
			//System.out.println("1111111111111111111111111111111");
			if ( count == 1 ) {
				
//				System.out.println(Dleft.getRefDomNode().getNodeName());
//				System.out.println(Dleft.getPosLikeChild());
//				System.out.println(Dright.getRefDomNode().getNodeName());
//				System.out.println(Dright.getPosLikeChild());
//				System.out.println("I M OUT");
//				System.out.println(" left ::: " + NodeDev.applyMainInformations(Dleft.refDomNode));
//				System.out.println(" right ::: " + NodeDev.applyMainInformations(Dright.refDomNode));

				//System.out.println(Dright.posLikeChild);
				return(tmpLeft.posLikeChild<tmpRight.posLikeChild) ;
			}
			//System.out.println("1111111111111111111111111111111");

		}
//		System.out.println(Dleft.getRefDomNode().getNodeName());
//		System.out.println(Dright.getRefDomNode().getNodeName());
//		System.out.println(count);
		
//		System.out.println("**********************");

		if(Dleft.getPosLikeChild() == Dright.getPosLikeChild())
			return ( left.getProfondeur() <= right.getProfondeur()); 
		else return( Dleft.getPosLikeChild() < Dright.getPosLikeChild());
		
		}
	
	
// Algo de tri fusion ( moins de complexit , plus de fiabilit ) : nlogn je crois ... a voir 
	public ArrayList<NodeDev> fusion( ArrayList<NodeDev> t1, ArrayList<NodeDev> t2) throws InputFileException{
		if (t1 == null || t1.isEmpty()) return t2  ; 
		if (t2 == null || t2.isEmpty()) return t1  ; 
		ArrayList<NodeDev> result = new ArrayList<NodeDev>() ; 
		
		if( leftBeforRight(t1.get(0) , t2.get(0) )) {
			result.add(t1.remove(0)) ; 
			result.addAll(fusion(t1,t2)); 
		}
		else {
			result.add(t2.remove(0)) ; 
			result.addAll(fusion(t1,t2)); 
		}
		
		return result ; 
	}
	
	
	public ArrayList<NodeDev> trifusion( ArrayList<NodeDev> list) throws InputFileException{
		 if(list.size() <2) return list ; 
		 ArrayList<NodeDev> list1 = new ArrayList<NodeDev>() ;
		 ArrayList<NodeDev> list2 = new ArrayList<NodeDev>() ;
		 int middle = list.size()/2 ; 
		 for (int i = 0; i < middle; i++) {
			 list1.add(list.get(i)) ; 		
		}
		 for (int i = middle; i < list.size(); i++) {
			 list2.add(list.get(i)) ; 
			 
		}
		 return fusion(trifusion(list1),trifusion(list2)) ; 
	
	}
	public void sortByTreeElements() throws InputFileException{
		ArrayList<Integer> listNodeNumbersSorted = new ArrayList<Integer>() ; 
		ArrayList<Dnode>  listDnodesSorted = new ArrayList<Dnode>() ; 
		this.listNodeDev = trifusion(this.listNodeDev) ; 
		for (NodeDev dev : this.listNodeDev) {
			listNodeNumbersSorted.add(dev.getNodeNumber()) ; 
			listDnodesSorted.add(dev.getDnodeElementDepthFather(dev.getProfondeur()))  ; 
		}
		this.listNodeNumbers = listNodeNumbersSorted ; 
		this.listNode = listDnodesSorted ; 

		}

	public void facto() {
		if( this.listNodeDev.size() != 0) {
		ArrayList<NodeDev> list_dev = new ArrayList<NodeDev>() ; 
		ArrayList<ArrayList<Node>> result = new ArrayList<ArrayList<Node>>() ; 

		
		
		for(int i = 0 ; i < this.listNodeDev.size() ; i++) {
			if(this.listNodeDev.get(i).isOrig())
			list_dev.add(this.listNodeDev.get(i)) ; 
		}
		this.listNodeDev.get(0).setHierarchyfacto(this.listNodeDev.get(0).getHierarchy());
		
		
		
		
		System.out.println("BEGIN FACTO");
		int sizelist = list_dev.size() ; 
		for(int i = 0 ; i < sizelist ; i++) {
			
			
			ArrayList<Node> sublist1 = new ArrayList<Node>(); 
			for(int inverse= list_dev.get(i).getHierarchy().size()-1 ; inverse >=0 ; inverse-- ) {
				//if( list_dev.get(i).isOrig())
				sublist1.add(list_dev.get(i).getHierarchy().get(inverse)) ; 
			}
			
			
			int size1  = sublist1.size() ; 
			for(int j = i+1 ; j< sizelist ; j++ ) {
				
				ArrayList<Node> sublist2 = new ArrayList<Node>(); 
				for(int inverse2= list_dev.get(j).getHierarchy().size()-1 ; inverse2 >=0 ; inverse2-- ) {
					//if( list_dev.get(j).isOrig())
					sublist2.add(list_dev.get(j).getHierarchy().get(inverse2)) ; 
				}
				
				int size2 = sublist2.size() ; 
				
				int profondeur =  minsize(size1 , size2 )-1  ; 
				while(profondeur >= 0 && !sublist1.get(profondeur).isEqualNode(sublist2.get(profondeur)) ){
					
					profondeur -- ; 
				}
				//System.out.println(profondeur);
				for(int cut = 0 ; cut <= profondeur ; cut++ ) {
					//System.out.println("je fais ma facto pour .....////////////////////////////////////");
					System.out.println(sublist2.remove(0));// exemple d hierarchie ( {document , front , article-title ... ) 
					}
				ArrayList<Node> inverse_sublist2 = new ArrayList<Node>(); 
				for(int reinverse= sublist2.size()-1 ; reinverse >=0 ; reinverse-- ) {
					inverse_sublist2.add(sublist2.get(reinverse)) ; 
				}
				if( this.listNodeDev.get(j).isOrig() )

				this.listNodeDev.get(j).setHierarchyfacto(inverse_sublist2); // on actualise l'hierarchie factoris� dans le devnode en question

				
			}
			
			result.add(sublist1)  ; 
 		}

		
		System.out.println("END FACTO");
		}
	}
	

	
	private String printPrc(String tab , ArrayList<String> change) { 
		String result = ""; 
		int ws = 0 ; 
		// oublie pas de mettre des constante ;) 
		String wsLeven ="" ; 
		if(!change.get(Protocol.JACCARD).equals("none")) {

			for (ws = 0 ; ws< 13 ; ws++) {
				wsLeven +=" " ; 
				if(ws == (13 - change.get(Protocol.JACCARD).length())/2 -1 ) {
					wsLeven += change.get(Protocol.JACCARD) ; 
					ws += change.get(Protocol.JACCARD).length() ; 
				}
			}
		}
		ws = 0 ; 
		String wsSim = "" ; 
		if(!change.get(Protocol.SIMILARTEXT).equals("none")) {

			for (ws = 0 ; ws< 13 ; ws++) {
				wsSim +=" " ; 
				if(ws == (13 - change.get(Protocol.SIMILARTEXT).length())/2-1  ) {
					wsSim += change.get(Protocol.SIMILARTEXT) ; 
					ws += change.get(Protocol.SIMILARTEXT).length() ; 
				}
			}
		}
		ws = 0 ; 
		String wsSimText ="" ; 
		if(!change.get(Protocol.SIMILARTEXT_WORD).equals("none")) {

			for (ws = 0 ; ws< 18 ; ws++) {
				wsSimText +=" " ; 
				if(ws == (18 - change.get(Protocol.SIMILARTEXT_WORD).length())/2 -1 ) {
					wsSimText += change.get(Protocol.SIMILARTEXT_WORD) ; 
					ws += change.get(Protocol.SIMILARTEXT_WORD).length() ; 
				}
			}
		}
		
		if(change.get(0).equals("text-update")) {
		if(!change.get(Protocol.ORIGINAL).equals("none")) 
		result +=  tab + "ORIGINAL : " + change.get(Protocol.ORIGINAL)+"\n" ;
		if(!change.get(Protocol.MODIFIED).equals("none")) 
		result +=  tab + "MODIFIED : " + change.get(Protocol.MODIFIED)+"\n" ;
		}
		if(!change.get(Protocol.JACCARD).equals("none")) 
		
		{
		result +=  tab + "+----------+-------------+-------------+------------------+" + "\n" ; 						  
		//result +=  tab + "|  SCORES  | LEVENSTEIN |   JACCARD   | SIMILARTEXT-WORD |" + "\n" ; 
		result +=  tab + "|  SCORES  |   JACCARD   | SIMILARTEXT | SIMILARTEXT-WORD |" + "\n" ; 
		result +=  tab + "+----------+-------------+-------------+------------------+" + "\n" ; 	
//		result +=  tab + "|  VALUES  |"+ "   " + changes.get(i).get(Protocol.JACCARD) + "   |    "
//					   + changes.get(i).get(Protocol.SIMILARTEXT)  + "    |       " 
//					   + changes.get(i).get(Protocol.SIMILARTEXT) + "     |\n" ;
		
		result +=  tab + "|  VALUES  |" + wsLeven + "|" + wsSim +"|" + wsSimText+ "|\n";
				  
		
		result +=  tab + "+----------+-------------+-------------+------------------+" ;
		}
		return result ; 
	}
	private String printChange(String tab , ArrayList<String> change) {
		String result = ""; 
		result += tab + "Change: " + change.get(Protocol.CHANGEMNT)+"\n" ;
		if(!change.get(Protocol.FROMTO).equals("none")) {
			result += tab + "Type  : "  + change.get(Protocol.FROMTO)+"\n" ;
		}
		if(!change.get(Protocol.POS).equals("none") && !change.get(Protocol.AT).equals("none")) {
			result += tab + "At position : " + change.get(Protocol.POS) + "\n" ; 
			result += tab + change.get(4) + "\n" ; 
			
		}
		return result ; 
		
	}
	private String printInformations(String tab , NodeDev nodeDev , int lenght ) {
		String space ="" ; 
		for (int s = 0 ; s < lenght ; s++) {
			space +=" "; 
		}
		String result ="    <===  " ; 
		
		
		ArrayList<ArrayList<String>> changes = this.hash.get((Integer)nodeDev.getNodeNumber()) ; 
		if(changes == null)  return result ; 
		result += "---------------------------------------------------------------------------\n" ; 
		tab+= space + "            " ; 
		int i ; 
		
		if(changes.size() == 1) {
			if(changes.get(0).get(4).contains("%")) {
				result += printPrc(tab , changes.get(0)) ; 
				return result ; 
			}
			else {
				result += printChange(tab , changes.get(0)) ; 
				
			}
		}
		else {
			for(i =0  ; i< changes.size()-1; i++) {			
				result += printChange(tab , changes.get(i)) ; 
			}
			if(changes.get(i).get(4).contains("%")) {
				result += printPrc(tab , changes.get(i)) ; 
			}else {
				result += printChange(tab , changes.get(i)) ; 
			}
		
		}
		return result ; 
	}
		
		
	public void printTree() {
		System.out.println("************************* TREE *************************");
		String information ="" ; 
		for (NodeDev nodeDev : this.listNodeDev) {
			
			ArrayList<Node> hierFacto = nodeDev.getHierarchyfacto(); 
			String tab =""; 
			for(int i =0  ; i< nodeDev.getProfondeur()-hierFacto.size();i++) {
				
				tab += "   " ; 
			}
			int indexNull = 0  ; 
			if( hierFacto.size()!=0) {
			while (NodeDev.applyMainInformations(hierFacto.get(indexNull)).equals("") && indexNull< hierFacto.size()-1) {
				indexNull ++ ; 
					
			}
			}
			
			//System.out.println(indexNull);
			
			for(int j = hierFacto.size()-1 ; j>indexNull ; j--) {
				information = NodeDev.applyMainInformations(hierFacto.get(j)) ; 
				if( !information.equals("")) {
				System.out.println(tab +   information);
				tab+= "   " ; 
				}
			}
			//System.out.println(tab + NodeDev.applyMainInformations(hierFacto.get(indexNull)) + "    <===   " + hash.get((Integer)nodeDev.getNodeNumber()));
			if ( hierFacto.size()!=0)
			System.out.println(tab + NodeDev.applyMainInformations(hierFacto.get(indexNull)) + printInformations(tab, nodeDev , NodeDev.applyMainInformations(hierFacto.get(indexNull)).length()));
			else {
				System.out.println(tab + NodeDev.applyMainInformations(nodeDev.getNode()) + printInformations(tab, nodeDev, 0));
			}
			indexNull = 0 ; 
		
		}

		 System.out.println("************************* END *************************");
		
	}
	public HashMap<Integer,ArrayList<ArrayList<String>>> getHash() {
		return hash;
	}
	public void setHash(HashMap<Integer,ArrayList<ArrayList<String>>> hash) {
		this.hash = hash;
	}
	public Dtree getTreeOriginal() {
		return treeOriginal;
	}
	public void setTreeOriginal(Dtree treeOriginal) {
		this.treeOriginal = treeOriginal;
	}
	public Dtree getTreeModified() {
		return treeModified;
	}
	public void setTreeModified(Dtree treeModified) {
		this.treeModified = treeModified;
	}
	
}
