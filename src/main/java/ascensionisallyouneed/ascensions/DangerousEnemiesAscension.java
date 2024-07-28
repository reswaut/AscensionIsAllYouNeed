package ascensionisallyouneed.ascensions;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import ascensionisallyouneed.patches.MapRoomNodePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;

import java.util.ArrayList;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.currMapNode;
import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.mapRng;

public class DangerousEnemiesAscension extends AbstractAscension {
    public static final String ID = AscensionIsAllYouNeed.makeID(DangerousEnemiesAscension.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] EXTRA_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public int getAscensionLevel() {
        return 22;
    }
    @Override
    public String getAscensionInfo() {
        return TEXT[0];
    }

    @Override
    public void modifyMap(ArrayList<ArrayList<MapRoomNode>> map) {
        ArrayList<MapRoomNode> normalEnemyNodes = new ArrayList<>();
        for (ArrayList<MapRoomNode> mapRoomNodes : map) {
            for (MapRoomNode node : mapRoomNodes) {
                if (node.room instanceof MonsterRoom && !(node.room instanceof MonsterRoomElite)) {
                    normalEnemyNodes.add(node);
                }
            }
        }
        if (normalEnemyNodes.isEmpty()) {
            return;
        }
        int burningCount = (normalEnemyNodes.size() - 1) / mapRng.random(5, 10) + 1;
        while (burningCount > 0) {
            burningCount -= 1;
            MapRoomNode chosenNode = normalEnemyNodes.get(mapRng.random(0, normalEnemyNodes.size() - 1));
            MapRoomNodePatch.IsBurningField.isBurning.set(chosenNode, true);
        }
    }

    @Override
    public int modifyGoldRewards(AbstractRoom room, int gold) {
        if (MapRoomNodePatch.IsBurningField.isBurning.get(AbstractDungeon.getCurrMapNode()) && !Settings.isDailyRun) {
            return Math.round(gold * 1.5F);
        }
        return gold;
    }
}
