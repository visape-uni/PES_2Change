package pes.twochange.domain.callback;

import pes.twochange.domain.model.Ad;

/**
 * Created by kredes on 24/05/2017.
 */

public interface AdResponse {
    void onSuccess(Ad ad);
    void onFailure(String error);
}
