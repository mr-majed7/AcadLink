// Styles
import "@mdi/font/css/materialdesignicons.css";
import "vuetify/styles";

// Vuetify
import { createVuetify } from "vuetify";

export default createVuetify({
  theme: {
    defaultTheme: "dark",
    themes: {
      dark: {
        colors: {
          background: "#0f161f",
        },
      },
    },
  },
});

// https://vuetifyjs.com/en/introduction/why-vuetify/#feature-guides
