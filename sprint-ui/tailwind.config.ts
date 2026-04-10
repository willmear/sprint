import type { Config } from "tailwindcss";

const config: Config = {
  content: ["./src/**/*.{js,ts,jsx,tsx,mdx}"],
  theme: {
    extend: {
      colors: {
        ink: "#171717",
        sand: "#f5efe5",
        ember: "#dd5e3f",
        pine: "#264653",
        moss: "#698f3f",
        cloud: "#f8fafc",
        line: "#d8d2c7"
      },
      boxShadow: {
        panel: "0 18px 50px rgba(23, 23, 23, 0.08)"
      },
      backgroundImage: {
        "mesh-radial": "radial-gradient(circle at top left, rgba(221, 94, 63, 0.22), transparent 36%), radial-gradient(circle at 80% 20%, rgba(38, 70, 83, 0.18), transparent 30%), radial-gradient(circle at bottom right, rgba(105, 143, 63, 0.2), transparent 28%)"
      },
      fontFamily: {
        sans: ["Avenir Next", "Segoe UI", "Helvetica Neue", "sans-serif"],
        display: ["Satoshi", "Avenir Next Condensed", "Trebuchet MS", "sans-serif"]
      }
    }
  },
  plugins: []
};

export default config;
