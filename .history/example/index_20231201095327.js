import { AppRegistry } from 'react-native';
import App from './src/App';
import { name as appName } from './app.json';

AppRegistry.registerComponent(appName, () => App);

AppRegistry.registerHeadlessTask('RNIncommingCall', () => (data) => {
  if (data.state === 'extra_state_ringing') {
    console.log('data', data);
    ToastAndroid.show('Ringing', ToastAndroid.LONG, ToastAndroid.BOTTOM);
    AsyncStorage.setItem('Ringing', JSON.stringify(data));
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
