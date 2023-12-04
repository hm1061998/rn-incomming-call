package com.rnincommingcall;

import android.telecom.Call;
import android.telecom.InCallService;
import android.content.Intent;
import android.net.Uri;

public class CallService extends InCallService {

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);

        // Khi có cuộc gọi được thêm vào, xử lý tại đây
         // Ví dụ: Kiểm tra số điện thoại đích và thực hiện cuộc gọi
    String destinationNumber = call.getDetails().getHandle().getSchemeSpecificPart();
    performOutgoingCall(destinationNumber);
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);

        // Khi cuộc gọi bị xóa, xử lý tại đây
    }

    private void performOutgoingCall(String phoneNumber) {
    // Thực hiện cuộc gọi đi
        // Ví dụ: Sử dụng Intent.ACTION_CALL để thực hiện cuộc gọi
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }
}
