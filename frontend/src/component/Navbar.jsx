import React, { useState, useEffect, useRef } from "react";
import { Link, useNavigate , useLocation} from "react-router-dom";
import Profile from "./Profile"; 
import Cookies from 'js-cookie';
import { FaUserCircle } from 'react-icons/fa';
import { FiLogOut } from 'react-icons/fi';
import axios from "../utils/axiosInstance";

const debounce = (func, wait) => {
  let timeout;
  return (...args) => {
    clearTimeout(timeout);
    timeout = setTimeout(() => func(...args), wait);
  };
};

const Navbar = () => {
  const [location, setLocation] = useState("");
  const [searchQuery, setSearchQuery] = useState("");
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const navigate = useNavigate();
  const myLoc = useLocation();
  

  const debouncedSearch = useRef(
    debounce((query) => {
      if (query) {
        navigate(`/search?query=${encodeURIComponent(query)}&location=${encodeURIComponent(location)}`);
      }
    }, 300)
  ).current;

  // useEffect(() => {
  //   const user = localStorage.getItem("user");
  //   setIsLoggedIn(!!user);
  // }, []);


  useEffect(() => {
    const token = Cookies.get("auth_token");
    setIsLoggedIn(!!token);
    }, [myLoc.pathname]);


  const handleSearchChange = (e) => {
    const query = e.target.value;
    setSearchQuery(query);
    debouncedSearch(query);
  };

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    if (searchQuery) {
      navigate(`/search?query=${encodeURIComponent(searchQuery)}&location=${encodeURIComponent(location)}`);
      if (isMenuOpen) toggleMenu();
    }
  };

  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  // autocomplete searchbar 
  const [input , setInput] = useState("")
  const [results , setResults] = useState([])
  const [events , setEvents] = useState([])

  useEffect(() => {
    const fetchEvents = async () => {
      try {
        const res = await axios.get('http://localhost:8080/api/event');
        if (res.data.success) {
          setEvents(res.data.data); // Store fetched events
        }
      } catch (error) {
        console.error('Failed to fetch events:', error);
      }
    };

    fetchEvents();
  }, []);

  const fetchData = async () => {
    const dummyData = [
      {
        "id": 1,
        "title": "Mumbai Music Fest"
      },
      {
        "id": 2,
        "title": "Delhi Tech Summit"
      },
      {
        "id": 3,
        "title": "Bangalore Startup Meetup"
      },
      {
        "id": 4,
        "title": "Chennai Coding Bootcamp"
      },
      {
        "id": 5,
        "title": "Hyderabad AI Conference"
      },
      {
        "id": 6,
        "title": "Pune Design Week"
      },
      {
        "id": 7,
        "title": "Jaipur Art Expo"
      },
      {
        "id": 8,
        "title": "Kolkata Literature Carnival"
      },
      {
        "id": 9,
        "title": "Ahmedabad Food Festival"
      },
      {
        "id": 10,
        "title": "Goa Electronic Music Bash"
      }
    ]

    if(input.length > 0 ){
      // const data =  awaitaxios.get('http://localhost:8080/api/event');
      // const json = await data.json();
      // console.log(dummyData.filter(r => r.title.toLowerCase().includes(input.toLowerCase())));
      // SetResults(json);
      const filteredData = events.filter(r => r.title.toLowerCase().includes(input.toLowerCase()));
      if(filteredData.length > 0){
        setResults(filteredData);
      }else{
        setResults([{ id: -1, title: `No results for "${input}"`, isMessage: true }]);
      }
    }else {
        setResults([]);
      }
    
  };

  useEffect(()=>{
    fetchData();
  } , [input]);

  return (
    <>
    <nav className="w-full px-4 sm:px-6 py-4 bg-white shadow-lg flex items-center justify-between sticky top-0 z-50 overflow-y-auto">
      <Link to="/" className="text-2xl font-bold text-indigo-600 hover:text-indigo-700 transition-colors">
        MeetMint
      </Link>
      <div className="hidden md:flex gap-3 items-center w-full max-w-3xl mx-4">
        {/* <form onSubmit={handleSearchSubmit} className="relative flex-grow">
          
          {searchQuery && (
            <button
              type="button"
              onClick={clearSearch}
              className="absolute right-10 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400 hover:text-gray-600"
              aria-label="Clear search"
            >
              <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          )}
          <button
            type="submit"
            className="absolute right-0 top-1/2 -translate-y-1/2 h-8 w-8 bg-indigo-600 text-white rounded-full flex items-center justify-center hover:bg-indigo-700 transition-colors"
            aria-label="Submit search"
          >
            <svg className="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </button>
        </form> */}
        <form onSubmit={handleSearchSubmit} className="relative flex-grow flex-col">
          <div className="block">
            <div>
              <input
                type="text"
                value={input}
                placeholder="Search events, artists, venues..."
                className="w-full px-4 py-2 pl-10 pr-10 border border-gray-300 rounded-full focus:outline-none focus:ring-2 focus:ring-indigo-500 transition-all duration-200 text-gray-700"
                aria-label="Search events"
                onChange={(e) => setInput(e.target.value)}
                />
              <svg
                  className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
          </div>
          <button
            type="submit"
            className="absolute mx-2 right-0 top-1/2 -translate-y-1/2 h-8 w-8 bg-indigo-600 text-white rounded-full flex items-center justify-center hover:bg-indigo-700 transition-colors"
            aria-label="Submit search"
          >
            <svg className="h-4 w-4 " fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </button>
      
        </form>

         
        

        {/* <select
          value={location}
          onChange={(e) => {
            setLocation(e.target.value);
            if (searchQuery || e.target.value) {
              navigate(`/search?query=${encodeURIComponent(searchQuery)}&location=${encodeURIComponent(e.target.value)}`);
            }
          }}
          className="w-36 sm:w-40 px-3 py-2 border border-gray-300 rounded-full focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-white text-gray-700 text-sm"
          aria-label="Select location"
        >
          <option value="" disabled>All Locations</option>
          <option value="mumbai">Mumbai</option>
          <option value="delhi">Delhi</option>
          <option value="bangalore">Bangalore</option>
          <option value="pune">Pune</option>
          <option value="hyderabad">Hyderabad</option>
          <option value="chennai">Chennai</option>
        </select> */}
      </div>

      <div className="hidden md:flex items-center gap-3">
        {isLoggedIn ? (
    <>
      <Link to="/profile">
        <img
          src="https://www.w3schools.com/howto/img_avatar.png" // Replace with dynamic profilePhotoUrl if you have it
          alt="Profile"
          className="w-9 h-9 rounded-full object-cover border-2 border-indigo-500 hover:scale-105 transition-transform"
        />
      </Link>
      <button
        onClick={() => {
          Cookies.remove("auth_token");
          setIsLoggedIn(false);
          navigate("/login");
        }}
        className="flex items-center gap-1 text-sm text-red-500 border border-red-400 px-3 py-1 rounded-full hover:bg-red-100 transition"
      >
        <FiLogOut className="text-base" />
        Logout
      </button>
    </>
  ): (
          <Link
            to="/login"
            className="px-4 py-2 border border-gray-300 rounded-full hover:bg-gray-100 transition-colors text-gray-700 font-medium text-sm"
          >
            Login
          </Link>
        )}
      </div>

      <button
        className="md:hidden text-gray-600 hover:text-gray-800 focus:outline-none"
        onClick={toggleMenu}
        aria-label="Toggle menu"
        aria-expanded={isMenuOpen}
      >
        <svg className="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d={isMenuOpen ? "M6 18L18 6M6 6l12 12" : "M4 6h16M4 12h16M4 18h16"} />
        </svg>
      </button>

      {isMenuOpen && (
        <div className="absolute top-16 left-0 w-full bg-white shadow-lg md:hidden flex flex-col items-center py-4 z-50">
          <form onSubmit={handleSearchSubmit} className="w-full px-4 mb-4">
            <div className="relative">
              <input
                type="text"
                value={searchQuery}
                onChange={handleSearchChange}
                placeholder="Search events, artists, venues..."
                className="w-full px-4 py-2 pl-10 pr-10 border border-gray-300 rounded-full focus:outline-none focus:ring-2 focus:ring-indigo-500 text-gray-700"
                aria-label="Search events"
              />
              <svg className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
          </form>

          <select
            value={location}
            onChange={(e) => {
              setLocation(e.target.value);
              if (searchQuery || e.target.value) {
                navigate(`/search?query=${encodeURIComponent(searchQuery)}&location=${encodeURIComponent(e.target.value)}`);
              }
              toggleMenu();
            }}
            className="w-40 px-3 py-2 mb-4 border border-gray-300 rounded-full focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-white text-gray-700 text-sm"
            aria-label="Select location"
          >
            <option value="" disabled>All Locations</option>
            <option value="mumbai">Mumbai</option>
            <option value="delhi">Delhi</option>
            <option value="bangalore">Bangalore</option>
            <option value="pune">Pune</option>
            <option value="hyderabad">Hyderabad</option>
            <option value="chennai">Chennai</option>
          </select>

          {isLoggedIn ? (
            <Profile />
          ) : (
            <Link
              to="/login"
              className="w-40 text-center px-4 py-2 border border-gray-300 rounded-full hover:bg-gray-100 transition-colors text-gray-700 font-medium text-sm"
              onClick={toggleMenu}
            >
              Login
            </Link>
          )}
        </div>
      )}
    </nav>
    <div className="mt-2 w-1/2 place-self-center bg-white shadow rounded-md">
        {results.map((r) =>
          r.isMessage ? (
            <p key={r.id} className="px-4 py-2 text-gray-400 italic">
              {r.title}
            </p>
          ) : (
            <span
              key={r.id}
              className="block px-4 py-2 text-gray-800 hover:bg-gray-100 cursor-pointer"
            >
              {r.title}
            </span>
          )
        )}
      </div>
    </>
  );
};

export default Navbar;
