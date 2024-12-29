<template>
    <v-container class="fill-height" fluid>
      <v-row align="center" justify="center">
        <v-col cols="12" sm="10" md="8" lg="6">
          <v-card class="elevation-12 rounded-lg">
            <v-toolbar dark flat>
              <v-toolbar-title>Add New Material</v-toolbar-title>
            </v-toolbar>
            <v-card-text class="py-5 px-4">
              <v-form @submit.prevent="addMaterial" ref="form">
                <v-text-field
                  v-model="materialName"
                  label="Material Name"
                  name="materialName"
                  placeholder="Enter material name"
                  prepend-icon="mdi-file-document"
                  :rules="[v => !!v || 'Material name is required']"
                  required
                  dense
                ></v-text-field>
  
                <v-select
                  v-model="category"
                  :items="categories"
                  label="Category"
                  name="category"
                  prepend-icon="mdi-folder"
                  :rules="[v => !!v || 'Category is required']"
                  required
                  dense
                ></v-select>
  
                <v-select
                  v-model="privacy"
                  :items="privacyOptions"
                  label="Privacy"
                  name="privacy"
                  prepend-icon="mdi-shield-lock"
                  :rules="[v => !!v || 'Privacy setting is required']"
                  required
                  dense
                ></v-select>
  
                <v-radio-group v-model="addType" row>
                  <v-radio label="Upload File" value="file"></v-radio>
                  <v-radio label="Add Link" value="link"></v-radio>
                </v-radio-group>
  
                <v-file-input
                  v-if="addType === 'file'"
                  v-model="file"
                  label="Upload File"
                  prepend-icon="mdi-cloud-upload"
                  :rules="[v => !!v || 'File is required']"
                  accept="application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/vnd.ms-powerpoint,application/vnd.openxmlformats-officedocument.presentationml.presentation,video/mp4,video/webm"
                  dense
                ></v-file-input>
  
                <v-text-field
                  v-if="addType === 'link'"
                  v-model="link"
                  label="Material Link"
                  name="link"
                  placeholder="Enter material link"
                  prepend-icon="mdi-link"
                  :rules="[
                    v => !!v || 'Link is required',
                  ]"
                  dense
                ></v-text-field>
  
                <v-textarea
                  v-model="description"
                  label="Description (Optional)"
                  name="description"
                  placeholder="Enter a brief description of the material"
                  prepend-icon="mdi-text"
                  rows="3"
                  dense
                ></v-textarea>
              </v-form>
            </v-card-text>
            <v-card-actions class="justify-center pb-4">
              <v-btn
                color="primary"
                @click="addMaterial"
                :loading="loading"
                :disabled="loading"
                class="px-6"
                large
                elevation="2"
              >
                Add Material
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
    name: 'AddMaterialView',
    data() {
      return {
        materialName: '',
        category: '',
        privacy: '',
        addType: 'file',
        file: null,
        link: '',
        description: '',
        categories: ['Books', 'Lecture Slides', 'Lecture Videos', 'Notes', 'Other Repository Link'],
        privacyOptions: ['Private', 'Public', 'Shared'],
        loading: false,
      };
    },
    methods: {
      ...mapActions(['addNewMaterial']),
      async addMaterial() {
        if (this.$refs.form.validate()) {
          this.loading = true;
          try {
            const materialData = {
              name: this.materialName,
              category: this.category,
              privacy: this.privacy,
              description: this.description,
              type: this.addType,
              content: this.addType === 'file' ? this.file : this.link
            };
  
            await this.addNewMaterial(materialData);
            this.$router.push('/dashboard'); // Redirect to dashboard after adding material
          } catch (error) {
            console.error('Error adding material:', error);
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
  
  