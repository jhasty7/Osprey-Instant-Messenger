
public class Status extends Packet {

    private ONLINE_STATUS onlineStatus;
    private USER_STATUS userStatus;
    private String statusText;

    Status() {
        onlineStatus = null;
        userStatus = null;
        statusText = null;
    }

    Status(ONLINE_STATUS onlineStatus, USER_STATUS userStatus, String statusText) {

        this.onlineStatus = onlineStatus;
        this.userStatus = userStatus;
        this.statusText = statusText;
    }

    public ONLINE_STATUS getOnlineStatus() {
        return onlineStatus;
    }

    public USER_STATUS getUserStatus() {
        return userStatus;
    }

    public String getStatusText() {
        return statusText;
    }
}
