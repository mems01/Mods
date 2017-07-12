package rafradek.TF2weapons.projectiles;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2weapons;

public class EntityFlame extends EntityProjectileBase {

	public EntityFlame(World world) {
		super(world);
	}

	public EntityFlame(World p_i1756_1_, EntityLivingBase p_i1756_2_, EnumHand hand) {
		super(p_i1756_1_, p_i1756_2_, hand);
		// TODO Auto-generated constructor stub
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRender3d(double x, double y, double z) {
		return false;
	}

	@Override
	public void onHitGround(int x, int y, int z, RayTraceResult mop) {
		if (!this.world.isRemote && TF2weapons.destTerrain==2
				&& this.world.getBlockState(mop.getBlockPos()).getMaterial().getCanBurn()
				&& this.world.getBlockState(mop.getBlockPos().offset(mop.sideHit)).getMaterial() != Material.FIRE
				&& this.world.getBlockState(mop.getBlockPos().offset(mop.sideHit)).getBlock().isReplaceable(world,
						getPosition().offset(mop.sideHit)))
			this.world.setBlockState(mop.getBlockPos().offset(mop.sideHit), Blocks.FIRE.getDefaultState());
		this.setDead();
	}

	@Override
	public void onHitMob(Entity entityHit, RayTraceResult mop) {
		if (!this.hitEntities.contains(entityHit)) {
			this.hitEntities.add(entityHit);
			int critical = TF2weapons.calculateCritPost(entityHit, shootingEntity, this.getCritical(),
					this.usedWeapon);
			// float distance= (float) new Vec3d(this.shootingEntity.posX,
			// this.shootingEntity.posY,
			// this.shootingEntity.posZ).distanceTo(new Vec3d(mop.hitVec.x,
			// mop.hitVec.y, mop.hitVec.z))+5.028f;
			float dmg = TF2weapons.calculateDamage(entityHit, world, this.shootingEntity, usedWeapon, critical,
					1 + this.ticksExisted / this.getMaxTime());
			// System.out.println("damage: "+dmg);
			// dmg*=ItemUsable.getData(this.usedWeapon).get("Min
			// damage").getDouble()+1-(this.ticksExisted/this.getMaxTime())*ItemUsable.getData(this.usedWeapon).get("Min
			// damage").getDouble();

			if (TF2weapons.dealDamage(entityHit, this.world, this.shootingEntity, this.usedWeapon, critical, dmg,
					TF2weapons.causeBulletDamage(this.usedWeapon, this.shootingEntity, critical, this).setFireDamage())
					&& entityHit.ticksExisted - entityHit.getEntityData().getInteger("LastHitBurn") > 18
					|| entityHit.getEntityData().getInteger("LastHitBurn") > entityHit.ticksExisted) {
				entityHit.getEntityData().setInteger("LastHitBurn", entityHit.ticksExisted);
				TF2weapons.igniteAndAchievement(entityHit, this.shootingEntity, (int) (6 * TF2Attribute.getModifier("Burn Time", this.usedWeapon, 1, shootingEntity) + 1));
			}

		}
	}

	@Override
	public void onUpdate() {
		if (this.isInsideOfMaterial(Material.WATER)) {
			this.setDead();
			return;
		}
		super.onUpdate();
	}

	@Override
	public void spawnParticles(double x, double y, double z) {
		// TODO Auto-generated method stub

	}

	/*
	 * public double getMaxDistance(){ return 6.2865; }
	 */

	@Override
	public int getMaxTime() {
		return Math.round(3 + (TF2Attribute.getModifier("Flame Range", this.usedWeapon, 2f, this.shootingEntity)));
	}

	@Override
	protected float getSpeed() {
		return 1.2570f;
	}

	@Override
	protected double getGravity() {
		return 0;
	}

	@Override
	public float getCollisionSize() {
		return 0.2f + this.ticksExisted * 0.18f;
	}
	
	public boolean isPushable() {
		return false;
	}
}
