package pes.twochange.domain.callback;

import android.graphics.Bitmap;

interface ImageResponse {
    void success(Bitmap bitmap);
    void failure(String s);
}
