/**
 * 
 */

/**
 * @author vinay
 *
 */
public class BnBNode {
    short [] decisions;
    int val;
    int availableCapacity;
    float potentialVal;
    int optimum;
    float[][] A;
    float[] b;
    float[] c;
    SimplexSolver simplexSoln;
    
    BnBNode(int numItems)
    {
    	decisions = new short [numItems];
    	numItems--;
    	while (numItems >= 0)
    	{
    		decisions[numItems] = -1;
    		numItems--;
    	}
    	val = 0;
    	optimum = 0;

    }
    
    public void deleteMatrix(){
    	A = null;
    	b = null;
    	c = null;
    }
    public void reduceMemory(){
    	deleteMatrix();
    	simplexSoln = null;
    }
    
    public void solveSimplex(){
    	simplexSoln = new SimplexSolver(A,b,c);
    }
       
}
