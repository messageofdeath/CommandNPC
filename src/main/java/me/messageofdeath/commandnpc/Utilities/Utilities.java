package me.messageofdeath.commandnpc.Utilities;

import java.util.ArrayList;
import java.util.Comparator;

public class Utilities {
	
	private static final String alpha = "^[a-zA-Z]*$";
	private static final String alphaNumeric = "^[a-zA-Z0-9]*$";
	private static final String intRegex = "\\d+";
	private static final String floatRegex = "[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)";
	private static final String Digits = "(\\p{Digit}+)";
	private static final String HexDigits = "(\\p{XDigit}+)";
	private static final String Exp = "[eE][+-]?"+Digits;
	private static final String doubleRegex = ("[\\x00-\\x20]*"+"[+-]?("+"NaN|"+"Infinity|"+"((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+
		    "(\\.("+Digits+")("+Exp+")?)|"+"(("+"(0[xX]"+HexDigits+"(\\.)?)|"+"(0[xX]"+HexDigits+"?(\\.)"+HexDigits+")"+")[pP][+-]?"+Digits+"))"+
		    "[fFdD]?))"+"[\\x00-\\x20]*");
	
	public static boolean isAlpha(String total) {
		return total.matches(alpha);
	}
	
	public static boolean isAlphanumeric(String total) {
		return total.matches(alphaNumeric);
	}
	
	public static boolean isNumeric(String total) {
		return isInteger(total) || isFloat(total) || isDouble(total);
	}
	
	public static boolean isDouble(String total) {
		return total.matches(doubleRegex);
	}
	
	public static boolean isInteger(String total) {
		return total.matches(intRegex);
	}
	
	public static boolean isFloat(String total) {
		return total.matches(floatRegex);
	}

	public static String getOrdinal(int n) {
		char[] args = (n + "").toCharArray();
		char last = args[args.length - 1];
		if(last == '1') {
			return n + "st";
		}else if(last == '2') {
			return n + "nd";
		}else if(last == '3') {
			return n + "rd";
		}else{
			return n + "th";
		}
	}
	
	public static String getTime(int time) {
		int days = time >= 60*60*24 ? time / (60*60*24) : 0;
		time -= days*60*60*24;
		int hours = time >= 60*60 ? time / (60*60) : 0;
		time -= hours*60*60;
		int minutes = time >= 60 ? time / 60 : 0;
		time -= minutes*60;
		int seconds = time;
		return (days != 0 ? days + (days == 1 ? " day" : " days") : "") + (hours != 0 ? hours + (hours == 1 ? " hour" : " hours") : "") + 
				(minutes != 0 ? minutes + (minutes == 1 ? " minute" : " minutes") : "") + (seconds != 0 ? seconds + (seconds == 1 ? " second" : " seconds") : "");
	}
	
	public static ArrayList<IDHolder> sortIDs(ArrayList<IDHolder> holders) {
		if (!holders.isEmpty()) {
			holders.sort(Utilities.compareIDs());
			int lastPosition = 0;
			int difference;
			IDHolder holder;
			for (int i = 0; i < holders.size(); i++) {
				holder = holders.get(i);
				if (lastPosition != holder.getID()) {
					difference = holder.getID() - lastPosition;
					if (difference > 1) {
						for (int x = i; x < holders.size(); x++) {
							holders.get(x).setID(holders.get(x).getID() - difference);
						}
					}
					difference = holder.getID() - lastPosition;
					if (difference == 0) {
						for (int x = i; x < holders.size(); x++) {
							holders.get(x).setID(holders.get(x).getID() + 1);
						}
					}
					lastPosition = holder.getID();
				}else if(lastPosition == holder.getID()){
					holder.setID(holder.getID() + 1);
					lastPosition = holder.getID();
				}
			}
			holders.sort(Utilities.compareIDs());
		}
		return holders;
	}
	
	public static ArrayList<IDHolder> setID(int oldID, int newID, ArrayList<IDHolder> holders) {
		boolean has = false;
		for(IDHolder holder : holders) {
			if(holder.getID() == oldID) {
				has = true;
				holder.setID(newID);
				continue;
			}
			if(has) {
				holder.setID(holder.getID() + 1);
			}
		}
		return Utilities.sortIDs(holders);
	}

	public static int getNextID(ArrayList<IDHolder> holders) {
		ArrayList<Integer> ids = new ArrayList<>();
		for (IDHolder holder : holders) {
			ids.add(holder.getID());
		}
		for(int i = 1; i < ids.size(); i++) {
			if (!ids.contains(i)) {
				return i;
			}
		}
		return 0;
	}
	
	private static Comparator<IDHolder> compareIDs() {
		return (holder1, holder2) -> {
            if (holder1.getID() > holder2.getID()) {
                return 1;
            }
            if (holder1.getID() < holder2.getID()) {
                return -1;
            }
            return 0;
        };
	}
}
