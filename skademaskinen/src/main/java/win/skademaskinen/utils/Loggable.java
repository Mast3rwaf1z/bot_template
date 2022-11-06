package win.skademaskinen.utils;

public interface Loggable {
    /*
     * Logs the execution of this class, this will include success tag
     */
    default public String log(String args, boolean successTag){
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        return Utils.timestamp() + " " + this.getClass().getSimpleName()+"::"+stack[stack.length-1].getMethodName()+" ["+args+"] "+ " Success? "+ successTag;
    }

    public String build();
}
