import { AppRegistry } from 'react-native';
import App from './src/App';
import { name as appName } from './app.json';
import AsyncStorage from '@react-native-async-storage/async-storage';

AppRegistry.registerComponent(appName, () => App);

const HeadlessEventTask = async (data) => {
  console.log('RNIncommingCall', data);
  if (data.state === 'extra_state_ringing') {
    console.log('data', data);
  } else if (data.state === 'extra_state_offhook') {
  } else if (data.state === 'extra_state_idle') {
  }
};

AppRegistry.registerHeadlessTask('RNIncommingCall', () => HeadlessEventTask);
