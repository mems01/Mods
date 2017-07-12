package rafradek.TF2weapons.projectiles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.weapons.ItemProjectileWeapon;

public class EntityFlameEffect extends Particle {

	protected EntityFlameEffect(World worldIn, double p_i1209_2_, double p_i1209_4_, double p_i1209_6_, double motionX,
			double motionY, double motionZ, int time) {
		super(worldIn, p_i1209_2_, p_i1209_4_, p_i1209_6_);
		this.motionX = motionX;
		this.motionY = motionY;
		this.motionZ = motionZ;
		// this.noClip=false;
		this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
		this.particleMaxAge = time;
		this.setParticleTextureIndex(48);
	}

	@Override
	public void renderParticle(BufferBuilder p_180434_1_, Entity p_180434_2_, float p_180434_3_, float p_180434_4_,
			float p_180434_5_, float p_180434_6_, float p_180434_7_, float p_180434_8_) {
		float f6 = (this.particleAge + p_180434_3_) / this.particleMaxAge;
		this.particleScale = 1.0F + f6 * f6 * 5.5F;
		super.renderParticle(p_180434_1_, p_180434_2_, p_180434_3_, p_180434_4_, p_180434_5_, p_180434_6_, p_180434_7_,
				p_180434_8_);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.particleAlpha *= 0.9f;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float p_70070_1_) {
		return 15728880;
	}

	public static EntityFlameEffect createNewEffect(World world, EntityLivingBase living, float step,boolean heater) {
		if(!heater) {
			double posX = living.posX - MathHelper.cos(living.rotationYawHead / 180.0F * (float) Math.PI) * 0.16F;
			double posY = living.posY + living.getEyeHeight() - 0.1;
			double posZ = living.posZ - MathHelper.sin(living.rotationYawHead / 180.0F * (float) Math.PI) * 0.16F;
			double motionX = -MathHelper.sin(living.rotationYawHead / 180.0F * (float) Math.PI)
					* MathHelper.cos(living.rotationPitch / 180.0F * (float) Math.PI);
			double motionZ = MathHelper.cos(living.rotationYawHead / 180.0F * (float) Math.PI)
					* MathHelper.cos(living.rotationPitch / 180.0F * (float) Math.PI);
			double motionY = (-MathHelper.sin(living.rotationPitch / 180.0F * (float) Math.PI));
			float f2 = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
			float speed = ((ItemProjectileWeapon) living.getHeldItemMainhand().getItem())
					.getProjectileSpeed(living.getHeldItemMainhand(), living);
			motionX = (motionX / f2 + world.rand.nextGaussian() * (world.rand.nextBoolean() ? -1 : 1) * 0.045D) * speed;
			motionY = (motionY / f2 + world.rand.nextGaussian() * (world.rand.nextBoolean() ? -1 : 1) * 0.045D) * speed;
			motionZ = (motionZ / f2 + world.rand.nextGaussian() * (world.rand.nextBoolean() ? -1 : 1) * 0.045D) * speed;
	
			return new EntityFlameEffect(world, posX + motionX * step, posY + motionY * step, posZ + motionZ * step,
					motionX, motionY, motionZ,
					Math.round(3 + (TF2Attribute.getModifier("Flame Range", living.getHeldItemMainhand(), 2, null))));
		}
		else {
			double posX = living.posX;
			double posY = living.posY + 0.1;
			double posZ = living.posZ;
			float angle = living.getRNG().nextFloat()* 2 *(float) Math.PI;
			double motionX = -MathHelper.sin(angle)
					* MathHelper.cos(2 / 180.0F * (float) Math.PI);
			double motionZ = MathHelper.cos(angle)
					* MathHelper.cos(2 / 180.0F * (float) Math.PI);
			double motionY = (-MathHelper.sin(2 / 180.0F * (float) Math.PI));
			float f2 = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
			float speed = 0.9f;
			motionX = (motionX / f2 + world.rand.nextGaussian() * (world.rand.nextBoolean() ? -1 : 1) * 0.045D) * speed;
			motionY = (motionY / f2 + world.rand.nextGaussian() * (world.rand.nextBoolean() ? -1 : 1) * 0.045D) * speed;
			motionZ = (motionZ / f2 + world.rand.nextGaussian() * (world.rand.nextBoolean() ? -1 : 1) * 0.045D) * speed;
	
			return new EntityFlameEffect(world, posX + motionX * step, posY + motionY * step, posZ + motionZ * step,
					motionX, motionY, motionZ, 5);
		}
	}
}
