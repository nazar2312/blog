import { Thought } from '../types';

function parseJwt(token: string) {
  try {
    return JSON.parse(atob(token.split('.')[1]));
  } catch {
    return null;
  }
}

class ApiService {
  private token: string | null = localStorage.getItem('token');

  setToken(token: string | null) {
    this.token = token;
    if (token) {
      localStorage.setItem('token', token);
    } else {
      localStorage.removeItem('token');
    }
  }

  private async fetchJson<T>(endpoint: string, options?: RequestInit): Promise<T> {
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
      ...options?.headers,
    };

    if (this.token) {
      headers['Authorization'] = `Bearer ${this.token}`;
    }

    const response = await fetch(endpoint, {
      ...options,
      headers,
      credentials: 'include',
    });

    if (!response.ok) {
      const contentType = response.headers.get('content-type');
      let errorMessage = response.statusText;
      if (contentType?.includes('application/json')) {
        const error = await response.json().catch(() => ({}));
        errorMessage = error.message || errorMessage;
      }
      throw new Error(errorMessage);
    }

    if (response.status === 204) return undefined as T;

    return response.json();
  }

  async getPosts(): Promise<Thought[]> {
    return this.fetchJson<Thought[]>('/api/posts');
  }

  async getPost(id: string): Promise<Thought> {
    return this.fetchJson<Thought>(`/api/posts/${id}`);
  }

  async createPost(post: {
    title: string;
    content: string;
    status: 'PUBLISHED' | 'DRAFT';
    category: { name: string };
    tags: { name: string }[];
  }): Promise<Thought> {
    return this.fetchJson<Thought>('/api/posts', {
      method: 'POST',
      body: JSON.stringify(post),
    });
  }

  async updatePost(id: string, post: {
    title: string;
    content: string;
    status: 'PUBLISHED' | 'DRAFT';
    category: { name: string };
    tags: { name: string }[];
  }): Promise<Thought> {
    return this.fetchJson<Thought>(`/api/posts/${id}`, {
      method: 'PUT',
      body: JSON.stringify(post),
    });
  }

  async deletePost(id: string): Promise<void> {
    return this.fetchJson<void>(`/api/posts/${id}`, { method: 'DELETE' });
  }

  async getCategories(): Promise<{ name: string; postCount: number }[]> {
    return this.fetchJson('/api/categories');
  }

  async getTags(): Promise<{ name: string; postCount: number }[]> {
    return this.fetchJson('/api/tags');
  }

  async login(credentials: { email: string; password: string }) {
    const data = await this.fetchJson<{ token: string }>('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify(credentials),
    });

    this.setToken(data.token);

    const payload = parseJwt(data.token);
    const user = {
      id: payload?.sub || '',
      username: credentials.email.split('@')[0],
      email: credentials.email,
    };
    localStorage.setItem('user', JSON.stringify(user));

    return { token: data.token, user };
  }

  async register(data: { username: string; email: string; password: string }) {
    await this.fetchJson('/api/registration', {
      method: 'POST',
      body: JSON.stringify(data),
    });
    return this.login({ email: data.email, password: data.password });
  }

  async logout(): Promise<void> {
    try {
      await this.fetchJson('/api/auth/logout', { method: 'POST' });
    } finally {
      this.setToken(null);
      localStorage.removeItem('user');
    }
  }
}

export const api = new ApiService();
