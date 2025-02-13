<template>
    <v-container class="fill-height gradient-background" fluid>
      <v-row align="center" justify="center">
        <v-col cols="12" sm="8" md="6" lg="4">
          <v-card class="elevation-12 rounded-lg">
            <v-toolbar dark flat>
              <v-toolbar-title>Sign In to AcadLink</v-toolbar-title>
            </v-toolbar>
            <v-card-text class="py-5 px-4">
              <v-alert v-if="errorMessage" type="error" class="mb-4" dense>
              {{ errorMessage }}
            </v-alert>
              <v-form @submit.prevent="signIn" ref="form">
                <v-text-field
                v-model="emailOrUsername"
                label="Email or Username"
                name="emailOrUsername"
                placeholder="Enter your email or username"
                prepend-icon="mdi-account"
                required
                dense
              ></v-text-field>


    
                <v-text-field
                  v-model="password"
                  label="Password"
                  name="password"
                  placeholder="Enter your password"
                  prepend-icon="mdi-lock"
                  :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                  :type="showPassword ? 'text' : 'password'"
                  :rules="[passwordRequired]"
                  required
                  dense
                  @click:append="showPassword = !showPassword"
                ></v-text-field>
    
                <div class="text-right my-2">
                  <a href="#" class="forgot-password">Forgot Password?</a>
                </div>
              </v-form>
            </v-card-text>
            <v-card-actions class="justify-center pb-4">
              <v-btn
                color="primary"
                @click="signIn"
                :loading="loading"
                :disabled="loading"
                class="px-6"
                large
                elevation="2"
              >
                Sign In
              </v-btn>
            </v-card-actions>
            <v-card-actions class="justify-center">
            <span>Don't have an account? <router-link to="/signup">Sign Up</router-link></span>
          </v-card-actions>

          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </template>
  
  <script>
  import axios from 'axios';
  import { mapMutations } from "vuex";

export default {
  name: "SignIn",
  data() {
    return {
      emailOrUsername: "",
      password: "",
      showPassword: false,
      loading: false,
      errorMessage: "",
    };
  },
  methods: {
    ...mapMutations(["setToken"]),
    async signIn() {
      this.errorMessage = "";

      try {
        const response = await axios.post("http://127.0.0.1:8080/public/login", {
          usernameorEmail: this.emailOrUsername,
          password: this.password,
        });

        if (response.status === 200) {
          const token = response.data;
          this.setToken(token);
          this.$router.push("/dashboard");
          console.log(this.$store.state.authToken);
        }
      } catch (error) {
        console.error("Sign-in error:", error);
        if (error.response && error.response.status === 400) {
          this.errorMessage = "Invalid credentials";
        } else {
          this.errorMessage = "An unexpected error occurred. Please try again.";
        }
      } finally {
        this.loading = false;
      }
    },
  },
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
  
  .forgot-password {
    font-size: 0.9rem;
    color: #3f51b5;
    text-decoration: none;
  }
  
  .forgot-password:hover {
    text-decoration: underline;
  }
  </style>
  