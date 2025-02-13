import { createStore } from "vuex";

const store = createStore({
  state: {
    snackbar: {
      message: "",
      color: "",
      visible: false,
    },
    authToken: null,
  },
  getters: {},
  mutations: {
    showSnackbar(state, payload) {
      state.snackbar.message = payload.message;
      state.snackbar.color = payload.color || "info";
      state.snackbar.visible = true;
    },
    hideSnackbar(state) {
      state.snackbar.visible = false;
    },
    setToken(state, token) {
      state.authToken = token;
    },
  },
  actions: {},
  modules: {},
});

export default store;
