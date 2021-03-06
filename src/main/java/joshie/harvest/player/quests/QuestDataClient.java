package joshie.harvest.player.quests;

import joshie.harvest.api.npc.INPC;
import joshie.harvest.api.quests.Quest.EventType;
import joshie.harvest.npc.entity.EntityNPC;
import joshie.harvest.api.quests.Quest;
import joshie.harvest.quests.packet.PacketQuestCompleted;
import joshie.harvest.quests.packet.PacketQuestStart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashSet;

import static joshie.harvest.core.network.PacketHandler.sendToServer;

@SideOnly(Side.CLIENT)
public class QuestDataClient extends QuestData {
    private HashSet<Quest> available = new HashSet<>();
    
    //Adds a quest to the current list
    @Override
    public void addAsCurrent(Quest quest) {
        current.add(quest);
        for (EventType handled: quest.getHandledEvents()) {
            eventHandlers.get(handled).add(quest);
        }
    }

    @Override
    public void markCompleted(Quest quest, boolean sendPacket) {
        markCompleted(quest);
        sendToServer(new PacketQuestCompleted(quest));
    }
    
    //Removes the quest from the current and available lists
    public void markCompleted(Quest quest) {
        if (!quest.isRepeatable()) {
            available.remove(quest);
        }

        current.remove(quest);
        for (EventType handled: quest.getHandledEvents()) {
            eventHandlers.get(handled).remove(quest);
        }
    }

    @Override
    public void setAvailable(Quest quest) {
        available.add(quest);
    }

    //Called to change the current quests stage
    @Override
    public void setStage(Quest quest, int stage) {
        Quest q = getAQuest(quest);
        if (q != null) q.setStage(stage);
    }

    private String getScript(Quest quest, EntityPlayer player, EntityNPC entity) {
        String script = quest.getScript(player, entity, entity.getNPC());
        return script == null ? null : quest.getLocalized(script);
    }

    //Returns a single lined script
    public Quest getSelection(EntityNPC npc) {
        if (current != null) {
            for (Quest q : current) {
                if (handlesScript(q, npc.getNPC())) {
                    if (q.getSelection(npc.getNPC()) != null) return q;
                }
            }
        }

        return null;
    }

    @Override
    public String getScript(EntityPlayer player, EntityNPC npc) {
        if (current != null) {
            for (Quest q : current) {
                if (handlesScript(q, npc.getNPC())) {
                    String script = getScript(q, player, npc);
                    if (script != null) return script;
                }
            }
        }

        //If we didn't return a current quest, search for a new one
        if (current.size() < 10) {
            for (Quest q : available) {
                if (!current.contains(q) && handlesScript(q, npc.getNPC())) {
                    try {
                        Quest quest = q.getClass().newInstance().setRegistryName(q.getRegistryName()).setStage(0); //Set the current quest to your new
                        current.add(quest);
                        for (EventType handled: quest.getHandledEvents()) {
                            eventHandlers.get(handled).add(quest);
                        }

                        sendToServer(new PacketQuestStart(q));
                        String script = getScript(q, player, npc);
                        if (script != null) return script;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return null;
    }

    private boolean handlesScript(Quest quest, INPC npc) {
        INPC[] npcs = quest.getNPCs();
        if (npcs == null) return false;
        else {
            for (INPC n: npcs) {
                if (n.equals(npc)) return true;
            }
        }

        return false;
    }
}
