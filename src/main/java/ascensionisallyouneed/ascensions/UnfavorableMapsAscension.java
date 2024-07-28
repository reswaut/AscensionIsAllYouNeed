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
        int normalEnemyCount = 0;
        ArrayList<ArrayList<MapRoomNode>> normalEnemyNodesByWidth = new ArrayList<>();
        for (ArrayList<MapRoomNode> mapRoomNodes : map) {
            int width = 0;
            for (MapRoomNode node : mapRoomNodes) {
                if (node.room instanceof MonsterRoom && !(node.room instanceof MonsterRoomElite)) {
                    ++width;
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
        int maxWidth = normalEnemyNodesByWidth.size() - 1;
        int burningCount = (normalEnemyCount - 1) / mapRng.random(5, 10) + 1;
        for (int i = 1; i <= maxWidth; ++i) {
            for (MapRoomNode node : normalEnemyNodesByWidth.get(i)) {
                if (mapRng.random(1, maxWidth) >= i) {
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
}
