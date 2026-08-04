package net.chaolux.createterminal.client.ponder.scene;

import com.mojang.authlib.GameProfile;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.chaolux.createterminal.Config;
import net.chaolux.createterminal.CreateTerminal;
import net.chaolux.createterminal.registry.item.ModItems;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

public class TerminalPonderScenes {
    private TerminalPonderScenes() {

    }

    private static final ResourceLocation FIRST_BIND=text("remote_terminal.first_bind");
    private static final ResourceLocation BIND=text("remote_terminal.bind");
    private static final ResourceLocation USE=text("remote_terminal.use");
    private static final ResourceLocation IN_RANGE=text("remote_terminal.in_range");
    private static final ResourceLocation OUT_RANGE=text("remote_terminal.out_range");
    private static final ResourceLocation DIMENSION=text("remote_terminal.dimension");
    private static final ResourceLocation LOAD=text("remote_terminal.load");
    private static final ResourceLocation NO_CHUNK_LOADING=text("remote_terminal.no_chunk_loading");
    private static final ResourceLocation MULTIPLE=text("advanced_remote_terminal.multiple");
    private static final ResourceLocation ADVANCED_BIND=text("advanced_remote_terminal.bind");
    private static final ResourceLocation NEAREST=text("advanced_remote_terminal.nearest");
    private static final ResourceLocation RANGE=text("advanced_remote_terminal.range");
    private static final ResourceLocation ADVANCED_OUT_RANGE=text("advanced_remote_terminal.out_range");
    private static final ResourceLocation ADVANCED_DIMENSION=text("advanced_remote_terminal.dimension");
    private static final ResourceLocation ADVANCED_LOAD=text("advanced_remote_terminal.load");
    private static final ResourceLocation ADVANCED_NO_CHUNK_LOADING=text("advanced_remote_terminal.no_chunk_loading");
    private static final Set<String> UNUSED_BLOCKS=Set.of("creative_motor","large_cogwheel");

