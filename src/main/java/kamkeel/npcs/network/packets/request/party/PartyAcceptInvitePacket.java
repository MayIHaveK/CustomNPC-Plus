package kamkeel.npcs.network.packets.request.party;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import kamkeel.npcs.network.AbstractPacket;
import kamkeel.npcs.network.PacketChannel;
import kamkeel.npcs.network.PacketHandler;
import kamkeel.npcs.network.enums.EnumRequestPacket;
import kamkeel.npcs.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.PlayerData;

import java.io.IOException;
import java.util.UUID;

public final class PartyAcceptInvitePacket extends AbstractPacket {
    public static final String packetName = "Request|PartyAcceptInvite";

    private String uuid;

    public PartyAcceptInvitePacket() {
    }

    public PartyAcceptInvitePacket(String playerUUID) {
        this.uuid = playerUUID;
    }

    @Override
    public Enum getType() {
        return EnumRequestPacket.PartyAcceptInvite;
    }

    @Override
    public PacketChannel getChannel() {
        return PacketHandler.REQUEST_PACKET;
    }


    @SideOnly(Side.CLIENT)
    @Override
    public void sendData(ByteBuf out) throws IOException {
        ByteBufUtils.writeString(out, this.uuid);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        PlayerData playerData = PlayerData.get(player);
        String uuidString = ByteBufUtils.readString(in);
        if (uuidString != null) {
            UUID uuid = UUID.fromString(uuidString);
            playerData.acceptInvite(uuid);
        }
    }
}
