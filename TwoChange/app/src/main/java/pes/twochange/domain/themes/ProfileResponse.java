package pes.twochange.domain.themes;

import pes.twochange.domain.model.Profile;

interface ProfileResponse {

    void success(Profile profile);
    void failure(String s);

}
