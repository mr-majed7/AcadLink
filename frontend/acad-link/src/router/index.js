import SignIn from "@/views/SignIn.vue";
import { createRouter, createWebHistory } from "vue-router";

const routes = [
  {
    path: "/",
    name: "home",
    component: () =>
      import(/* webpackChunkName: "home" */ "../views/HomePage.vue"),
  },
  {
    path: "/dashboard",
    name: "dashboard",
    component: () =>
      import(/* webpackChunkName: "dashboard" */ "../views/DashBoard.vue"),
  },
  {
    path: "/signin",
    name: "signin",
    component: SignIn,
  },
  {
    path: "/signup",
    name: "signup",
    component: () =>
      import(/* webpackChunkName: "signup" */ "../views/SignUp.vue"),
  },
  {
    path: "/addfolder",
    name: "addfolder",
    component: () =>
      import(/* webpackChunkName: "addfolder" */ "../views/AddFolder.vue"),
  },
  {
    path: "/addmaterial",
    name: "addmaterial",
    component: () =>
      import(/* webpackChunkName: "addmaterail" */ "../views/AddMaterial.vue"),
  },
  {
    path: "/about",
    name: "about",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/AboutView.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
