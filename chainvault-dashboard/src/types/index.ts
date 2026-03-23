export interface Migration {
  id: string;
  docId: string;
  title: string;
  status: "PENDING" | "RUNNING" | "SUCCESS" | "FAILED" | "COMPENSATED";
  createdAt: string;
  updatedAt: string;
  processInstanceKey: string;
  traceId?: string;
  pageCount: number;
  ocrAttempted: boolean;
  ocrSuccess?: boolean;
  ocrPageCount?: number;
  ocrTotalTextLength?: number;
  failureReason?: string;
}

export interface MigrationStats {
  total: number;
  pending: number;
  running: number;
  success: number;
  failed: number;
  compensated: number;
  last24h: number;
}
