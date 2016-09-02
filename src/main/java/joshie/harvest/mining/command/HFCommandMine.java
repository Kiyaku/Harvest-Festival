package joshie.harvest.mining.command;

import joshie.harvest.core.commands.AbstractHFCommand;
import joshie.harvest.core.commands.HFCommand;
import joshie.harvest.mining.MiningHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;

@HFCommand
public class HFCommandMine extends AbstractHFCommand {
    @Override
    public String getCommandName() {
        return "mine";
    }

    @Override
    public String getUsage() {
        return "Usage: /hf mine <mineID>";
    }

    @Override
    public boolean execute(MinecraftServer server, ICommandSender sender, String[] parameters) {
        int mineID = 0;
        if (parameters.length == 1) {
            mineID = parseInt(parameters[0]);
        }

        if (sender instanceof Entity) {
            return MiningHelper.teleportToMine((Entity)sender, mineID);
        } else return false;
    }
}