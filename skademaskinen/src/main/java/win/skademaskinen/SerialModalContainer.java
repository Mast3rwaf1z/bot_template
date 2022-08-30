package win.skademaskinen;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import net.dv8tion.jda.api.interactions.ModalInteraction;

public class SerialModalContainer implements Serializable {
	private ArrayList<ModalInteraction> modals = null;

	public SerialModalContainer(ArrayList<ModalInteraction> modals){
		this.modals = modals;
	}

	public static void serialize(SerialModalContainer object){
		try {
			FileOutputStream file = new FileOutputStream("serial_modal");
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(object);
			out.close();
			file.close();
		} catch (IOException e) {
			Colors.exceptionHandler(e, false);
		}

	}

	public static SerialModalContainer deserialize(){
		try {
			FileInputStream file = new FileInputStream("serial_modal");
			ObjectInputStream in = new ObjectInputStream(file);
			SerialModalContainer object = (SerialModalContainer) in.readObject();
			in.close();
			file.close();
			return object;
		} catch (IOException | ClassNotFoundException e) {
			Colors.exceptionHandler(e, false);
		}
		
		return null;
	}
	public ArrayList<ModalInteraction> get(){
		return modals;
	}
}
