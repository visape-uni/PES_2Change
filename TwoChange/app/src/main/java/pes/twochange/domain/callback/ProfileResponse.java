package pes.twochange.domain.callback;

import pes.twochange.domain.model.Profile;

public interface ProfileResponse {

    void success(Profile profile);
    void failure(String s);

}
