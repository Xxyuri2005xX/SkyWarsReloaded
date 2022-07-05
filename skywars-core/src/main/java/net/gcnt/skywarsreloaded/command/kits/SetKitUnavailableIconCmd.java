package net.gcnt.skywarsreloaded.command.kits;

import net.gcnt.skywarsreloaded.SkyWarsReloaded;
import net.gcnt.skywarsreloaded.command.Cmd;
import net.gcnt.skywarsreloaded.game.kits.SWKit;
import net.gcnt.skywarsreloaded.utils.Item;
import net.gcnt.skywarsreloaded.utils.properties.MessageProperties;
import net.gcnt.skywarsreloaded.wrapper.sender.SWCommandSender;
import net.gcnt.skywarsreloaded.wrapper.entity.SWPlayer;

import java.util.ArrayList;
import java.util.List;

public class SetKitUnavailableIconCmd extends Cmd {

    public SetKitUnavailableIconCmd(SkyWarsReloaded plugin) {
        super(plugin, "skywarskit", "unavailableicon", "skywars.command.kit.unavailable-icon", true, "<kit>", "Set the kit unavailable icon.", "ui");
    }

    @Override
    public boolean run(SWCommandSender sender, String[] args) {
        if (args.length == 0) {
            plugin.getMessages().getMessage(MessageProperties.KITS_ENTER_NAME.toString()).send(sender);
            return true;
        }

        SWPlayer swp = (SWPlayer) sender;
        Item item = swp.getItemInHand(false);
        if (item == null) {
            plugin.getMessages().getMessage(MessageProperties.ERROR_MUST_HOLD_ITEM.toString()).send(sender);
            return true;
        }

        final String kitName = args[0];
        SWKit kit = plugin.getKitManager().getKitByName(kitName);
        if (kit == null) {
            plugin.getMessages().getMessage(MessageProperties.KITS_DOESNT_EXIST.toString()).replace("%kit%", kitName).send(sender);
            return true;
        }

        kit.setUnavailableIcon(item);
        kit.saveData();
        plugin.getMessages().getMessage(MessageProperties.KITS_SET_UNAVAILABLE_ICON.toString())
                .replace("%kit%", kitName)
                .send(sender);
        return true;
    }

    @Override
    public List<String> onTabCompletion(SWCommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> kits = new ArrayList<>();
            plugin.getKitManager().getKits().forEach(kit -> kits.add(kit.getId()));
            return kits;
        }
        return new ArrayList<>();
    }
}
