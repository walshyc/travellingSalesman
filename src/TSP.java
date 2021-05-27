import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.ActionListener;
import java.net.URL;
import java.awt.event.ActionEvent;

public class TSP {

	// initialise some global variables
	private JFrame frmApachePizzaDelivery;
	private static JTextArea textArea;
	private static JTextArea solutionText;
	private static JButton computeButton;
	public static double shortestDistance = Integer.MAX_VALUE;
	static ArrayList<Address> rows = new ArrayList<Address>();
	public static ArrayList<Address> visitedGlobalAddresses = new ArrayList<Address>();
	public static double globalDelay = Integer.MAX_VALUE;

	// main application driver
	public static void main(String[] args) {

		// creates a new window and displays it on screen
		TSP window = new TSP();
		window.frmApachePizzaDelivery.setVisible(true);

	}

	// method to get the distance between two GPS locations
	// using the haversine formula
	static double getDistance(double latOne, double longOne, double latTwo, double longTwo) {
		int radius = 6371; // radius of the earth

		// get the lat and long difference between the 2 locations
		double latDistance = Math.toRadians(latTwo - latOne);
		double longDistance = Math.toRadians(longTwo - longOne);

		// apply the formula
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(latOne))
				* Math.cos(Math.toRadians(latTwo)) * Math.sin(longDistance / 2) * Math.sin(longDistance / 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = (radius * c);

		// return the distance
		return distance * 10;
	}

	// method to calculate a route for the pizza delivery
	// takes the arraylist of rows as a parameter
	static void calculate(ArrayList<Address> rows) {

		// create the starting address at Apache Pizza
		// and add it to the start of the arraylist
		Address apache = new Address(0, "Apache Pizza Maynooth", 0, 53.3811621, -6.5930347);
		rows.add(0, apache);

		int delay = 0; // to track the delay for the calculation

		// create an empty distance matrix
		double[][] matrix = new double[rows.size()][rows.size()];

		// loop through the rows and create a distance matrix
		for (int i = 0; i < matrix.length; i++) {
			double rowLat = rows.get(i).getLat();
			double rowLong = rows.get(i).getLon();

			for (int j = 0; j < matrix[i].length; j++) {

				double compareLat = rows.get(j).getLat();
				double compareLong = rows.get(j).getLon();

				// use the haversine formula to get the distance between the two coordinates
				double distance = getDistance(compareLat, compareLong, rowLat, rowLong);

				// if the lat and longs are the same
				// the loop is at the same index as it is checking
				if (compareLat == rowLat && compareLong == rowLong) {
					matrix[i][j] = -1; // sets the distance to -1
				} else {
					// add a random number between 1 & 10 to get a better solution
					Random rn = new Random();
					int random = rn.nextInt(10);
					matrix[i][j] = (distance + random);
				}
			}

		}

		// creates an empty arraylist to store the visited addresses.
		ArrayList<Address> visitedAddresses = new ArrayList<Address>();
		// adds apache pizza to the visited addresses
		visitedAddresses.add(rows.get(0));

		int row = 0; // starts on the first row
		// loop that runs until all addresses have been added to the visited addresses
		// array list
		while (visitedAddresses.size() < matrix[0].length) {
			// initial variables
			double shortest = 100000;
			int shortestIndex = 0;

			// loop that goes through each distance
			// picks the shortest unvisited address and stores a reference
			for (int j = 0; j < matrix[row].length; j++) {
				if (matrix[row][j] < shortest && !visitedAddresses.contains(rows.get(j)) && matrix[row][j] != -1) {
					shortest = matrix[row][j];
					shortestIndex = j;
				}

			}

			Address chosenRow = rows.get(shortestIndex); // the chosen address
			// checks if the chose address is already visited
			if (!visitedAddresses.contains(chosenRow)) {
				// if the delay for that address is greater than 30mins
				// updates the methods delay total
				if (chosenRow.getDelayedMinutes() > 30) {
					delay += (chosenRow.getDelayedMinutes() - 30) / 10;
				}
				// adds the chose row to the visited address array list
				visitedAddresses.add(chosenRow);
				// loops through the rows array lists and adds the distance travelled
				// to each addresses count
				for (Address add : rows) {
					add.setDelayedMinutes(shortest);
				}
			}
			// sets the new row to search for the next iteration
			row = shortestIndex;

		}
		// removes apache pizza from the visited addresses when finished
		visitedAddresses.remove(0);

		// checks if the methods delay count is less than or equal to the global delay
		// tracker
		if (delay <= globalDelay) {
			// if so updates the global best route and best delay
			visitedGlobalAddresses = visitedAddresses;
			globalDelay = delay;
		}
		// removes apache pizza from the rows
		rows.remove(0);
		// resets each addresses' starting delay for the next iteration
		for (Address add : rows) {
			add.resetDelayedMinutes(add.getStartingMinutes());
		}

	}

	// new window class
	public TSP() {
		initialize();
	}

