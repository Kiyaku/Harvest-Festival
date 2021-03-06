package joshie.harvest.npc.entity.ai;

import joshie.harvest.npc.entity.EntityNPC;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAITask extends EntityAIBase {
    private EntityNPC npc;

    public EntityAITask(EntityNPC npc) {
        this.npc = npc;
        this.setMutexBits(5);
    }

    @Override
    public boolean shouldExecute() {
        return npc.getTask() != null;
    }

    @Override
    public boolean continueExecuting() {
        if (npc.getTask() != null) {
            if (npc.getTask().shouldTerminate(npc)) {
                npc.setTask(null);
            }
        }

        return false;
    }

    @Override
    public void startExecuting() {
        npc.getTask().execute(npc);
    }
}
