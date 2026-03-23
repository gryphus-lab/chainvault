import { useQuery } from "@tanstack/react-query";
import { getMigrations, getStats } from "../lib/api";
import type { Migration, MigrationStats } from "../types";

export function useMigrationStats() {
  return useQuery<MigrationStats>({
    queryKey: ["migration-stats"],
    queryFn: getStats,
    staleTime: 30 * 1000,
  });
}

export function useMigrations() {
  return useQuery<Migration[]>({
    queryKey: ["migrations"],
    queryFn: () => getMigrations({ limit: 50 }),
    staleTime: 60 * 1000,
  });
}
