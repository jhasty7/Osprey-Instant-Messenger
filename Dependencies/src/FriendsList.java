
import java.io.Serializable;
import java.util.ArrayList;

public class FriendsList implements Serializable {

    private ArrayList<Friend> onlineFriends;
    private ArrayList<Friend> offlineFriends;
    
    public FriendsList() {
        onlineFriends = new ArrayList<>();
        onlineFriends.add(new Friend("", false, null, ""));
        offlineFriends = new ArrayList<>();
        offlineFriends.add(new Friend("", false, null, ""));
    }

    /* constructor if you have one list of mixed online/offline */
    public FriendsList(ArrayList<Friend> friendsList) {
        onlineFriends = new ArrayList<>();
        offlineFriends = new ArrayList<>();

        sortList(friendsList);
    }

    /*constructor if you have separate online/offline lists */
    public FriendsList(ArrayList<Friend> onlineFriends, ArrayList<Friend> offlineFriends) {
        this.onlineFriends = onlineFriends;
        this.offlineFriends = offlineFriends;
    }

    public ArrayList<Friend> getOnlineFriends() {
        return onlineFriends;
    }

    public ArrayList<Friend> getOfflineFriends() {
        return offlineFriends;
    }

    /* sorts the one mixed online/offline friends list into 2 lists */
    private void sortList(ArrayList<Friend> friendsList) {
        int i;
        for (i = 0; i < friendsList.size(); i++) {

            if (friendsList.get(i).getOnlineStatus()) {
                onlineFriends.add(friendsList.get(i));
            }
            else {
                offlineFriends.add(friendsList.get(i));
            }
        }

        if (onlineFriends.isEmpty()) {
            onlineFriends.add(new Friend("", false, null, ""));
        }
        if (offlineFriends.isEmpty()) {
            offlineFriends.add(new Friend("", false, null, ""));
        }
    }

    /* returns online friends as ArrayList<String> */
    public ArrayList<String> getOnlineFriendsAsString() {
        ArrayList<String> tempList = new ArrayList<>();

        if (!onlineFriends.isEmpty()
                && !onlineFriends.get(0).getUsername().equals("")) {

            int i;
            for (i = 0; i < onlineFriends.size(); i++) {
                tempList.add(onlineFriends.get(i).getUsername() + " - "
                        + onlineFriends.get(i).getCurrentStatus() + " - " + onlineFriends.get(i).getTextStatus());
            }

            return tempList;
        }else{
            tempList.add("");
        }
        return tempList;
    }

    /* returns offline friends as ArrayList<String> */
    public ArrayList<String> getOfflineFriendsAsString() {
        ArrayList<String> tempList = new ArrayList<>();
        if (!offlineFriends.isEmpty()
                && !offlineFriends.get(0).getUsername().equals("")) {

            int i;
            for (i = 0; i < offlineFriends.size(); i++) {
                tempList.add(offlineFriends.get(i).getUsername());
            }

            return tempList;
        }
        else{
            tempList.add("");
        }
        return tempList;
    }

    /* changing friend from online to offline and vice versa */
    public void updateFriend(Friend friend) {

        if (friend.getOnlineStatus()) {

            int i;
            for (i = 0; i < offlineFriends.size(); i++) {
                if (friend.getUsername().equals(offlineFriends.get(i).getUsername())) {
                    offlineFriends.remove(i);
                    if (onlineFriends.get(0).getUsername().equals("")) {
                        onlineFriends.remove(0);
                    }
                    onlineFriends.add(friend);
                }
            }
            if (offlineFriends.isEmpty()) {
                offlineFriends.add(new Friend("", false, null, ""));
            }
        }
        else {
            int i;
            for (i = 0; i < onlineFriends.size(); i++) {
                if (friend.getUsername().equals(onlineFriends.get(i).getUsername())) {
                    onlineFriends.remove(i);
                    if (offlineFriends.get(0).getUsername().equals("")) {
                        offlineFriends.remove(0);
                    }
                    offlineFriends.add(friend);
                }
            }
            if (onlineFriends.isEmpty()) {
                onlineFriends.add(new Friend("", false, null, ""));
            }
        }
    }

    /* adding friend */
    public void addFriend(Friend friend) {
        if (friend.getOnlineStatus()) {
            if (onlineFriends.get(0).getUsername().equals("")) {
                onlineFriends.remove(0);
            }
            onlineFriends.add(friend);
        }
        else {
            if (offlineFriends.get(0).getUsername().equals("")) {
                offlineFriends.remove(0);
            }
            offlineFriends.add(friend);
        }
    }

    /* removing friend */
    public void removeFriend(String friend) {
        /* check online */
        int i;
        for (i = 0; i < onlineFriends.size(); i++) {
            if (friend.equals(onlineFriends.get(i).getUsername())) {
                onlineFriends.remove(i);
            }
        }
        if (onlineFriends.isEmpty()) {
            onlineFriends.add(new Friend("", false, null, ""));
        }
        /* check offline */
        for (i = 0; i < offlineFriends.size(); i++) {
            if (friend.equals(offlineFriends.get(i).getUsername())) {
                offlineFriends.remove(i);
            }
        }
        if (offlineFriends.isEmpty()) {
            offlineFriends.add(new Friend("", false, null, ""));
        }

    }
}
