package win.skademaskinen.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.api.interactions.modals.ModalMapping;


public class ModalData implements Serializable {
	HashMap<String, String> data = new HashMap<String, String>();
	String modalId = "";
	String memberId = "";

	public ModalData(List<ModalMapping> data, String modalId, String memberId){
		for(ModalMapping mapping : data){
			this.data.put(mapping.getId(), mapping.getAsString());
		}
		this.modalId = modalId;
		this.memberId = memberId;
	}

	public Object getId() {
		return modalId;
	}

	public String getMemberId() {
		return memberId;
	}
	public String get(String key){
		return data.get(key);
	}
}