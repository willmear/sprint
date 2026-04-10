import { env } from "@/config/env";
import type { ApiErrorResponse } from "@/types/api";

export class ApiError extends Error {
  readonly status: number;
  readonly details?: ApiErrorResponse | string;

  constructor(message: string, status: number, details?: ApiErrorResponse | string) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.details = details;
  }
}

type ApiRequestInit = Omit<RequestInit, "body"> & {
  body?: unknown;
};

export async function apiClient<T>(path: string, init: ApiRequestInit = {}): Promise<T> {
  const response = await fetch(`${env.apiBaseUrl}${path}`, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      ...(init.headers ?? {}),
    },
    body: init.body === undefined ? undefined : JSON.stringify(init.body),
    cache: "no-store",
  });

  if (!response.ok) {
    const errorBody = await parseErrorBody(response);
    throw new ApiError(resolveErrorMessage(response.status, errorBody), response.status, errorBody);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return (await response.json()) as T;
}

async function parseErrorBody(response: Response): Promise<ApiErrorResponse | string | undefined> {
  const contentType = response.headers.get("content-type") ?? "";
  if (contentType.includes("application/json")) {
    return (await response.json()) as ApiErrorResponse;
  }

  const text = await response.text();
  return text || undefined;
}

function resolveErrorMessage(status: number, details?: ApiErrorResponse | string) {
  if (typeof details === "string" && details.trim()) {
    return details;
  }
  if (details && typeof details === "object") {
    if (details.message) {
      return details.message;
    }
    if (details.error) {
      return details.error;
    }
  }
  return `Request failed with status ${status}.`;
}
