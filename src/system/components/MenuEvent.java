package system.components;

public class MenuEvent {
    public final static int MENU = 1;
    public final static int SUBMENU = 2;
    private int type_item;
    private int index;

    public int get_index() {
        return index;
    }

    public int get_type_item() {
        return type_item;
    }

    protected void set_event(int type_item, int index) {
        this.type_item = type_item;
        this.index = index;
    }
}