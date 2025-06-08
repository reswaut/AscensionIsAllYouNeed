package ascensionisallyouneed.ascensions;

import ascensionisallyouneed.AscensionIsAllYouNeed;
import ascensionisallyouneed.patches.MapRoomNodePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;

import java.util.ArrayList;

import static com.megacrit.cardcrawl.dungeons.AbstractDungeon.mapRng;

public class UnfavorableMapsAscension extends AbstractAscension {
    public static final String ID = AscensionIsAllYouNeed.makeID(UnfavorableMapsAscension.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] EXTRA_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public int getAscensionLevel() {
        return 26;
    }
    @Override
    public String getAscensionInfo() {
        return TEXT[0];
    }

    @Override
    public void modifyMap(ArrayList<ArrayList<MapRoomNode>> map) {
        int normalEnemyCount = 0, burningCount = 0;
        ArrayList<ArrayList<MapRoomNode>> normalEnemyNodesByWidth = new ArrayList<>();
        for (ArrayList<MapRoomNode> mapRoomNodes : map) {
            int width = 0;
            for (MapRoomNode node : mapRoomNodes) {
                if (node.room instanceof MonsterRoom && !(node.room instanceof MonsterRoomElite)) {
                    ++width;
                    if (MapRoomNodePatch.IsBurningField.isBurning.get(node)) {
                        ++burningCount;
//                        MapRoomNodePatch.IsBurningField.isBurning.set(node, false);
                    }
                }
            }
            while (normalEnemyNodesByWidth.size() <= width) {
                normalEnemyNodesByWidth.add(new ArrayList<>());
            }
            for (MapRoomNode node : mapRoomNodes) {
                if (node.room instanceof MonsterRoom && !(node.room instanceof MonsterRoomElite)) {
                    normalEnemyNodesByWidth.get(width).add(node);
                    normalEnemyCount += 1;
                }
            }
        }
        if (normalEnemyCount <= 0) {
            return;
        }
        burningCount = Math.max(burningCount / 2, 1);
        int maxWidth = normalEnemyNodesByWidth.size() - 1;
        for (int i = 1; i < maxWidth; ++i) {
            for (int j = 1; j < normalEnemyNodesByWidth.get(i).size(); ++j) {
                int index = mapRng.random(0, j);
                if (index != j) {
                    MapRoomNode node = normalEnemyNodesByWidth.get(i).get(j);
                    normalEnemyNodesByWidth.get(i).set(j, normalEnemyNodesByWidth.get(i).get(index));
                    normalEnemyNodesByWidth.get(i).set(index, node);
                }
            }
            for (MapRoomNode node : normalEnemyNodesByWidth.get(i)) {
                if (mapRng.random(1, maxWidth) > i) {
                    MapRoomNodePatch.IsBurningField.isBurning.set(node, true);
                    burningCount -= 1;
                    if (burningCount <= 0) {
                        break;
                    }
                }
            }
            if (burningCount <= 0) {
                break;
            }
        }
    }

    @Override
    public float modifyShopRoomChance(float chance) {
        return chance * 0.8F;
    }

    @Override
    public float modifyRestRoomChance(float chance) {
        return chance * 0.8F;
    }

    @Override
    public float modifyEliteRoomChance(float chance) {
        return chance * 1.2F;
    }

    @Override
    public float modifyEventRoomChance(float chance) {
        return chance * 1.2F;
    }
}
