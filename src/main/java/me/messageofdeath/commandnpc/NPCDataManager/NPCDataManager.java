package me.messageofdeath.commandnpc.NPCDataManager;

import java.util.ArrayList;

public class NPCDataManager {

	private final ArrayList<NPCData> data;
	
	public NPCDataManager() {
		this.data = new ArrayList<>();
	}
	
	public void addNPCData(NPCData data) {
		if(!this.hasNPCData(data.getId())) {
			this.data.add(data);
		}
	}
	
	public void removeNPCData(int id) {
		if(this.hasNPCData(id)) {
			this.data.remove(this.getNPCData(id));
		}
	}
	
	public boolean hasNPCData(int id) {
		return this.getNPCData(id) != null;
	}
	
	public NPCData getNPCData(int id) {
		for(NPCData data : this.data) {
			if(data.getId() == id) {
				return data;
			}
		}
		return null;
	}
	
	public ArrayList<NPCData> getNPCDatas() {
		ArrayList<NPCData> data = new ArrayList<>();
		data.addAll(this.data);
		return data;
	}
}
