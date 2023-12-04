import { AppRegistry } from 'react-native';
import App from './src/App';
import { name as appName } from './app.json';
import AsyncStorage from '@react-native-async-storage/async-storage';

AppRegistry.registerComponent(appName, () => App);

const HeadlessEventTask = async (data) => {
  if (data.state === 'extra_state_ringing') {
    console.log('data', data);

    AsyncStorage.setItem('Ringing', JSON.stringify(data));
  } else if (data.state === 'extra_state_offhook') {
  } else if (data.state === 'extra_state_idle') {
  }
  console.log('RNIncommingCall');
  // Make your call here
  // RNCallKeep.displayIncomingCall(callUUID, handle, name);
  return Promise.resolve();
};

AppRegistry.registerHeadlessTask('RNIncommingCall', () => HeadlessEventTask);
