package ascensionisallyouneed.ascensions;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import ascensionisallyouneed.patches.MapRoomNodePatch.IsBurningField;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;

import java.util.ArrayList;

public class DangerousEnemiesAscension extends AbstractAscension {
    public static final String ID = AscensionIsAllYouNeed.makeID(DangerousEnemiesAscension.class.getSimpleName());
    private static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    private static final String[] EXTRA_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

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
        int burningCount = (normalEnemyNodes.size() - 1) / AbstractDungeon.mapRng.random(6, 10) + 1;
        while (burningCount > 0) {
            burningCount -= 1;
            MapRoomNode chosenNode = normalEnemyNodes.get(AbstractDungeon.mapRng.random(0, normalEnemyNodes.size() - 1));
            IsBurningField.isBurning.set(chosenNode, true);
        }
    }

    @Override
    public int modifyCardChance(int chance) {
        if (IsBurningField.isBurning.get(AbstractDungeon.getCurrMapNode())) {
            return chance - AscensionIsAllYouNeed.modConfigs.rareCardProb;
        }
        return chance;
    }

    @Override
    public int modifyPotionChance(AbstractRoom room, int chance) {
        if (IsBurningField.isBurning.get(AbstractDungeon.getCurrMapNode())) {
            AbstractRoom.blizzardPotionMod += 10;
            return 120;
        }
        return chance;
    }

    @Override
    public Color getColor() {
        return Settings.GOLD_COLOR;
    }
}
