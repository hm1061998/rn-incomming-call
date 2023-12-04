import * as React from 'react';
import { Platform } from 'react-native';
import { AppRegistry, ToastAndroid } from 'react-native';

import { StyleSheet, View, Text } from 'react-native';
import { multiply } from 'rn-incomming-call';

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
    multiply(3, 7).then(setResult);
  }, []);

  return (
    <View style={styles.container}>
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
