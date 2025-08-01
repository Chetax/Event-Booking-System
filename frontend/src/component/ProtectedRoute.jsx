import React from 'react';
import { Navigate } from 'react-router-dom';
import Cookies from 'js-cookie';

const ProtectedRoute = ({ children }) => {
  const token = Cookies.get("auth_token");
  return token ? children : <Navigate to="/login" replace />;
};

export default ProtectedRoute;
