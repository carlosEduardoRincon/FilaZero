import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

const apiProxy = {
  "/api": { target: "http://localhost:8080", changeOrigin: true },
  "/health": { target: "http://localhost:8080", changeOrigin: true },
};

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: apiProxy,
  },
  // `npm run preview` não herdava proxy — sem isso, /api ia para a porta 4173 e quebrava.
  preview: {
    port: 4173,
    proxy: apiProxy,
  },
});
