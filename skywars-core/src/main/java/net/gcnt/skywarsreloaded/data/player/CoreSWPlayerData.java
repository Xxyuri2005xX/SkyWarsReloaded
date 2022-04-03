package net.gcnt.skywarsreloaded.data.player;

/**
 * Stats and player cosmetics for a currently connected player
 */
public class CoreSWPlayerData implements SWPlayerData {

    private boolean initialized;

    private SWPlayerStats stats;

    private String selectedSoloCage;
    private String selectedTeamCage;
    private String selectedParticle;
    private String selectedKillEffect;
    private String selectedWinEffect;
    private String selectedProjectileEffect;
    private String killMessagesTheme;

    public CoreSWPlayerData() {
        this.initialized = false;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void initData(SWPlayerStats statsIn,
                         String selectedSoloCageIn,
                         String selectedTeamCageIn,
                         String selectedParticleIn,
                         String selectedKillEffectIn,
                         String selectedWinEffectIn,
                         String selectedProjectileEffectIn,
                         String killMessagesThemeIn) {
        this.stats = statsIn;
        this.selectedSoloCage = selectedSoloCageIn;
        this.selectedTeamCage = selectedTeamCageIn;
        this.selectedParticle = selectedParticleIn;
        this.selectedKillEffect = selectedKillEffectIn;
        this.selectedWinEffect = selectedWinEffectIn;
        this.selectedProjectileEffect = selectedProjectileEffectIn;
        this.killMessagesTheme = killMessagesThemeIn;
        this.initialized = true;
    }

    @Override
    public SWPlayerStats getStats() {
        return stats;
    }

    @Override
    public String getSoloCage() {
        return selectedSoloCage;
    }

    @Override
    public void setSoloCage(String value) {
        selectedSoloCage = value;
    }

    @Override
    public String getTeamCage() {
        return selectedTeamCage;
    }

    @Override
    public void setTeamCage(String value) {
        selectedTeamCage = value;
    }

    @Override
    public String getParticle() {
        return selectedParticle;
    }

    @Override
    public void setParticle(String value) {
        selectedParticle = value;
    }

    @Override
    public String getKillEffect() {
        return selectedKillEffect;
    }

    @Override
    public void setKillEffect(String value) {
        selectedKillEffect = value;
    }

    @Override
    public String getWinEffect() {
        return selectedWinEffect;
    }

    @Override
    public void setWinEffect(String value) {
        selectedWinEffect = value;
    }

    @Override
    public String getProjectileParticle() {
        return selectedProjectileEffect;
    }

    @Override
    public void setProjectileParticle(String value) {
        selectedProjectileEffect = value;
    }

    @Override
    public String getKillMessagesTheme() {
        return killMessagesTheme;
    }

    @Override
    public void setKillMessagesTheme(String value) {
        killMessagesTheme = value;
    }
}