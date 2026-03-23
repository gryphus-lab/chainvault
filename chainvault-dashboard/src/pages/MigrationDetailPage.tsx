import { useParams, Link } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { format, parseISO } from "date-fns";
import {
  ArrowLeft,
} from "lucide-react";
import { getMigrationById } from "../lib/api";
import type { Migration } from "../types";
import Timeline from "../components/Dashboard/Timeline";
import { Badge } from "../components/ui/Badge";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "../components/ui/Card";

export default function MigrationDetailPage() {
  const { id } = useParams<{ id: string }>();

  const {
    data: migration,
    isLoading,
    error,
  } = useQuery<Migration>({
    queryKey: ["migration", id],
    queryFn: () => getMigrationById(id!),
    enabled: !!id,
  });

  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-64">
        Loading migration...
      </div>
    );
  }

  if (error || !migration) {
    return (
      <div className="text-center py-12 text-red-600">
        Failed to load migration {id}.{" "}
        <Link to="/" className="underline">
          Go back
        </Link>
      </div>
    );
  }

  const statusColor =
    {
      SUCCESS: "bg-green-100 text-green-800",
      FAILED: "bg-red-100 text-red-800",
      COMPENSATED: "bg-yellow-100 text-yellow-800",
      RUNNING: "bg-blue-100 text-blue-800",
      PENDING: "bg-gray-100 text-gray-800",
    }[migration.status] || "bg-gray-100 text-gray-800";

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <Link to="/" className="text-gray-600 hover:text-gray-900">
            <ArrowLeft className="h-6 w-6" />
          </Link>
          <h1 className="text-2xl font-bold text-gray-900">
            Migration {migration.id}
          </h1>
        </div>
        <Badge className={statusColor}>{migration.status}</Badge>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-500">
              Document
            </CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-2xl font-bold">{migration.title}</p>
            <p className="text-sm text-gray-500 mt-1">ID: {migration.docId}</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-500">
              Created
            </CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-2xl font-bold">
              {format(parseISO(migration.createdAt), "PPp")}
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-500">
              Pages
            </CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-2xl font-bold">{migration.pageCount}</p>
            <p className="text-sm text-gray-500 mt-1">
              OCR attempted: {migration.ocrAttempted ? "Yes" : "No"}
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-gray-500">
              Trace ID
            </CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-lg font-mono break-all">
              {migration.traceId || "—"}
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Timeline */}
      <Card>
        <CardHeader>
          <CardTitle>Migration Timeline</CardTitle>
        </CardHeader>
        <CardContent>
          <Timeline migration={migration} />
        </CardContent>
      </Card>

      {/* OCR Preview / Logs (placeholder) */}
      <Card>
        <CardHeader>
          <CardTitle>OCR & Processing Details</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            <div>
              <h4 className="font-medium">OCR Status</h4>
              <p>
                {migration.ocrSuccess === true ? (
                  <span className="text-green-600">Success</span>
                ) : migration.ocrSuccess === false ? (
                  <span className="text-red-600">Failed</span>
                ) : (
                  "Not attempted"
                )}
              </p>
            </div>
            {migration.ocrTotalTextLength && (
              <div>
                <h4 className="font-medium">Extracted Text Length</h4>
                <p>
                  {migration.ocrTotalTextLength.toLocaleString()} characters
                </p>
              </div>
            )}
            {migration.failureReason && (
              <div>
                <h4 className="font-medium text-red-600">Failure Reason</h4>
                <p className="text-red-700">{migration.failureReason}</p>
              </div>
            )}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
