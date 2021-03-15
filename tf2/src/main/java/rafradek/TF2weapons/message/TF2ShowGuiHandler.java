package rafradek.TF2weapons.message;

import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.message.TF2Message.ShowGuiMessage;

public class TF2ShowGuiHandler implements IMessageHandler<TF2Message.ShowGuiMessage, IMessage> {


    @Override
    public IMessage onMessage(final ShowGuiMessage message, MessageContext ctx) {
        if (message.id != 99) {
            FMLNetworkHandler.openGui(ctx.getServerHandler().player, TF2weapons.instance, message.id,
                    ctx.getServerHandler().player.world, 0, 0, 0);
        }
        return null;
    }

}
