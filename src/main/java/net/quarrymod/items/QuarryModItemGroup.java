package net.quarrymod.items;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.quarrymod.QuarryMod;
import net.quarrymod.init.QuarryManagerContent;

public class QuarryModItemGroup {
    public static final ItemGroup QUARRY_MOD_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(QuarryManagerContent.DRILL_TUBE))
            .displayName(Text.translatable("itemGroup.quarrymod.item_group"))
            .build();

    public static void registerItemGroups() {
        Registry.register(Registries.ITEM_GROUP, new Identifier(QuarryMod.MOD_ID, "item_group"), QUARRY_MOD_GROUP);
    }
}
