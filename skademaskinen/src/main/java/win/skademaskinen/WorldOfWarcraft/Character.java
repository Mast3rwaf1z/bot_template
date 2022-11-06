package win.skademaskinen.WorldOfWarcraft;

public class Character {
	private String Main;
	private String Class;
	private String Specialization;
	private String Role;
	private int Status;
	private int AverageIlvl;
	private int EquippedIlvl;


	Character(String main, String characterClass, String specialization, int role, int status, int averageIlvl, int equippedIlvl){
		this.Main = main;
		this.Class = characterClass;
		this.Specialization = specialization;
		this.Role = getRoleFromId(role);
		this.Status = status;
		this.AverageIlvl = averageIlvl;
		this.EquippedIlvl = equippedIlvl;
	}
	private String getRoleFromId(int role) {
		switch(role){
			case 0:
				return "Tank";
			case 1:
				return "Healer";
			case 2:
				return "Ranged Damage";
			case 3:
				return "Melee Damage";
		}
		return null;

	}
	public String main(){
		return this.Main;
	}
	public String characterClass(){
		return this.Class;
	}
	public String specialization(){
		return this.Specialization;
	}
	public String role(){
		return this.Role;
	}
	public int status(){
		return this.Status;
	}
	public int averageIlvl(){
		return this.AverageIlvl;
	}
	public int equippedIlvl(){
		return this.EquippedIlvl;
	}
}
