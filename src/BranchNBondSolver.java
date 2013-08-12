import java.util.ArrayList;

/**
 * 
 */

/**
 * @author vinay
 *
 */
public class BranchNBondSolver {
	int optValue;
	int optVerified = 0;
	short[] itemsPicked;
	int maxCapacity;
	Items items;
	ArrayList <BnBNode> nodes;
	
	BranchNBondSolver(int capacity,int numItems)
	{
		maxCapacity = capacity;
		itemsPicked = new short [numItems];
		nodes = new ArrayList <BnBNode> (0);
	}
	
	int chooseBranch(BnBNode thisNode){
		int i;
		int node;
		
		i = 0;
		node = -1;
		
		while((node < 0 ) && (i<thisNode.path.length))
		{
			if (thisNode.path[i] == -1)
				node = i;
			else
				node = -1;
			i++;
		}
		
		if (node == -1)
			return -1;
		return node;
		
	}

	BnBNode getFirstNode()
	{
		BnBNode firstNode = new BnBNode (items.size());
		firstNode.potentialVal = 0;
		for (Item thisItem: items.itemList )
				firstNode.potentialVal += thisItem.value;
		firstNode.availableCapacity = maxCapacity;
		firstNode.val = 0;
		return firstNode;
	}
	
    void iterateOneStep(BnBNode thisNode, int next_path){
		BnBNode [] branch;
		
		branch = new BnBNode [2];
	
		//item not picked
		branch[0] = new BnBNode(items.size());
		branch[0].availableCapacity = thisNode.availableCapacity;
		branch[0].path = thisNode.path.clone();
		branch[0].path[next_path] = 0;
		branch[0].val = thisNode.val;
		//item picked
		branch[1] = new BnBNode(items.size());
		branch[1].availableCapacity = thisNode.availableCapacity - items.get(next_path).weight;
		branch[1].path = thisNode.path.clone();
		branch[1].path[next_path] = 1;
		branch[1].val = thisNode.val+items.get(next_path).value;
		
		for (int j = 0; j < 2; j++){
			branch[j].potentialVal = 0;
			for (int i = 0; i < branch[j].path.length; i++){
				if (branch[j].path[i] != 0){
					if ((items.get(i).weight <= branch[j].availableCapacity)||(i<=next_path)){
						branch[j].potentialVal += items.get(i).value;
					}
				}
			}
		}
		nodes.remove(thisNode);
		if (branch[0].availableCapacity>=0){
			nodes.add(branch[0]);
		}
		if (branch[1].availableCapacity>=0){
			nodes.add(branch[1]);
		}
    }
    
    BnBNode chooseNextNode(){
    	BnBNode bestNode;
    	bestNode = nodes.get(0);
    	
    	for (BnBNode node : nodes){
    		if (node.potentialVal > bestNode.potentialVal){
    			bestNode = node;
    		}
    	}
    	
    	if (bestNode.potentialVal == bestNode.val ){
    		bestNode.optimum = 1;
    		for (int i = 0; i < bestNode.path.length;i++){
    			if (bestNode.path[i] == -1){
    				bestNode.path[i] = 0;
    			}
    		}
    	}
    	
    	return bestNode;
    }
    
	BnBNode traverseTree(BnBNode thisNode)
	{
		int optimum_reached = 0;
		
		while (optimum_reached == 0){
			int next_path;
			next_path = chooseBranch(thisNode);
//			System.out.println("Path "+thisNode.path+ " Val "+ thisNode.path+ " Potential "+thisNode.potentialVal);
			if (next_path >=0){
				iterateOneStep(thisNode,next_path);
			}
			thisNode = chooseNextNode();
			optimum_reached = thisNode.optimum;
		}
		return thisNode;

	}
	
	void solve(Items itemList)
	{
		BnBNode firstNode;
		BnBNode optNode;
		items = itemList;
		firstNode = getFirstNode();
		nodes.add(firstNode);
		optNode = traverseTree (firstNode);
		optValue = optNode.val;
		optVerified = 0;
		itemsPicked = optNode.path;
	}
}
