
public class SimplexBnBSolver extends BranchNBondSolver {
	
	public SimplexBnBSolver(int capacity, int numItems) {
		super(capacity, numItems);
		// TODO Auto-generated constructor stub
	}
	
	public BnBNode chooseNextNode(){
		BnBNode temp;
		float[]x;
		int cnt = 0;
		
		temp = super.chooseNextNode();
		x = temp.simplexSoln.primal();
//		System.out.print(temp.potentialVal +" ");
//		for(int i = 0; i < x.length; i++)
//			System.out.print(x[i]+" ");
//		System.out.print("\n");
		for(int i = 0; i < x.length; i++){
			if (x[i]!=Math.round(x[i])){
				return temp;
			}
		}
		//Finding the path that corresponds to x
		
		for (int i = 0; i<temp.path.length;i++ ){
			if (temp.path[i]== -1){
				temp.path[i]= (short) (x[cnt]);
				if (temp.path[i] == 1){
					temp.val += items.get(i).value;
				}
				cnt++;
			}
		}		
		temp.optimum = 1;
		return temp;
		
	}
	int chooseBranch(BnBNode thisNode){
		int i,cnt;
		float [] x;
		float error;
		int idx;
		
		x = thisNode.simplexSoln.primal();
		error = 1;
		idx = 0;
		//if all solutions are integral values optimum is reached
		for (i = 0; i <x.length;i++){
			if (x[i]!=Math.round(x[i])){
				break;
			}
			else if (i==(x.length-1)){
				return -1;
			}
		}
		
		for (i = 0; i <x.length;i++){
			if (Math.abs(0.5-x[i]) < error){
				error = Math.abs(0.5f-x[i]);
				idx = i;
			}
		}
		//Finding the path idx that corresponds to the idx in x
		cnt = 0;
		for (i = 0; i<thisNode.path.length;i++ ){
			if ((cnt == idx) && (thisNode.path[i] == -1)){
				idx = i;
				break;
			}
			
			if (thisNode.path[i]== -1)
				cnt++;				
		}
		thisNode.reduceMemory();
		return idx;
		
	}

	BnBNode getFirstNode()
	{
		BnBNode firstNode = new BnBNode (items.size());
		firstNode.availableCapacity = maxCapacity;
		firstNode.createMatrix(items);
		firstNode.solveSimplex();
		firstNode.potentialVal = firstNode.simplexSoln.value();
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
		branch[0].createMatrix(items);
		branch[0].solveSimplex();
		branch[0].potentialVal = branch[0].val+branch[0].simplexSoln.value();
		branch[0].deleteMatrix();
		
		//item picked
		branch[1] = new BnBNode(items.size());
		branch[1].availableCapacity = thisNode.availableCapacity - items.get(next_path).weight;
		branch[1].path = thisNode.path.clone();
		branch[1].path[next_path] = 1;
		branch[1].val = thisNode.val+items.get(next_path).value;
		branch[1].createMatrix(items);
		branch[1].solveSimplex();
		branch[1].potentialVal = branch[1].val+branch[1].simplexSoln.value();
		branch[1].deleteMatrix();
		
		nodes.remove(thisNode);
		if (branch[0].availableCapacity>=0){
			nodes.add(branch[0]);
		}
		if (branch[1].availableCapacity>=0){
			nodes.add(branch[1]);
		}
    }
    

}
