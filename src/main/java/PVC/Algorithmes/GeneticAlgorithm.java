import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GeneticAlgorithm {

	private double pCrossover;
	private double pMutation;
	
	private static int nbCities;
	private int tournamentSize;
	private static int generationSize;
	private int maxGeneration;
	
	private static ArrayList<City> cities = new ArrayList<City>();
	
	private ArrayList<Route> initialPopulation;
	private ArrayList<Route> population;
			
    private ArrayList<Double> averageDistanceOfEachGeneration;
    private ArrayList<Double> bestDistanceOfEachGeneration;

    
	public GeneticAlgorithm(){
		initialPopulation = generateRandomGeneration();
		for (Route r:initialPopulation) {
			population.add(new Route(r));
			}
		maxGeneration = 15;
		generationSize = 30;
		tournamentSize = 3;
		nbCities = getCities().size();
		pCrossover = 0.9;
		pMutation = 0.01;
		
		averageDistanceOfEachGeneration = new ArrayList<Double>();
        bestDistanceOfEachGeneration = new ArrayList<Double>();
		}
	
	public void setPopulation (ArrayList<Route> population) {
       
        initialPopulation = population;
        ArrayList<Route> pop= new ArrayList<Route>();
        for (Route r:initialPopulation) {
			pop.add(new Route(r));
			}
        this.population=pop;
		}
	
	public void setPMutation (double pMutation) {
        this.pMutation = pMutation;
    	}

	public void setPCrossover (double pCrossover) {
        this.pCrossover = pCrossover;
    	}
	
	public void setMaxGeneration (int maxGeneration) {
        this.maxGeneration = maxGeneration;
    	}
	
	public void setTournamentSize (int tournamentSize) {
        this.tournamentSize = tournamentSize;
    	}
	
	public static void setCities(ArrayList<City> cities) {
		GeneticAlgorithm.cities = cities;
	}
	
	public static ArrayList<City> getCities() {
		return cities;
	}
	
	public ArrayList<Double> getAverageDistanceOfEachGeneration() {
        return averageDistanceOfEachGeneration;
    	}
	
	public ArrayList<Double> getBestDistanceOfEachGeneration() {
        return bestDistanceOfEachGeneration;
    	}
	

	public void run () {
        for (int i = 0; i < maxGeneration; i++) {
            population = createNewGeneration();
            averageDistanceOfEachGeneration.add(getAverageDistance(population));
            bestDistanceOfEachGeneration.add(choseTheBest(population,1).get(0).getTotalDistance());
            System.out.println(i);
            }
        }
	
	private ArrayList<Route> createNewGeneration (){
		
		ArrayList<Route> newGen = new ArrayList<Route>();
		ArrayList<Route> populationSelected = new ArrayList<Route>();
		
		for (int i=0;i<generationSize;i++) {
			populationSelected.add(tournamentSelection());
			}
		
		int reste = generationSize%2;
		if (reste == 1) {
			newGen.add(populationSelected.get(populationSelected.size()-1));
			}
		System.out.println("NewGen avant cross et mut : "+populationSelected);
		
		// Crossover
		for (int i=0;i<generationSize;i=i+2){
			if (Math.random()<pCrossover){
				ArrayList<Route> crossover = new ArrayList<Route>(cross(populationSelected.get(i),populationSelected.get(i+1)));
				newGen.add(crossover.get(0));
				newGen.add(crossover.get(1));
				}
			else{
				newGen.add(populationSelected.get(i));
				newGen.add(populationSelected.get(i+1));
				}
			}
		// Mutation
		for (int i=0;i<generationSize;i++) {
			if (Math.random()<pMutation){
				Route routei = new Route(newGen.get(i));
				newGen.set(i,mutate(routei));
				}
			 }
		System.out.println("newGen : "+newGen);
		return selectNewGeneration(population,newGen);
		}
		
	
	
	private ArrayList<Route> selectNewGeneration (ArrayList<Route> pop, ArrayList<Route> newGen){
		
		newGen.addAll(pop);
		System.out.println("newGen+population : "+newGen);
		return choseTheBest(newGen, generationSize);
		}
	
	

	private static ArrayList<Route> generateRandomGeneration(){
		
		ArrayList<Route> firstGeneration = new ArrayList<Route>();
		
		for (int i=0;i<generationSize;i++) {
			
			ArrayList<City> listCities = new ArrayList<City>(getCities());
			ArrayList<City> listCitiesForFirstGeneration = new ArrayList<City>();
			
			Random rand = new Random();
			
			for (int j = 0; j < nbCities; j++) {
				
		        int randomIndex = rand.nextInt(nbCities-j);
		        listCitiesForFirstGeneration.add(listCities.get(randomIndex));
		        listCities.remove(randomIndex);
				}
			Route routeForFirstGeneration = new Route(listCitiesForFirstGeneration);
	
			firstGeneration.add(routeForFirstGeneration);
			}
		System.out.println("generateRandomGeneration : "+firstGeneration);
		return firstGeneration;
		}
	
	private Route mutate(Route route) {
		 
		int nbAleas1 = (int)(Math.random() * nbCities);
		int nbAleas2 = (int)(Math.random() * nbCities);
			
		while (nbAleas1==nbAleas2) {			
			nbAleas2 = (int)(Math.random() * nbCities);
			}
			
		ArrayList<City> cities = new ArrayList<City>();
			
		cities = route.getCities();
			
		Collections.swap(cities, nbAleas1, nbAleas2);
			
		Route routeChild = new Route(cities);
			
		return routeChild;
		}
	
	private ArrayList<Route> cross(Route route1, Route route2) {
		
		Random random = new Random();
	    int breakpoint = random.nextInt(nbCities);
	    
	    ArrayList<Route> children = new ArrayList<Route>();
	    
		ArrayList<City> parent1 = new ArrayList<City>();
		ArrayList<City> parent2 = new ArrayList<City>();
		
		ArrayList<City> child1 = new ArrayList<City>();
		ArrayList<City> child2 = new ArrayList<City>();
 		
		parent1 = route1.getCities();
		parent2 = route2.getCities();
		
		child1 = route1.getCities();
		child2 = route2.getCities();
		
		City newCity;
		
		for (int i=0;i<breakpoint;i++) {
	        
			newCity = parent1.get(i);
			
	        Collections.swap(child1, child1.indexOf(newCity),i);
	        
	        newCity = parent2.get(i);
	        
	        Collections.swap(child2, child2.indexOf(newCity),i);
			}
		children.add(new Route(child1));
		children.add(new Route(child2));
		
		return children;
		}
	
	
	
	// Function to select the best route between several route
	private Route tournamentSelection() {
		
		ArrayList<Route> selected = pickNRandomRoute(this.population, tournamentSize);
		
		return choseTheBest(selected, 1).get(0);
		}
	
	private static ArrayList<Route> choseTheBest(ArrayList<Route> population, int n){
		
		ArrayList<Route> best = new ArrayList<Route>();
		
		ArrayList<Route> copyPopulation = new ArrayList<Route>(population);
		
		ArrayList<Double> totalDistanceForRoute = new ArrayList<Double>();
		
		for (Route route : copyPopulation) {
			totalDistanceForRoute.add(route.getTotalDistance());
			}
		// find maximum in population and put it in a new list, then remove element from population
		for (int i=0;i<n;i++) {
			double min = (Collections.min(totalDistanceForRoute));
			
			int indexMin = totalDistanceForRoute.indexOf(min);
			
			best.add(copyPopulation.get(indexMin));
			
			copyPopulation.remove(indexMin);
			}
			
		return best;
		}
	
	private static double getAverageDistance(ArrayList<Route> population) {
		double sumDistances=0;
		
		for (int i=0;i<generationSize;i++) {
			sumDistances += population.get(i).getTotalDistance();
			}
		return sumDistances/generationSize;
		}
	
	// A helper function to pick n random elements from the population
		// so we could enter them into a tournament
	private static ArrayList<Route> pickNRandomRoute(ArrayList<Route> pop, int n) {
		
		Random r = new Random();    
		
		ArrayList<Route> randomRoute = new ArrayList<Route>();
		
		ArrayList<Integer> listIndex = new ArrayList<Integer>();
 		
	    for (int i = 0; i <  n; ++i) {
	    	
	    	int j = r.nextInt(generationSize);
	    	
	    	while (!listIndex.contains(j)){
	    		
	    		j = r.nextInt(generationSize);
	    		
	    		}
	    	
	    	randomRoute.add(pop.get(r.nextInt(i + 1)));
    		
    		listIndex.add(j);
    		
	    	randomRoute.add(pop.get(j));
	    	// Collections.swap(list, i , r.nextInt(i + 1));
		   	}
	    return randomRoute;
		}
	
	public void reset () {
		for (int i=0;i<generationSize;i++) {
			population.set(i, initialPopulation.get(i));
			}
        averageDistanceOfEachGeneration = new ArrayList<>();
        bestDistanceOfEachGeneration = new ArrayList<>();  
    	}
	
	public void printProperties () {
        System.out.println("-------Genetic Algorithm Properties-------");
        System.out.println("Number of Cities:           	" + nbCities);
        System.out.println("Population Size:    		" + generationSize);
        System.out.println("Max. Generation:		" + maxGeneration);
        System.out.println("Nb. route in 1 tournament :	" + tournamentSize);
        System.out.println("Crossover Rate:     		" + (pCrossover*100) + "%");
        System.out.println("Mutation Rate:      		" + (pMutation*100) + "%");
		}
	
	public void printResults () {

        System.out.println("--------Genetic Algorithm Results---------");
        System.out.println("Average Distance of First Generation:  " +
        		getAverageDistanceOfEachGeneration().get(0));
      
        System.out.println("Average Distance of Last Generation:   " +
        		getAverageDistanceOfEachGeneration().get(maxGeneration-1));
        
        System.out.println("Best Distance of First Generation:     " +
                getBestDistanceOfEachGeneration().get(0));
        
        System.out.println("Best Distance of Last Generation:      " +
        		getBestDistanceOfEachGeneration().get(maxGeneration-1));
        
    }

	
	}
	