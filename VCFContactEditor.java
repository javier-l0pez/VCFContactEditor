import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Scanner;

/**
 * VCF contact parser and editor
 * Will remove duplicate phone numbers within an entry
 * Also will be able to edit name and nickname of contacts
 * 
 * Tested on VCF files:
 * VERSION:3.0
 * PRODID:ez-vcard 0.10.5
 * 
 * @param vcf file to be parsed
 *
 * @author Javier Lopez
 */

public class VCFContactEditor {	
	public static void main(String[] args) {
		
		if(args.length != 1) {
			System.out.println("You must use 1 parameters, like follow:");
			System.out.println("java VCFContactEditor file");
			System.exit(1);
		}
		
		System.out.println("##############################");
		System.out.println("#                            #");
		System.out.println("#         VCF EDITOR         #");
		System.out.println("#                            #");
		System.out.println("##############################\n");
		
		try {
//			BufferedReader br = new BufferedReader(new FileReader("testcontacts"));	//DEBUG
			BufferedReader br = new BufferedReader(new FileReader(args[0]));	//NON DEBUG
			BufferedWriter bw = new BufferedWriter(new FileWriter(args[0] + "_parsed"));	//NON DEBUG
			String line = br.readLine();
			
			String header = "";
			String name = "";
			String nick = "";
			ArrayList<String> phoneList = new ArrayList<>();
			ArrayList<String> phoneLine = new ArrayList<>();
			ArrayList<String> emailList = new ArrayList<>();
			ArrayList<String> emailLine = new ArrayList<>();
			String opt = "";
			String bday = "";
			ArrayList<String> adrLine = new ArrayList<>();
			ArrayList<String> categories = new ArrayList<>();
			String optcase = "x";
			String end = "";
			
			while (line != null) {
//				FIRST 3 LINES BEGIN/VERSION/PRODID
				if (line.equals("BEGIN:VCARD")) {
					
//					#=====HEADER=====#
					header = line + "\n";	//BEGIN
					line = br.readLine();
					header += line + "\n";	//VERSION
					line = br.readLine();
					header += line;			//PRODID
					line = br.readLine();
					
//					#=====NAME=====#
					name = "";
					if (line.startsWith("N:")) {
												
						name = "N:" + nameContact(line, 1) + ";" + nameContact(line, 2);
						if (nameContact(line, 3) != "") {
							name += ";" + nameContact(line, 3);
						}
					} 
					line = br.readLine();
					
//					#=====NICKNAME=====#
					nick = "";
					while (line.startsWith("NICKNAME:")) {
						nick = line;
						line = br.readLine();
					}
					
//					#=====PHONE=====#
					while (line.startsWith("TEL;")) {
//						If phone # is new, then save to the list.
						if (phoneLine.contains("TEL;TYPE=" + telContact(line, 2) + ":" + phoneParser(telContact(line, 3)))) {
							//This means the same number for the same contact, so discard auto.
						} else if (! phoneList.contains(phoneParser(telContact(line, 3)))) {
							phoneList.add(phoneParser(telContact(line, 3)));
							phoneLine.add("TEL;TYPE=" + telContact(line, 2) + ":" + phoneParser(telContact(line, 3)));
						} else {
							System.out.print("The number \"" + phoneParser(telContact(line, 3)) + "\" is already in your contact list. Do you wish to add it again? (y/N): ");
							opt = sc.nextLine();
							if (opt.equalsIgnoreCase("y")) {
								phoneLine.add("TEL;TYPE=" + telContact(line, 2) + ":" + phoneParser(telContact(line, 3)));
								phoneList.add(phoneParser(telContact(line, 3)));
							}
						}
						line = br.readLine();
					}
					
//					#=====EMAIL=====#
					while (line.startsWith("EMAIL;")) {
//						If email is new, then save to the list.
						if (emailLine.contains("EMAIL;TYPE=" + emailContact(line, 2) + ":" + emailContact(line, 3))) {
							//This means the same email for the same contact, so discard auto.
						} else if (! emailList.contains(emailContact(line, 3))) {
							emailList.add(emailContact(line, 3));
							emailLine.add("EMAIL;TYPE=" + emailContact(line, 2) + ":" + emailContact(line, 3));
						} else {
							System.out.print("The email \"" + emailContact(line, 3) + "\" is already in your contact list. Do you want to add it again? (y/N): ");
							opt = sc.nextLine();
							if (opt.equalsIgnoreCase("y")) {
								emailLine.add("EMAIL;TYPE=" + emailContact(line, 2) + ":" + emailContact(line, 3));
							}
						}
						line = br.readLine();
					}
					
//					#=====BDAY=====#
					bday = "";
					while (line.startsWith("BDAY:")) {
						bday = line;
						line = br.readLine();
					}
					
//					#=====ADR=====#
					while (line.startsWith("ADR:")) {
						adrLine.add(line);
						line = br.readLine();
					}
					
//					#=====CAT=====#
					if (line.startsWith("CATEGORIES:")) {
						catContact(line, categories);
						line = br.readLine();
					}
					
//					#=====END=====#
					if (line.equals("END:VCARD")) {
						end = "END:VCARD\n";
					} 
				} 
				if (line.equals("END:VCARD")) {
					do {
						showContact(name, nick, phoneLine, emailLine, bday, adrLine, categories);
						optcase = showMenu();
						switch (optcase) {
						case "1":
							name = setName(name);
							break;
						case "2":
							nick = setNick(nick);
							break;
						case "3":
							editPhone(phoneLine, phoneList);
							break;
						case "4":
							editEmail(emailLine, emailList);
							break;
						case "5":
							bday = setBday(bday);
							break;
						case "6":
							editAdr(adrLine);
							break;
						case "7":
							editCategories(categories);
							break;
						case "0":
						default:
							break;
						}
					} while (! optcase.equals("0"));
					
//					----------DEBUGGING DATA----------			
//					System.out.println("##################################################");
//					System.out.print(header + "\n");
//					System.out.print(name + "\n");
//					if (! nick.isEmpty()) {
//						System.out.print(nick + "\n");
//					}
//					for (String str : phoneLine) {
//						System.out.print(str + "\n");
//					}
//					for (String str : emailLine) {
//						System.out.print(str + "\n");
//					}
//					if (! bday.isEmpty()) {
//						System.out.print(bday + "\n");
//					}
//					for (String str : adrLine) {
//						System.out.print(str + "\n");
//					}
//					if (! categories.isEmpty()) {
//						System.out.print("CATEGORIES:");
//						String allcat = "";
//						for (String str : categories) {
//							allcat += str + ",";
//						}
//						System.out.print(allcat.substring(0, allcat.length() - 1) + "\n");
//					}
//					System.out.print(end);
//					System.out.println("##################################################");
					
					
//					----------WRITE DATA----------				//NON DEBUG
					bw.write(header + "\n");
					bw.write(name + "\n");
					if (! nick.isEmpty()) {
						bw.write(nick + "\n");
					}
					for (String str : phoneLine) {
						bw.write(str + "\n");
					}
					for (String str : emailLine) {
						bw.write(str + "\n");
					}
					if (! bday.isEmpty()) {
						bw.write(bday + "\n");
					}
					for (String str : adrLine) {
						bw.write(adrContact(str, 0));
						bw.write(adrContact(str, 1));
						bw.write(adrContact(str, 2));
						bw.write(toEscape((adrContact(str, 3)), ":;,=") + "\n");	//In order to be able to add these characters in the address line, they will be escaped
					}
					if (! categories.isEmpty()) {
						bw.write("CATEGORIES:");
						String allcat = "";
						for (String str : categories) {
							allcat += str + ",";
						}
						bw.write(allcat.substring(0, allcat.length() - 1) + "\n");
					}
					bw.write(end);
					
				}
				
				
//				----------NEXT CONTACT----------
				phoneLine.clear();
				emailLine.clear();
				adrLine.clear();
				categories.clear();
				line = br.readLine();
			}
			
			br.close();
			bw.close();	//NON DEBUG
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
	}
	

	private static Scanner sc = new Scanner(System.in);
	
	
	//	#====================PARSING====================#
	/**
	 * Returns the part specified of the name
	 *
	 * @param name Contact name string
	 * @param num Option to select first(1), second(2), mid name(3)
	 * @return Name parsed from option selected
	 */
	private static String nameContact(String name, int num) {
		String delims = "[:;]";
		String[] tokens = name.split(delims);
		try {
			return tokens[num];
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * Returns the nickname segment
	 *
	 * @param bday Raw birthday string
	 * @return Birthday parsed
	 */
	private static String nickContact(String nick) {
		String delims = "[:]";
		String[] tokens = nick.split(delims);
		try {
			return tokens[1];
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * Returns the part specified of the telephone
	 *
	 * @param tel Telephone string
	 * @param num Option to select either the type(2) or the number(3)
	 * @return Type or number from telephone
	 */
	private static String telContact(String tel, int num) {
		String delims = "[:;=]";
		String[] tokens = tel.split(delims);
		return tokens[num];
	}
	
	/**
	 * Returns the phone number spaced and adds prefix
	 * Custom format will be: "+xx xxx xx xx xxxxxx"
	 * It does not support catching only int
	 *
	 * @param num Phone number to be parsed
	 * @return Number already transformed
	 */
	private static String phoneParser(String num) {
		try {
			num = num.replace('-', ' ');
			num = num.replaceAll("\\s","");
			if (num.charAt(0) != '+') {
				num = "+34" + num;
			}
			if (num.charAt(3) != ' ') {
				num = num.substring(0, 3) + " " + num.substring(3);
			}
			for (int i = 7; i < 14; i += 3) {
				if (num.charAt(i) != ' ') {
					num = num.substring(0, i) + " " + num.substring(i);
				}
			}
			return num;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Returns the birthday and roughly prevents weird numbers //TO BE IMPLEMENTED!
	 * Format must be xxxx-xx-xx
	 *
	 * @param bday Birthday string to be parsed
	 * @return Nothing if date was introduced badly
	 */
//	private static String bdayParser(String bday) {
//		try {
//			return bday;
//		} catch (Exception e) {
//			return "";
//		}
//
//	}
	
	/**
	 * Returns the part specified of the email
	 *
	 * @param email Email string
	 * @param num Option to select either the type(2) or the account(3)
	 * @return Type or number from email
	 */
	private static String emailContact(String email, int num) {
		String delims = "[:;=]";
		String[] tokens = email.split(delims);
		return tokens[num];
	}
	
	/**
	 * Returns the birthday segment
	 *
	 * @param bday Raw birthday string
	 * @return Birthday parsed
	 */
	private static String bdayContact(String bday) {
		String delims = "[:]";
		String[] tokens = bday.split(delims);
		try {
			return tokens[1];
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * Returns the part specified of the address
	 *
	 * @param adr Address string
	 * @param num Option to select either the type(2) or the address(3)
	 * @return Type or number from address
	 */
	private static String adrContact(String adr, int num) {
		String delims = "[:;=]";
		String[] tokens = adr.split(delims);
		return tokens[num];
	}
	
	/**
	 * Adds categories into a list 
	 *
	 * @param cat Categories raw string
	 * @param catLine List with parsed categories
	 */
	private static void catContact(String line, ArrayList<String> categories) {
		String delims = "[:,]";
		String[] tokens = line.split(delims);
		for (int i = 1; i < tokens.length; i++) {
			categories.add(tokens[i]);
		}
	}
	
	/**
	 * Returns the string with characters escaped
	 *
	 * @param inoutString String to be escaped
	 * @param toEscape String with characters to be escaped
	 * @return Original string with characters escaped
	 */
	private static String toEscape(String inputString, String toEscape) {
		String str = "";
		String escape = "";
		for (int i = 0; i < toEscape.length(); i++) {
			escape = toEscape.substring(i, i + 1);
			int ix = -1;
			while (inputString.indexOf(escape) != -1) {
				ix = inputString.indexOf(escape);
				str += inputString.substring(0, ix) + "\\" + escape;
				inputString = inputString.substring(ix + 1);
			}
		}
		return str + inputString;
	}
	

//	#====================MENU====================#
	private static void showContact(String name, String nick, ArrayList<String> phoneLine, ArrayList<String> emailLine, String bday, ArrayList<String> adrLine, ArrayList<String> categories) {
		System.out.println("==================================================");
		System.out.println("1st Name: " + nameContact(name, 2));
		if (! nameContact(name, 3).isEmpty()) {
			System.out.println("Mid Name: " + nameContact(name, 3));
		}
		System.out.println("Surname: " + nameContact(name, 1));
		if (! nickContact(nick).isEmpty()) {
			System.out.println("Nickname: " + nickContact(nick));
		}
		for (String str : phoneLine) {
			System.out.print("Number Type: " + telContact(str, 2));
			System.out.println("\tNumber: " + phoneParser(telContact(str, 3)));
		}
		for (String str : emailLine) {
			System.out.print("Email Type: " + emailContact(str, 2));
			System.out.println("\tEmail: " + emailContact(str, 3));
		}
		if (! bdayContact(bday).isEmpty()) {
			System.out.println("Birthday: " + bdayContact(bday));
		}
		for (String str : adrLine) {
			System.out.print("Address Type: " + adrContact(str, 2));
			System.out.println("\tAddress: " + adrContact(str, 3));
		}
		if (! categories.isEmpty()) {
			System.out.print("Categories: ");
			String allcat = "";
			for (String str : categories) {
				allcat += str + ",";
			}
			System.out.println(allcat.substring(0, allcat.length() - 1));
		}
		System.out.println("==================================================");
	}
	
	private static String showMenu() {
		System.out.println("Press any enter to continue...");
		sc.nextLine();
		System.out.println("--------------------------------------------------");
		System.out.println("0.Next\n1.Name\n2.Nickname\n3.Phone Numbers\n4.Email\n5.Birthday\n6.Address\n7.Categories/groups");
		System.out.println("If you wish to edit any of the data for this contact type the number option: ");
		String opt = sc.nextLine();
		System.out.println("--------------------------------------------------");
		return opt;
	}
	
	
//	#====================SETTERS====================#
	private static String setName(String name) {
		System.out.println("1st Name: " + nameContact(name, 2));
		if (! nameContact(name, 3).isEmpty()) {
			System.out.println("Mid Name: " + nameContact(name, 3));
		}
		System.out.println("Surname: " + nameContact(name, 1));
//		OVERWRITE NAME
		System.out.print("Write new first name: ");
		String var = sc.nextLine();
		System.out.print("Write new surname: ");
		String newName = "N:" + sc.nextLine() + ";" + var;
		System.out.print("Write new mid name: ");
		var = sc.nextLine();
		if (! var.isEmpty()) {
			newName += ";" + var;
		}
		return newName;
	}
	
	private static String setNick(String nick) {
		if (! nickContact(nick).isEmpty()) {
			System.out.println("Nickname: " + nickContact(nick));
		}
		System.out.print("Write new nickname: ");
		String var = sc.nextLine();
		if (! var.isEmpty()) {
			String newNick = "NICKNAME:" + var;
			return newNick;
		} else {
			return "";
		}
	}
	
	/**
	 * Asks for a type of value and returns it, making sure it's only 1 of the allowed values
	 *
	 * @return Type of entry, will be only one of the below specified
	 */
	private static String setType() {
		String type = "";
		do {
			System.out.println("(Addresses cannot be type cell)");
			System.out.println("Which type of element this is? (HOME|CELL|WORK): ");
			type = sc.nextLine();
		} while ((! type.equalsIgnoreCase("HOME")) & (! type.equalsIgnoreCase("CELL")) & (! type.equalsIgnoreCase("WORK")));
		return type.toUpperCase();
	}
	
	private static String repeated(ArrayList<String> list, String item) {
		if (! list.contains(item)) {
			return item;
		} else {
			System.out.println("That item is already saved in your contacts list.\nDo you want to proceed anyway? (y/N): ");
			String opt = sc.nextLine();
			if (opt.equalsIgnoreCase("y")) {
				return item;
			}
			return "";
		}
	}
	
	private static void editPhone(ArrayList<String> phoneLine, ArrayList<String> phoneList) {
		String opt = "";
		String newNum = "";
		for (int i = 0; i < phoneLine.size(); i++) {
			System.out.print("Number Type: " + telContact(phoneLine.get(i), 2));
			System.out.println("\tNumber: " + phoneParser(telContact(phoneLine.get(i), 3)));
			System.out.println("Do you want to edit or remove this number? (y/N): ");
			opt = sc.nextLine();
			if (opt.equalsIgnoreCase("y")) {
				System.out.print("Write the edited phone number (leave empty to remove): ");
				newNum = phoneParser(sc.nextLine());
				if (newNum.isEmpty()) {
					phoneList.remove(phoneParser(telContact(phoneLine.get(i), 3)));
					phoneLine.remove(i);	//Removing entry will skip the next one from the loop
				} else if (! repeated(phoneList, newNum).isEmpty()) {	//If not repeated
					phoneLine.set(i, "TEL;TYPE=" + setType() + ":" + newNum);
					phoneList.remove(phoneParser(telContact(phoneLine.get(i), 3)));	//Editing will remove its entry from contact list
					phoneList.add(newNum);
				}
			}
		}
		System.out.println("Do you want to add a new number? (y/N): ");
		opt = sc.nextLine();
		while (opt.equalsIgnoreCase("y")) {
			System.out.print("Write the new phone number: ");
			newNum = addPhone();
			if (! repeated(phoneList, newNum).isEmpty()) {
				phoneLine.add("TEL;TYPE=" + setType() + ":" + newNum);
				phoneList.add(newNum);
			}
			System.out.println("Do you want to add a new number? (y/N): ");
			opt = sc.nextLine();
		}
	}
	
	/**
	 * Forces good syntax on adding new number
	 *
	 * @return Parsed number phone or calls itself
	 */
	private static String addPhone() {
		System.out.print("Write the new phone number: ");
		try {
			String newNum = phoneParser(sc.nextLine());
			return newNum;
		} catch (Exception e) {
			System.out.println("The number you entered was incorrect, try again: ");
			return addPhone();
		}
	}
	
	private static void editEmail(ArrayList<String> emailLine, ArrayList<String> emailList) {
		String opt = "";
		String newEmail = "";
		for (int i = 0; i < emailLine.size(); i++) {
			System.out.print("Email Type: " + emailContact(emailLine.get(i), 2));
			System.out.println("\tEmail: " + emailContact(emailLine.get(i), 3));
			System.out.println("Do you want to edit this email? (y/N): ");
			opt = sc.nextLine();
			if (opt.equalsIgnoreCase("y")) {
				System.out.print("Write the edited email (leave empty to remove): ");
				newEmail =sc.nextLine();
				if (newEmail.isEmpty()) {
					emailList.remove(emailContact(emailLine.get(i), 3));
					emailLine.remove(i);	//Removing entry will skip the next one from the loop
				} else if (! repeated(emailList, newEmail).isEmpty()) {	//If not repeated
					emailLine.set(i, "EMAIL;TYPE=" + setType() + ":" + newEmail);
					emailList.remove(emailContact(emailLine.get(i), 3)); //Editing will remove its entry from contact list
					emailList.add(newEmail);
				}
			}
		}
		System.out.print("Do you want to add a new email? (y/N): ");
		opt = sc.nextLine();
		while (opt.equalsIgnoreCase("y")) {
			System.out.print("Write the new email: ");
			newEmail = sc.nextLine();
			if (! repeated(emailList, newEmail).isEmpty()) {
				emailLine.add("EMAIL;TYPE=" + setType() + ":" + newEmail);
				emailList.add(newEmail);
			}
			System.out.print("Do you want to add a new email? (y/N): ");
			opt = sc.nextLine();
		}
		
		
		if (opt.equalsIgnoreCase("y")) {
			do {
				System.out.println("Write the new email (leave empty to remove): ");
				newEmail = repeated(emailList, sc.nextLine());
			} while (newEmail.isEmpty());
			emailLine.add("EMAIL;TYPE=" + setType() + ":" + newEmail);
			emailList.add(newEmail);
		}
	}
	
	private static String setBday(String bday) {
		if (! bdayContact(bday).isEmpty()) {
			System.out.println("Birthday: " + bdayContact(bday));
		}
		System.out.print("Write new birthday (yyyy-mm-dd): ");
		String var = sc.nextLine();
		if (! var.isEmpty()) {
			String newBday = "BDAY: " + var;
			return newBday;
		} else {
			return "";
		}
	}
	
	private static void editAdr(ArrayList<String> adrLine) {
		String opt = "";
		String newAdr = "";
		for (int i = 0; i < adrLine.size(); i++) {
			System.out.print("Address Type: " + adrContact(adrLine.get(i), 2));
			System.out.println("\tAddress: " + adrContact(adrLine.get(i), 3));
			System.out.println("Do you want to edit this address? (y/N): ");
			opt = sc.nextLine();
			if (opt.equalsIgnoreCase("y")) {
				System.out.print("Write the edited address (leave empty to remove): ");
				newAdr = sc.nextLine();
				if (newAdr.isEmpty()) {
					adrLine.remove(i);	//Removing entry will skip the next one from the loop
				} else if (! repeated(adrLine, newAdr).isEmpty()) {
					adrLine.set(i, "ADR;TYPE=" + setType() + ":" + newAdr);
				}
			}
		}
		System.out.println("Do you want to add a new address? (y/N): ");
		opt = sc.nextLine();
		while (opt.equalsIgnoreCase("y")) {
			System.out.println("Write the new address: ");
			newAdr = sc.nextLine();
			if (! repeated(adrLine, newAdr).isEmpty()) {	//If not repeated
				adrLine.add("ADR;TYPE=" + setType() + ":" + newAdr);
			}
			System.out.println("Do you want to add a new address? (y/N): ");
			opt = sc.nextLine();
		}
	}
	
	private static void editCategories(ArrayList<String> categories) {
		String opt = "";
		String cat = "";
		System.out.println("Categories list: ");
		for (String str : categories) {
			System.out.println(str);
		}
		if (! categories.isEmpty()) {
			System.out.print("Do you want to remove any categorie? (y/N): ");
			opt = sc.nextLine();
			if (opt.equalsIgnoreCase("y")) {
				System.out.print("Write exactly the categorie you want to remove: ");
				categories.remove(sc.nextLine());
			}
		}
		System.out.print("Do you want to add a new categorie? (y/N): ");
		opt = sc.nextLine();
		while (opt.equalsIgnoreCase("y")) {
			System.out.print("Write the name of the categorie to add: ");
			cat = sc.nextLine();
			if (! categories.contains(cat)) {
				categories.add(cat);
			} else {
				System.out.println("This contact is already in that categorie.");
			}
			System.out.print("Do you want to add a new categorie? (y/N): ");
			opt = sc.nextLine();
		}
	}
	
}
