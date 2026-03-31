/*
 * Copyright (c) 2026. Gryphus Lab
 */
import ky from "ky";

const api = ky.create({
  prefixUrl: "/api", // All requests will be prefixed with /api
  timeout: 15000, // 15 seconds timeout
  retry: {
    limit: 2, // Retry failed requests up to 2 times
    methods: ["get", "post", "put", "delete"],
  },
  headers: {
    "Content-Type": "application/json",
  },
});

// Export typed API functions
export const getMigrations = async (params?: { limit?: number }) => {
  return api.get("migrations", { searchParams: params }).json();
};

export const getMigrationStats = async () => {
  return api.get("migrations/stats").json();
};

export const getMigrationDetail = async (id: string) => {
  return api.get(`migrations/${id}`).json();
};

// Optional: Add more methods as needed
export const getMigrationEvents = async (migrationId: string) => {
  return api.get(`migrations/${migrationId}/events`).json();
};

// You can also export the raw ky instance if you need custom requests later
export { api };
