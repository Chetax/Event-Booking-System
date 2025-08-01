import React from "react";
import { useNavigate } from "react-router-dom";
import { CalendarIcon, MapPinIcon } from "@heroicons/react/24/outline";

const EventCard = ({ id, title, date, location, price, image ,totalTickets ,ticketsAvailable}) => {
  const navigate = useNavigate();

  const handleImageError = (e) => {
    e.target.src = "https://media.istockphoto.com/id/1055079680/vector/black-linear-photo-camera-like-no-image-available.jpg?s=612x612&w=0&k=20&c=P1DebpeMIAtXj_ZbVsKVvg-duuL0v9DlrOZUvPG6UJk=";
  };

  return (
    <div
      onClick={() => navigate(`/events/${id}`)}
      className="w-full max-w-sm bg-white border border-gray-200 rounded-lg shadow-md hover:shadow-lg hover:-translate-y-1 transition-transform duration-300 cursor-pointer flex flex-col"
    >
      <img
        src={image}
        alt={title}
        onError={handleImageError}
        className="w-full h-48 object-cover rounded-t-lg"
      />

      <div className="p-4 flex-1 flex flex-col justify-between">
        <h3 className="text-md font-semibold text-gray-900 mb-2 line-clamp-2">
          {title}
        </h3>

        <div className="flex items-center text-sm text-gray-600 mb-3">
          <CalendarIcon className="w-4 h-4 mr-1 text-gray-500" />
          <span>{date}</span>
        </div>

        <div className="flex items-center text-sm text-gray-600 mb-2">
          <MapPinIcon className="w-4 h-4 mr-1 text-gray-500" />
          <span>{location}</span>
        </div>

        <span className="inline-block text-sm text-white bg-blue-600 px-3 py-1 rounded-full w-fit font-medium">
          {price === 0 ? "Free" : "Paid"}
        </span>
      </div>


    </div>
  );
};

export default EventCard;
