package mc;

import java.util.HashMap;
import java.util.Map;

public class MinecraftEntity {
	public boolean isActive;
	public double dx;
	public double dy;
	public double dz;
	public String name;
	public MinecraftEntity(boolean isActive,double dx,double dy,double dz,String name) {
		this.isActive = isActive;
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;
		this.name = name;
	}
	
	public static final MinecraftEntity[] entity = new MinecraftEntity[] {
			new MinecraftEntity(false,0,0,0,"minecraft:area_effect_cloud"),
			new MinecraftEntity(false,1,1,2,"minecraft:armor_stand"),
			new MinecraftEntity(true,1,1,1,"minecraft:arrow"),
			new MinecraftEntity(false,1.3,1.3,0.6,"minecraft:axolotl"),
			new MinecraftEntity(false,0.5,0.5,0.9,"minecraft:bat"),
			new MinecraftEntity(false,0.7,0.7,0.6,"minecraft:bee"),
			new MinecraftEntity(true,0.6,0.6,1.8,"minecraft:blaze"),
			new MinecraftEntity(false,1.375,1.375,0.5625,"minecraft:boat"),
			new MinecraftEntity(false,0.6,0.6,0.7,"minecraft:cat"),
			new MinecraftEntity(true,0.7,0.7,0.5,"minecraft:cave_spider"),
			new MinecraftEntity(false,0.4,0.4,0.7,"minecraft:chicken"),
			new MinecraftEntity(false,0.5,0.5,0.3,"minecraft:cod"),
			new MinecraftEntity(false,0.9,0.9,1.4,"minecraft:cow"),
			new MinecraftEntity(true,0.6,0.6,1.7,"minecraft:creeper"),
			new MinecraftEntity(false,0.9,0.9,0.6,"minecraft:dolphin"),
			new MinecraftEntity(false,1.5,1.5,1.39648,"minecraft:donkey"),
			new MinecraftEntity(true,1.0,1.0,1.0,"minecraft:dragon_fireball"),
			new MinecraftEntity(true,0.6,0.6,1.95,"minecraft:drowned"),
			new MinecraftEntity(true,2.35,2.35,2.35,"minecraft:elder_guardian"),
			new MinecraftEntity(false,2.0,2.0,2.0,"minecraft:end_crystal"),
			new MinecraftEntity(true,16.0,16.0,8.0,"minecraft:ender_dragon"),
			new MinecraftEntity(true,0.6,0.6,2.9,"minecraft:enderman"),
			new MinecraftEntity(true,1.4,1.4,1.3,"minecraft:endermite"),
			new MinecraftEntity(true,0.6,0.6,1.95,"minecraft:evoker"),
			new MinecraftEntity(false,0.5,0.5,0.8,"minecraft:evoker_fangs"),
			new MinecraftEntity(false,0,0,0,"minecraft:experience_orb"),
			new MinecraftEntity(false,0,0,0,"minecraft:eye_of_ender"),
			new MinecraftEntity(false,0.98,0.98,0.98,"minecraft:falling_block"),
			new MinecraftEntity(false,0.25,0.25,0.25,"minecraft:firework_rocket"),
			new MinecraftEntity(false,0.6,0.6,0.7,"minecraft:fox"),
			new MinecraftEntity(true,4.0,4.0,4.0,"minecraft:ghast"),
			new MinecraftEntity(true,3.6,3.6,12.0,"minecraft:giant"),
			new MinecraftEntity(false,0.75,0.0625,0.75,"minecraft:glow_item_frame"),
			new MinecraftEntity(false,0.8,0.8,0.8,"minecraft:glow_squid"),
			new MinecraftEntity(false,1.3,1.3,0.9,"minecraft:goat"),
			new MinecraftEntity(true,0.85,0.85,0.85,"minecraft:guardian"),
			new MinecraftEntity(true,1.39648,1.39648,1.4,"minecraft:hoglin"),
			new MinecraftEntity(false,1.39648,1.39648,1.6,"minecraft:horse"),
			new MinecraftEntity(true,0.6,0.6,1.95,"minecraft:husk"),
			new MinecraftEntity(true,0.6,0.6,1.95,"minecraft:illusioner"),
			new MinecraftEntity(false,1.4,1.4,2.7,"minecraft:iron_golem"),
			new MinecraftEntity(false,0.25,0.25,0.25,"minecraft:item"),
			new MinecraftEntity(false,0.75,0.0625,0.75,"minecraft:item_frame"),
			new MinecraftEntity(true,1.0,1.0,1.0,"minecraft:fireball"),
			new MinecraftEntity(false,0.375,0.375,0.5,"minecraft:leash_knot"),
			new MinecraftEntity(false,0,0,0,"minecraft:lightning_bolt"),
			new MinecraftEntity(false,0.9,0.9,1.87,"minecraft:llama"),
			new MinecraftEntity(false,0.25,0.25,0.25,"minecraft:llama_spit"),
			new MinecraftEntity(true,3,3,3,"minecraft:magma_cube"),
			new MinecraftEntity(false,0,0,0,"minecraft:marker"),
			new MinecraftEntity(false,0.98,0.98,0.7,"minecraft:minecart"),
			new MinecraftEntity(false,0.98,0.98,0.7,"minecraft:chest_minecart"),
			new MinecraftEntity(false,0.98,0.98,0.7,"minecraft:command_block_minecart"),
			new MinecraftEntity(false,0.98,0.98,0.7,"minecraft:furnace_minecart"),
			new MinecraftEntity(false,0.98,0.98,0.7,"minecraft:hopper_minecart"),
			new MinecraftEntity(false,0.98,0.98,0.7,"minecraft:spawner_minecart"),
			new MinecraftEntity(false,0.98,0.98,0.7,"minecraft:tnt_minecart"),
			new MinecraftEntity(false,1.39648,1.39648,1.6,"minecraft:mule"),
			new MinecraftEntity(false,0.9,0.9,1.4,"minecraft:mooshroom"),
			new MinecraftEntity(false,0.6,0.6,0.7,"minecraft:ocelot"),
			new MinecraftEntity(false,0,0,0,"minecraft:painting"),
			new MinecraftEntity(false,1.3,1.3,1.25,"minecraft:panda"),
			new MinecraftEntity(false,0.5,0.5,0.9,"minecraft:parrot"),
			new MinecraftEntity(true,0.9,0.9,0.5,"minecraft:phantom"),
			new MinecraftEntity(false,0.9,0.9,0.9,"minecraft:pig"),
			new MinecraftEntity(true,0.6,0.6,1.95,"minecraft:piglin"),
			new MinecraftEntity(true,0.6,0.6,1.95,"minecraft:piglin_brute"),
			new MinecraftEntity(true,0.6,0.6,1.95,"minecraft:pillager"),
			new MinecraftEntity(false,1.4,1.4,1.4,"minecraft:polar_bear"),
			new MinecraftEntity(true,0.98,0.98,0.98,"minecraft:tnt"),
			new MinecraftEntity(true,0.7,0.7,0.7,"minecraft:pufferfish"),
			new MinecraftEntity(false,0.4,0.4,0.5,"minecraft:rabbit"),
			new MinecraftEntity(true,1.95,1.95,2.2,"minecraft:ravager"),
			new MinecraftEntity(false,0.7,0.7,0.4,"minecraft:salmon"),
			new MinecraftEntity(false,0.9,0.9,1.3,"minecraft:sheep"),
			new MinecraftEntity(true,1.0,1.0,2.0,"minecraft:shulker"),
			new MinecraftEntity(true,0.3125,0.3125,0.3125,"minecraft:shulker_bullet"),
			new MinecraftEntity(true,0.4,0.4,0.3,"minecraft:silverfish"),
			new MinecraftEntity(true,0.6,0.6,1.99,"minecraft:skeleton"),
			new MinecraftEntity(false,1.39648,1.39648,1.6,"minecraft:skeleton_horse"),
			new MinecraftEntity(true,3,3,3,"minecraft:slime"),
			new MinecraftEntity(true,0.3125,0.3125,0.3125,"minecraft:small_fireball"),
			new MinecraftEntity(false,0.7,0.7,1.9,"minecraft:snow_golem"),
			new MinecraftEntity(false,0.25,0.25,0.25,"minecraft:snowball"),
			new MinecraftEntity(false,0.5,0.5,0.5,"minecraft:spectral_arrow"),
			new MinecraftEntity(true,1.4,1.4,0.9,"minecraft:spider"),
			new MinecraftEntity(false,0.8,0.8,0.8,"minecraft:squid"),
			new MinecraftEntity(true,0.6,0.6,1.99,"minecraft:stray"),
			new MinecraftEntity(true,0.9,0.9,1.7,"minecraft:strider"),
			new MinecraftEntity(false,0.25,0.25,0.25,"minecraft:egg"),
			new MinecraftEntity(false,0.25,0.25,0.25,"minecraft:ender_pearl"),
			new MinecraftEntity(false,0.25,0.25,0.25,"minecraft:experience_bottle"),
			new MinecraftEntity(false,0.25,0.25,0.25,"minecraft:potion"),
			new MinecraftEntity(false,0.5,0.5,0.5,"minecraft:trident"),
			new MinecraftEntity(false,0.9,0.9,0.5,"minecraft:trader_llama"),
			new MinecraftEntity(false,0.5,0.5,0.4,"minecraft:tropical_fish"),
			new MinecraftEntity(false,1.2,1.2,0.4,"minecraft:turtle"),
			new MinecraftEntity(true,0,0,0,"minecraft:vex"),
			new MinecraftEntity(false,0.6,0.6,1.95,"minecraft:villager"),
			new MinecraftEntity(true,0.6,0.6,1.95,"minecraft:vindicator"),
			new MinecraftEntity(false,0.6,0.6,1.95,"minecraft:wandering_trader"),
			new MinecraftEntity(true,0.6,0.6,1.95,"minecraft:witch"),
			new MinecraftEntity(true,0.9,0.9,3.5,"minecraft:wither"),
			new MinecraftEntity(true,0.7,0.7,2.4,"minecraft:wither_skeleton"),
			new MinecraftEntity(true,0.3125,0.3125,0.3125,"minecraft:wither_skull"),
			new MinecraftEntity(false,0.6,0.6,0.85,"minecraft:wolf"),
			new MinecraftEntity(true,1.39648,1.39648,1.4,"minecraft:zoglin"),
			new MinecraftEntity(true,0.6,0.6,1.95,"minecraft:zombie"),
			new MinecraftEntity(false,1.39648,1.39648,1.6,"minecraft:zombie_horse"),
			new MinecraftEntity(true,0.6,0.6,1.95,"minecraft:zombie_villager"),
			new MinecraftEntity(true,0.6,0.6,1.95,"minecraft:zombified_piglin"),
			new MinecraftEntity(false,0.6,0.6,1.8,"minecraft:player"),
			new MinecraftEntity(false,0.25,0.25,0.25,"minecraft:fishing_bobber"),
	};
	private static Map<String,MinecraftEntityID> entityMap = new HashMap<>();
	static {
		for(int i = 0;i < entity.length;i++) entityMap.put(entity[i].name, MinecraftEntityID.values()[i]);
	}
	public static MinecraftEntityID getEntityByName(String name) throws Exception{
		if(entityMap.containsKey(name) == false) throw new Exception("No such block name");
		return entityMap.get(name);
	}

}
