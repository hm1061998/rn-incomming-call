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
} from 'react-native';

import RNMinimizeApp from 'react-native-minimize';
import CallDetectorManager from 'rn-incomming-call';
import AsyncStorage from '@react-native-async-storage/async-storage';

if (Platform.OS === 'android') {
  AppRegistry.registerHeadlessTask(
    'RNIncommingCall',
    async ({ event, ids }) => {
      console.log('event', event);
      console.log('ids', ids);
    }
  );
  // AppRegistry.registerHeadlessTask('RNIncommingCall', () => (data) => {
  //   if (data.state === 'extra_state_ringing') {
  //     console.log('data', data);
  //     ToastAndroid.show('Ringing', ToastAndroid.LONG, ToastAndroid.BOTTOM);
  //     AsyncStorage.setItem('Ringing', JSON.stringify(data));
  //   } else if (data.state === 'extra_state_offhook') {
  //     ToastAndroid.showWithGravity(
  //       'Call Started',
  //       ToastAndroid.LONG,
  //       ToastAndroid.BOTTOM
  //     );
  //   } else if (data.state === 'extra_state_idle') {
  //     ToastAndroid.showWithGravity(
  //       'Call Ended',
  //       ToastAndroid.LONG,
  //       ToastAndroid.BOTTOM
  //     );
  //   }

  //   console.log('RNIncommingCall');
  //   // Make your call here
  //   // RNCallKeep.displayIncomingCall(callUUID, handle, name);
  //   return Promise.resolve();
  // });
}

export default function App() {
  const [result, setResult] = React.useState();
  const callDetector = React.useRef();
  React.useEffect(() => {
    PermissionsAndroid.request('android.permission.CALL_PHONE');
    PermissionsAndroid.request('android.permission.READ_CALL_LOG');

    AsyncStorage.getItem('Ringing').then((res) => {
      if (res) {
        console.log(JSON.parse(res));
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
      true, // if you want to read the phone number of the incoming call [ANDROID], otherwise false
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

  return (
    <View style={styles.container}>
      <TouchableOpacity
        onPress={() => {
          // RNMinimizeApp.minimizeApp();
        }}
      >
        <Text>minimize</Text>
      </TouchableOpacity>
      <Text>Result: {result}</Text>
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
