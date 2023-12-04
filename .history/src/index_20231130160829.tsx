import {
  NativeModules,
  NativeEventEmitter,
  Platform,
  PermissionsAndroid,
} from 'react-native';
export const permissionDenied = 'PERMISSION DENIED';
import BatchedBridge from 'react-native/Libraries/BatchedBridge/BatchedBridge';
import updateActionModule from './updateActionModule';

BatchedBridge.registerCallableModule(
  'rnIncommingCallUpdateActionModule',
  updateActionModule
);

const LINKING_ERROR =
  `The package 'rn-incomming-call' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const RnIncommingCall = NativeModules.RnIncommingCall
  ? NativeModules.RnIncommingCall
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

const requestPermissionsAndroid = async (permissionMessage) => {
  await PermissionsAndroid.check(
    PermissionsAndroid.PERMISSIONS.READ_PHONE_STATE
  ).then(async (gotPermission) =>
    gotPermission
      ? true
      : await PermissionsAndroid.request(
          PermissionsAndroid.PERMISSIONS.READ_PHONE_STATE,
          permissionMessage
        ).then((result) => result === PermissionsAndroid.RESULTS.GRANTED)
  );
};

class RnIncommingCallManager {
  subscription;
  callback;
  constructor(
    callback,
    readPhoneNumberAndroid = false,
    permissionDeniedCallback = () => {},
    permissionMessage = {
      title: 'Phone State Permission',
      message:
        'This app needs access to your phone state in order to react and/or to adapt to incoming calls.',
    }
  ) {
    this.callback = callback;
    if (Platform.OS === 'ios') {
      RnIncommingCall && RnIncommingCall.startListener();
      this.subscription = new NativeEventEmitter(RnIncommingCall);
      this.subscription.addListener('PhoneCallStateUpdate', callback);
    } else {
      if (RnIncommingCall) {
        if (readPhoneNumberAndroid) {
          requestPermissionsAndroid(permissionMessage)
            .then((permissionGranted) => {
              if (!permissionGranted) {
                permissionDeniedCallback(permissionDenied);
              }
            })
            .catch(permissionDeniedCallback);
        }
        RnIncommingCall.startListener();
      }
      updateActionModule.callback = callback;
    }
  }

  dispose() {
    RnIncommingCall && RnIncommingCall.stopListener();
    updateActionModule.callback = undefined;
    if (this.subscription) {
      this.subscription.removeAllListeners('PhoneCallStateUpdate');
      this.subscription = undefined;
    }
  }
}

export default RnIncommingCallManager;
