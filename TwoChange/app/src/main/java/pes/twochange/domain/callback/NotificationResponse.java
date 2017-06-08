package pes.twochange.domain.callback;

import pes.twochange.domain.model.Profile;

/**
 * Created by Adrian on 07/06/2017.
 */

public interface NotificationResponse {
    void sendNotis(boolean notifications);
    void changeNotis(Profile profile);
}
