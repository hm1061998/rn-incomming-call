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
import {
  multiply,
  askForDispalayOverOtherAppsPermission,
} from 'rn-incomming-call';
import RNMinimizeApp from 'react-native-minimize';

if (Platform.OS === 'android') {
  AppRegistry.registerHeadlessTask('RNIncommingCall', () => (data) => {
    if (data.state === 'extra_state_ringing') {
      ToastAndroid.showWithGravity(
        'Ringing',
        ToastAndroid.LONG,
        ToastAndroid.BOTTOM
      );
    } else if (data.state === 'extra_state_offhook') {
      ToastAndroid.showWithGravity(
        'Call Started',
        ToastAndroid.LONG,
        ToastAndroid.BOTTOM
      );
    } else if (data.state === 'extra_state_idle') {
      ToastAndroid.showWithGravity(
        'Call Ended',
        ToastAndroid.LONG,
        ToastAndroid.BOTTOM
      );
    }

    console.log('RNIncommingCall');
    // Make your call here
    // RNCallKeep.displayIncomingCall(callUUID, handle, name);
    return Promise.resolve();
  });
}

export default function App() {
  const [result, setResult] = React.useState();

  React.useEffect(() => {
    PermissionsAndroid.request('android.permission.CALL_PHONE');
    multiply(3, 7).then(setResult);
  }, []);

  return (
    <View style={styles.container}>
      <TouchableOpacity
        onPress={() => {
          RNMinimizeApp.minimizeApp();
          // askForDispalayOverOtherAppsPermission()
          //   .then((res) => {
          //     console.log('res', res);
          //     // res will be true if permission was granted
          //   })
          //   .catch((e) => {
          //     console.log('e', e);
          //     // permission was declined
          //   });
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
