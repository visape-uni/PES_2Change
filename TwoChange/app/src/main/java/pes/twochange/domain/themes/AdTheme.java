package pes.twochange.domain.themes;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdTheme {

    private static final AdTheme ourInstance = new AdTheme();
    private static final String REFERENCE = "lists";
    private static String user;

    public static AdTheme getInstance() {
        return ourInstance;
    }

    // Interface of the list of adds
    public interface AdList {
        // removes an element with key = $key from the list
        void remove(String key);
        // adds the element with key = $key to the list
        void add(String key);
        // get the Database Reference from Firebase of the list
        DatabaseReference getReference();
    }

    // common implementation of both lists
    private abstract static class ListImpl implements AdList {

        @Override
        public void remove(String key) {

        }

        @Override
        public void add(String key) {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(REFERENCE)
                    .child(user)
                    .child(getListName())
                    .child(key)
                    .removeValue();
        }

        @Override
        public DatabaseReference getReference() {
            return FirebaseDatabase.getInstance()
                    .getReference()
                    .child(REFERENCE)
                    .child(user)
                    .child(getListName());
        }

        protected abstract String getListName();
    }

    // Singleton List of Wanted items
    public static class Wanted extends ListImpl {

        private static final AdList ourInstance = new Wanted();
        private static final String LIST = "wanted";

        public static AdList getInstance(String username) {
            user = username;
            return ourInstance;
        }

        @Override
        protected String getListName() {
            return LIST;
        }
    }

    // Singleton List of Offered items
    public static class Offered extends ListImpl {

        private static final AdList ourInstance = new Offered();
        private static final String LIST = "offered";

        public static AdList getInstance(String username) {
            user = username;
            return ourInstance;
        }

        @Override
        protected String getListName() {
            return LIST;
        }

    }


}
