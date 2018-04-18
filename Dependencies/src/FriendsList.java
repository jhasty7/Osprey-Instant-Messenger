
import java.io.Serializable;
import java.util.ArrayList;

public class FriendsList implements Serializable {

    private ArrayList<Friend> onlineFriends;
    private ArrayList<Friend> offlineFriends;
    private ArrayList<Friend> pendingFriends;
    private boolean hasPendingFriends = false;

    public FriendsList() {
        onlineFriends = new ArrayList<>();
        offlineFriends = new ArrayList<>();
    }

    /* constructor if you have one list of mixed online/offline/pending */
    public FriendsList(ArrayList<Friend> friendsList) {
        onlineFriends = new ArrayList<>();
        offlineFriends = new ArrayList<>();
        pendingFriends = new ArrayList<>();

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

    public ArrayList<Friend> getPendingFriends() {
        return pendingFriends;
    }

    /* sorts the one mixed online/offline friends list into 2 lists */
    private void sortList(ArrayList<Friend> friendsList) {
        int i;
        for (i = 0; i < friendsList.size(); i++) {
            if (friendsList.get(i).isPendingAdd()) {
                pendingFriends.add(friendsList.get(i));
                hasPendingFriends = true;
            }
            else if (friendsList.get(i).getOnlineStatus()) {
                onlineFriends.add(friendsList.get(i));
            }
            else {
                offlineFriends.add(friendsList.get(i));
            }
        }
        if (pendingFriends.isEmpty()) {
            hasPendingFriends = false;
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
        }
        else {
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
        else {
            tempList.add("");
        }
        return tempList;
    }

    /* returns pending friends as ArrayList<String> */
    public ArrayList<String> getPendingFriendsAsString() {
        ArrayList<String> tempList = new ArrayList<>();
        if (!pendingFriends.isEmpty()
                && !pendingFriends.get(0).getUsername().equals("")) {

            int i;
            for (i = 0; i < pendingFriends.size(); i++) {
                tempList.add(pendingFriends.get(i).getUsername());
            }

            return tempList;
        }
        else {
            tempList.add("");
        }
        return tempList;
    }

    /* changing friend from online to offline and vice versa */
    public void updateFriend(Friend friend) {
        if (friend.isAcceptedFriendRequest()) {
            friend.setAcceptedFriendRequest(false);
            for (int i = 0; i < pendingFriends.size(); i++) {
                if (friend.getUsername().equals(pendingFriends.get(i).getUsername())) {
                    pendingFriends.remove(i);
                    if(friend.getOnlineStatus()){
                        onlineFriends.add(friend);
                    }else{
                        offlineFriends.add(friend);
                    }
                }
            }
            if (pendingFriends.isEmpty()) {
                hasPendingFriends = false;
            }
        }
        else {
            if (friend.getOnlineStatus()) {

                int i;
                for (i = 0; i < offlineFriends.size(); i++) {
                    if (friend.getUsername().equals(offlineFriends.get(i).getUsername())) {
                        offlineFriends.remove(i);
                        onlineFriends.add(friend);
                    }
                }
            }
            else {
                int i;
                for (i = 0; i < onlineFriends.size(); i++) {
                    if (friend.getUsername().equals(onlineFriends.get(i).getUsername())) {
                        onlineFriends.remove(i);
                        offlineFriends.add(friend);
                    }
                }
            }
        }
    }

    /* adding friend */
    public void addFriend(Friend friend) {
        if(friend.isPendingAdd()){
            pendingFriends.add(friend);
            hasPendingFriends = true;
        }
        else if (friend.getOnlineStatus()) {
            onlineFriends.add(friend);
        }
        else {
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
        /* check offline */
        for (i = 0; i < offlineFriends.size(); i++) {
            if (friend.equals(offlineFriends.get(i).getUsername())) {
                offlineFriends.remove(i);
            }
        }
    }

    public boolean hasPendingFriends() {
        return hasPendingFriends;
    }

}
