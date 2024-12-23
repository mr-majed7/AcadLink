<template>
    <v-app-bar app dark elevation="0" class="gradient-background transparent">
      <!-- AcadLink Title -->
      <v-row align="center" class="ml-4">
        <v-col cols="auto">
          <v-toolbar-title class="text-h6">
            <router-link to="/dashboard" style="color: white; text-decoration: none;">
              AcadLink
            </router-link>
          </v-toolbar-title>
        </v-col>
  
        <!-- Search Bar -->
        <v-col cols="auto" class="ml-4">
          <v-text-field
            v-model="search"
            dense
            flat
            hide-details
            prepend-inner-icon="mdi-magnify"
            placeholder="Search materials, questions, or users..."
            class="custom-search"
            @keyup.enter="performSearch"
          ></v-text-field>
        </v-col>
      </v-row>
  
      <v-spacer></v-spacer>
  
      <!-- Navbar Icons -->
      <v-btn icon to="/materials" v-tooltip="'My Materials'">
        <v-icon>mdi-folder-multiple</v-icon>
      </v-btn>
      <v-btn icon to="/timer" v-tooltip="'Pomodoro Timer'">
        <v-icon>mdi-timer</v-icon>
      </v-btn>
      <v-btn icon to="/forum" v-tooltip="'Q&A Forum'">
        <v-icon>mdi-frequently-asked-questions</v-icon>
      </v-btn>
      <v-btn icon to="/groups" v-tooltip="'Study Groups'">
        <v-icon>mdi-account-group</v-icon>
      </v-btn>
  
      <!-- Notifications and User Menu -->
      <v-menu offset-y>
        <template v-slot:activator="{ on, attrs }">
          <v-btn icon v-bind="attrs" v-on="on">
            <v-badge dot color="error" v-if="hasNotifications">
              <v-icon>mdi-bell</v-icon>
            </v-badge>
            <v-icon v-else>mdi-bell</v-icon>
          </v-btn>
        </template>
        <v-list>
          <v-list-item v-for="notification in notifications" :key="notification.id">
            <v-list-item-content>
              <v-list-item-title v-text="notification.text"></v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list>
      </v-menu>
  
      <v-menu offset-y>
        <template v-slot:activator="{ on, attrs }">
          <v-btn text v-bind="attrs" v-on="on" class="text-none">
            <v-avatar size="32" color="primary" class="mr-2">
              <span class="white--text text-subtitle-2">{{ userInitials }}</span>
            </v-avatar>
            {{ userName }}
            <v-icon right>mdi-chevron-down</v-icon>
          </v-btn>
        </template>
        <v-list>
          <v-list-item to="/profile">
            <v-list-item-icon class="mr-2">
              <v-icon small>mdi-account</v-icon>
            </v-list-item-icon>
            <v-list-item-title>Profile</v-list-item-title>
          </v-list-item>
          <v-list-item to="/settings">
            <v-list-item-icon class="mr-2">
              <v-icon small>mdi-cog</v-icon>
            </v-list-item-icon>
            <v-list-item-title>Settings</v-list-item-title>
          </v-list-item>
          <v-divider></v-divider>
          <v-list-item @click="logout">
            <v-list-item-icon class="mr-2">
              <v-icon small>mdi-logout</v-icon>
            </v-list-item-icon>
            <v-list-item-title>Logout</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </v-app-bar>
  </template>
  
  <script>
  export default {
    name: 'CommonNavbar',
    data: () => ({
      search: '',
      notifications: [
        { id: 1, text: 'New message in study group' },
        { id: 2, text: 'Your question has been answered' },
      ],
      userName: 'John Doe', // Dynamically set based on the logged-in user
    }),
    computed: {
      hasNotifications() {
        return this.notifications.length > 0;
      },
      userInitials() {
        return this.userName.split(' ').map(n => n[0]).join('');
      },
    },
    methods: {
      performSearch() {
        console.log('Searching for:', this.search);
        this.$router.push({ name: 'search-results', query: { q: this.search } });
      },
      logout() {
        console.log('Logging out');
        this.$router.push('/login');
      },
    },
  };
  </script>
  
  <style scoped>
  .gradient-background {
    background: linear-gradient(135deg, #12100e 0%, #2b4162 100%);
  }
  
  .custom-search {
    width: 500px;
    border-radius: 8px;
    background-color: rgba(255, 255, 255, 0.1);
    color: white;
  }
  
  .custom-search .v-input__control {
    color: white;
  }
  
  .custom-search .v-input__control::placeholder {
    color: rgba(255, 255, 255, 0.7);
  }
  
  .custom-search:hover {
    background-color: rgba(255, 255, 255, 0.2);
  }
  </style>
  