<template>
    <v-container class="fill-height gradient-background" fluid>
      <v-row align="center" justify="center">
        <v-col cols="12" sm="8" md="6" lg="4">
          <v-card class="elevation-12 rounded-lg">
            <v-toolbar dark flat>
              <v-toolbar-title>Sign In to AcadLink</v-toolbar-title>
            </v-toolbar>
            <v-card-text class="py-5 px-4">
              <v-form @submit.prevent="signIn" ref="form">
                <v-text-field
                  v-model="email"
                  label="Email"
                  name="email"
                  placeholder="Enter your email"
                  prepend-icon="mdi-email"
                  type="email"
                  :rules="[emailRequired, validEmail]"
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
  export default {
    name: 'SignIn',
    data() {
      return {
        email: '',
        password: '',
        showPassword: false,
        loading: false,
      };
    },
    methods: {
      // Validation Rules
      emailRequired(value) {
        return !!value || 'Email is required';
      },
      validEmail(value) {
        const pattern =
          /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
        return pattern.test(value) || 'Enter a valid email';
      },
      passwordRequired(value) {
        return !!value || 'Password is required';
      },
      async signIn() {
        if (this.$refs.form.validate()) {
          this.loading = true;
          // Simulate a delay for API call
          await new Promise((resolve) => setTimeout(resolve, 1000));
          console.log('Signing in with:', this.email, this.password);
          this.loading = false;
          // Redirect or update the store after successful sign-in
          // this.$router.push('/dashboard');
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
  