import { AppRegistry } from 'react-native';
import App from './src/App';
import { name as appName } from './app.json';
import AsyncStorage from '@react-native-async-storage/async-storage';
import dayjs from 'dayjs';

AppRegistry.registerComponent(appName, () => App);

const HeadlessEventTask = async (data) => {
  console.log('RNIncommingCall', data);
  if (data.state === 'RINGING') {
    AsyncStorage.setItem('Ringing', dayjs().format('DD/MM/YYYY HH:mm:ss'));
  } else if (data.state === 'extra_state_offhook') {
  } else if (data.state === 'extra_state_idle') {
  }
};

AppRegistry.registerHeadlessTask('RNIncommingCall', () => HeadlessEventTask);
