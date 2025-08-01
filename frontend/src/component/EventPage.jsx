import React, { useEffect, useState } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import axios from "../utils/axiosInstance";

const EventPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [event, setEvent] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    axios
      .get(`http://localhost:8080/api/event/${id}`)
      .then((res) => {
        setEvent(res.data.data);
        setLoading(false);
      })
      .catch((err) => {
        console.error("Error fetching event:", err);
        setLoading(false);
      });
      console.log(axios)
  }, [id]);

  const handleImageError = (e) => {
    e.target.src = "https://via.placeholder.com/600x300?text=No+Image+Available";
  };

  if (loading) return <p className="text-center mt-10">Loading...</p>;

  if (!event)
    return (
      <div className="text-center mt-10">
        <p className="text-red-500">Event not found</p>
        <button
          onClick={() => navigate(-1)}
          className="mt-4 px-4 py-2 bg-gray-200 text-gray-800 rounded hover:bg-gray-300"
        >
          ← Go Back
        </button>
      </div>
    );

  const availableTickets = event.ticketCount - event.ticketBooked;

  return (
    <div className="max-w-7xl mx-auto px-6 py-8">
      <button
        onClick={() => navigate(-1)}
        className="mb-6 px-4 py-2 bg-gray-200 text-gray-800 rounded hover:bg-gray-300 transition"
      >
        ← Back
      </button>

      <div className="flex flex-col lg:flex-row gap-8">
        <div className="w-full lg:w-2/3 space-y-6">
          <div className="h-[400px] rounded-xl overflow-hidden shadow-md">
            <img
              src={event.eventImageUrl}
              alt={event.title}
              onError={handleImageError}
              className="w-full h-full object-cover"
            />
          </div>
          <div className="bg-white shadow-md rounded-xl p-6">
            <h3 className="text-xl font-semibold text-gray-800 mb-2">About the Event</h3>
            <p className="text-gray-700">{event.description || "No description available."}</p>
          </div>
        </div>

        <div className="w-full lg:w-1/3 h-[400px] flex flex-col justify-between bg-white shadow-md rounded-xl p-6">
          <div className="space-y-2 overflow-hidden">
            <h2 className="text-2xl font-bold text-gray-800">{event.title}</h2>
            <p className="text-gray-600"><span className="font-semibold">🏷️ Tag:</span> {event.tag}</p>
            <p className="text-gray-600">
              <span className="font-semibold">📅 Starts:</span>{" "}
              {new Date(event.startTime).toLocaleString()}
            </p>
            <p className="text-gray-600">
              <span className="font-semibold">🕓 Ends:</span>{" "}
              {new Date(event.endTime).toLocaleString()}
            </p>
            <p className="text-gray-600"><span className="font-semibold">💰 Price:</span> ₹{event.price}</p>
            <p className={`font-semibold ${availableTickets > 0 ? 'text-green-600' : 'text-red-600'}`}>
              {availableTickets > 0
                ? `🎟️ ${availableTickets} of ${event.ticketCount} Tickets Available`
                : '❌ Tickets Sold Out'}
            </p>
          </div>

          <Link
            to={availableTickets > 0 ? `/book/${event.id}` : "#"}
            className={`px-6 py-3 text-center rounded-lg transition 
              ${availableTickets > 0
                ? 'bg-indigo-600 text-white hover:bg-indigo-700'
                : 'bg-gray-400 text-white cursor-not-allowed pointer-events-none'}`}
          >
            {availableTickets > 0 ? 'Book Now' : 'Sold Out'}
          </Link>
        </div>
      </div>
    </div>
  );
};

export default EventPage;
