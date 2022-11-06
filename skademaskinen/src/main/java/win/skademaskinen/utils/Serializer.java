package win.skademaskinen.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Serializer implements Serializable {
	private ArrayList<ModalData> modals = new ArrayList<ModalData>();
	private static String path = "files/modals.ser";

	public Serializer(ArrayList<ModalData> modals){
		for(ModalData modal : modals){
			this.modals.add(modal);
		}
	}

	public static void serialize(Serializer object){
		try {
			FileOutputStream file = new FileOutputStream(path);
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(object);
			out.close();
			file.close();
		} catch (IOException e) {
			Colors.exceptionHandler(e);
		}

	}

	public static Serializer deserialize(){
		try {
			FileInputStream file = new FileInputStream(path);
			ObjectInputStream in = new ObjectInputStream(file);
			Serializer object = (Serializer) in.readObject();
			in.close();
			file.close();
			return object;
		} catch (IOException | ClassNotFoundException e) {
			Colors.exceptionHandler(e);
		}
		
		return null;
	}
	public ArrayList<ModalData> get(){
		return modals;
	}
}
