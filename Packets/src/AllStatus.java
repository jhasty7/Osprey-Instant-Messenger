
public class AllStatus extends Packet {

    private eONLINE_STATUS onlineStatus;
    private eCURRENT_STATUS userStatus;
    private String statusText;

    AllStatus() {
        onlineStatus = null;
        userStatus = null;
        statusText = null;
    }

    AllStatus(eONLINE_STATUS onlineStatus, eCURRENT_STATUS userStatus, String statusText) {

        this.onlineStatus = onlineStatus;
        this.userStatus = userStatus;
        this.statusText = statusText;
    }

    public eONLINE_STATUS getOnlineStatus() {
        return onlineStatus;
    }

    public eCURRENT_STATUS getUserStatus() {
        return userStatus;
    }

    public String getStatusText() {
        return statusText;
    }
}
