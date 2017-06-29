package rafradek.TF2weapons.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.weapons.ItemProjectileWeapon;
import rafradek.TF2weapons.weapons.ItemSniperRifle;

public class EntityProjectileSimple extends EntityProjectileBase {

	float damage = -1;
	boolean impact = false;

	public EntityProjectileSimple(World world) {
		super(world);
		this.setSize(0.3F, 0.3F);
	}

	public EntityProjectileSimple(World world, EntityLivingBase living, EnumHand hand) {
		super(world, living, hand);
		this.setSize(0.3F, 0.3F);
		if(ItemFromData.getData(this.usedWeapon).getString(PropertyType.PROJECTILE).equals("repairclaw"))
			this.setType(0);
		if(ItemFromData.getData(this.usedWeapon).getString(PropertyType.PROJECTILE).equals("syringe"))
			this.setType(1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onHitGround(int x, int y, int z, RayTraceResult mop) {
		if (!this.world.isRemote) {
			this.impact = true;

			if (ItemFromData.getData(this.usedWeapon).hasProperty(PropertyType.HIT_SOUND)) {
				SoundEvent event = ItemFromData.getData(this.usedWeapon).hasProperty(PropertyType.HIT_WORLD_SOUND)
						? ItemFromData.getSound(this.usedWeapon, PropertyType.HIT_WORLD_SOUND)
						: ItemFromData.getSound(this.usedWeapon, PropertyType.HIT_SOUND);
				this.playSound(event, 1.3f, 1f);
			}

			if (TF2Attribute.getModifier("Destroy Block", this.usedWeapon, 0, shootingEntity) > 0) {

				float damage = this.damage;
				if (damage == -1) {
					damage = TF2weapons.calculateDamage(TF2weapons.dummyEnt, this.world, this.shootingEntity,
							this.usedWeapon, this.getCritical(),
							(float) this.shootingEntity.getPositionVector().distanceTo(mop.hitVec));
					if (this.usedWeapon.getItem() instanceof ItemSniperRifle)
						damage *= 2.52f;
					damage *= TF2Attribute.getModifier("Destroy Block", this.usedWeapon, 0, this.shootingEntity);
				}
				this.damage = TF2weapons.damageBlock(mop.getBlockPos(), this.shootingEntity, this.world,
						this.usedWeapon, this.getCritical(), damage, null, null);
				if (this.damage <= 0)
					this.setDead();
			} else
				this.setDead();
		}
	}

	@Override
	public void onHitMob(Entity entityHit, RayTraceResult mop) {
		if (!this.world.isRemote) {
			if (!this.hitEntities.contains(entityHit)) {
				this.hitEntities.add(entityHit);
				float distance = (float) TF2weapons.getDistanceBox(this.shootingEntity, entityHit.posX, entityHit.posY, entityHit.posZ, entityHit.width+0.1, entityHit.height+0.1);
				int critical = TF2weapons.calculateCritPost(entityHit, shootingEntity, this.getCritical(),
						this.usedWeapon);
				float dmg = TF2weapons.calculateDamage(entityHit, world, this.shootingEntity, usedWeapon, critical,
						distance);
				boolean proceed=((ItemProjectileWeapon)this.usedWeapon.getItem()).onHit(usedWeapon, this.shootingEntity, entityHit, dmg, critical);
				if(!proceed || TF2weapons.dealDamage(entityHit, this.world, this.shootingEntity, this.usedWeapon, critical, dmg,
						TF2weapons.causeBulletDamage(this.usedWeapon, this.shootingEntity, critical, this))) {
					if (TF2Attribute.getModifier("Penetration", this.usedWeapon, 0, shootingEntity) == 0)
						this.setDead();
				}
			}
		}
	}

	@Override
	public void spawnParticles(double x, double y, double z) {
		// TODO Auto-generated method stub

	}

	public boolean isPushable() {
		return this.getType()!=1;
	}
}
