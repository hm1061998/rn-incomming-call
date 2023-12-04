import * as React from 'react';
import {
  Platform,
  TouchableOpacity,
  StyleSheet,
  View,
  Text,
  AppRegistry,
  ToastAndroid,
  PermissionsAndroid,
  Linking,
  Modal,
  Alert,
} from 'react-native';

import RNMinimizeApp from 'react-native-minimize';
import CallDetectorManager, {
  askForDispalayOverOtherAppsPermission,
} from 'rn-incomming-call';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { AppState } from 'react-native';
import BackgroundFetch from 'react-native-background-fetch'; // Hoặc sử dụng thư viện khác nếu bạn chọn sử dụng react-native-background-task

if (Platform.OS === 'android') {
}

export default function App() {
  const [result, setResult] = React.useState();
  const callDetector = React.useRef();
  const [visible, setVisible] = React.useState(false);

  React.useEffect(() => {
    Linking.getInitialURL().then((url) => {
      console.log('url', url);
    });

    Linking.addEventListener('url', (url) => {
      console.log('url event', url);
    });
    PermissionsAndroid.request('android.permission.CALL_PHONE');
    PermissionsAndroid.request('android.permission.READ_CALL_LOG');

    AsyncStorage.getItem('Ringing').then((res) => {
      if (res) {
        console.log('dataring', res);
      } else {
        console.log('no data ringing');
      }
    });
    // multiply(3, 7).then(setResult);
  }, []);

  React.useEffect(() => {
    callDetector.current = new CallDetectorManager(
      (event, phoneNumber) => {
        // For iOS event will be either "Connected",
        // "Disconnected","Dialing" and "Incoming"

        // For Android event will be either "Offhook",
        // "Disconnected", "Incoming" or "Missed"
        // phoneNumber should store caller/called number

        if (event === 'Disconnected') {
          console.log('Disconnected');
          // Do something call got disconnected
        } else if (event === 'Connected') {
          console.log('Connected');
          // Do something call got connected
          // This clause will only be executed for iOS
        } else if (event === 'Incoming') {
          console.log('Incoming', phoneNumber);
          // Do something call got incoming
        } else if (event === 'Dialing') {
          console.log('Dialing');

          // Do something call got dialing
          // This clause will only be executed for iOS
        } else if (event === 'Offhook') {
          console.log('Offhook');

          //Device call state: Off-hook.
          // At least one call exists that is dialing,
          // active, or on hold,
          // and no calls are ringing or waiting.
          // This clause will only be executed for Android
        } else if (event === 'Missed') {
          console.log('Missed');

          // Do something call got missed
          // This clause will only be executed for Android
        }
      },
      () => {}, // callback if your permission got denied [ANDROID] [only if you want to read incoming number] default: console.error
      {
        title: 'Phone State Permission',
        message:
          'This app needs access to your phone state in order to react and/or to adapt to incoming calls.',
      } // a custom permission request message to explain to your user, why you need the permission [recommended] - this is the default one
    );

    return () => {
      callDetector.current.dispose();
    };
  }, []);

  React.useEffect(() => {
    // Đăng ký tác vụ background fetch
    // BackgroundFetch.configure(
    //   {
    //     minimumFetchInterval: 15, // Đặt thời gian giữa các lần fetch
    //     stopOnTerminate: false, // Cho phép chạy trong nền khi ứng dụng bị đóng
    //     startOnBoot: true, // Cho phép bắt đầu khi thiết bị khởi động
    //   },
    //   async (taskId) => {
    //     // Thực hiện logic hiển thị overlay ở đây
    //     setVisible(true);

    //     // Kết thúc tác vụ
    //     BackgroundFetch.finish(taskId);
    //   },
    //   (error) => {
    //     console.log('[BackgroundFetch] configure error: ', error);
    //   }
    // );

    // Đăng ký sự kiện khi ứng dụng được mở từ background
    const onAppResume = () => {
      // Thực hiện logic hiển thị overlay ở đây
      setVisible(true);
    };

    // Đăng ký sự kiện
    // AppState.addEventListener('change', (newState) => {
    //   if (newState === 'active') {
    //     // onAppResume();
    //   }
    // });

    return () => {
      // Huỷ đăng ký sự kiện khi component bị hủy
      // AppState.removeEventListener('change', onAppResume);
    };
  }, []); // Chạy useEffect chỉ một lần khi component được tạo

  return (
    <View style={styles.container}>
      <TouchableOpacity
        onPress={() => {
          // Alert.alert('test alett');
          // RNMinimizeApp.minimizeApp();
          // setVisible(true);

          askForDispalayOverOtherAppsPermission();
        }}
      >
        <Text>minimize</Text>
      </TouchableOpacity>
      <Text>Result: {result}</Text>

      <Modal
        transparent
        animationType="slide"
        visible={visible}
        onRequestClose={() => setVisible(false)}
      >
        <View
          style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}
        >
          <View style={{ width: '80%', height: 350, backgroundColor: 'white' }}>
            <Text>You have an incoming call from </Text>
            {/* Add other components or styles as needed */}
          </View>
        </View>
      </Modal>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
