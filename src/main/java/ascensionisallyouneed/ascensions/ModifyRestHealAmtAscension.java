package ascensionisallyouneed.ascensions;

public interface ModifyRestHealAmtAscension {
    float modifyRestHealAmt(float healAmt);
    String getRestOptionDescription(boolean nightTerrorsModEnabled, int healAmt);
}
