package ModelGroup;

public class MacroEvent {
    private int DeviceID;
    private int MacroIdx;
    private String MacroMode;
    private String SiteName;
    private String SiteUrl;
    private String Keyword;

    public int getDeviceID() {
        return DeviceID;
    }

    public int getMacroIdx() {
        return MacroIdx;
    }

    public String getMacroMode() {
        return MacroMode;
    }

    public String getSiteName() {
        return SiteName;
    }

    public String getSiteUrl() {
        return SiteUrl;
    }

    public String getKeyword() {
        return Keyword;
    }

    public void setSiteWaitTime(long siteWaitTime) {
        SiteWaitTime = siteWaitTime;
    }

    public long getSiteWaitTime() {
        return SiteWaitTime;
    }

    public boolean isActive() {
        return IsActive;
    }

    private long SiteWaitTime;

    private boolean IsActive;

    public long getAirPlanModeSec() {
        return AirPlanModeSec;
    }

    public void setAirPlanModeSec(long time){
        AirPlanModeSec = time;
    }
    private long AirPlanModeSec;

    public int getScrollForwardLimitCnt() {
        return ScrollForwardLimitCnt;
    }

    public void setSiteUrl(String siteUrl) {
        SiteUrl = siteUrl;
    }

    public void setKeyword(String keyword) {
        Keyword = keyword;
    }

    public void setScrollForwardLimitCnt(int scrollForwardLimitCnt) {
        ScrollForwardLimitCnt = scrollForwardLimitCnt;
    }

    private int ScrollForwardLimitCnt;

    public String getRndClickData() {
        return RndClickData;
    }

    public void setRndClickData(String rndClickData) {
        RndClickData = rndClickData;
    }

    private String RndClickData;

    private String Model;
    private int MacroSetDelayMin;
    private int MacroRunDelayMin;

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public int getMacroSetDelayMin() {
        return MacroSetDelayMin;
    }

    public void setMacroSetDelayMin(int macroSetDelayMin) {
        MacroSetDelayMin = macroSetDelayMin;
    }

    public int getMacroRunDelayMin() {
        return MacroRunDelayMin;
    }

    public void setMacroRunDelayMin(int macroRunDelayMin) {
        MacroRunDelayMin = macroRunDelayMin;
    }

    private String SearchType;

    public String getSearchType() {
        return SearchType;
    }

    public void setSearchType(String searchType) {
        SearchType = searchType;
    }

    public MacroEvent(int deviceID, int macroIdx, String macroMode, String siteName, String siteUrl, String keyword
            , long siteWaitTime
            , long AirPlanModeSec
            , int ScrollForwardLimitCnt
            , String RndClickData
            , boolean isActive
            , String model, int macroSetDelayMin, int macroRunDelayMin
            , String searchType) {
        this.DeviceID = deviceID;
        this.MacroIdx = macroIdx;
        this.MacroMode = macroMode;
        this.SiteName = siteName;
        this.SiteUrl = siteUrl;
        this.Keyword = keyword;
        this.SiteWaitTime = siteWaitTime;
        this.AirPlanModeSec = AirPlanModeSec;
        this.ScrollForwardLimitCnt = ScrollForwardLimitCnt;
        this.RndClickData = RndClickData;
        this.IsActive = isActive;
        this.Model = model;
        this.MacroSetDelayMin = macroSetDelayMin;
        this.MacroRunDelayMin = macroRunDelayMin;
        this.SearchType = searchType;
    }


}
