import {
  NativeModules,
  NativeEventEmitter,
  Platform,
  PermissionsAndroid,
  type Permission,
  type Rationale,
} from 'react-native';
export const permissionDenied = 'PERMISSION DENIED';
import BatchedBridge from 'react-native/Libraries/BatchedBridge/BatchedBridge';
import updateActionModule from './updateActionModule';

BatchedBridge.registerCallableModule(
  'RnIncommingUpdateActionModule',
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

const requestPermissionsAndroid = async (
  permissionMessage: string
): Promise<boolean> => {
  try {
    const gotPermission = await PermissionsAndroid.check(
      PermissionsAndroid.PERMISSIONS.READ_PHONE_STATE as Permission
    );
    console.log('====================================');
    console.log(gotPermission);
    console.log('====================================');
    if (gotPermission) {
      return true;
    }

    let unknownValue: unknown = permissionMessage;
    const result = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.READ_PHONE_STATE as Permission,
      unknownValue as Rationale
    );

    return result === PermissionsAndroid.RESULTS.GRANTED;
  } catch (error) {
    return false;
  }
};

class RnIncommingCallManager {
  private subscription?: NativeEventEmitter;

  constructor(
    callback: (event: any) => void,
    readPhoneNumberAndroid = false,
    permissionDeniedCallback: () => void = () => {},
    permissionMessage = {
      title: 'Phone State Permission',
      message:
        'This app needs access to your phone state in order to react and/or to adapt to incoming calls.',
    }
  ) {
    if (Platform.OS === 'android') {
      if (readPhoneNumberAndroid) {
        requestPermissionsAndroid(permissionMessage.message)
          .then((permissionGranted) => {
            if (!permissionGranted) {
              permissionDeniedCallback();
            }
          })
          .catch(permissionDeniedCallback);
      }
    }
    RnIncommingCall.startListener();
    this.subscription = new NativeEventEmitter(RnIncommingCall);
    this.subscription.addListener('PhoneCallStateUpdate', callback);
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
