package rafradek.TF2weapons.characters;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2Sounds;
import rafradek.TF2weapons.TF2weapons;

public class EntityScout extends EntityTF2Character {
	public boolean doubleJumped;
	private int jumpDelay;

	public EntityScout(World par1World) {
		super(par1World);
		if (this.attack != null) {
			this.attack.setDodge(true, true);
			this.attack.jump = true;
			this.attack.jumprange = 40;
			this.attack.dodgeSpeed = 1.25f;
		}
		this.ammoLeft = 24;
		this.experienceValue = 15;

		// this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND,
		// ItemUsable.getNewStack("Minigun"));

	}

	/*
	 * protected void addWeapons() {
	 * this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND,
	 * ItemFromData.getNewStack("scattergun")); }
	 */
	@Override
	protected ResourceLocation getLootTable() {
		return TF2weapons.lootScout;
	}
	
	public float[] getDropChance() {
		return new float[] { 0.1f, 0.12f, 0.11f };
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.364D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (jumpDelay > 0 && --jumpDelay == 0)
			this.jump();
		if (this.onGround)
			this.doubleJumped = false;

	}

	@Override
	protected void jump() {
		super.jump();
		/*
		 * double speed=Math.sqrt(motionX*motionX+motionZ*motionZ);
		 * if(speed!=0){
		 * 
		 * double speedMultiply=this.getEntityAttribute(SharedMonsterAttributes.
		 * MOVEMENT_SPEED).getAttributeValue()/speed;
		 * this.motionX*=speedMultiply; this.motionZ*=speedMultiply; }
		 */
		this.motionX = (-MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI));
		this.motionZ = (MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI));
		float f2 = (float) (MathHelper.sqrt(motionX * motionX + motionZ * motionZ)
				* this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue());
		this.motionX *= f2;
		this.motionZ *= f2;
		this.fallDistance = -3.0F;
		if (!this.doubleJumped && this.jump) {
			this.doubleJumped = true;
			this.jumpDelay = 8;
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return TF2Sounds.MOB_SCOUT_SAY;
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	@Override
	protected SoundEvent getHurtSound() {
		return TF2Sounds.MOB_SCOUT_HURT;
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	@Override
	protected SoundEvent getDeathSound() {
		return TF2Sounds.MOB_SCOUT_DEATH;
	}

	/*
	 * public int getAttackStrength(Entity par1Entity) { ItemStack itemstack =
	 * this.getHeldItem(EnumHand.MAIN_HAND); float f =
	 * (float)(this.getMaxHealth() - this.getHealth()) /
	 * (float)this.getMaxHealth(); int i = 4 + MathHelper.floor_float(f * 4.0F);
	 * 
	 * if (item!stack.isEmpty()) { i += itemstack.getDamageVsEntity(this); }
	 * 
	 * return i; }
	 */

	/**
	 * Plays step sound at given x, y, z for the entity
	 */
	@Override
	protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
		if (this.rand.nextFloat() < 0.60f + p_70628_2_ * 0.30f)
			this.entityDropItem(ItemFromData.getNewStack("bonk"), 0);
		if (this.rand.nextFloat() < 0.12f + p_70628_2_ * 0.075f)
			this.entityDropItem(ItemFromData.getNewStack("scattergun"), 0);
		if (this.rand.nextFloat() < 0.15f + p_70628_2_ * 0.075f)
			this.entityDropItem(ItemFromData.getNewStack("pistol"), 0);
	}

	@Override
	public float getMotionSensitivity() {
		return 0;
	}
}
