import java.util.ArrayList;


public class WarehouseNode extends BnBNode {
	
	WarehouseNode(ArrayList<warehouse> warehouses, ArrayList<warehouse> customers){
		super(warehouses.size()+customers.size());
	}
	
    public void createMatrix(ArrayList<warehouse> warehouses, ArrayList<warehouse> customers){
    	int count = 0;
    	for(int i = 0; i<decisions.length;i++){
    		if ((decisions[i] == -1) && (itemList.get(i).weight <= availableCapacity)){
    	  			count++;
    		}
    	}
    	b = new float [count+1];
    	A = new float [count+1][count];
    	c = new float [count];
    	
    	count = 0;
    	b[0] = availableCapacity;
    	for(int i = 0; i<path.length;i++){
    		if ((path[i] == -1) && (itemList.get(i).weight <= availableCapacity)){
    			A[0][count] = itemList.get(i).weight;
    			A[count+1][count] = 1;
    			b[count+1] = 1;
    			c[count] = -1*itemList.get(i).value;
    			count++;
    		}
    		else if ((path[i] == -1) && (itemList.get(i).weight > availableCapacity)){
    			path[i] = 0;
    		}
    	}
    }
}
