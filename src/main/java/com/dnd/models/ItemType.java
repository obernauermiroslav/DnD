package com.dnd.models;
public enum ItemType {
    HELMET("helmet"),
    ARMOR("armor"),
    TROUSERS("trousers"),
    BOOTS("boots"),
    GLOVES("gloves"),
    CLOAK("cloak"),
    WEAPON("weapon"),
    SHIELD("shield"),
    POTION("potion"),
    GOLD("gold"),
    SPELL("spell");

    private final String label;

    private ItemType(String label) {
        this.label = label;
    }
    public String getLabel() {
        return label;
    }
}