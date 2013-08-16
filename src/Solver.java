import java.io.*;
import java.util.List;
import java.util.ArrayList;

/**
 * The class <code>Solver</code> is an implementation of a greedy algorithm to solve the knapsack problem.
 *
 */
public class Solver {
    
    /**
     * The main class
     */
    public static void main(String[] args) {
        try {
            solve(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Read the instance, solve it, and print the solution in the standard output
     */
    public static void solve(String[] args) throws IOException {
        String fileName = null;
        SimplexBnBSolver warehouseSolver;
        ArrayList<warehouse> warehouses;
        ArrayList<customer> customers;
        
        // get the temp file name
        //System.out.print(args[0]+"\n");
        warehouses = new ArrayList <warehouse>();
        customers = new ArrayList <customer>();
        for(String arg : args){
            if(arg.startsWith("-file=")){
                fileName = arg.substring(6);
            } 
        }
        if(fileName == null)
            return;
        
        // read the lines out of the file
        List<String> lines = new ArrayList<String>();

        BufferedReader input =  new BufferedReader(new FileReader(fileName));
        try {
            String line = null;
            while (( line = input.readLine()) != null){
                lines.add(line);
            }
        }
        finally {
            input.close();
        }
        
        // parse the data in the file
        String[] firstLine = lines.get(0).split("\\s+");
        int num_warehouses = Integer.parseInt(firstLine[0]);
        int num_customers = Integer.parseInt(firstLine[1]);
        
        for(int i=1; i < num_warehouses+1; i++){
          float warehouse_cost, warehouse_capacity;
          warehouse temp_wh;
          temp_wh = null;
          String line = lines.get(i);
          String[] parts = line.split("\\s+");

          warehouse_cost = Float.parseFloat(parts[0]);
          warehouse_capacity = Float.parseFloat(parts[1]);
          temp_wh = new warehouse(warehouse_cost, warehouse_capacity);
          warehouses.add(temp_wh);
        }

        for(int i=1; i < num_customers+1; i++){
            customer temp_cm;
            
            temp_cm = null;
            String line = lines.get(i);
            temp_cm = new customer(num_warehouses);
            temp_cm.demand = Double.parseDouble(line);
            line = lines.get(i);
            String[] parts = line.split("\\s+");
            for (int j=0; j < num_warehouses; j++){
                 temp_cm.cost[j] = Double.parseDouble(parts[j]);              	
            }
           
            customers.add(temp_cm);
          }
        
        warehouseSolver = new SimplexBnBSolver (num_warehouses);
        warehouseSolver.solve(warehouses,customers);/*
		System.out.println(knapSackSolver.optValue+" "+knapSackSolver.optVerified);
		for (int i = 0; i < items; i++)
			System.out.print(knapSackSolver.itemsPicked[i]+" ");
			*/	
    }
}