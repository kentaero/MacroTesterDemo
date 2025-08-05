package ModelGroup;

public class ClickAddresItem {

    public String getCaption() {
        return Caption;
    }

    public void setCaption(String caption) {
        Caption = caption;
    }

    public String getField() {
        return Field;
    }

    public void setField(String field) {
        Field = field;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getBottm() {
        return bottm;
    }

    public void setBottm(int bottm) {
        this.bottm = bottm;
    }

    public String getObjClass() {
        return ObjClass;
    }

    public void setObjClass(String objClass) {
        ObjClass = objClass;
    }
    private String Caption;
    private String Field;
    private String ObjClass;
    private int left;
    private int top;
    private int right;
    private int bottm;

    public ClickAddresItem(String caption, String field, String ObjClass,int left, int top, int right, int bottm) {
        this.Caption = caption;
        this.Field = field;
        this.ObjClass = ObjClass;
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottm = bottm;
    }
}
