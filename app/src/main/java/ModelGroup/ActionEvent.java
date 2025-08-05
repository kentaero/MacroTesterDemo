package ModelGroup;

import kotlin.text.UStringsKt;

public class ActionEvent {
    private int DeviceID;
    private int MacroIdx;
    private int Item_seq;
    private String Item_name;
    private String Item_type;
    private int Addr_x;
    private int Addr_y;
    private String Input_text;
    private String Remarks;
    private int Sort;

    public int getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(int deviceID) {
        DeviceID = deviceID;
    }

    public int getMacroIdx() {
        return MacroIdx;
    }

    public void setMacroIdx(int macroIdx) {
        MacroIdx = macroIdx;
    }

    public int getItem_seq() {
        return Item_seq;
    }

    public void setItem_seq(int item_seq) {
        Item_seq = item_seq;
    }

    public String getItem_name() {
        return Item_name;
    }

    public void setItem_name(String item_name) {
        Item_name = item_name;
    }

    public String getItem_type() {
        return Item_type;
    }

    public void setItem_type(String item_type) {
        Item_type = item_type;
    }

    public int getAddr_x() {
        return Addr_x;
    }

    public void setAddr_x(int addr_x) {
        Addr_x = addr_x;
    }

    public int getAddr_y() {
        return Addr_y;
    }

    public void setAddr_y(int addr_y) {
        Addr_y = addr_y;
    }

    public String getInput_text() {
        return Input_text;
    }

    public void setInput_text(String input_text) {
        Input_text = input_text;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public int getSort() {
        return Sort;
    }

    public void setSort(int sort) {
        Sort = sort;
    }


}
