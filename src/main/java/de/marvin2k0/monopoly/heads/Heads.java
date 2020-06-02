package de.marvin2k0.monopoly.heads;

import org.bukkit.inventory.ItemStack;

public enum Heads
{
    DICE("ZDQyMDY0YWE0ZGUzNWY2NjliMDRlODdiMWUwNGQ0MmVjZWU4MjliOWJjNTY2MTM4YWEyNzk0Nzk4YWZiZWMifX19==", "dice");

    private ItemStack item;
    private String idTag;
    private String prefix = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv";

    private Heads(String texture, String id)
    {
        item = Main.createSkull(prefix + texture, id);
        idTag = id;
    }


    public ItemStack getItemStack()
    {
        return item;
    }

    public String getName()
    {
        return idTag;
    }


}