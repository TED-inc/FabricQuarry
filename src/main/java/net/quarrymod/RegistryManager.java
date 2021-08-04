package net.quarrymod;

import net.quarrymod.init.QMContent.Machine;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import techreborn.TechReborn;
import reborncore.RebornRegistry;

import java.util.Arrays;

public class RegistryManager {
		
    public static void Init()
    {
		Arrays.stream(Machine.values()).forEach(value -> RebornRegistry.registerBlock(
			value.block, 
			new Item.Settings().group(TechReborn.ITEMGROUP),
			new Identifier(QuarryMod.MOD_ID, value.name)));
    }

    @SuppressWarnings("MethodCallSideOnly")
    public static void ClientInit() { }
}