    public static void remoteTerminal(SceneBuilder sceneBuilder, SceneBuildingUtil sceneBuildingUtil) {
        CreateSceneBuilder createSceneBuilder=new CreateSceneBuilder(sceneBuilder);
        createSceneBuilder.title("remote_terminal","Using a Remote Terminal");
        createSceneBuilder.configureBasePlate(0,0,8);
        createSceneBuilder.scaleSceneView(0.86f);
        createSceneBuilder.setSceneOffsetY(-0.5f);
        removeUnused(createSceneBuilder,sceneBuildingUtil);
        createSceneBuilder.showBasePlate();
        BlockPos blockPos=sceneBuildingUtil.grid().at(3,1,1);
        Selection selection=sceneBuildingUtil.select().position(blockPos);
        Selection network=sceneBuildingUtil.select().fromTo(3,1,5,5,3,6);
        Block stockTicker=ForgeRegistries.BLOCKS.getValue(new ResourceLocation("create","stock_ticker"));
        if(stockTicker != null) createSceneBuilder.world().setBlock(blockPos,stockTicker.defaultBlockState(),false);
        ItemStack unbound=new ItemStack(ModItems.REMOTE_TERMINAL.get());
        ItemStack bound=createBound(blockPos);
        Vec3 inside=new Vec3(5.75,1.02,3.0);
        Vec3 outside=new Vec3(1.15,1.02,4.95);
        Vec3 tickerCenter=sceneBuildingUtil.vector().centerOf(blockPos);
        Vec3 rangeVec=new Vec3(blockPos.getX() + 0.5,1.04,blockPos.getZ() + 0.5);
        createSceneBuilder.idle(10);
        createSceneBuilder.world().showSection(network, Direction.DOWN);
        createSceneBuilder.idle(15);
        createSceneBuilder.world().showSection(selection,Direction.DOWN);
        createSceneBuilder.idle(20);
        createSceneBuilder.overlay().showText(70).sharedText(FIRST_BIND).attachKeyFrame().placeNearTarget().pointAt(sceneBuildingUtil.vector().centerOf(blockPos));
        createSceneBuilder.idle(80);
        createSceneBuilder.overlay().showControls(sceneBuildingUtil.vector().topOf(blockPos), Pointing.DOWN,60).rightClick().withItem(unbound);
        createSceneBuilder.overlay().chaseBoundingBoxOutline(PonderPalette.BLUE,"remote_terminal_binding",new AABB(blockPos),70);
        createSceneBuilder.effects().indicateSuccess(blockPos);
        createSceneBuilder.overlay().showText(70).sharedText(BIND).colored(PonderPalette.BLUE).attachKeyFrame().placeNearTarget().pointAt(tickerCenter);
        createSceneBuilder.idle(80);
        ElementLink<EntityElement> elementLink=createPlayer(createSceneBuilder,inside,bound);
        createSceneBuilder.idle(15);
        createSceneBuilder.overlay().showControls(inside.add(0,1.8,0),Pointing.DOWN,55).rightClick().withItem(bound);
        createSceneBuilder.overlay().showText(65).sharedText(USE).attachKeyFrame().placeNearTarget().pointAt(inside.add(0,1.2,0));
        createSceneBuilder.idle(75);
        showRangeVec(createSceneBuilder,rangeVec,3.15,PonderPalette.BLUE,170);
        createSceneBuilder.overlay().showLine(PonderPalette.GREEN,tickerCenter,inside.add(0,1.1,0),90);
        createSceneBuilder.overlay().showText(75).sharedText(IN_RANGE).colored(PonderPalette.GREEN).attachKeyFrame().placeNearTarget().pointAt(inside.add(0,1.1,0));
        createSceneBuilder.idle(85);
        createSceneBuilder.world().modifyEntity(elementLink, Entity::discard);
        createSceneBuilder.idle(10);
        ElementLink<EntityElement> elementElementLink=createPlayer(createSceneBuilder,outside,bound);
        createSceneBuilder.overlay().showLine(PonderPalette.RED,tickerCenter,outside.add(0,1.1,0),95);
        createSceneBuilder.overlay().showControls(outside.add(0,1.8,0),Pointing.DOWN,55).rightClick().withItem(bound);
        createSceneBuilder.overlay().showText(85).sharedText(OUT_RANGE).colored(PonderPalette.RED).attachKeyFrame().placeNearTarget().pointAt(outside.add(0,1.1,0));
        createSceneBuilder.idle(95);
        createSceneBuilder.world().modifyEntity(elementElementLink,Entity::discard);
        createSceneBuilder.idle(10);
        createSceneBuilder.overlay().showText(70).sharedText(DIMENSION).attachKeyFrame().placeNearTarget().pointAt(tickerCenter);
        createSceneBuilder.idle(80);
        createSceneBuilder.overlay().showOutline(PonderPalette.RED,"remote_terminal_load_ticker",selection,105);
        createSceneBuilder.overlay().showOutline(PonderPalette.RED,"remote_terminal_load_network",network,105);
        createSceneBuilder.overlay().showText(85).sharedText(LOAD).colored(PonderPalette.RED).attachKeyFrame().placeNearTarget().pointAt(tickerCenter);
        createSceneBuilder.idle(95);
        createSceneBuilder.overlay().showText(70).sharedText(NO_CHUNK_LOADING).colored(PonderPalette.RED).attachKeyFrame().placeNearTarget().pointAt(tickerCenter);
        createSceneBuilder.idle(80);
    }

