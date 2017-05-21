package me.razorrider7.commandnpc.Utils.npcdatamanager;

import java.util.ArrayList;

public class NPCDataManager {

	private ArrayList<NPCData> data;
	
	public NPCDataManager() {
		this.data = new ArrayList<NPCData>();
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
		ArrayList<NPCData> data = new ArrayList<NPCData>();
		data.addAll(this.data);
		return data;
	}
}
