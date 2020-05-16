/*
 *    MCreator note:
 *
 *    This file is autogenerated to connect all MCreator mod elements together.
 *
 */
package net.mcreator.testmod;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.potion.Potion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.Block;

import net.mcreator.testmod.gui.GuiTestGUI;
import net.mcreator.testmod.gui.GuiCyanDecraftingTableGUI;
import net.mcreator.testmod.gui.GuiCyanChestGUI8;
import net.mcreator.testmod.gui.GuiCyanChestGUI7;
import net.mcreator.testmod.gui.GuiCyanChestGUI6;
import net.mcreator.testmod.gui.GuiCyanChestGUI5;
import net.mcreator.testmod.gui.GuiCyanChestGUI4;
import net.mcreator.testmod.gui.GuiCyanChestGUI3;
import net.mcreator.testmod.gui.GuiCyanChestGUI2;
import net.mcreator.testmod.gui.GuiCyanChestGUI1;
import net.mcreator.testmod.gui.GuiCyanChestGUI0;
import net.mcreator.testmod.gui.GuiCyanChestGUI;

import java.util.function.Supplier;
import java.util.Random;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Collections;
import java.util.ArrayList;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

public class ElementsTestMod implements IFuelHandler, IWorldGenerator {
	public final List<ModElement> elements = new ArrayList<>();
	public final List<Supplier<Block>> blocks = new ArrayList<>();
	public final List<Supplier<Item>> items = new ArrayList<>();
	public final List<Supplier<Biome>> biomes = new ArrayList<>();
	public final List<Supplier<EntityEntry>> entities = new ArrayList<>();
	public final List<Supplier<Potion>> potions = new ArrayList<>();
	public static Map<ResourceLocation, net.minecraft.util.SoundEvent> sounds = new HashMap<>();
	public ElementsTestMod() {
	}

