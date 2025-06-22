import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Only add auth header for API requests
  if (req.url.startsWith('/api/')) {
    const token = localStorage.getItem('authToken');
    
    if (token) {
      const authReq = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`)
      });
      return next(authReq);
    }
  }
  
  return next(req);
};
