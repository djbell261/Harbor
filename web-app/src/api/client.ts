const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8081';
const REQUEST_TIMEOUT_MS = 12000;

interface ApiErrorBody {
  message?: string;
  correlationId?: string;
}

export class ApiError extends Error {
  status: number;
  correlationId?: string;

  constructor(message: string, status: number, correlationId?: string) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.correlationId = correlationId;
  }
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const controller = new AbortController();
  const timeoutId = window.setTimeout(() => controller.abort(), REQUEST_TIMEOUT_MS);
  const headers = new Headers(init?.headers);

  if (init?.body && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json');
  }

  try {
    const response = await fetch(`${API_BASE_URL}${path}`, {
      ...init,
      headers,
      signal: controller.signal
    });

    if (!response.ok) {
      const correlationId = response.headers.get('X-Correlation-Id') ?? undefined;
      throw new ApiError(await errorMessage(response), response.status, correlationId);
    }

    if (response.status === 204) {
      return undefined as T;
    }

    return response.json() as Promise<T>;
  } catch (error) {
    if (error instanceof DOMException && error.name === 'AbortError') {
      throw new ApiError('Harbor is taking longer than expected to respond.', 0);
    }

    if (error instanceof ApiError) {
      throw error;
    }

    throw new ApiError('Harbor could not reach the backend service.', 0);
  } finally {
    window.clearTimeout(timeoutId);
  }
}

async function errorMessage(response: Response): Promise<string> {
  const contentType = response.headers.get('Content-Type') ?? '';

  if (contentType.includes('application/json')) {
    const body = (await response.json().catch(() => null)) as ApiErrorBody | null;
    if (body?.message) {
      return body.message;
    }
  }

  const text = await response.text().catch(() => '');
  return text || `Request failed with status ${response.status}`;
}

export const apiClient = {
  get: <T>(path: string) => request<T>(path),
  post: <T>(path: string, body: unknown) =>
    request<T>(path, {
      method: 'POST',
      body: JSON.stringify(body)
    })
};
