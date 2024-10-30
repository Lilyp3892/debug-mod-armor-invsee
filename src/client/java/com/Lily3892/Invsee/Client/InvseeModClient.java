package com.Lily3892.Invsee.Client;

//import com.google.gson.JsonElement;
import com.Lily3892.Invsee.Client.LibGUI.CustomGUI;
import com.Lily3892.Invsee.Client.LibGUI.GuiScreen;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
//import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
//import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import io.github.cottonmc.cotton.gui.GuiDescription;
//import net.minecraft.client.gui.screen.ingame.Generic3x3ContainerScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
//import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.Component;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
//import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.inventory.SimpleInventory;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
//import net.minecraft.nbt.NbtElement;
//import net.minecraft.registry.Registry;
//import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
//import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.lwjgl.glfw.GLFW;
import net.minecraft.util.hit.HitResult;
import java.io.File;
import java.net.URI;
import java.nio.file.Paths;

public class InvseeModClient implements ClientModInitializer {
	private static KeyBinding keyBinding;


	//from https://fabricmc.net/wiki/tutorial:pixel_raycast until END

	private static HitResult raycastInDirection(MinecraftClient client, float tickDelta, Vec3d direction, double extendedReachDistance) {
		extendedReachDistance = extendedReachDistance * 13;
		Entity entity = client.getCameraEntity();
		if (entity == null || client.world == null) {
			return null;
		}

		assert client.interactionManager != null;
		double reachDistance = getReachDistance(client);
		HitResult target = raycast(entity, reachDistance, tickDelta, false, direction);
		boolean tooFar = false;
		double extendedReach = reachDistance;

		if (hasExtendedReach(client)) {
			extendedReach = extendedReachDistance;
			reachDistance = extendedReach;
		} else {
			if (reachDistance > 3.0D) {
				tooFar = true;
			}
		}

		Vec3d cameraPos = entity.getCameraPosVec(tickDelta);

		extendedReach = extendedReach * extendedReach;
		if (target != null) {
			extendedReach = target.getPos().squaredDistanceTo(cameraPos);
		}

		extendedReach = extendedReachDistance;

		Vec3d vec3d3 = cameraPos.add(direction.multiply(reachDistance));
		Box box = entity.getBoundingBox()
				.stretch(entity.getRotationVec(1.0F).multiply(reachDistance))
				.expand(1.0D, 1.0D, 1.0D);
		EntityHitResult entityHitResult = ProjectileUtil.raycast(
				entity,
				cameraPos,
				vec3d3,
				box,
				(entityx) -> true,
				extendedReach
		);

		if (entityHitResult == null) {
			return target;
		}

		Entity entity2 = entityHitResult.getEntity();
		Vec3d vec3d4 = entityHitResult.getPos();
		double g = cameraPos.squaredDistanceTo(vec3d4);
		if (tooFar && g > 9.0D) {
			return null;
		} else if (g < extendedReach || target == null) {
			target = entityHitResult;
			if (entity2 instanceof LivingEntity || entity2 instanceof ItemFrameEntity) {
				client.targetedEntity = entity2;
			}
		}

		return target;
	}

	private static HitResult raycast(Entity entity, double maxDistance, float tickDelta, boolean includeFluids, Vec3d direction) {
		Vec3d start = entity.getCameraPosVec(tickDelta);
		Vec3d end = start.add(direction.multiply(maxDistance));
		return entity.getWorld().raycast(new RaycastContext(
				start,
				end,
				RaycastContext.ShapeType.OUTLINE,
				includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE,
				entity
		));
	}

	private static double getReachDistance(MinecraftClient client) {
		// Get the default reach distance
		return 4.5;
	}

	private static boolean hasExtendedReach(MinecraftClient client) {
		return true;
	}

	//END of code from https://fabricmc.net/wiki/tutorial:pixel_raycast

