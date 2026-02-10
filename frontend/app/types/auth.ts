export interface LoginRequest {
  username: string;
  password: string;
  "remember-me"?: string;
}

export interface RegisterRequest {
  username: string;
  name: string;
  email: string;
  dateOfBirth: string;
  phoneNumber: string;
  password: string;
  role: string;
  profileImageUrl?: string;
}
