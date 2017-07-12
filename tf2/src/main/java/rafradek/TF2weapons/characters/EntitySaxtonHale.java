package rafradek.TF2weapons.characters;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2Achievements;
import rafradek.TF2weapons.TF2DamageSource;
import rafradek.TF2weapons.TF2Sounds;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.building.EntityBuilding;
import rafradek.TF2weapons.characters.ai.EntityAINearestChecked;
import rafradek.TF2weapons.characters.ai.EntityAISeek;
import rafradek.TF2weapons.weapons.ItemKnife;
import rafradek.TF2weapons.weapons.ItemWeapon;

public class EntitySaxtonHale extends EntityCreature implements INpc, IMerchant {

	public EntityPlayer trader;
	public MerchantRecipeList tradeOffers;
	public float rage;
	public boolean hostile;
	public boolean superJump;
	public int jumpCooldown;
	public boolean endangered;

	private final BossInfoServer bossInfo = (new BossInfoServer(this.getDisplayName(), BossInfo.Color.PURPLE,
			BossInfo.Overlay.PROGRESS));

	public EntitySaxtonHale(World worldIn) {
		super(worldIn);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.1F, false));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityTF2Character.class, 8.0F));
		this.tasks.addTask(7, new EntityAISeek(this));
		this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, true));
		this.experienceValue = 1500;
	}

	@Override
	public void setCustomer(EntityPlayer player) {
		this.trader = player;
	}

	@Override
	public EntityPlayer getCustomer() {
		// TODO Auto-generated method stub
		return trader;
	}

	@Override
	public MerchantRecipeList getRecipes(EntityPlayer player) {
		// TODO Auto-generated method stub
		if (this.tradeOffers == null)
			makeOffers();
		return tradeOffers;
	}

	@Override
	protected ResourceLocation getLootTable() {
		return TF2weapons.lootHale;
	}

	public void makeOffers() {
		this.tradeOffers = new MerchantRecipeList();
		this.tradeOffers.add(new MerchantRecipe(new ItemStack(TF2weapons.itemTF2, 5, 2), ItemStack.EMPTY,
				new ItemStack(TF2weapons.itemTF2, 1, 7), 0, 100));
		int weaponCount = 13 + this.rand.nextInt(2);
		for (int i = 0; i < weaponCount; i++) {
			ItemStack item = ItemFromData.getRandomWeapon(this.rand, ItemFromData.VISIBLE_WEAPON);
			int cost = ItemFromData.getData(item).getInt(PropertyType.COST);
			ItemStack ingot = new ItemStack(TF2weapons.itemTF2, cost / 9, 2);
			ItemStack nugget = new ItemStack(TF2weapons.itemTF2, cost % 9, 6);
			this.tradeOffers.add(new MerchantRecipe(ingot.getCount() > 0 ? ingot : nugget,
					nugget.getCount() > 0 && ingot.getCount() > 0 ? nugget : ItemStack.EMPTY, item, 0, 100));
		}
		int hatCount = 4 + this.rand.nextInt(6);

		for (int i = 0; i < hatCount; i++) {

			ItemStack item = ItemFromData.getRandomWeaponOfClass("cosmetic", this.rand, false);
			int cost = ItemFromData.getData(item).getInt(PropertyType.COST);
			ItemStack ingot = new ItemStack(TF2weapons.itemTF2, cost / 9, 2);
			ItemStack nugget = new ItemStack(TF2weapons.itemTF2, cost % 9, 6);
			this.tradeOffers.add(new MerchantRecipe(ingot.getCount() > 0 ? ingot : nugget,
					nugget.getCount() > 0 ? nugget : ItemStack.EMPTY, item, 0, 100));
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isEntityInvulnerable(source))
			return false;
		else if (super.attackEntityFrom(source, amount)) {
			if (source == DamageSource.DROWN || source == DamageSource.LAVA) {
				this.superJump = true;
				this.jump();
			}
			Entity entity = source.getTrueSource();
			if (entity instanceof EntityPlayerMP)
				this.bossInfo.addPlayer((EntityPlayerMP) entity);
			this.rage += amount / 100f;
			if (source instanceof TF2DamageSource && ((TF2DamageSource) source).getCritical() == 2
					&& !((TF2DamageSource) source).getWeapon().isEmpty()
					&& ((TF2DamageSource) source).getWeapon().getItem() instanceof ItemKnife)
				this.playSound(TF2Sounds.MOB_SAXTON_STAB, 2.5F, 1f);
			return this.getRidingEntity() != entity && this.getRidingEntity() != entity ? true : true;

		} else
			return false;
	}

	public void setHostile() {
		this.targetTasks.addTask(1, new EntityAINearestChecked(this, EntityLivingBase.class, true, false,
				new Predicate<EntityLivingBase>() {

					@Override
					public boolean apply(EntityLivingBase input) {
						// TODO Auto-generated method stub
						return input instanceof EntityPlayer || input instanceof EntityTF2Character;
					}

				}, true));
		this.hostile = true;
	}

	@Override
	public void setRecipes(MerchantRecipeList recipeList) {
		this.tradeOffers = recipeList;
	}

	@Override
	public void useRecipe(MerchantRecipe recipe) {
		// TODO Auto-generated method stub

	}

	@Override
	public void verifySellingItem(ItemStack stack) {
		// TODO Auto-generated method stub
		if(!stack.isEmpty() && stack.hasTagCompound())
			stack.getTagCompound().setBoolean("Bought", true);
		/*if (this.trader != null && !stack.isEmpty() &&stack.getItem() instanceof ItemWeapon)
			this.trader.addStat(TF2Achievements.MANN_CO_MADE);*/

	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (!this.world.isRemote) {
			this.jumpCooldown--;

			if (this.getAttackTarget() == null)
				this.heal(0.35f);

			this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
			// System.out.println("Has path: "+this.getNavigator().noPath());
			List<AxisAlignedBB> boxes = this.world.getCollisionBoxes(this, getEntityBoundingBox().grow(1, 0, 1));
			boolean obscuredView = false;
			for (AxisAlignedBB box : boxes)
				if (box.calculateIntercept(this.getPositionVector().addVector(0, this.getEyeHeight(), 0),
						this.getPositionVector().add(this.getVectorForRotation(0, this.rotationYawHead).addVector(0,
								this.getEyeHeight(), 0))) != null) {
					obscuredView = true;
					break;
				}

			if (this.getAttackTarget() != null && this.getAttackTarget().isEntityAlive() && obscuredView) {
				this.superJump = true;
				this.jump();
			}
			if (this.rage > 1) {
				List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class,
						this.getEntityBoundingBox().grow(12, 12, 12), new Predicate<EntityLivingBase>() {

							@Override
							public boolean apply(EntityLivingBase input) {
								// TODO Auto-generated method stub
								return !(input instanceof EntitySaxtonHale)
										&& !(input instanceof EntityPlayer && ((EntityPlayer) input).isCreative())
										&& input.getDistanceSqToEntity(EntitySaxtonHale.this) < 144;
							}

						});
				if (!list.isEmpty()) {
					this.rage = 0;
					this.playSound(TF2Sounds.MOB_SAXTON_RAGE, 2.5F, 1F);
					for (EntityLivingBase living : list)
						TF2weapons.stun(living, 160, false);
					this.superJump = true;
					this.jump();
				}
			}
		}
	}

	@Override
	public void setAttackTarget(EntityLivingBase living) {
		super.setAttackTarget(living);
		if (!endangered) {
			this.endangered = true;
			this.playSound(TF2Sounds.MOB_SAXTON_START, 2F, 1F);
		}
	}

	@Override
	protected SoundEvent getSwimSound() {
		return SoundEvents.ENTITY_HOSTILE_SWIM;
	}

	@Override
	protected SoundEvent getSplashSound() {
		return SoundEvents.ENTITY_HOSTILE_SPLASH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_HOSTILE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return TF2Sounds.MOB_SAXTON_DEATH;
	}

	@Override
	public boolean isNonBoss() {
		return !hostile;
	}

	@Override
	public Team getTeam() {
		return this.hostile ? this.world.getScoreboard().getTeam("TF2Bosses") : null;
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
		super.fall(distance, 0);
	}

	@Override
	protected float getJumpUpwardsMotion() {
		if (superJump && jumpCooldown <= 0)
			return 2.7F;
		return 0.7F;
	}

	@Override
	public void jump() {
		/*
		 * if(this.getAttackTarget()!=null&&this.getAttackTarget().posY-this.
		 * posY>=3){ this.superJump=true; }
		 */
		if (superJump && jumpCooldown <= 0)
			this.playSound(TF2Sounds.MOB_SAXTON_JUMP, 2F, 1F);

		if (this.onGround || this.jumpCooldown <= 0) {
			this.motionY = 0;
			super.jump();
		}

		if (superJump)
			this.superJump = false;

		this.jumpCooldown = 20;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(50.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1000.0D);
		this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.8D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.364D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(20D);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		float f = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
		int i = 0;

		if (entityIn instanceof EntityLivingBase) {
			f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(),
					((EntityLivingBase) entityIn).getCreatureAttribute());
			i += EnchantmentHelper.getKnockbackModifier(this);
		}

		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), f);

		if (flag) {
			if (i > 0 && entityIn instanceof EntityLivingBase) {
				((EntityLivingBase) entityIn).knockBack(this, i * 0.5F, MathHelper.sin(this.rotationYaw * 0.017453292F),
						(-MathHelper.cos(this.rotationYaw * 0.017453292F)));
				this.motionX *= 0.6D;
				this.motionZ *= 0.6D;
			}

			int j = EnchantmentHelper.getFireAspectModifier(this);

			if (j > 0)
				entityIn.setFire(j * 4);

			if (entityIn instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer) entityIn;
				ItemStack itemstack = this.getHeldItemMainhand();
				ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : null;

				if (!itemstack.isEmpty() && itemstack1 != null && itemstack.getItem() instanceof ItemAxe
						&& itemstack1.getItem() == Items.SHIELD) {
					float f1 = 0.25F + EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;

					if (this.rand.nextFloat() < f1) {
						entityplayer.getCooldownTracker().setCooldown(Items.SHIELD, 100);
						this.world.setEntityState(entityplayer, (byte) 30);
					}
				}
			}

			this.applyEnchantments(this, entityIn);

			if (entityIn instanceof EntityLivingBase && ((EntityLivingBase) entityIn).getHealth() <= 0)
				if (entityIn instanceof EntityBuilding)
					this.playSound(TF2Sounds.MOB_SAXTON_DESTROY, 2.2F, 1f);
				else
					this.playSound(TF2Sounds.MOB_SAXTON_KILL, 2.2F, 1f);
		}

		return flag;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readEntityFromNBT(par1NBTTagCompound);
		if (par1NBTTagCompound.getBoolean("Hostile"))
			this.setHostile();
		if (par1NBTTagCompound.hasKey("Offers")) {
			this.tradeOffers = new MerchantRecipeList();
			this.tradeOffers.readRecipiesFromTags(par1NBTTagCompound.getCompoundTag("Offers"));
		}
		this.endangered = par1NBTTagCompound.getBoolean("Endangered");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		if (this.tradeOffers != null)
			par1NBTTagCompound.setTag("Offers", this.tradeOffers.getRecipiesAsTags());
		par1NBTTagCompound.setBoolean("Hostile", hostile);
		par1NBTTagCompound.setBoolean("Endangered", this.endangered);
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		if (!(player.getHeldItemMainhand() != null
				&& player.getHeldItemMainhand().getItem() instanceof ItemMonsterPlacerPlus)
				&& this.getAttackTarget() == null && this.isEntityAlive() && !this.isTrading() && !this.isChild()
				&& !player.isSneaking()) {
			if (this.world.isRemote && player.getTeam() == null && !player.isCreative())
				ClientProxy.displayScreenJoinTeam();
			else if (!this.world.isRemote && (player.getTeam() != null || player.isCreative())
					&& (this.tradeOffers == null || !this.tradeOffers.isEmpty())) {
				this.setCustomer(player);
				player.displayVillagerTradeGui(this);
			}

			player.addStat(StatList.TALKED_TO_VILLAGER);
			return true;
		} else
			return super.processInteract(player, hand);
	}

	public boolean isTrading() {
		// TODO Auto-generated method stub
		return this.trader != null;
	}

	@Override
	public void addTrackingPlayer(EntityPlayerMP player) {
		super.addTrackingPlayer(player);
		if (this.hostile)
			this.bossInfo.addPlayer(player);
	}

	@Override
	public void removeTrackingPlayer(EntityPlayerMP player) {
		super.removeTrackingPlayer(player);
		this.bossInfo.removePlayer(player);
	}

	@Override
	public World getWorld() {
		// TODO Auto-generated method stub
		return this.world;
	}

	@Override
	public BlockPos getPos() {
		// TODO Auto-generated method stub
		return this.getPos();
	}
}
