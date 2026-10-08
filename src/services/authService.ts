import apiClient from './api';
import { User, UserRole } from '../types';

/**
 * =========================================================================
 * AUTHENTICATION SERVICE - Spring Boot Connected
 * =========================================================================
 * Endpoints:
 * - POST /api/auth/login
 * - POST /api/auth/register
 * - GET  /api/auth/me
 * =========================================================================
 */

export interface LoginCredentials {
  email: string;
  password?: string;
  rememberMe?: boolean;
}

export interface StudentRegisterPayload {
  name: string;
  email: string;
  phone: string;
  password?: string;
  department?: string;
  year?: string;
}

function normalizeUser(userData: any): User {
  const rawRole = userData.role ? String(userData.role).toLowerCase() : 'student';
  const role: UserRole = rawRole === 'faculty' ? 'faculty' : 'student';

  return {
    id: String(userData.id),
    name: userData.name || '',
    email: userData.email || '',
    role,
    phone: userData.phone || '',
    department: userData.department || 'General Studies',
    year: userData.year || '3rd Year (Junior)',
    designation: userData.designation || undefined,
    avatar: userData.avatar || (role === 'student'
      ? 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=300'
      : 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=300'),
  };
}

export const authService = {
  // Student Login
  async studentLogin(credentials: LoginCredentials): Promise<{ user: User; token: string }> {
    try {
      const response = await apiClient.post('/auth/login', {
        email: credentials.email,
        password: credentials.password,
      });

      const token = response.data.token;
      const user = normalizeUser(response.data.user);

      localStorage.setItem('token', token);
      localStorage.setItem('cems_jwt_token', token);
      localStorage.setItem('cems_current_user', JSON.stringify(user));

      return { user, token };
    } catch (error: any) {
      // If network error (backend not running locally), provide seamless fallback for UI preview
      if (!error.response || error.code === 'ERR_NETWORK' || error.message?.includes('Network Error')) {
        console.info('[AuthService] Backend not detected on http://localhost:8080. Operating with fallback demo session.');
        const dummyToken = 'spring-boot-jwt-token-student-' + Date.now();
        const demoUser: User = {
          id: '1',
          name: credentials.email.includes('varshitha') ? 'Varshitha Chitimireddy' : 'Student Scholar',
          email: credentials.email,
          role: 'student',
          phone: '+1 (555) 234-8901',
          department: 'Computer Science & Engineering',
          year: '3rd Year (Junior)',
          avatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=300',
        };

        localStorage.setItem('token', dummyToken);
        localStorage.setItem('cems_jwt_token', dummyToken);
        localStorage.setItem('cems_current_user', JSON.stringify(demoUser));
        return { user: demoUser, token: dummyToken };
      }

      // If real backend responded with error (e.g. 401 Bad Credentials)
      const errorMsg = error.response?.data?.message || 'Login failed. Please verify your credentials.';
      throw new Error(errorMsg);
    }
  },

  // Faculty Login
  async facultyLogin(credentials: LoginCredentials): Promise<{ user: User; token: string }> {
    try {
      const response = await apiClient.post('/auth/login', {
        email: credentials.email,
        password: credentials.password,
      });

      const token = response.data.token;
      const user = normalizeUser(response.data.user);

      localStorage.setItem('token', token);
      localStorage.setItem('cems_jwt_token', token);
      localStorage.setItem('cems_current_user', JSON.stringify(user));

      return { user, token };
    } catch (error: any) {
      if (!error.response || error.code === 'ERR_NETWORK' || error.message?.includes('Network Error')) {
        console.info('[AuthService] Backend not detected on http://localhost:8080. Operating with fallback demo session.');
        const dummyToken = 'spring-boot-jwt-token-faculty-' + Date.now();
        const demoUser: User = {
          id: '2',
          name: credentials.email.includes('vance') ? 'Dr. Robert Vance' : 'Faculty Coordinator',
          email: credentials.email,
          role: 'faculty',
          phone: '+1 (555) 472-9102',
          department: 'School of Computing & Engineering',
          designation: 'Associate Dean of Student Activities & Event Coordinator',
          avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=300',
        };

        localStorage.setItem('token', dummyToken);
        localStorage.setItem('cems_jwt_token', dummyToken);
        localStorage.setItem('cems_current_user', JSON.stringify(demoUser));
        return { user: demoUser, token: dummyToken };
      }

      const errorMsg = error.response?.data?.message || 'Login failed. Please verify your credentials.';
      throw new Error(errorMsg);
    }
  },

  // Student Registration
  async registerStudent(payload: StudentRegisterPayload): Promise<{ user: User; token: string }> {
    try {
      const response = await apiClient.post('/auth/register', {
        name: payload.name,
        email: payload.email,
        phone: payload.phone,
        password: payload.password,
        department: payload.department || 'Computer Science & Engineering',
        year: payload.year || '1st Year (Freshman)',
        role: 'STUDENT',
      });

      const token = response.data.token;
      const user = normalizeUser(response.data.user);

      localStorage.setItem('token', token);
      localStorage.setItem('cems_jwt_token', token);
      localStorage.setItem('cems_current_user', JSON.stringify(user));

      return { user, token };
    } catch (error: any) {
      if (!error.response || error.code === 'ERR_NETWORK' || error.message?.includes('Network Error')) {
        console.info('[AuthService] Backend not detected on http://localhost:8080. Simulating registration fallback.');
        const dummyToken = 'spring-boot-jwt-token-registered-' + Date.now();
        const demoUser: User = {
          id: String(Math.floor(Math.random() * 1000) + 10),
          name: payload.name,
          email: payload.email,
          role: 'student',
          phone: payload.phone,
          department: payload.department || 'Computer Science & Engineering',
          year: payload.year || '1st Year (Freshman)',
        };

        localStorage.setItem('token', dummyToken);
        localStorage.setItem('cems_jwt_token', dummyToken);
        localStorage.setItem('cems_current_user', JSON.stringify(demoUser));
        return { user: demoUser, token: dummyToken };
      }

      const errorMsg = error.response?.data?.message || 'Registration failed. Please check your details.';
      throw new Error(errorMsg);
    }
  },

  // Get current user from backend
  async getCurrentUser(): Promise<User | null> {
    try {
      const response = await apiClient.get('/auth/me');
      return normalizeUser(response.data);
    } catch {
      const stored = localStorage.getItem('cems_current_user');
      return stored ? JSON.parse(stored) : null;
    }
  },

  // Logout
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('cems_jwt_token');
    localStorage.removeItem('cems_current_user');
  }
};

