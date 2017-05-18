package me.messageofdeath.CommandNPC.Database;

public enum ClickType {

	LEFT(),

	RIGHT(),

	BOTH();
	
	public static boolean hasClickType(String clickType) {
		return ClickType.getClickType(clickType) != null;
	}
	
	public static ClickType getClickType(String clickType) {
		for(ClickType type : ClickType.values()) {
			if(type.name().equalsIgnoreCase(clickType)) {
				return type;
			}
		}
		return null;
	}
}