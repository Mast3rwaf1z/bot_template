package win.skademaskinen.utils;

public class Colors {
	public static final String RESET = "\u001B[0m";
	public static final String BLACK = "\u001B[30;1m";
	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String YELLOW = "\u001B[33m";
	public static final String BLUE = "\u001B[34m";
	public static final String PURPLE = "\u001B[35m";
	public static final String CYAN = "\u001B[36m";
	public static final String WHITE = "\u001B[37m";

	public static String black(String string){
		return BLACK+string+RESET;
	}
	public static String red(String string){
		return RED+string+RESET;
	}
	public static String green(String string){
		return GREEN+string+RESET;
	}
	public static String yellow(String string){
		return YELLOW+string+RESET;
	}
	public static String blue(String string){
		return BLUE+string+RESET;
	}
	public static String purple(String string){
		return PURPLE+string+RESET;
	}
	public static String cyan(String string){
		return CYAN+string+RESET;
	}
	public static String white(String string){
		return WHITE+string+RESET;
	}
	public static void exceptionHandler(Exception e){
		Shell.printer(yellow(e.toString()));
		for(StackTraceElement element : e.getStackTrace()){
			Shell.printer("\t" + Colors.red(element.toString()));
		}
	}
}