	public void preInit(FMLPreInitializationEvent event) {
		try {
			for (ASMDataTable.ASMData asmData : event.getAsmData().getAll(ModElement.Tag.class.getName())) {
				Class<?> clazz = Class.forName(asmData.getClassName());
				if (clazz.getSuperclass() == ElementsTestMod.ModElement.class)
					elements.add((ElementsTestMod.ModElement) clazz.getConstructor(this.getClass()).newInstance(this));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Collections.sort(elements);
		elements.forEach(ElementsTestMod.ModElement::initElements);
		this.addNetworkMessage(TestModVariables.WorldSavedDataSyncMessageHandler.class, TestModVariables.WorldSavedDataSyncMessage.class, Side.SERVER,
				Side.CLIENT);
	}

	public void registerSounds(RegistryEvent.Register<net.minecraft.util.SoundEvent> event) {
		for (Map.Entry<ResourceLocation, net.minecraft.util.SoundEvent> sound : sounds.entrySet())
			event.getRegistry().register(sound.getValue().setRegistryName(sound.getKey()));
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator cg, IChunkProvider cp) {
		elements.forEach(element -> element.generateWorld(random, chunkX * 16, chunkZ * 16, world, world.provider.getDimension(), cg, cp));
	}

	@Override
	public int getBurnTime(ItemStack fuel) {
		for (ModElement element : elements) {
			int ret = element.addFuel(fuel);
			if (ret != 0)
				return ret;
		}
		return 0;
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
		if (!event.player.world.isRemote) {
			WorldSavedData mapdata = TestModVariables.MapVariables.get(event.player.world);
			WorldSavedData worlddata = TestModVariables.WorldVariables.get(event.player.world);
			if (mapdata != null)
				TestMod.PACKET_HANDLER.sendTo(new TestModVariables.WorldSavedDataSyncMessage(0, mapdata), (EntityPlayerMP) event.player);
			if (worlddata != null)
				TestMod.PACKET_HANDLER.sendTo(new TestModVariables.WorldSavedDataSyncMessage(1, worlddata), (EntityPlayerMP) event.player);
		}
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event) {
		if (!event.player.world.isRemote) {
			WorldSavedData worlddata = TestModVariables.WorldVariables.get(event.player.world);
			if (worlddata != null)
				TestMod.PACKET_HANDLER.sendTo(new TestModVariables.WorldSavedDataSyncMessage(1, worlddata), (EntityPlayerMP) event.player);
		}
	}
	private int messageID = 0;
	public <T extends IMessage, V extends IMessage> void addNetworkMessage(Class<? extends IMessageHandler<T, V>> handler, Class<T> messageClass,
			Side... sides) {
		for (Side side : sides)
			TestMod.PACKET_HANDLER.registerMessage(handler, messageClass, messageID, side);
		messageID++;
	}
	public static class GuiHandler implements IGuiHandler {
		@Override
		public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
			if (id == GuiTestGUI.GUIID)
				return new GuiTestGUI.GuiContainerMod(world, x, y, z, player);
			if (id == GuiCyanChestGUI.GUIID)
				return new GuiCyanChestGUI.GuiContainerMod(world, x, y, z, player);
			if (id == GuiCyanChestGUI0.GUIID)
				return new GuiCyanChestGUI0.GuiContainerMod(world, x, y, z, player);
			if (id == GuiCyanChestGUI1.GUIID)
				return new GuiCyanChestGUI1.GuiContainerMod(world, x, y, z, player);
			if (id == GuiCyanChestGUI2.GUIID)
				return new GuiCyanChestGUI2.GuiContainerMod(world, x, y, z, player);
			if (id == GuiCyanChestGUI3.GUIID)
				return new GuiCyanChestGUI3.GuiContainerMod(world, x, y, z, player);
			if (id == GuiCyanChestGUI4.GUIID)
				return new GuiCyanChestGUI4.GuiContainerMod(world, x, y, z, player);
			if (id == GuiCyanChestGUI5.GUIID)
				return new GuiCyanChestGUI5.GuiContainerMod(world, x, y, z, player);
			if (id == GuiCyanChestGUI6.GUIID)
				return new GuiCyanChestGUI6.GuiContainerMod(world, x, y, z, player);
			if (id == GuiCyanChestGUI7.GUIID)
				return new GuiCyanChestGUI7.GuiContainerMod(world, x, y, z, player);
			if (id == GuiCyanChestGUI8.GUIID)
				return new GuiCyanChestGUI8.GuiContainerMod(world, x, y, z, player);
			if (id == GuiCyanDecraftingTableGUI.GUIID)
				return new GuiCyanDecraftingTableGUI.GuiContainerMod(world, x, y, z, player);
			return null;
		}

		@Override
		public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
			if (id == GuiTestGUI.GUIID)
				return new GuiTestGUI.GuiWindow(world, x, y, z, player);
			if (id == GuiCyanChestGUI.GUIID)
				return new GuiCyanChestGUI.GuiWindow(world, x, y, z, player);
			if (id == GuiCyanChestGUI0.GUIID)
				return new GuiCyanChestGUI0.GuiWindow(world, x, y, z, player);
			if (id == GuiCyanChestGUI1.GUIID)
				return new GuiCyanChestGUI1.GuiWindow(world, x, y, z, player);
			if (id == GuiCyanChestGUI2.GUIID)
				return new GuiCyanChestGUI2.GuiWindow(world, x, y, z, player);
			if (id == GuiCyanChestGUI3.GUIID)
				return new GuiCyanChestGUI3.GuiWindow(world, x, y, z, player);
			if (id == GuiCyanChestGUI4.GUIID)
				return new GuiCyanChestGUI4.GuiWindow(world, x, y, z, player);
			if (id == GuiCyanChestGUI5.GUIID)
				return new GuiCyanChestGUI5.GuiWindow(world, x, y, z, player);
			if (id == GuiCyanChestGUI6.GUIID)
				return new GuiCyanChestGUI6.GuiWindow(world, x, y, z, player);
			if (id == GuiCyanChestGUI7.GUIID)
				return new GuiCyanChestGUI7.GuiWindow(world, x, y, z, player);
			if (id == GuiCyanChestGUI8.GUIID)
				return new GuiCyanChestGUI8.GuiWindow(world, x, y, z, player);
			if (id == GuiCyanDecraftingTableGUI.GUIID)
				return new GuiCyanDecraftingTableGUI.GuiWindow(world, x, y, z, player);
			return null;
		}
	}
	public List<ModElement> getElements() {
		return elements;
	}

	public List<Supplier<Block>> getBlocks() {
		return blocks;
	}

	public List<Supplier<Item>> getItems() {
		return items;
	}

	public List<Supplier<Biome>> getBiomes() {
		return biomes;
	}

	public List<Supplier<EntityEntry>> getEntities() {
		return entities;
	}

	public List<Supplier<Potion>> getPotions() {
		return potions;
	}
	public static class ModElement implements Comparable<ModElement> {
		@Retention(RetentionPolicy.RUNTIME)
		public @interface Tag {
		}
		protected final ElementsTestMod elements;
		protected final int sortid;
		public ModElement(ElementsTestMod elements, int sortid) {
			this.elements = elements;
			this.sortid = sortid;
		}

		public void initElements() {
		}

		public void init(FMLInitializationEvent event) {
		}

		public void preInit(FMLPreInitializationEvent event) {
		}

		public void generateWorld(Random random, int posX, int posZ, World world, int dimID, IChunkGenerator cg, IChunkProvider cp) {
		}

		public void serverLoad(FMLServerStartingEvent event) {
		}

		public void registerModels(ModelRegistryEvent event) {
		}

		public int addFuel(ItemStack fuel) {
			return 0;
		}

		@Override
		public int compareTo(ModElement other) {
			return this.sortid - other.sortid;
		}
	}
}