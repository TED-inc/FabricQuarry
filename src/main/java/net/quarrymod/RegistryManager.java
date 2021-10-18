package net.quarrymod;

import net.quarrymod.init.QMContent;
import net.quarrymod.init.QMContent.Machine;
import net.quarrymod.init.QMContent.Upgrades;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.util.Identifier;

import techreborn.TechReborn;
import reborncore.RebornRegistry;

import java.util.Arrays;

public class RegistryManager {
  
  public static final Settings itemGroup = new Item.Settings().group(TechReborn.ITEMGROUP);

    public static void Init()
    {
      RebornRegistry.registerBlock(QMContent.DRILL_TUBE, itemGroup, new Identifier(QuarryMod.MOD_ID, "drill_tube"));
      
		  Arrays.stream(Machine.values()).forEach(value -> RebornRegistry.registerBlock(
			  value.block, 
			  itemGroup,
			  new Identifier(QuarryMod.MOD_ID, value.name)));

      Arrays.stream(Upgrades.values()).forEach(value -> RebornRegistry.registerItem(
			  value.item, 
			  new Identifier(QuarryMod.MOD_ID, value.name)));
    }

    @SuppressWarnings("MethodCallSideOnly")
    public static void ClientInit() { }
}
