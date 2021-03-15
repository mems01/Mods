package rafradek.TF2weapons.client.gui.inventory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import rafradek.TF2weapons.NBTLiterals;
import rafradek.TF2weapons.inventory.ContainerConfigurable;
import rafradek.TF2weapons.inventory.ContainerUpgrades;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.tileentity.IEntityConfigurable;
import rafradek.TF2weapons.tileentity.TileEntityUpgrades;
import rafradek.TF2weapons.util.TF2Util;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.client.gui.GuiTooltip;
import rafradek.TF2weapons.client.gui.TF2GuiConfig;
import rafradek.TF2weapons.common.TF2Attribute;
import rafradek.TF2weapons.common.TF2Attribute.Type;
import rafradek.TF2weapons.entity.building.EntityTeleporter;

public class GuiConfigurable extends GuiContainer {
	public GuiConfigurable(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		// TODO Auto-generated constructor stub
	}

	private static final ResourceLocation UPGRADES_GUI_TEXTURES = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/gui/container/upgrades.png");

	// public ItemStack[] itemsToRender;

	private boolean isScrolling;
	private boolean wasClicking;
	public int firstIndex;
	public float scroll;
	public NBTTagCompound tag;
	public String[] keys = new String[0];
	public int[] types = new int[0];
	public Gui[] fields = new Gui[0];
	public BlockPos pos;

	public GuiConfigurable(InventoryPlayer playerInv, IEntityConfigurable station, World worldIn,
			BlockPos blockPosition) {
		super(new ContainerConfigurable(Minecraft.getMinecraft().player, playerInv, station, worldIn, blockPosition));
		this.xSize = 230;
		this.ySize = 225;
		this.pos = blockPosition;
		// this.itemsToRender=new ItemStack[9];
	}

	@Override
	public void initGui() {
		super.initGui();
		
		int i = 0;
		this.buttonList.clear();
		if (tag != null) {
			keys = new String[tag.getSize()];
			types = new int[tag.getSize()];
			fields = new Gui[tag.getSize()];
			for (String key : tag.getKeySet()) {
				keys[i] = key;
				int type = tag.getTag(key).getId();
				types[i] = type;
				if (type == 8) {
					fields[i] = new GuiTextField(5, fontRenderer, this.guiLeft + 10, this.guiTop + 10 + i * 20, 120, 20);
					((GuiTextField)fields[i]).setText(tag.getString(key));
				}
				
				i++;
			}
		}
		/*for (int x = 0; x < 2; x++)
			for (int y = 0; y < 3; y++) {
				this.buttonList.add(buttons[x * 2 + y * 4] = new GuiButton(x * 2 + y * 4, this.guiLeft + 81 + x * 101,
						this.guiTop + 47 + y * 30, 12, 12, "+"));
				this.buttonList.add(buttons[x * 2 + y * 4 + 1] = new GuiButton(x * 2 + y * 4 + 1,
						this.guiLeft + 94 + x * 101, this.guiTop + 47 + y * 30, 12, 12, "-"));
			}
		this.buttonList.add(refund = new GuiButton(12, this.guiLeft + 123,
						this.guiTop + 121, 100, 20, I18n.format("container.upgrades.refund")));*/
		setButtons();
	}

	public void setButtons() {
		/*
		 * for(int i=0;i<12;i++){
		 * //System.out.println("Buttons: "+buttonsItem[i]+" "+firstIndex);
		 * if(i+firstIndex<TF2CraftingManager.INSTANCE.getRecipeList().size()){
		 * buttonsItem[i].stackToDraw=TF2CraftingManager.INSTANCE.getRecipeList(
		 * ).get(i+firstIndex).getRecipeOutput();
		 * buttonsItem[i].selected=i+firstIndex==((ContainerTF2Workbench)this.
		 * inventorySlots).currentRecipe; } else{
		 * buttonsItem[i].stackToDraw=null; buttonsItem[i].selected=false; } }
		 */
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		boolean flag = Mouse.isButtonDown(0);
		int i = this.guiLeft;
		int j = this.guiTop;
		int k = i + 209;
		int l = j + 30;
		int i1 = k + 14;
		int j1 = l + 96;

		if (!this.wasClicking && flag && mouseX >= k && mouseY >= l && mouseX < i1 && mouseY < j1)
			this.isScrolling = true;

		if (!flag)
			this.isScrolling = false;

		this.wasClicking = flag;

		if (this.isScrolling) {
			int size = this.keys.length;
			if (size >= 6) {
				this.scroll = (mouseY - l - 7.5F) / (j1 - l - 15.0F);
				this.scroll = MathHelper.clamp(this.scroll, 0.0F, 1.0F);
				this.firstIndex = Math.round(this.scroll * (size - 6) / 2) * 2;
				this.setButtons();
			}
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		if (this.tag != null)
		for (int t = 0; t < this.keys.length; t ++) {
			if (fields[t] instanceof GuiTextField) {
				((GuiTextField)fields[t]).drawTextBox();
			}
		}
		/*this.refund.enabled = !this.inventorySlots.inventorySlots.get(0).getStack().isEmpty() 
				&& this.inventorySlots.inventorySlots.get(0).getStack().getTagCompound().getInteger("TotalSpent") > 0;
		
		this.renderHoveredToolTip(mouseX, mouseY);
		for (GuiTooltip tooltip : this.tooltip) {
			tooltip.drawButton(mc, mouseX, mouseY, partialTicks);
		}*/
	}

	@Override
	public void drawHoveringText(List<String> textLines, int x, int y) {
		drawHoveringText(textLines, x, y, fontRenderer);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id < 12)
			this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, button.id + this.firstIndex * 2);
		else if (button.id == 12)
			this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, -1);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		for (int t = 0; t < this.keys.length; t ++) {
			if (fields[t] instanceof GuiTextField) {
				((GuiTextField)fields[t]).mouseClicked(mouseX, mouseY, mouseButton);
			}
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		
		boolean entered = false;
		for (int t = 0; t < this.keys.length; t ++) {
			if (fields[t] instanceof GuiTextField) {
				entered |= ((GuiTextField)fields[t]).textboxKeyTyped(typedChar, keyCode);
			}
		}
		if (!entered) {
			if (keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode))
	        {
				this.sendChanges();
	        }
			super.keyTyped(typedChar, keyCode);
		}
		
		//this.teleportField.setText(Integer.toString(channel));
	}

	public void onGuiClosed()
    {
		super.onGuiClosed();
		
    }
	
	public void sendChanges()
    {
		NBTTagCompound newtag = new NBTTagCompound();
		for (int t = 0; t < this.keys.length; t ++) {
			if (types[t] == 8) {
				newtag.setString(keys[t], ((GuiTextField)fields[t]).getText());
			}
		}
		TF2weapons.network.sendToServer(new TF2Message.GuiConfigMessage(newtag, this.pos));
    }
	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of
	 * the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		for (int i = 0; i < 6; i++) {
			
		}
		this.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 36, this.ySize - 96 + 3,
				4210752);
	}

	/**
	 * Draws the background layer of this container (behind the items).
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(UPGRADES_GUI_TEXTURES);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
		// this.drawTab(0);
		// this.drawTab(1);

		x = this.guiLeft + 210;
		y = this.guiTop + 31;
		int k = y + 96;

		this.drawTexturedModalRect(x, y + (int) ((k - y - 17) * this.scroll), 232, 0, 12, 15);
	}
}