package noppes.npcs.client.gui.global;

import kamkeel.npcs.network.PacketClient;
import kamkeel.npcs.network.packets.request.bank.BankGetPacket;
import kamkeel.npcs.network.packets.request.bank.BankRemovePacket;
import kamkeel.npcs.network.packets.request.bank.BankSavePacket;
import kamkeel.npcs.network.packets.request.bank.BanksGetPacket;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumScrollData;
import noppes.npcs.containers.ContainerManageBanks;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class GuiNPCManageBanks extends GuiContainerNPCInterface2 implements IScrollData, ICustomScrollListener, ITextfieldListener, IGuiData {
    private GuiCustomScroll scroll;
    private HashMap<String, Integer> data = new HashMap<String, Integer>();
    private ContainerManageBanks container;
    private Bank bank = new Bank();
    private String selected = null;
    private String search = "";

    public GuiNPCManageBanks(EntityNPCInterface npc, ContainerManageBanks container) {
        super(npc, container);
        this.container = container;
        drawDefaultBackground = false;
        PacketClient.sendClient(new BanksGetPacket());
        setBackground("npcbanksetup.png");
        ySize = 200;
    }

    public void initGui() {
        super.initGui();

        this.addButton(new GuiNpcButton(6, guiLeft + 340, guiTop + 10, 45, 20, "gui.add"));
        this.addButton(new GuiNpcButton(7, guiLeft + 340, guiTop + 32, 45, 20, "gui.remove"));
        if (scroll == null)
            scroll = new GuiCustomScroll(this, 0, 0);
        scroll.setSize(160, 180);
        scroll.guiLeft = guiLeft + 174;
        scroll.guiTop = guiTop + 8;
        addScroll(scroll);

        for (int i = 0; i < 6; i++) {
            int x = guiLeft + 6;
            int y = (guiTop + 36) + (i * 22);
            addButton(new GuiNpcButton(i, x + 50, y, 80, 20, new String[]{"Can Upgrade", "Can't Upgrade", "Upgraded"}, 0));
            getButton(i).setEnabled(false);
        }

        addTextField(new GuiNpcTextField(0, this, this.fontRendererObj, guiLeft + 8, guiTop + 8, 160, 16, ""));
        getTextField(0).setMaxStringLength(20);

        addTextField(new GuiNpcTextField(1, this, this.fontRendererObj, guiLeft + 10, guiTop + 80, 16, 16, ""));
        getTextField(1).integersOnly = true;
        getTextField(1).setMaxStringLength(1);

        addTextField(new GuiNpcTextField(2, this, this.fontRendererObj, guiLeft + 10, guiTop + 110, 16, 16, ""));
        getTextField(2).integersOnly = true;
        getTextField(2).setMaxStringLength(1);

        addTextField(new GuiNpcTextField(33, this, fontRendererObj, guiLeft + 174, guiTop + 8 + 3 + 180, 160, 20, search));
    }

    public void drawScreen(int x, int y, float f) {
        super.drawScreen(x, y, f);
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;
        if (button.id == 6) {
            save();
            scroll.clear();
            String name = "New";
            while (data.containsKey(name))
                name += "_";
            Bank bank = new Bank();
            bank.name = name;

            NBTTagCompound compound = new NBTTagCompound();
            bank.writeEntityToNBT(compound);
            PacketClient.sendClient(new BankSavePacket(compound));
        } else if (button.id == 7) {
            if (data.containsKey(scroll.getSelected()))
                PacketClient.sendClient(new BankRemovePacket(data.get(selected)));
        } else if (button.id >= 0 && button.id < 6) {
            bank.slotTypes.put(button.id, button.getValue());
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        fontRendererObj.drawString("Tab Cost", 23, 28, CustomNpcResourceListener.DefaultTextColor);
        fontRendererObj.drawString("Upg. Cost", 123, 28, CustomNpcResourceListener.DefaultTextColor);
        fontRendererObj.drawString("Start", 6, 70, CustomNpcResourceListener.DefaultTextColor);
        fontRendererObj.drawString("Max", 9, 100, CustomNpcResourceListener.DefaultTextColor);
    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        Bank bank = new Bank();
        bank.readEntityFromNBT(compound);
        this.bank = bank;

        if (bank.id == -1) {
            getTextField(0).setText("");
            getTextField(1).setText("");
            getTextField(2).setText("");

            for (int i = 0; i < 6; i++) {
                getButton(i).setDisplay(0);
                getButton(i).setEnabled(false);
            }
        } else {
            getTextField(0).setText(bank.name);
            getTextField(1).setText(Integer.toString(bank.startSlots));
            getTextField(2).setText(Integer.toString(bank.maxSlots));

            for (int i = 0; i < 6; i++) {
                int type = 0;
                if (bank.slotTypes.containsKey(i))
                    type = bank.slotTypes.get(i);
                getButton(i).setDisplay(type);
                getButton(i).setEnabled(true);
            }
        }
        setSelected(bank.name);
    }

    public void keyTyped(char c, int i) {
        super.keyTyped(c, i);
        if (getTextField(33) != null) {
            if (getTextField(33).isFocused()) {
                if (search.equals(getTextField(33).getText()))
                    return;
                search = getTextField(33).getText().toLowerCase();
                scroll.setList(getSearchList());
            }
        }
    }

    private List<String> getSearchList() {
        if (search.isEmpty()) {
            return new ArrayList<String>(this.data.keySet());
        }
        List<String> list = new ArrayList<String>();
        for (String name : this.data.keySet()) {
            if (name.toLowerCase().contains(search))
                list.add(name);
        }
        return list;
    }

    @Override
    public void setData(Vector<String> list, HashMap<String, Integer> data, EnumScrollData type) {
        String name = scroll.getSelected();
        this.data = data;
        scroll.setList(getSearchList());

        if (name != null)
            scroll.setSelected(name);
    }

    @Override
    public void setSelected(String selected) {
        this.selected = selected;
        scroll.setSelected(selected);
    }

    @Override
    public void customScrollClicked(int i, int j, int k, GuiCustomScroll guiCustomScroll) {
        if (guiCustomScroll.id == 0) {
            save();
            selected = scroll.getSelected();
            PacketClient.sendClient(new BankGetPacket(data.get(selected)));
        }
    }

    @Override
    public void save() {
        if (selected != null && data.containsKey(selected) && bank != null) {
            NBTTagCompound compound = new NBTTagCompound();
            bank.currencyInventory = container.bank.currencyInventory;
            bank.upgradeInventory = container.bank.upgradeInventory;
            bank.writeEntityToNBT(compound);
            PacketClient.sendClient(new BankSavePacket(compound));
        }
    }

    @Override
    public void unFocused(GuiNpcTextField guiNpcTextField) {
        if (bank.id != -1) {
            if (guiNpcTextField.id == 0) {
                String name = guiNpcTextField.getText();
                if (!name.isEmpty() && !data.containsKey(name)) {
                    String old = bank.name;
                    data.remove(bank.name);
                    bank.name = name;
                    data.put(bank.name, bank.id);
                    selected = name;
                    scroll.replace(old, bank.name);
                }
            } else if (guiNpcTextField.id == 1 || guiNpcTextField.id == 2) {
                int num = 1;

                if (!guiNpcTextField.isEmpty())
                    num = guiNpcTextField.getInteger();

                if (num > 6)
                    num = 6;

                if (num < 0)
                    num = 0;

                if (guiNpcTextField.id == 1) {
                    bank.startSlots = num;
                } else if (guiNpcTextField.id == 2) {
                    bank.maxSlots = num;
                }

                if (bank.startSlots > bank.maxSlots)
                    bank.maxSlots = bank.startSlots;

                guiNpcTextField.setText(Integer.toString(num));
            }
        }
    }

}
