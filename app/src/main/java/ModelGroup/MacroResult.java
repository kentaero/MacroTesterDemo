package ModelGroup;

public class MacroResult {
    private int ComID;
    private int DeviceID;
    private int MacroIdx;
    private String SiteName;
    private String StartTime;
    private String EndTime = "";
    private boolean SearchResult = false;

    private long siteWaitTime = 0L;
    private String remarks = "";

    private String IPAddress = "";

    public String getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }

    public long getSiteWaitTime() {
        return siteWaitTime;
    }

    public void setSiteWaitTime(long siteWaitTime) {
        this.siteWaitTime = siteWaitTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public int getDeviceID() {
        return DeviceID;
    }

    public int getMacroIdx() {
        return MacroIdx;
    }

    public String getSiteName() {
        return SiteName;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public boolean getSearchResult() {
        return SearchResult;
    }

    public void setSearchResult(boolean searchResult) {
        SearchResult = searchResult;
    }

    public int getComID() {
        return ComID;
    }

    public void setComID(int comID) {
        ComID = comID;
    }

    public MacroResult(int comID, int deviceID, int macroIdx, String siteName) {
        ComID = comID;
        DeviceID = deviceID;
        MacroIdx = macroIdx;
        SiteName = siteName;
    }
}
