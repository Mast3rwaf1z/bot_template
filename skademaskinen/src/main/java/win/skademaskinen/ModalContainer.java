package win.skademaskinen;

import java.io.Serializable;

import net.dv8tion.jda.api.interactions.ModalInteraction;

public class ModalContainer implements Serializable {
	long responseNumber;
	ModalInteraction interaction;

	public ModalContainer(long responseNumber, ModalInteraction interaction){
		this.responseNumber = responseNumber;
		this.interaction = interaction;
	}
}