    public static void advancedRemoteTerminal(SceneBuilder sceneBuilder,SceneBuildingUtil sceneBuildingUtil) {
        CreateSceneBuilder createSceneBuilder = new CreateSceneBuilder(sceneBuilder);
        createSceneBuilder.title("advanced_remote_terminal", "Using a Advanced Remote Terminal");
        createSceneBuilder.configureBasePlate(0, 0, 8);
        createSceneBuilder.scaleSceneView(0.86f);
        createSceneBuilder.setSceneOffsetY(-0.5f);
        removeUnused(createSceneBuilder,sceneBuildingUtil);
        createSceneBuilder.showBasePlate();
        BlockPos first = sceneBuildingUtil.grid().at(2, 1, 1);
        BlockPos second = sceneBuildingUtil.grid().at(5, 1, 1);
        Selection firstTicker = sceneBuildingUtil.select().position(first);
        Selection secondTicker = sceneBuildingUtil.select().position(second);
        Selection ticker = sceneBuildingUtil.select().position(first).add(secondTicker);
        Selection network = sceneBuildingUtil.select().fromTo(3, 1, 5, 5, 3, 6);
        Block stockTicker = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("create", "stock_ticker"));
        if (stockTicker != null) {
            createSceneBuilder.world().setBlock(first, stockTicker.defaultBlockState(), false);
            createSceneBuilder.world().setBlock(second, stockTicker.defaultBlockState(), false);
        }
        ItemStack unbound = new ItemStack(ModItems.ADVANCED_REMOTE_TERMINAL.get());
        ItemStack firstBound = createBoundAdvancedTerminal(first);
        ItemStack secondBound = createBoundAdvancedTerminal(first, second);
        Vec3 firstTickerCenter = sceneBuildingUtil.vector().centerOf(first);
        Vec3 secondTickerCenter = sceneBuildingUtil.vector().centerOf(second);
        Vec3 inside = new Vec3(3.25, 1.02, 3.0);
        Vec3 outside = new Vec3(0.75, 1.02, 6.15);
        Vec3 rangeCenter = new Vec3(first.getX() + 0.5, 1.04, first.getZ() + 0.5);
        createSceneBuilder.idle(10);
        createSceneBuilder.world().showSection(network, Direction.DOWN);
        createSceneBuilder.idle(15);
        createSceneBuilder.world().showSection(ticker, Direction.DOWN);
        createSceneBuilder.idle(20);
        createSceneBuilder.overlay().showText(75).sharedText(MULTIPLE).attachKeyFrame().placeNearTarget().pointAt(sceneBuildingUtil.vector().centerOf(first));
        createSceneBuilder.idle(85);
        createSceneBuilder.overlay().showControls(sceneBuildingUtil.vector().topOf(first), Pointing.DOWN, 50).rightClick().withItem(unbound);
        createSceneBuilder.overlay().chaseBoundingBoxOutline(PonderPalette.BLUE, "advanced_first_binding", new AABB(first), 60);
        createSceneBuilder.effects().indicateSuccess(first);
        createSceneBuilder.idle(30);
        createSceneBuilder.overlay().showControls(sceneBuildingUtil.vector().topOf(second), Pointing.DOWN, 35).rightClick().withItem(firstBound);
        createSceneBuilder.overlay().chaseBoundingBoxOutline(PonderPalette.BLUE, "advanced_second_binding", new AABB(second), 45);
        createSceneBuilder.effects().indicateSuccess(second);
        createSceneBuilder.idle(40);
        createSceneBuilder.overlay().showText(70).sharedText(ADVANCED_BIND).colored(PonderPalette.BLUE).attachKeyFrame().placeNearTarget().pointAt(secondTickerCenter);
        createSceneBuilder.idle(80);
        ElementLink<EntityElement> elementLink = createPlayer(createSceneBuilder, inside, secondBound);
        createSceneBuilder.idle(15);
        createSceneBuilder.overlay().showControls(inside.add(0, 1.8, 0), Pointing.DOWN, 55).rightClick().withItem(secondBound);
        Vec3 green=inside.add(-0.38,1.45,-0.08);
        Vec3 blue=inside.add(0.38,1.45,-0.08);
        Vec3 firstVec3=sceneBuildingUtil.vector().topOf(first).add(0,0.2,0);
        Vec3 secondVec3=sceneBuildingUtil.vector().topOf(second).add(0,0.2,0);
        createSceneBuilder.overlay().showLine(PonderPalette.GREEN,firstVec3,green,100);
        createSceneBuilder.overlay().showLine(PonderPalette.BLUE,secondVec3,blue,100);
        createSceneBuilder.overlay().showOutline(PonderPalette.GREEN, "advanced_nearest_ticker", firstTicker, 100);
        createSceneBuilder.overlay().showOutline(PonderPalette.BLUE, "advanced_ticker", secondTicker, 100);
        createSceneBuilder.overlay().showText(80).sharedText(NEAREST).colored(PonderPalette.GREEN).attachKeyFrame().placeNearTarget().pointAt(firstTickerCenter);
        createSceneBuilder.idle(90);
        showRangeVec(createSceneBuilder, rangeCenter, 2.35, PonderPalette.GREEN, 170);
        createSceneBuilder.overlay().showText(75).sharedText(RANGE).colored(PonderPalette.GREEN).attachKeyFrame().placeNearTarget().pointAt(rangeCenter.add(0, 0, 2.35));
        createSceneBuilder.idle(85);
        createSceneBuilder.world().modifyEntity(elementLink, Entity::discard);
        createSceneBuilder.idle(10);
        ElementLink<EntityElement> elementElementLink = createPlayer(createSceneBuilder, outside, secondBound);
        createSceneBuilder.overlay().showLine(PonderPalette.RED, outside.add(0, 1.1, 0), firstTickerCenter, 95);
        createSceneBuilder.overlay().showControls(outside.add(0, 1.8, 0), Pointing.DOWN, 55).rightClick().withItem(secondBound);
        createSceneBuilder.overlay().showText(85).sharedText(ADVANCED_OUT_RANGE).colored(PonderPalette.RED).attachKeyFrame().placeNearTarget().pointAt(outside.add(0, 1.1, 0));
        createSceneBuilder.idle(95);
        createSceneBuilder.world().modifyEntity(elementElementLink, Entity::discard);
        createSceneBuilder.idle(10);
        createSceneBuilder.overlay().showText(70).sharedText(ADVANCED_DIMENSION).attachKeyFrame().placeNearTarget().pointAt(firstTickerCenter);
        createSceneBuilder.idle(80);
        createSceneBuilder.overlay().showOutline(PonderPalette.RED, "advanced_load_tickers", ticker, 105);
        createSceneBuilder.overlay().showOutline(PonderPalette.RED, "advanced_load_network", network, 105);
        createSceneBuilder.overlay().showText(85).sharedText(ADVANCED_LOAD).colored(PonderPalette.RED).attachKeyFrame().placeNearTarget().pointAt(firstTickerCenter);
        createSceneBuilder.idle(95);
        createSceneBuilder.overlay().showText(75).sharedText(ADVANCED_NO_CHUNK_LOADING).colored(PonderPalette.RED).attachKeyFrame().placeNearTarget().pointAt(firstTickerCenter);
        createSceneBuilder.idle(85);
    }

    private static ResourceLocation text(String string) {
        return new ResourceLocation(CreateTerminal.MOD_ID,string);
    }

    private static ElementLink<EntityElement> createPlayer(CreateSceneBuilder createSceneBuilder,Vec3 vec3,ItemStack itemStack) {
        return createSceneBuilder.world().createEntity(level -> {
            ArmorStand armorStand=new ArmorStand(level,vec3.x,vec3.y,vec3.z);
            armorStand.setNoGravity(true);
            armorStand.setShowArms(true);
            armorStand.setYRot(180.0f);
            armorStand.setItemSlot(EquipmentSlot.HEAD,new ItemStack(Items.PLAYER_HEAD));
            armorStand.setItemSlot(EquipmentSlot.MAINHAND,itemStack.copy());
            return armorStand;
        });
    }

    private static void showRangeVec(CreateSceneBuilder createSceneBuilder,Vec3 vec3,double radius,PonderPalette palette,int value) {
        int segment=16;
        Vec3 previous=getArcPoint(vec3,radius,0);
        for (int index=1;index <= segment;index++) {
            double direction=Math.PI * index / segment;
            Vec3 next=getArcPoint(vec3,radius,direction);
            createSceneBuilder.overlay().showLine(palette,previous,next,value);
            previous=next;
        }
    }

    private static Vec3 getArcPoint(Vec3 vec3,double radius,double direction) {
        return new Vec3(vec3.x + Math.cos(direction) * radius,vec3.y,vec3.z + Math.sin(direction) * radius);
    }

    private static ItemStack createBound(BlockPos blockPos) {
        ItemStack itemStack=new ItemStack(ModItems.REMOTE_TERMINAL.get());
        CompoundTag compoundTag=itemStack.getOrCreateTag();
        compoundTag.putLong("boundPos",blockPos.asLong());
        compoundTag.putString("boundDim", Level.OVERWORLD.location().toString());
        compoundTag.putString("style","blaze");
        return itemStack;
    }

    private static ItemStack createBoundAdvancedTerminal(BlockPos... blockPos) {
        ItemStack itemStack=new ItemStack(ModItems.ADVANCED_REMOTE_TERMINAL.get());
        CompoundTag compoundTag=itemStack.getOrCreateTag();
        ListTag listTag=new ListTag();
        ListTag tags=new ListTag();
        for(BlockPos pos : blockPos) {
            listTag.add(LongTag.valueOf(pos.asLong()));
            tags.add(StringTag.valueOf(Level.OVERWORLD.location().toString()));
        }
        compoundTag.put("terminals",listTag);
        compoundTag.put("dims",tags);
        compoundTag.putString("style","blaze");
        return itemStack;
    }

    private static void removeUnused(CreateSceneBuilder createSceneBuilder,SceneBuildingUtil sceneBuildingUtil) {
        createSceneBuilder.world().modifyBlocks(sceneBuildingUtil.select().everywhere(),blockState -> {
            ResourceLocation resourceLocation=ForgeRegistries.BLOCKS.getKey(blockState.getBlock());
            if(resourceLocation == null) return blockState;
            if(!resourceLocation.getNamespace().equals("create")) return blockState;
            if(!UNUSED_BLOCKS.contains(resourceLocation.getPath())) return blockState;
            return Blocks.AIR.defaultBlockState();
        },false);
    }
}
