import { navbar } from "vuepress-theme-hope";

export const enNavbar = navbar([
  "/",
  "/demo/",
  {
    text: "Guide",
    icon: "lightbulb",
    prefix: "/guide/",
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
    text: "V2 Docs",
    icon: "book",
    link: "https://theme-hope.vuejs.press/",
  },
]);
