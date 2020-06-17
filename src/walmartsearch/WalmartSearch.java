package walmartsearch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

public class WalmartSearch {
	static String html = "";
	static String search = "";
	static String page = "";
	static boolean start = true; 
	static boolean inStock = false;
	static String product = "productPageUrl\":\"";
	static String price = "\"offerPrice\":";
	static String quantity = "\"quantity\":";
	static String delimiter1 = ",\"";
	static String delimiter2 = "\"";
	static ArrayList<String> productLink = new ArrayList<>();
	static ArrayList<String> productPrice = new ArrayList<>();
	static ArrayList<String> productQuantity = new ArrayList<>();
	
	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter search keywords: ");
		search = sc.nextLine();
		// generate url
		generatePage(search);
		// get html page
		getPage(page);
		searchResults();
		iterateProducts();
		System.out.print("Press 'Q' to quit or select a product: ");
		int selection = sc.nextInt();
		// quit (set boolean [start] to false) or check location availablity
		selectProduct(selection - 1);
		
		
//		while(start) {
//			getPage(search);
//			System.out.println();
//		}
		sc.close();
	}
	
	public static void generatePage(String search) {
		if(search.contains(" ")) {
			search = search.replace(" ", "%20");
		}
		page = "https://www.walmart.com/search/?query=" + search;
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
	
	public static void iterateProducts() {
		// READ PRODUCT LINKS

		int itemNumber = 0;
				
		Iterator <String> productIterator = productLink.iterator(); 
		Iterator <String> priceIterator = productPrice.iterator();
		Iterator <String> quantityIterator = productQuantity.iterator();
				
		while(productIterator.hasNext()) {
			itemNumber += 1;	
			System.out.println("(" +  itemNumber + ")");
			// simplify product discription
			System.out.println("Link: " + "https://www.walmart.com/" + productIterator.next());
			System.out.println("Price: " + priceIterator.next());
			System.out.println("Stock: " + quantityIterator.next());
			System.out.println();
		}
	}
	
	public static void selectProduct(int selection) throws Exception {
		String link = "https://www.walmart.com/" + productLink.get(selection); 
		productLink.clear();	// clear product list
		getPage(link);
		System.out.println("\nPrice: " + productPrice.get(selection));
		productPrice.clear();	// clear price list
		productQuantity.clear();	// clear quantity list
		// in stock?
		if(html.contains("Add to cart")) {
			// maybe put the date?
			System.out.println("Product is in stock");
		} else {
			System.out.println("Product is in not in stock");
		}
		
		
		
		
	}


}
