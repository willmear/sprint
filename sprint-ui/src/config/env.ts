const fallbackApiBaseUrl = "http://localhost:8080";

export const env = {
  apiBaseUrl: process.env.NEXT_PUBLIC_API_BASE_URL ?? fallbackApiBaseUrl,
};
