interface UpdateActionModule {
  callStateUpdated(state: string, incomingNumber: string): void;
}

const updateActionModule: UpdateActionModule = {
  callStateUpdated(state, incomingNumber) {
    this.callback && this.callback(state, incomingNumber);
  },
};

export default updateActionModule;
