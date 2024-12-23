<template>
  <v-app>
    <!-- Conditionally Render NavBar -->
    <NavBar v-if="showNavBar" />
    <v-main>
      <router-view />
    </v-main>
  </v-app>
</template>

<script>
import NavBar from './components/NavBar.vue';

export default {
  name: 'App',

  components: {
    NavBar,
  },

  computed: {
    showNavBar() {
      // Hide NavBar for specific routes
      const hiddenRoutes = ['/', '/signin', '/signup'];
      return !hiddenRoutes.includes(this.$route.path);
    },
  },
};
</script>