	public void onInitializeClient() {
		keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Invsee Key",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_END,
				"Invsee Mod"
		));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {

            if (client.currentScreen != null) {
				//System.out.println(client.currentScreen);
				if (client.currentScreen.toString().contains("net.minecraft.client.gui.screen.ingame.GenericContainerScreen")) {
					assert client.interactionManager != null;
					assert client.cameraEntity != null;
					HitResult hit = raycastInDirection(client, 1.0F, client.cameraEntity.getRotationVec(1.0F), 100.0D);
					assert hit != null;
					if (hit.getType().equals(HitResult.Type.ENTITY)) {

						Entity targetedEntity = ((EntityHitResult) hit).getEntity();

						if (targetedEntity instanceof ItemEntity) {

							ItemEntity Itemtarget = (ItemEntity) targetedEntity;

							int ageSeconds = (int) Math.floor(Double.parseDouble(String.valueOf(Itemtarget.age)) / 20);

							int ageMinutes = (int) Math.floor((double) ageSeconds / 60);

							int ageSecondsformat;

							if (ageSeconds >= 60) {
								if (ageSeconds % 60 == 0) {
									ageSecondsformat = 0;
								} else {
									ageSecondsformat = ageSeconds % 60;
								}
							} else {
								ageSecondsformat = ageSeconds;
							}

							String formattedTime = String.format("%d:%02d", ageMinutes, ageSecondsformat);

							MutableText TimeText = Text.literal(String.valueOf(formattedTime));

							SimpleInventory boxman = new SimpleInventory(9);

							boxman.setStack(4, Itemtarget.getStack());

							GenericContainerScreen screen1 = new GenericContainerScreen(new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X1, 69423, client.player.getInventory(), boxman, 1), client.player.getInventory(), TimeText);

							client.setScreen(screen1);
						}
					}
				}
			}
			if (keyBinding.wasPressed()) {

				assert client.interactionManager != null;
				assert client.cameraEntity != null;
				HitResult hit = raycastInDirection(client, 1.0F, client.cameraEntity.getRotationVec(1.0F), 100.0D);

				if (hit != null) {
					System.out.println("Type: " + hit.getType().toString());
					if (hit.getType().equals(HitResult.Type.ENTITY)) {
						EntityHitResult entityHit = (EntityHitResult) hit;
						Entity targetedEntity = entityHit.getEntity();

						if (targetedEntity != null) {
							System.out.println(targetedEntity.toString() + ": press");
						}

						if (targetedEntity instanceof LivingEntity) {
							LivingEntity target = (LivingEntity) targetedEntity;
							if (target != null) {

								var nbtcopy = new NbtCompound();

								String name = "error3";

								if (target.getName().getContent().toString() != null) {
									name = target.getName().getContent().toString();
									name = name.replaceAll(".*\\{", "").replaceAll("}.*", "");
									if (name.length() >= 5 && name.substring(0, 5).equals("key='")) {
										int closingQuoteIndex = name.indexOf("'", 5);
										if (closingQuoteIndex != -1) {
											name = name.substring(5, closingQuoteIndex);
										} else {
											name = "error2";
										}
									}
								} else {
									name = "error1";
								}

								target.writeNbt(nbtcopy);
								System.out.println("NBT: " + name);
								System.out.println(nbtcopy);
								System.out.println("NBT: " + name);
								nbtcopy = new NbtCompound();

								var helmetDurability = (target.getEquippedStack(EquipmentSlot.HEAD).getMaxDamage() - target.getEquippedStack(EquipmentSlot.HEAD).getDamage()) + "/" + (target.getEquippedStack(EquipmentSlot.HEAD).getMaxDamage());
								var chestplateDurability = (target.getEquippedStack(EquipmentSlot.CHEST).getMaxDamage() - target.getEquippedStack(EquipmentSlot.CHEST).getDamage()) + "/" + (target.getEquippedStack(EquipmentSlot.CHEST).getMaxDamage());
								var leggingsDurability = (target.getEquippedStack(EquipmentSlot.LEGS).getMaxDamage() - target.getEquippedStack(EquipmentSlot.LEGS).getDamage()) + "/" + (target.getEquippedStack(EquipmentSlot.LEGS).getMaxDamage());
								var bootsDurability = (target.getEquippedStack(EquipmentSlot.FEET).getMaxDamage() - target.getEquippedStack(EquipmentSlot.FEET).getDamage()) + "/" + (target.getEquippedStack(EquipmentSlot.FEET).getMaxDamage());
								var MainhandDurability = (target.getEquippedStack(EquipmentSlot.MAINHAND).getMaxDamage() - target.getEquippedStack(EquipmentSlot.MAINHAND).getDamage()) + "/" + (target.getEquippedStack(EquipmentSlot.MAINHAND).getMaxDamage());
								var OffhandDurability = (target.getEquippedStack(EquipmentSlot.OFFHAND).getMaxDamage() - target.getEquippedStack(EquipmentSlot.OFFHAND).getDamage()) + "/" + (target.getEquippedStack(EquipmentSlot.OFFHAND).getMaxDamage());
								SimpleInventory shulker = new SimpleInventory(9);

								for (int i = 0; i < 6; i++) {
									if (i == 0) {
										//target.getEquippedStack(EquipmentSlot.HEAD).comp;
										shulker.setStack(i, target.getEquippedStack(EquipmentSlot.HEAD).copy());
									} else if (i == 1) {
										//target.getEquippedStack(EquipmentSlot.CHEST).writeNbt(nbtcopy);
										shulker.setStack(i, target.getEquippedStack(EquipmentSlot.CHEST).copy());
									} else if (i == 2) {
										//target.getEquippedStack(EquipmentSlot.LEGS).writeNbt(nbtcopy);
										shulker.setStack(i, target.getEquippedStack(EquipmentSlot.LEGS).copy());
									} else if (i == 3) {
										//target.getEquippedStack(EquipmentSlot.FEET).writeNbt(nbtcopy);
										shulker.setStack(i, target.getEquippedStack(EquipmentSlot.FEET).copy());
									} else if (i == 4) {
										//target.getEquippedStack(EquipmentSlot.MAINHAND).writeNbt(nbtcopy);
										shulker.setStack(i, target.getEquippedStack(EquipmentSlot.MAINHAND).copy());
									} else {
										//target.getEquippedStack(EquipmentSlot.OFFHAND).writeNbt(nbtcopy);
										shulker.setStack(i, target.getEquippedStack(EquipmentSlot.OFFHAND).copy());
									}
									nbtcopy = new NbtCompound();
								}

								GenericContainerScreen screen = new GenericContainerScreen(new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X1, 69420, client.player.getInventory(), shulker, 1), client.player.getInventory(), Text.translatable(name));

								//client.setScreen(screen);

								client.setScreen(new GuiScreen(new CustomGUI(target.getEquippedStack(EquipmentSlot.HEAD))));


								// Print the armor durability to the console
								System.out.println("Helmet Durability: " + helmetDurability);
								System.out.println(target.getEquippedStack(EquipmentSlot.HEAD).copy());
								System.out.println("Chestplate Durability: " + chestplateDurability);
								System.out.println(target.getEquippedStack(EquipmentSlot.CHEST).copy());
								System.out.println("Leggings Durability: " + leggingsDurability);
								System.out.println(target.getEquippedStack(EquipmentSlot.LEGS).copy());
								System.out.println("Boots Durability: " + bootsDurability);
								System.out.println(target.getEquippedStack(EquipmentSlot.FEET).copy());
								System.out.println("Main hand Durability: " + MainhandDurability);
								System.out.println(target.getEquippedStack(EquipmentSlot.MAINHAND).copy());
								System.out.println("Off hand Durability: " + OffhandDurability);
								System.out.println(target.getEquippedStack(EquipmentSlot.OFFHAND).copy());

								// Send a chat message with the armor durability to the player
								boolean printDurability = false;
								if (printDurability) {
									client.player.sendMessage(Text.literal("Helmet Durability: " + helmetDurability), false);
									client.player.sendMessage(Text.literal("Chestplate Durability: " + chestplateDurability), false);
									client.player.sendMessage(Text.literal("Leggings Durability: " + leggingsDurability), false);
									client.player.sendMessage(Text.literal("Boots Durability: " + bootsDurability), false);
									client.player.sendMessage(Text.literal("Main hand Durability: " + MainhandDurability), false);
									client.player.sendMessage(Text.literal("Off hand Durability: " + OffhandDurability), false);
								}

								File file = Paths.get(URI.create(FabricLoader.getInstance().getGameDir().toUri() + "/logs/latest.log")).toFile();
								MutableText text = Text.literal("Data sent to " + (String) file.getName()).formatted(Formatting.UNDERLINE).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath())));
								client.player.sendMessage(text, false);

							}
						} else if (targetedEntity instanceof ItemEntity) {

							var nbtcopy = new NbtCompound();

							if (targetedEntity != null) {

								String name = "error4";

								if (targetedEntity.getName().getContent().toString() != null) {
									name = targetedEntity.getName().getContent().toString();
									name = name.replaceAll(".*\\{", "").replaceAll("}.*", "");
									if (name.length() >= 5 && name.substring(0, 5).equals("key='")) {
										int closingQuoteIndex = name.indexOf("'", 5);
										if (closingQuoteIndex != -1) {
											name = name.substring(5, closingQuoteIndex);
										} else {
											name = "error3";
										}
									} else {
										name = "error2";
									}
								} else {
									name = "error1";
								}

								targetedEntity.writeNbt(nbtcopy);
								System.out.println("NBT: " + name);
								System.out.println(nbtcopy);
								System.out.println("NBT: " + name);
								nbtcopy = new NbtCompound();
								targetedEntity.writeNbt(nbtcopy);

								File file = Paths.get(URI.create(FabricLoader.getInstance().getGameDir().toUri() + "/logs/latest.log")).toFile();
								MutableText text = Text.literal("Data sent to " + (String) file.getName()).formatted(Formatting.UNDERLINE).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath())));
								assert client.player != null;
								client.player.sendMessage(text, false);

								JsonObject JsonData = JsonParser.parseString(nbtcopy.toString()).getAsJsonObject();
								;

								String ageTick = JsonData.get("Age").toString().replace("s", "").replace("\"", "");

								int ageSeconds = (int) Math.floor(Double.parseDouble(ageTick) / 20);

								int ageMinutes = (int) Math.floor((double) ageSeconds / 60);

								int ageSecondsformat;

								if (ageSeconds >= 60) {
									if (ageSeconds % 60 == 0) {
										ageSecondsformat = 0;
									} else {
										ageSecondsformat = ageSeconds % 60;
									}
								} else {
									ageSecondsformat = ageSeconds;
								}

								String formattedTime = String.format("%d:%02d", ageMinutes, ageSecondsformat);

								MutableText TimeText = Text.literal(String.valueOf(formattedTime));

								SimpleInventory boxman = new SimpleInventory(9);

								GenericContainerScreen screen = new GenericContainerScreen(new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X1, 69423, client.player.getInventory(), boxman, 1), client.player.getInventory(), TimeText);

								client.setScreen(screen);
							}
						} else {

							var nbtcopy = new NbtCompound();

							if (targetedEntity != null) {

								String name = "error4";

								if (targetedEntity.getName().getContent().toString() != null) {
									name = targetedEntity.getName().getContent().toString();
									name = name.replaceAll(".*\\{", "").replaceAll("}.*", "");
									if (name.length() >= 5 && name.substring(0, 5).equals("key='")) {
										int closingQuoteIndex = name.indexOf("'", 5);
										if (closingQuoteIndex != -1) {
											name = name.substring(5, closingQuoteIndex);
										} else {
											name = "error3";
										}
									} else {
										name = "error2";
									}
								} else {
									name = "error1";
								}

								targetedEntity.writeNbt(nbtcopy);
								System.out.println("NBT: " + name);
								System.out.println(nbtcopy);
								System.out.println("NBT: " + name);
								nbtcopy = new NbtCompound();

								File file = Paths.get(URI.create(FabricLoader.getInstance().getGameDir().toUri() + "/logs/latest.log")).toFile();
								MutableText text = Text.literal("Data sent to " + (String) file.getName()).formatted(Formatting.UNDERLINE).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath())));
								client.player.sendMessage(text, false);

							}
						}
					}
				}
			}
		});
	}
}