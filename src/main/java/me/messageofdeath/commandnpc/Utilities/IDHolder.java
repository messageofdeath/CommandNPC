package me.messageofdeath.commandnpc.Utilities;

public class IDHolder {

	private int ID;
	private Object object;
	
	public IDHolder(int ID, Object object) {
		this.ID = ID;
		this.object = object;
	}
	
	public int getID() {
		return this.ID;
	}
	
	public Object getObject() {
		return this.object;
	}
	
	public void setID(int ID) {
		this.ID = ID;
	}
	
	public void setObject(Object object) {
		this.object = object;
	}
}
