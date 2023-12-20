import { navbar } from "vuepress-theme-hope";

export const zhNavbar = navbar([
  "/zh/",
  "/zh/demo/",
  {
    text: "指南",
    icon: "lightbulb",
    prefix: "/zh/guide/",
    children: [
      {
        text: "yucong",
        icon: "lightbulb",
        prefix: "yucong/",
        children: ["yc-action-server", "yc-hub", "yc-process"],
      },
    ],
  },
  {
    text: "V2 文档",
    icon: "book",
    link: "https://theme-hope.vuejs.press/zh/",
  },
]);
