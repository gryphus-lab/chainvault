import { Cn } from "../../lib/utils";
import * as React from "react";

interface CardProps extends React.HTMLAttributes<HTMLDivElement> {}

export function Card({ className, ...props }: CardProps) {
  return (
    <div
      className={Cn("bg-white shadow rounded-lg overflow-hidden", className)}
      {...props}
    />
  );
}

export function CardHeader({ className, ...props }: CardProps) {
  return (
    <div
      className={Cn("px-6 py-5 border-b border-gray-200", className)}
      {...props}
    />
  );
}

export function CardTitle({ className, ...props }: CardProps) {
  return (
    <h3
      className={Cn("text-lg font-medium text-gray-900", className)}
      {...props}
    />
  );
}

export function CardContent({ className, ...props }: CardProps) {
  return <div className={Cn("px-6 py-5", className)} {...props} />;
}
