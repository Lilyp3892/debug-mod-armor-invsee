package com.Lily3892.Invsee.Client.LibGUI;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CustomGUI extends LightweightGuiDescription {
    public CustomGUI(ItemStack itemStack) {
        WGridPanel root = new WGridPanel(20);
        setRootPanel(root);
        root.setSize(256, 240);
        root.setInsets(Insets.ROOT_PANEL);

        // Initialize custom inventory with 1 slot
        CustomInventory customInventory = new CustomInventory(1);
        customInventory.setStack(0, new ItemStack(Items.STICK, 1));

        // Debugging to ensure the item stack is correctly set
        System.out.println("CustomInventory Stack: " + customInventory.getStack(0).toString());

        // Add a label to the GUI
        WLabel label = new WLabel(Text.literal("Inventory").formatted(Formatting.BLACK));
        root.add(label, 0, 0, 2, 1);

        // Add an item slot to the GUI
        WItemSlot itemSlot = WItemSlot.of(customInventory, 0);
        root.add(itemSlot, 1, 1); // Position the slot at (1, 1) in the grid

        // Validate the root panel to ensure all components are set up correctly
        root.validate(this);
    }
}
