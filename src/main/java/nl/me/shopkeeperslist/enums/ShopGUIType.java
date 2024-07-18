package nl.me.shopkeeperslist.enums;

public enum ShopGUIType {
    SELLING_ITEM("Shops selling item"),
    BUYING_ITEM("Shops buying item"),
    PLAYER("Shops owned by player"),
    ALL("All shops");

    private final String name;

    ShopGUIType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
