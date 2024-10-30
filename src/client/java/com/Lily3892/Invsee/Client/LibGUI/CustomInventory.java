package com.Lily3892.Invsee.Client.LibGUI;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

public class CustomInventory implements Inventory {
    private final SimpleInventory inventory;

    public CustomInventory(int size) {
        this.inventory = new SimpleInventory(size);
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return this.inventory.removeStack(slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return this.inventory.removeStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.setStack(slot, stack);
    }

    @Override
    public void markDirty() {
        this.inventory.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }
}