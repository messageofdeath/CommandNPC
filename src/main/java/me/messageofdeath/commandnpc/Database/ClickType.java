package me.messageofdeath.commandnpc.Database;

public enum ClickType {
	
	LEFT(new String[] {"punch"}),

	RIGHT(new String[] {"interact"}),

	BOTH(new String[] {"all"});
	
	private final String[] alternatives;
	
	ClickType(String[] alternatives) {
		this.alternatives = alternatives;
	}
	
	public static boolean hasClickType(String clickType) {
		return ClickType.getClickType(clickType) != null;
	}
	
	public static ClickType getClickType(String clickType) {
		for(ClickType type : ClickType.values()) {
			if(type.name().equalsIgnoreCase(clickType)) {
				return type;
			}
			for(String alternatives : type.alternatives) {
				if(clickType.equalsIgnoreCase(alternatives)) {
					return type;
				}
			}
		}
		return null;
	}
}