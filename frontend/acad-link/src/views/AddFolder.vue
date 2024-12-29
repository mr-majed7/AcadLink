<template>
    <v-container class="fill-height" fluid>
      <v-row align="center" justify="center">
        <v-col cols="12" sm="8" md="6" lg="4">
          <v-card class="elevation-12 rounded-lg">
            <v-toolbar dark flat>
              <v-toolbar-title>Create New Folder</v-toolbar-title>
            </v-toolbar>
            <v-card-text class="py-5 px-4">
              <v-form @submit.prevent="createFolder" ref="form">
                <v-text-field
                  v-model="folderName"
                  label="Folder/Course Name"
                  name="folderName"
                  placeholder="Enter folder or course name"
                  prepend-icon="mdi-folder"
                  :rules="[v => !!v || 'Folder name is required']"
                  required
                  dense
                ></v-text-field>
                <v-select
                v-model="privacy"
                :items="privacyOptions"
                label="Privacy"
                name="privacy"
                prepend-icon="mdi-shield-lock"
                required
                dense
                ></v-select>


                <v-textarea
                  v-model="description"
                  label="Description (Optional)"
                  name="description"
                  placeholder="Enter a brief description of the folder or course"
                  prepend-icon="mdi-text"
                  rows="3"
                  dense
                ></v-textarea>
              </v-form>
            </v-card-text>
            <v-card-actions class="justify-center pb-4">
              <v-btn
                color="primary"
                @click="createFolder"
                :loading="loading"
                :disabled="loading"
                class="px-6"
                large
                elevation="2"
              >
                Create Folder
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </template>
  
  <script>
  import { mapActions } from 'vuex';
  
  export default {
    name: 'AddFolderView',
    data() {
      return {
        folderName: '',
        privacy: 'Private',
        description: '',
        privacyOptions: ['Private', 'Public', 'Shared'],
        loading: false,
      };
    },
    methods: {
      ...mapActions(['createNewFolder']),
      async createFolder() {
        if (this.$refs.form.validate()) {
          this.loading = true;
          try {
            await this.createNewFolder({
              name: this.folderName,
              privacy: this.privacy,
              description: this.description
            });
            this.$router.push('/dashboard'); // Redirect to dashboard after creation
          } catch (error) {
            console.error('Error creating folder:', error);
            // Handle error (e.g., show error message to user)
          } finally {
            this.loading = false;
          }
        }
      }
    }
  };
  </script>
  
  <style scoped>
  .v-toolbar {
    background: linear-gradient(135deg, #12100e 0%, #2b4162 100%);
    color: #fff;
    border-radius: 16px 16px 0 0;
  }
  
  .v-card {
    background: linear-gradient(135deg, #12100e 0%, #2b4162 100%);
    transition: all 0.3s ease-in-out;
    border-radius: 16px;
  }
  
  .v-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.2);
  }
  
  .v-btn {
    text-transform: none;
    font-weight: 600;
    letter-spacing: 0.5px;
  }
  
  .v-btn:hover {
    box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
  }
  </style>
  
  