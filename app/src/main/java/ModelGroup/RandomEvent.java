package ModelGroup;

public class RandomEvent {
    public static final String EVENT_TOKEN = ";";
    public static final String NAVE_VALUE_TOKEN = "=";
    public static final String VALUE_TOKEN = ",";

    private String Command;
    private String Param1;
    private String Param2;
    private String Param3;
    private String Param4;

    public String getCommand() {
        return Command;
    }

    public void setCommand(String command) {
        Command = command;
    }

    public String getParam1() {
        return Param1;
    }

    public void setParam1(String param1) {
        Param1 = param1;
    }

    public String getParam2() {
        return Param2;
    }

    public void setParam2(String param2) {
        Param2 = param2;
    }

    public String getParam3() {
        return Param3;
    }

    public void setParam3(String param3) {
        Param3 = param3;
    }

    public String getParam4() {
        return Param4;
    }

    public void setParam4(String param4) {
        Param4 = param4;
    }

    public static String[] getCommandList(){
        String[] cmds = new String[7];

        cmds[0] = "addr";
        cmds[1] = "field";
        cmds[2] = "caption";
        cmds[3] = "back";
        cmds[4] = "bScroll";
        cmds[5] = "tScroll";
        cmds[6] = "rndScroll";

        return cmds;
    }

    public static Boolean ContainsCommand(String cmd){
        String[] cmds = getCommandList();

        Boolean result = false;
        for (int i = 0; i < cmds.length; i++) {
            if(cmds[i].equalsIgnoreCase(cmd))
            {
                result = true;
                break;
            }
        }

        return result;
    }
}
