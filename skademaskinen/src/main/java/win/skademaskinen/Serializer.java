package win.skademaskinen;

import java.io.Externalizable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

public class Serializer implements Serializable {
	private ArrayList<ModalContainer> modals = null;

	public Serializer(ArrayList<ModalInteractionEvent> modals){
		for(ModalInteractionEvent modal : modals){
			this.modals.add(new ModalContainer(modal.getResponseNumber(), modal.getInteraction()));
		}
	}

	public static void serialize(Serializer object){
		Serializer backup = deserialize();
		try {
			FileOutputStream file = new FileOutputStream("serial_modal");
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(object);
			out.close();
			file.close();
		} catch (IOException e) {
			serialize(backup);
			Colors.exceptionHandler(e, false);
		}

	}

	public static Serializer deserialize(){
		try {
			FileInputStream file = new FileInputStream("serial_modal");
			ObjectInputStream in = new ObjectInputStream(file);
			Serializer object = (Serializer) in.readObject();
			in.close();
			file.close();
			return object;
		} catch (IOException | ClassNotFoundException e) {
			Colors.exceptionHandler(e, true);
		}
		
		return null;
	}
	public ArrayList<ModalInteractionEvent> get(JDA jda){
		ArrayList<ModalInteractionEvent> result = new ArrayList<ModalInteractionEvent>();
		for(ModalContainer container : modals){
			result.add(new ModalInteractionEvent(jda, container.responseNumber, container.interaction));
		}
		return result;
	}
}
