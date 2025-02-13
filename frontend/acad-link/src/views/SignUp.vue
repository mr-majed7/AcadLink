<template>
  <v-container class="fill-height gradient-background" fluid>
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="6" lg="4">
        <v-card class="elevation-12 rounded-lg">
          <v-toolbar dark flat>
            <v-toolbar-title>Create an Account</v-toolbar-title>
          </v-toolbar>
          <v-card-text class="py-5 px-4">
            <v-form @submit.prevent="signUp" ref="form">
              <v-text-field
                v-model="firstName"
                label="First Name"
                name="firstName"
                placeholder="Enter your first name"
                prepend-icon="mdi-account"
                required
                dense
              ></v-text-field>
              <v-text-field
                v-model="lastName"
                label="Last Name"
                name="lastName"
                placeholder="Enter your last name"
                prepend-icon="mdi-account"
                required
                dense
              ></v-text-field>
              <v-text-field
                v-model="email"
                label="Email"
                name="email"
                placeholder="Enter your email"
                prepend-icon="mdi-email"
                type="email"
                :rules="[v => !!v || 'Email is required', v => /.+@.+\..+/.test(v) || 'Invalid email']"
                required
                dense
              ></v-text-field>
              <v-checkbox
                v-model="enrolled"
                label="Currently enrolled in any educational institution?"
                class="mt-3"
              ></v-checkbox>
              <v-text-field
                v-if="enrolled"
                v-model="institution"
                label="Institution Name"
                name="institution"
                placeholder="Enter your institution name"
                prepend-icon="mdi-school"
                required
                dense
              ></v-text-field>
              <v-text-field
                v-model="username"
                label="Username"
                name="username"
                placeholder="Enter your username"
                prepend-icon="mdi-account"
                required
                dense
                @blur="checkUsernameAvailability"
                :error-messages="usernameError"
              />
              <v-text-field
                v-model="password"
                label="Password"
                name="password"
                placeholder="Enter your password"
                prepend-icon="mdi-lock"
                :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                :type="showPassword ? 'text' : 'password'"
                :rules="[v => !!v || 'Password is required', v => v.length >= 8 || 'Minimum 8 characters', v => /[0-9!@#$%^&*]/.test(v) || 'Include a number or special character']"
                required
                dense
                @click:append="showPassword = !showPassword"
              ></v-text-field>
              <v-text-field
                v-model="confirmPassword"
                label="Confirm Password"
                name="confirmPassword"
                placeholder="Re-enter your password"
                prepend-icon="mdi-lock-check"
                :type="showPassword ? 'text' : 'password'"
                :rules="[v => !!v || 'Confirm password is required', v => v === password || 'Passwords must match']"
                required
                dense
              ></v-text-field>
            </v-form>
          </v-card-text>
          <v-card-actions class="justify-center pb-4">
            <v-btn
              color="primary"
              @click="signUp"
              :loading="loading"
              :disabled="loading"
              class="px-6"
              large
              elevation="2"
            >
              Sign Up
            </v-btn>
          </v-card-actions>
          <v-card-actions class="justify-center">
            <span>Already signed up? <router-link to="/signin">Sign In</router-link></span>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import axios from 'axios';

export default {
  name: 'SignUp',
  data() {
    return {
      firstName: '',
      lastName: '',
      email: '',
      password: '',
      confirmPassword: '',
      enrolled: false,
      institution: '',
      username: '',
      usernameError: '',
      showPassword: false,
      loading: false,
    };
  },
  methods: {
    async checkUsernameAvailability() {
      if (!this.username) {
        this.usernameError = 'Username is required';
        return;
      }

      try {
        const response = await axios.get(`http://127.0.0.1:8080/public/check-username/${encodeURIComponent(this.username)}`);

        if (response.data===true) {
          this.usernameError = 'Username is already taken';
        } else {
          this.usernameError = '';
        }
      } catch (error) {
        console.error('Error checking username:', error);
        this.usernameError = 'Error checking username';
      }
    },

    async signUp() {
      if (this.$refs.form.validate() && !this.usernameError) {
        this.loading = true;

        try {
          const response = await axios.post('http://127.0.0.1:8080/public/sign-up', {
            firstName: this.firstName,
            lastName: this.lastName,
            userName: this.username,
            email: this.email,
            institute: this.institution,
            password: this.password,
          });

          console.log('Signup successful:', response.data);
          this.$router.push('/signin');
        } catch (error) {
          console.error('Signup error:', error);
        }

        this.loading = false;
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
