package walmartsearch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.awt.Desktop;
import java.net.URI;

public class WalmartSearch {
	static String html = "";
	static String search = "";
	static String page = "";
	static boolean start = true; 
	static boolean inStock = false;
	static int selection = 0;
	static String userOption = "";
	static String product = "productPageUrl\":\"";
	static String price = "\"offerPrice\":";
	static String quantity = "\"quantity\":";
	static String delimiter1 = ",\"";
	static String delimiter2 = "\"";
	static ArrayList<String> productLink = new ArrayList<>();
	static ArrayList<String> productPrice = new ArrayList<>();
	static ArrayList<String> productQuantity = new ArrayList<>();
	static ArrayList<String> productNames = new ArrayList<>();
	
	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in);
		boolean restart = true;
		programTitle();
		while(start) {
			System.out.print("Enter search keywords: ");
			search = sc.nextLine();
			generatePage(search);	// generate URL
			getPage(page);			// get html page
			searchResults();		// get search results
			productNames();			// format products names
			iterateProducts();		// print products
			options(sc);			// select option
			if(userOption.matches("Q")) {
				break;
			} else {
				selectProduct(sc, selection);	// select product 
				restart = true;
			}
			/* Restart selection loop if "S" or "Q" is not selected */
			while(restart) {
				System.out.println("Type 'S' for a new search or 'Q' to quit");
				userOption = sc.next();
				userOption = userOption.toUpperCase();
				if(userOption.matches("S")) {
					clearArrays();
					sc.nextLine();	// clear scanner
					restart = false;
					start = true;
				} else if(userOption.matches("Q")) {
					restart = false;
					start = false;
				} else {
					System.out.println("Incorrect selection");
					restart = true;
				}
			}
			
		}
		System.out.println("\nGoodbye");
		sc.close();
	}
	
	public static void programTitle() {
		System.out.println("*--------------------------------------*");
		System.out.println("|                                      |");
		System.out.println("|       Walmart Inventory Search       |");
		System.out.println("|                                      |");
		System.out.println("*--------------------------------------*");
	}
	
	public static void generatePage(String search) {
		if(search.contains(" ")) {
			search = search.replace(" ", "%20");
		}
		page = "https://www.walmart.com/search/?query=" + search;
		search = "";	// clear search
	}
	
	public static void getPage(String page) throws Exception {
		URL url = new URL(page);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String line = "";
		while((line = reader.readLine()) != null) {
			html += line;
		}
		reader.close();
	}
	
	public static void searchResults() {
		String [] tokens1 = html.split(product);
		String [] tokens2 = html.split(price);
		String [] tokens3 = html.split(quantity);
		
		html = "";
		
		for(int i = 1; i < tokens2.length; i++) {
			String [] tokens4 = tokens1[i].split(delimiter2);
			productLink.add(tokens4[0]);
			String [] tokens5 = tokens2[i].split(delimiter1);
			productPrice.add(tokens5[0]);
			String [] tokens6 = tokens3[i].split(delimiter1);
			productQuantity.add(tokens6[0]);
		}
		
	}
	
	public static void productNames() {
		Iterator <String> productIterator = productLink.iterator(); 
				
		while(productIterator.hasNext()) {
			// simplify product description
			String productName = productIterator.next().replace("/ip/","");
			String t1 [] = productName.split("/");
			productName = t1[0];
			productName = productName.replace("-", " ");
			productNames.add(productName);
		}
	}
	
	public static void iterateProducts() {
		// READ PRODUCT LINKS
		int itemNumber = 0;
				
		Iterator <String> productIterator = productNames.iterator(); 
		Iterator <String> priceIterator = productPrice.iterator();
		Iterator <String> quantityIterator = productQuantity.iterator();
				
		while(productIterator.hasNext()) {
			itemNumber += 1;	
			System.out.println("(" +  itemNumber + ")");
			// simplify product description
			System.out.println("Product: " + productIterator.next());
			System.out.println("Price: " + priceIterator.next());
			System.out.println("Stock: " + quantityIterator.next());
			System.out.println();
		}
	}
	
	public static void selectProduct(Scanner sc, int selection) throws Exception {
		String link = "https://www.walmart.com/" + productLink.get(selection); 
		productLink.clear();	// clear product list
		getPage(link);
		System.out.println("\nProduct: " + productNames.get(selection));
		System.out.println("Price: " + productPrice.get(selection));
		productPrice.clear();	// clear price list
		productQuantity.clear();	// clear quantity list
		// in stock?
		if(html.contains("Add to cart")) {
			// maybe put the date?
			System.out.println("*** Product is in stock ***\n");
		} else {
			System.out.println("*** Product is in not in stock ***\n");
		}
		viewPage(sc, link);
	}
	
	public static void viewPage(Scanner sc, String link) throws Exception {
		System.out.println("Do you want to view the product in your default web browser? Yes or No?");
		String browserSelection = sc.next();
		browserSelection = browserSelection.toUpperCase();
		if(browserSelection.matches("YES") || browserSelection.matches("Y")) {
			URI uri = new URI(link);
			Desktop desktop = Desktop.getDesktop();
			desktop.browse(uri);
		} 
	}
	
	public static void options(Scanner sc) {
		boolean restart = true;
		int totalProducts = productNames.size() - 1;
		System.out.print("---------------------------------------------------------------------"
				+ "--------------------------------------------\n");
		/* Restart selection loop if "Q" or a number is not selected */
		while(restart) {
			try {
				System.out.print("Press 'Q' to quit or select a product: ");
				userOption = sc.next();
				userOption = userOption.toUpperCase();
				if(userOption.matches("Q")) {
					// Quit
					return;
				} else {
					// it's a number or another letter
					selection = Integer.parseInt(userOption);
					selection = selection - 1;
					if(selection > 0 && selection <= totalProducts) {
						restart = false;	
					} else {
						System.out.println("Invalid selection");
						restart = true;
					}
				}
				sc.nextLine();
			} catch(NumberFormatException ex) {
				System.out.println("Invalid selection. Please enter a number.");
				restart = true;
			}
		}
		
	}
	
	public static void clearArrays() {
		productPrice.clear();
		productNames.clear();
	}
}
