import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig(() => {
  return {
    build: {
      outDir: 'build',
    },
    plugins: [react()],
    server: {
      port: 8000,
    },
    test: {
      exclude: ["**/e2e/**", "**/node_modules/**"],
    },
  };
});
