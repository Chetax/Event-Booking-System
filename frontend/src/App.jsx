import React, { Suspense, lazy } from 'react';
import { Routes, Route } from 'react-router-dom';
import Navbar from './component/Navbar';
import Footer from './component/Footer';
import Profile from './component/Profile';
import ProtectedRoute from './component/ProtectedRoute';


const HeroSection = lazy(() => import('./component/HeroSection'));
const EventPool = lazy(() => import('./component/EventPool'));
const EventPage = lazy(() => import('./component/EventPage'));
const SearchPage = lazy(() => import('./component/SearchPage'));
const BookingPage = lazy(() => import('./component/BookingPage'));
const LoginPage = lazy(() => import('./component/Login'));
const SignupPage = lazy(() => import('./component/Signup'));
const CreateEvent = lazy(() => import('./component/CreateEvent'));


const Loading = () => (
  <div className="flex justify-center items-center h-screen">
    <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-indigo-600"></div>
  </div>
);

class ErrorBoundary extends React.Component {
  state = { hasError: false };

  static getDerivedStateFromError() {
    return { hasError: true };
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="flex flex-col items-center justify-center h-screen text-center p-4">
          <h1 className="text-2xl font-bold text-red-600 mb-4">Something went wrong</h1>
          <p className="text-gray-600 mb-4">Please try refreshing the page.</p>
          <button
            className="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors"
            onClick={() => window.location.reload()}
          >
            Refresh Page
          </button>
        </div>
      );
    }
    return this.props.children;
  }
}

const App = () => {
  return (
    <div className="flex flex-col min-h-screen bg-gray-50">
      <header>
        <Navbar />
      </header>
      <main className="flex-grow max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <ErrorBoundary>
          <Suspense fallback={<Loading />}>
            <Routes>
              <Route
                path="/"
                element={
                  <>
                    <section aria-label="Hero Section">
                      <HeroSection />
                    </section>
                    <section aria-label="Events Section">
                      <EventPool />
                    </section>
                  </>
                }
              />
              {/* <Route
                path="/events/:id"
                element={
                  <section aria-label="Event Details">
                    <EventPage />
                  </section>
                }
              /> */}
              <Route
                path="/events/:id"
                element={
                  <ProtectedRoute>
                    <section aria-label="Event Details">
                      <EventPage />
                    </section>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/search"
                element={
                  <section aria-label="Search Results">
                    <SearchPage />
                  </section>
                }
              />
              {/* <Route
                path="/book/:id"
                element={
                  <section aria-label="Booking Page">
                    <BookingPage />
                  </section>
                }
              /> */}

              <Route
                path="/book/:id"
                element={
                  <ProtectedRoute>
                    <section aria-label="Booking Page">
                      <BookingPage />
                    </section>
                  </ProtectedRoute>
                }
              />

              <Route
                path="/login"
                element={
                  <section aria-label="Login Page">
                    <LoginPage />
                  </section>
                }
              />
              <Route
                path="/signup"
                element={
                  <section aria-label="Signup Page">
                    <SignupPage />
                  </section>
                }
              />
                  {/* <Route
                path="/profile"
                element={
                  <section aria-label="Profile page">
                    <Profile />
                  </section>
                }
              /> */}

              <Route
                path="/profile"
                element={
                  <ProtectedRoute>
                    <section aria-label="Profile Page">
                      <Profile />
                    </section>
                  </ProtectedRoute>
                }
              />
                     <Route
                              path="/create-event"
                              element={
                                <ProtectedRoute>
                                  <section aria-label="Create event Page">
                                    <CreateEvent />
                                  </section>
                                </ProtectedRoute>
                              }
                            />


              <Route
                path="*"
                element={
                  <div className="flex flex-col items-center justify-center h-screen text-center">
                    <h1 className="text-4xl font-bold text-gray-800 mb-4">404 - Page Not Found</h1>
                    <p className="text-gray-600 mb-4">The page you're looking for doesn't exist.</p>
                    <a
                      href="/"
                      className="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors"
                    >
                      Back to Home
                    </a>
                  </div>
                }
              />
            </Routes>
          </Suspense>
        </ErrorBoundary>
      </main>
      <footer>
        <Footer />
      </footer>
    </div>
  );
};

export default App;
