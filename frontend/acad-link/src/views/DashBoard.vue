<template>
    <v-app dark>  
      <!-- Main Content -->
      <v-main class="dashboard-background">
        <v-container fluid class="pa-6">
          <!-- Main Grid Layout -->
          <v-row>
            <!-- Materials Section -->
            <v-col cols="12" lg="4">
              <v-card class="mb-6" elevation="2">
                <v-card-title class="d-flex align-center py-3">
                  <v-icon left color="primary">mdi-folder-multiple</v-icon>
                  My Materials
                  <v-spacer></v-spacer>
                  <v-btn
                    small
                    text
                    color="primary"
                    class="px-2"
                    to="/materials"
                  >
                    View All
                    <v-icon right small>mdi-chevron-right</v-icon>
                  </v-btn>
                </v-card-title>
                <v-divider></v-divider>
                <v-list>
                  <v-list-item v-for="(material, index) in recentMaterials" :key="index" link>
                    <v-list-item-avatar>
                      <v-icon :color="material.color">{{ material.icon }}</v-icon>
                    </v-list-item-avatar>
                    <v-list-item-content>
                      <v-list-item-title class="font-weight-medium">
                        {{ material.name }}
                      </v-list-item-title>
                      <v-list-item-subtitle>{{ material.course }}</v-list-item-subtitle>
                    </v-list-item-content>
                    <v-list-item-action>
                      <v-btn icon small>
                        <v-icon small>mdi-dots-vertical</v-icon>
                      </v-btn>
                    </v-list-item-action>
                  </v-list-item>
                </v-list>
              </v-card>
  
              <!-- Study Timer -->
              <v-card elevation="2">
                <v-card-title class="py-3">
                  <v-icon left color="primary">mdi-clock-outline</v-icon>
                  Study Timer
                </v-card-title>
                <v-divider></v-divider>
                <v-card-text class="text-center pa-6">
                  <div class="text-h2 font-weight-bold mb-4">25:00</div>
                  <v-btn-toggle v-model="timerMode" mandatory class="mb-4">
                    <v-btn value="focus">Focus</v-btn>
                    <v-btn value="break">Break</v-btn>
                  </v-btn-toggle>
                  <v-btn
                    color="primary"
                    block
                    large
                    :loading="timerRunning"
                    @click="toggleTimer"
                  >
                    {{ timerRunning ? 'Pause' : 'Start' }}
                  </v-btn>
                </v-card-text>
              </v-card>
            </v-col>
  
            <!-- Middle Section -->
            <v-col cols="12" lg="4">
              <!-- Study Progress -->
              <v-card class="mb-6" elevation="2">
                <v-card-title class="d-flex align-center py-3">
                  <v-icon left color="primary">mdi-chart-line</v-icon>
                  Study Progress
                </v-card-title>
                <v-divider></v-divider>
                <v-card-text class="pa-4">
                  <div v-for="(course, index) in courseProgress" :key="index" class="mb-4">
                    <div class="d-flex justify-space-between mb-1">
                      <span class="font-weight-medium">{{ course.name }}</span>
                      <span class="text-caption">{{ course.progress }}%</span>
                    </div>
                    <v-progress-linear
                      :value="course.progress"
                      height="8"
                      rounded
                      :color="course.color"
                    ></v-progress-linear>
                  </div>
                </v-card-text>
              </v-card>
  
              <!-- Recent Questions -->
              <v-card elevation="2">
                <v-card-title class="d-flex align-center py-3">
                  <v-icon left color="primary">mdi-frequently-asked-questions</v-icon>
                  Recent Questions
                  <v-spacer></v-spacer>
                  <v-btn
                    small
                    text
                    color="primary"
                    class="px-2"
                    to="/forum"
                  >
                    View All
                    <v-icon right small>mdi-chevron-right</v-icon>
                  </v-btn>
                </v-card-title>
                <v-divider></v-divider>
                <v-list two-line>
                  <v-list-item v-for="(question, index) in recentQuestions" :key="index" link>
                    <v-list-item-content>
                      <v-list-item-title class="font-weight-medium">
                        {{ question.title }}
                      </v-list-item-title>
                      <v-list-item-subtitle>
                        {{ question.askedBy }} • {{ question.timeAgo }}
                      </v-list-item-subtitle>
                    </v-list-item-content>
                    <v-list-item-action>
                      <v-chip x-small outlined>
                        {{ question.answers }} answers
                      </v-chip>
                    </v-list-item-action>
                  </v-list-item>
                </v-list>
              </v-card>
            </v-col>
  
            <!-- Study Groups Section -->
            <v-col cols="12" lg="4">
              <v-card elevation="2">
                <v-card-title class="d-flex align-center py-3">
                  <v-icon left color="primary">mdi-account-group</v-icon>
                  Study Groups
                  <v-spacer></v-spacer>
                  <v-btn
                    small
                    text
                    color="primary"
                    class="px-2"
                    to="/groups"
                  >
                    Find More
                    <v-icon right small>mdi-chevron-right</v-icon>
                  </v-btn>
                </v-card-title>
                <v-divider></v-divider>
                <v-list>
                  <v-list-item v-for="(group, index) in studyGroups" :key="index">
                    <v-list-item-content>
                      <v-list-item-title class="font-weight-medium">
                        {{ group.name }}
                      </v-list-item-title>
                      <v-list-item-subtitle>
                        <v-icon small left>mdi-account-multiple</v-icon>
                        {{ group.members }} members
                      </v-list-item-subtitle>
                    </v-list-item-content>
                    <v-list-item-action>
                      <v-btn
                        small
                        outlined
                        color="primary"
                        :loading="group.joining"
                        @click="joinGroup(index)"
                      >
                        Join
                      </v-btn>
                    </v-list-item-action>
                  </v-list-item>
                </v-list>
              </v-card>
            </v-col>
          </v-row>
        </v-container>
      </v-main>
    </v-app>
  </template>
  
  <script>
  export default {
    name: 'Dashboard',
    data: () => ({
      drawer: true,
      timerMode: 'focus',
      timerRunning: false,
      menuItems: [
        { title: 'Dashboard', icon: 'mdi-view-dashboard', to: '/dashboard' },
        { title: 'My Materials', icon: 'mdi-folder-multiple', to: '/materials' },
        { title: 'Study Groups', icon: 'mdi-account-group', to: '/groups' },
        { title: 'Q&A Forum', icon: 'mdi-frequently-asked-questions', to: '/forum' },
        { title: 'Profile', icon: 'mdi-account', to: '/profile' },
      ],
      recentMaterials: [
        { name: 'Calculus Notes', course: 'MATH 101', icon: 'mdi-notebook', color: 'blue' },
        { name: 'Physics Lab Report', course: 'PHYS 201', icon: 'mdi-file-document', color: 'green' },
        { name: 'Literature Essay', course: 'ENGL 301', icon: 'mdi-file-document-edit', color: 'purple' },
      ],
      courseProgress: [
        { name: 'MATH 101', progress: 75, color: 'blue' },
        { name: 'PHYS 201', progress: 60, color: 'green' },
        { name: 'ENGL 301', progress: 90, color: 'purple' },
      ],
      studyGroups: [
        { name: 'Calculus Study Group', members: 15, joining: false },
        { name: 'Physics Lab Prep', members: 8, joining: false },
        { name: 'Literature Discussion', members: 12, joining: false },
      ],
      recentQuestions: [
        { title: 'How to solve this integral?', askedBy: 'John D.', timeAgo: '2h ago', answers: 3 },
        { title: 'Explanation for Newton\'s Third Law', askedBy: 'Sarah M.', timeAgo: '5h ago', answers: 2 },
        { title: 'Analysis of Hamlet\'s soliloquy', askedBy: 'Emma W.', timeAgo: '1d ago', answers: 5 },
      ],
    }),
    methods: {
      toggleTimer() {
        this.timerRunning = !this.timerRunning;
      },
      joinGroup(index) {
        this.$set(this.studyGroups[index], 'joining', true);
        // Simulate API call
        setTimeout(() => {
          this.$set(this.studyGroups[index], 'joining', false);
        }, 1000);
      },
      logout() {
        // Implement logout logic
        this.$router.push('/signin');
      },
    },
  }
  </script>
  
  <style scoped>
  .dashboard-background {
    background: linear-gradient(135deg, #12100e 0%, #2b4162 100%);
    min-height: 100vh;
  }
  
  .gradient-background {
    background: linear-gradient(135deg, #12100e 0%, #2b4162 100%);
  }
  
  .v-card {
    backdrop-filter: blur(10px);
    background-color: rgba(255, 255, 255, 0.05) !important;
    border: 1px solid rgba(255, 255, 255, 0.1);
  }
  
  .v-list {
    background-color: transparent !important;
  }
  
  .v-list-item:not(:last-child) {
    border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  }
  
  .v-card-title {
    font-size: 1.1rem !important;
  }
  </style>
  
  