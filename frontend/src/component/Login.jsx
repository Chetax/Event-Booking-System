import React from 'react';
import Spline from '@splinetool/react-spline';
import { Link, useNavigate } from 'react-router-dom';
import LeftSideModel from './LeftSideModel';
import Cookies from 'js-cookie';
import { useState , useEffect} from 'react';
import toast from 'react-hot-toast';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const token = Cookies.get("auth_token");
    if (token) {
      // If already logged in, go to home
      navigate("/");
    }
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log(email, password);
    try {
      const response = await fetch('/api/users/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) {
        throw new Error('Login failed');
      }

      const data = await response.json();
      const token = data.token;

      toast.success('Login successful!');
      Cookies.set('auth_token', token, { expires: 7 }); // Store token
      localStorage.setItem("userId", data.data.id);
      console.log("Login Response:", data);



      navigate('/'); // Redirect to home page
    } catch (error) {
      console.error(error);
      toast.error('Login failed, please try again.');
      alert('Invalid email or password');
    }
  };

  return (
    <div className="flex h-screen w-screen overflow-hidden">
      <div className="w-1/2 h-full">
        <LeftSideModel />
      </div>

      <div className="w-1/2 flex items-center justify-center bg-white p-10">
        <div className="w-full max-w-md shadow-lg rounded-lg p-8 bg-white">
          <h2 className="text-3xl font-bold text-indigo-600 mb-6 text-center">Login</h2>

          <form className="space-y-5" onSubmit={handleSubmit}>
            <div>
              <label htmlFor="email" className="block mb-1 font-medium text-gray-700">
                Email Address
              </label>
              <input
                id="email"
                type="email"
                required
                placeholder="you@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full border border-gray-300 rounded px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              />
            </div>

            <div>
              <label htmlFor="password" className="block mb-1 font-medium text-gray-700">
                Password
              </label>
              <input
                id="password"
                type="password"
                required
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full border border-gray-300 rounded px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              />
            </div>

            <button
              type="submit"
              className="w-full bg-indigo-600 text-white py-2 rounded hover:bg-indigo-700 transition"
            >
              Login
            </button>
          </form>

          <p className="text-center text-sm text-gray-600 mt-4">
            Don’t have an account?{' '}
            <Link to="/signup" className="text-indigo-600 hover:underline">
              Sign up
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;
