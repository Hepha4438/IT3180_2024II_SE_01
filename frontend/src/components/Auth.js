
export const isLoggedIn = () => !!localStorage.getItem('token');


export const getRole = () => localStorage.getItem('role');

export const fetchWithAuth = (url, options = {}) => {
  const token = localStorage.getItem('token');
  
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers
  };
  
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  
  return fetch(url, {
    ...options,
    headers
  });
};