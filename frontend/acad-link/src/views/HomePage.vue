<template>
    <v-app dark>
      <v-app-bar app color="transparent" dark elevation="0">
        <v-toolbar-title>AcadLink</v-toolbar-title>
        <v-spacer></v-spacer>
        <v-btn text to="/signin">Sign In</v-btn>
        <v-btn outlined to="/signup">Sign Up</v-btn>
      </v-app-bar>
  
      <v-main>
        <v-container fluid class="pa-0">
          <!-- Hero Section with Features -->
          <v-row class="hero" align="center" justify="center">
            <v-col cols="12" class="text-center py-12">
              <h1 class="text-h2 font-weight-bold mb-4">Welcome to AcadLink</h1>
              <p class="text-h5 mb-8">Your ultimate platform for academic collaboration and resource sharing</p>
            </v-col>
            <v-col cols="12">
              <v-row justify="center" class="feature-cards">
                <v-col v-for="(feature, index) in features" :key="index" cols="12" sm="6" md="4" lg="2">
                  <v-card class="mx-auto feature-card" height="100%" dark>
                    <v-card-text class="text-center">
                      <v-icon size="48" class="mb-4">{{ feature.icon }}</v-icon>
                      <h3 class="text-h6 font-weight-bold mb-2">{{ feature.title }}</h3>
                      <p class="text-body-2">{{ feature.description }}</p>
                    </v-card-text>
                  </v-card>
                </v-col>
              </v-row>
            </v-col>
            <v-col cols="12" class="text-center mt-12">
              <v-btn x-large color="primary" to="/signup">Get Started</v-btn>
            </v-col>
          </v-row>
  
          <!-- Call to Action -->
          <v-row class="cta mt-16" align="center" justify="center">
            <v-col cols="12" md="8" class="text-center">
              <h2 class="text-h3 mb-4">Ready to enhance your academic journey?</h2>
              <v-btn x-large color="secondary" to="/signup">Join AcadLink Today</v-btn>
            </v-col>
          </v-row>
        </v-container>
      </v-main>
  
      <v-footer color="transparent" dark app>
        <v-col class="text-center" cols="12">
          {{ new Date().getFullYear() }} — <strong>AcadLink</strong>
        </v-col>
      </v-footer>
    </v-app>
  </template>
  
  <script>
  export default {
    name: 'Home',
    data() {
      return {
        features: [
          {
            title: 'Organize Materials',
            description: 'Create folders and manage your academic resources',
            icon: 'mdi-folder-multiple'
          },
          {
            title: 'Study Smart',
            description: 'Set timers and track progress with AI assistance',
            icon: 'mdi-brain'
          },
          {
            title: 'Collaborate',
            description: 'Post questions and join study groups',
            icon: 'mdi-account-group'
          },
          {
            title: 'Find Resources',
            description: 'Search and bookmark materials easily',
            icon: 'mdi-magnify'
          },
          {
            title: 'Connect',
            description: 'Build your academic network',
            icon: 'mdi-account-network'
          },
          {
            title: 'Group Sessions',
            description: 'Join or create study sessions with chat',
            icon: 'mdi-video'
          }
        ]
      }
    }
  }
  </script>
  
  <style scoped>
  .hero {
    min-height: 100vh;
    background: linear-gradient(135deg, #12100e 0%, #2b4162 100%);
    color: white;
  }
  
  .feature-cards {
    max-width: 1200px;
    margin: 0 auto;
  }
  
  .feature-card {
    background-color: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    transition: all 0.3s ease;
  }
  
  .feature-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
  }
  
  .cta {
    background-color: rgba(44, 44, 44, 0.8);
    padding: 4rem 0;
  }
  </style>
  
  <style>
  body {
    background: linear-gradient(135deg, #12100e 0%, #2b4162 100%);
  }
  </style>
  
  