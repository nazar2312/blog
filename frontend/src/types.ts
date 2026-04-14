export interface User {
  id: string;
  username: string;
  email: string;
  role?: 'ADMIN' | 'USER';
}

export interface Category {
  name: string;
  postCount: number;
}

export interface Tag {
  name: string;
  postCount: number;
}

export interface Thought {
  id: string;
  title: string;
  content: string;
  created: string; // ISO date-time
  author: User;
  status: 'PUBLISHED' | 'DRAFT';
  readingTime: number;
  category: Category;
  tags: Tag[];
}

export type View = 'discovery' | 'compose' | 'profile' | 'login' | 'register';
