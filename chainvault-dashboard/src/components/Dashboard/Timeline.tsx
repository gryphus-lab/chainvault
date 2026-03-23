import { format, parseISO } from "date-fns";
import { CheckCircle, Clock, XCircle } from "lucide-react";
import type { Migration } from "../../types";

interface TimelineProps {
  migration: Migration;
}

// Mock timeline steps (replace with real data from backend later)
const mockSteps = [
  {
    id: 1,
    name: "Extract & Hash",
    status: "SUCCESS",
    timestamp: "2026-03-23T18:45:00Z",
  },
  {
    id: 2,
    name: "Sign TIFF Pages",
    status: "SUCCESS",
    timestamp: "2026-03-23T18:46:30Z",
  },
  {
    id: 3,
    name: "Perform OCR",
    status: "SUCCESS",
    timestamp: "2026-03-23T18:48:10Z",
  },
  {
    id: 4,
    name: "Merge to PDF",
    status: "SUCCESS",
    timestamp: "2026-03-23T18:50:00Z",
  },
  {
    id: 5,
    name: "Create Chain ZIP",
    status: "SUCCESS",
    timestamp: "2026-03-23T18:51:20Z",
  },
  {
    id: 6,
    name: "Upload to SFTP",
    status: "SUCCESS",
    timestamp: "2026-03-23T18:52:45Z",
  },
];

function getColors(
  step:
    | { id: number; name: string; status: string; timestamp: string }
    | {
        id: number;
        name: string;
        status: string;
        timestamp: string;
      }
    | { id: number; name: string; status: string; timestamp: string }
    | {
        id: number;
        name: string;
        status: string;
        timestamp: string;
      }
    | { id: number; name: string; status: string; timestamp: string }
    | {
        id: number;
        name: string;
        status: string;
        timestamp: string;
      },
) {
  return step.status === "SUCCESS"
    ? "bg-green-500"
    : step.status === "FAILED"
      ? "bg-red-500"
      : "bg-gray-400";
}

export default function Timeline({ migration }: TimelineProps) {
  // In real app: fetch steps from API or use migration events
  const steps = mockSteps; // replace with real data

  return (
    <div className="flow-root">
      <ul role="list" className="-mb-8">
        {steps.map((step, stepIdx) => (
          <li key={step.id}>
            <div className="relative pb-8">
              {stepIdx !== steps.length - 1 ? (
                <span
                  className="absolute left-4 top-4 -ml-px h-full w-0.5 bg-gray-200"
                  aria-hidden="true"
                />
              ) : null}
              <div className="relative flex space-x-3">
                <span
                  className={`flex h-8 w-8 items-center justify-center rounded-full ring-8 ring-white ${getColors(
                    step,
                  )}`}
                >
                  {step.status === "SUCCESS" ? (
                    <CheckCircle className="h-5 w-5 text-white" />
                  ) : step.status === "FAILED" ? (
                    <XCircle className="h-5 w-5 text-white" />
                  ) : (
                    <Clock className="h-5 w-5 text-white" />
                  )}
                </span>
                <div className="flex min-w-0 flex-1 justify-between space-x-4 pt-1.5">
                  <div>
                    <p className="text-sm font-medium text-gray-900">
                      {step.name}
                    </p>
                    {step.status === "FAILED" && (
                      <p className="mt-1 text-sm text-red-600">Failed</p>
                    )}
                  </div>
                  <div className="whitespace-nowrap text-right text-sm text-gray-500">
                    {format(parseISO(step.timestamp), "PPp")}
                  </div>
                </div>
              </div>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}
