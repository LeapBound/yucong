import { defineUserConfig } from "vuepress";
import theme from "./theme.js";

export default defineUserConfig({
  base: "/",

  locales: {
    "/": {
      lang: "en-US",
      title: "LeapBound",
      description: "In Leaps and Bounds",
    },
    "/zh/": {
      lang: "zh-CN",
      title: "LeapBound",
      description: "突飞猛进",
    },
  },

  theme,

  // Enable it with pwa
  // shouldPrefetch: false,
});
