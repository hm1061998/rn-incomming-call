package com.rnincommingcall;

import android.telecom.Call;
import android.telecom.InCallService;

public class CallService extends InCallService {

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);

        // Khi có cuộc gọi được thêm vào, xử lý tại đây
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);

        // Khi cuộc gọi bị xóa, xử lý tại đây
    }
}
