import { AppRegistry } from 'react-native';
import App from './src/App';
import { name as appName } from './app.json';

AppRegistry.registerComponent(appName, () => App);
AppRegistry.registerHeadlessTask('RNIncommingCall', async ({ event, ids }) => {
  console.log('event', event);
  console.log('ids', ids);
});