	private void initialize() {
		// setup new jframe
		frmApachePizzaDelivery = new JFrame();
		frmApachePizzaDelivery.setTitle("Apache Pizza Delivery");
		frmApachePizzaDelivery.setBounds(100, 100, 910, 747);
		frmApachePizzaDelivery.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmApachePizzaDelivery.getContentPane().setLayout(null);

		// JLabel for the enter route label
		JLabel lblNewLabel = new JLabel("Enter Route");
		lblNewLabel.setFont(new Font("Arial", Font.BOLD, 18));
		lblNewLabel.setBounds(10, 2, 235, 32);
		frmApachePizzaDelivery.getContentPane().add(lblNewLabel);

		// JButton for the compute button
		computeButton = new JButton("Compute");
		computeButton.setForeground(SystemColor.info);
		computeButton.setBackground(SystemColor.textHighlight);
		computeButton.setFont(new Font("Arial", Font.BOLD, 18));
		computeButton.setBounds(713, 31, 150, 40);
		frmApachePizzaDelivery.getContentPane().add(computeButton);
		
		// reference to the map
		String mapImage = "/map.png";
		URL url = getClass().getResource(mapImage);

		ImageIcon imageIcon = new ImageIcon(url); // load image as image icon
		Image image = imageIcon.getImage(); // transform it
		Image newimg = image.getScaledInstance(649, 525, java.awt.Image.SCALE_SMOOTH); // scale image to the correct size
		imageIcon = new ImageIcon(newimg); // transform it back
		
		// JLabel for the map
		JLabel map = new JLabel(imageIcon); 
		map.setHorizontalAlignment(SwingConstants.LEFT);
		map.setVerticalAlignment(SwingConstants.TOP);
		map.setBounds(10, 82, 665, 539);
		frmApachePizzaDelivery.getContentPane().add(map);

		// JLabel for the suggested route
		JLabel lblSuggestedRoute = new JLabel("Suggested Route");
		lblSuggestedRoute.setFont(new Font("Arial", Font.BOLD, 18));
		lblSuggestedRoute.setBounds(10, 626, 235, 32);
		frmApachePizzaDelivery.getContentPane().add(lblSuggestedRoute);

		// Scroll pane and text area to get the list of rows
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 31, 654, 40);
		frmApachePizzaDelivery.getContentPane().add(scrollPane);
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

		// Scroll pane and text area to  display the suggested route
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 661, 848, 40);
		frmApachePizzaDelivery.getContentPane().add(scrollPane_1);
		solutionText = new JTextArea();
		scrollPane_1.setViewportView(solutionText);
		
		// action listener for when the compute button is pressed
		computeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				long startTime = System.nanoTime();
				//solutionText.setText("loading...");
				// gets the inputed text as a string
				String input = textArea.getText();
				// splits the input on each new line
				String addressLine[] = input.split("\\r?\\n");
				//loops through each line
				for (int i = 0; i < addressLine.length; i++) {
					String details[] = addressLine[i].trim().split(","); // splits based on the ,
					// create an Address using the split information
					Address address = new Address(Integer.parseInt(details[0]), details[1],
							Integer.parseInt(details[2]), Double.parseDouble(details[3]),
							Double.parseDouble(details[4]));
					// add the address to the row array list
					rows.add(address);
				}
				// variables to keep track of the time
				long endTime = 0;
				long timeElapsed = 0;
				// loop that runs for 9 seconds to find the best route
				while ((timeElapsed / 1000000) < 9000) {
					calculate(rows); // calculate the best route
					endTime = System.nanoTime();
					timeElapsed = endTime - startTime; // update the elapsed time
				}

				String output = ""; // initialise the output 
				
				// loop through the best route found and
				// add the order number to the output in the requested format
				for (int i = 0; i < visitedGlobalAddresses.size(); i++) {
					if (i == visitedGlobalAddresses.size() - 1) {
						output += visitedGlobalAddresses.get(i).getOrderNumber();
					} else {
						output += visitedGlobalAddresses.get(i).getOrderNumber() + ",";
					}
				}
				// print the route in order numbers to the console
				System.out.println(output);

				// set the text for the suggested route to the UI
				solutionText.setText(output);

				// reset the global variables so that a new route can be calculated
				visitedGlobalAddresses.clear();
				rows.clear();
				shortestDistance = Double.MAX_VALUE;
				globalDelay = Double.MAX_VALUE;

			}

		});

	}
}

// Address class to store each address
class Address {
	int orderNumber; // order number
	String details; // address line
	double startingMinutes; // initial waiting period
	double delayMinutes; // moving waiting period
	double lat; // latitude
	double lon; // longitude

	// constructor
	public Address(int num, String details, double min, double lat, double lon) {
		this.orderNumber = num;
		this.details = details;
		this.lat = lat;
		this.lon = lon;
		this.startingMinutes = min;
		this.delayMinutes = min;
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public double getStartingMinutes() {
		return startingMinutes;
	}

	public void setStartingMinutes(int minutes) {
		this.startingMinutes += minutes;
	}

	public double getDelayedMinutes() {
		return delayMinutes;
	}

	public void setDelayedMinutes(double shortest) {
		this.delayMinutes += shortest;
	}

	public void resetDelayedMinutes(double d) {
		this.delayMinutes = d;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	@Override
	public String toString() {
		return getLat() + ", " + getLon() + ", " + getOrderNumber() + ", " + getDetails() + ", delay: "
				+ getDelayedMinutes();
	}
}
