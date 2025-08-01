import React, { useEffect, useState } from "react";
import axios from "../utils/axiosInstance";
import { useNavigate } from "react-router-dom";
import { toast } from "react-hot-toast";

const Profile = () => {
  const [user, setUser] = useState(null);
  const [registeredEvents, setRegisteredEvents] = useState([]);
  const [createdEvents, setCreatedEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isEditing, setIsEditing] = useState(false);
  const [selectedEventId, setSelectedEventId] = useState(null);
  const [editEventFormData, setEditEventFormData] = useState(null);
  const [isEventEditing, setIsEventEditing] = useState(false);

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    profilePhotoUrl: "",
  });

  const navigate = useNavigate();

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleUpdateProfile = async () => {
    try {
      const res = await axios.put(`/api/users/${user.id}`, formData, {
        withCredentials: true,
      });

      if (res.data.success) {
        toast.success("Profile updated successfully!");
        setUser(res.data.data);
        setIsEditing(false);
      } else {
        toast.error("Failed to update profile.");
      }
    } catch (err) {
      console.error("Update error:", err);
      toast.error("Error updating profile.");
    }
  };

  const handleDeleteProfile = async () => {
    if (!window.confirm("Are you sure you want to delete your profile? This action is irreversible.")) {
      return;
    }

    try {
      const res = await axios.delete(`/api/users/${user.id}`, {
        withCredentials: true,
      });

      if (res.data.success) {
        toast.success("Profile deleted successfully!");
        setUser(null);
        navigate("/login");
      } else {
        toast.error(res.data.message || "Failed to delete profile.");
      }
    } catch (err) {
      console.error("Delete profile error:", err);
      toast.error("Error deleting profile.");
    }
  };

  const fetchCurrentUser = async () => {
    try {
      const res = await axios.get("/api/users/me", {
        withCredentials: true,
      });

      if (res.data.success) {
        setUser(res.data.data);
      }
    } catch (err) {
      console.error("Failed to fetch user:", err);
    } finally {
      setLoading(false);
    }
  };

  const fetchRegisteredEvents = async () => {
    try {
      const resUser = await axios.get("/api/users/me", {
        withCredentials: true,
      });

      const email = resUser.data?.data?.email;
      if (!email) return;

      const res = await axios.get(`/api/ticket/email/${email}`, {
        withCredentials: true,
      });

      if (res.data.success) {
        setRegisteredEvents(res.data.data);
      }
    } catch (err) {
      console.error("Error fetching tickets:", err);
    }
  };

  const fetchCreatedEvents = async () => {
    try {
      const res = await axios.get("/api/organizer/events/getMyRegister", {
        withCredentials: true,
      });

      if (res.data.success) {
        setCreatedEvents(res.data.data);
      } else {
        setCreatedEvents([]);
      }
    } catch (err) {
      console.error("Failed to fetch created events:", err);
      setCreatedEvents([]);
    }
  };

  const handleDeleteTicket = async (ticketId) => {
    if (!window.confirm("Are you sure you want to cancel this booking?")) return;

    try {
      const res = await axios.delete(`/api/ticket/cancelMyBooking/${ticketId}`, {
        withCredentials: true,
      });

      if (res.data.success) {
        setRegisteredEvents((prev) => prev.filter((ticket) => ticket.id !== ticketId));
        toast.success("Ticket cancelled successfully!");
      } else {
        toast.error(res.data.message || "Failed to cancel ticket.");
      }
    } catch (err) {
      console.error("Delete ticket error:", err);
      toast.error("Error cancelling ticket. Please try again.");
    }
  };

  const handleDeleteEvent = async (eventId) => {
    if (!window.confirm("Are you sure you want to delete this event?")) return;

    try {
      await axios.delete(`/api/organizer/events/${eventId}`, {
        withCredentials: true,
      });
      fetchCreatedEvents();
      toast.success("Event deleted successfully!");
    } catch (err) {
      console.error("Delete failed:", err);
      toast.error("Failed to delete event.");
    }
  };

  const handleEditEvent = (event) => {
    setSelectedEventId(event.id);
    setEditEventFormData({ ...event });
    setIsEventEditing(true);
  };

  const handleEditEventChange = (e) => {
    const { name, value } = e.target;
    setEditEventFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSaveEditedEvent = async () => {
    try {
      const res = await axios.put(`/api/organizer/events/${selectedEventId}`, editEventFormData, {
        withCredentials: true,
      });

      if (res.data.success) {
        toast.success("Event updated successfully!");
        setIsEventEditing(false);
        setEditEventFormData(null);
        setSelectedEventId(null);
        fetchCreatedEvents();
      } else {
        toast.error(res.data.message || "Failed to update event.");
      }
    } catch (err) {
      console.error("Event update failed:", err);
      toast.error("Error updating event.");
    }
  };

  useEffect(() => {
    fetchCurrentUser();
  }, []);

  useEffect(() => {
    if (user) {
      setFormData({
        firstName: user.firstName || "",
        lastName: user.lastName || "",
        profilePhotoUrl: user.profilePhotoUrl || "",
      });

      fetchRegisteredEvents();
      if (user.organiser) {
        fetchCreatedEvents();
      }
    }
  }, [user]);

  if (loading) return <p>Loading...</p>;
  if (!user) return <p>Could not fetch user data.</p>;

  return (
    <div className="max-w-3xl mx-auto p-6 bg-white shadow-md rounded-lg mt-10">
      {/* Profile Info */}
      <div className="text-center mb-6">
        <img
          src={user.profilePhotoUrl || "https://via.placeholder.com/150"}
          alt="Profile"
          className="w-32 h-32 rounded-full mx-auto object-cover mb-4"
        />
        <h2 className="text-2xl font-bold">
          {user.firstName} {user.lastName}
        </h2>
        <p className="text-gray-600">{user.email}</p>
        <p className="text-sm text-green-500 mt-1">
          {user.organiser ? "Organizer" : "User"}
        </p>
      </div>

      {/* Profile Edit */}
      <div className="mb-8">
        <div className="flex justify-between items-center mb-2">
          <h3 className="text-xl font-semibold">Profile</h3>
          <div className="flex gap-4">
            <button
              onClick={() => setIsEditing(!isEditing)}
              className="bg-blue-600 hover:bg-blue-700 text-white font-semibold px-4 py-2 rounded"
            >
              {isEditing ? "Cancel" : "Edit Profile"}
            </button>
            <button
              onClick={handleDeleteProfile}
              className="bg-red-600 hover:bg-red-700 text-white font-semibold px-4 py-2 rounded"
            >
              Delete My Profile
            </button>
          </div>
        </div>

        {isEditing && (
          <div className="grid grid-cols-1 gap-4 border p-4 rounded bg-gray-50">
            <input type="text" name="firstName" value={formData.firstName} onChange={handleInputChange} className="border px-4 py-2 rounded" placeholder="First Name" />
            <input type="text" name="lastName" value={formData.lastName} onChange={handleInputChange} className="border px-4 py-2 rounded" placeholder="Last Name" />
            <input type="text" name="profilePhotoUrl" value={formData.profilePhotoUrl} onChange={handleInputChange} className="border px-4 py-2 rounded" placeholder="Profile Photo URL" />
            <button onClick={handleUpdateProfile} className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700">
              Save Changes
            </button>
          </div>
        )}
      </div>

      {/* Tickets */}
      <div className="mb-8">
        <h3 className="text-xl font-semibold mb-2">Tickets You've Booked</h3>
        {registeredEvents.length > 0 ? (
          registeredEvents.map((ticket) => (
            <div key={ticket.id} className="border p-4 rounded mb-3">
              <h4 className="font-bold">Event: {ticket.eventTitle}</h4>
              <p className="text-sm text-gray-600">Name: {ticket.holderName}</p>
              <p className="text-sm text-gray-600">Email: {ticket.holderEmail}</p>
              <p className="text-sm text-gray-600">Start: {new Date(ticket.startTime).toLocaleString()}</p>
              <p className="text-sm text-gray-600">End: {new Date(ticket.endTime).toLocaleString()}</p>
              <p className="text-sm text-gray-600">Price: ${ticket.ticketPrice}</p>
              <button onClick={() => handleDeleteTicket(ticket.id)} className="mt-2 px-4 py-1 bg-red-500 text-white rounded hover:bg-red-600">
                Cancel Booking
              </button>
            </div>
          ))
        ) : (
          <p className="text-gray-500">No tickets booked.</p>
        )}
      </div>

      {/* Events Created */}
      {user.organiser && (
        <div className="mb-8">
          <div className="flex justify-between items-center mb-2">
            <h3 className="text-xl font-semibold">Events You Created</h3>
            <button onClick={() => navigate("/create-event")} className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">
              Create New Event
            </button>
          </div>

          {createdEvents.map((event) => (
            <div key={event.id} className="border p-4 rounded mb-3">
              <h4 className="font-bold">{event.title}</h4>
              <p className="text-sm text-gray-600">{event.description}</p>
              <p className="mt-2 font-semibold text-blue-600">
                {event.ticketBooked} / {event.ticketCount} tickets sold
              </p>

              <div className="mt-2 flex gap-4">
                <button onClick={() => handleEditEvent(event)} className="text-blue-600 font-semibold">
                  Edit
                </button>

              </div>
            </div>
          ))}

          {/* Edit Event Form */}
          {isEventEditing && editEventFormData && (
            <div className="border p-4 rounded bg-gray-100 mt-6">
              <h3 className="text-lg font-bold mb-2">Edit Event</h3>
              <input type="text" name="title" value={editEventFormData.title} onChange={handleEditEventChange} className="border px-3 py-2 rounded w-full mb-2" placeholder="Title" />
              <textarea name="description" value={editEventFormData.description} onChange={handleEditEventChange} className="border px-3 py-2 rounded w-full mb-2" placeholder="Description" />
              <input type="text" name="location" value={editEventFormData.location} onChange={handleEditEventChange} className="border px-3 py-2 rounded w-full mb-2" placeholder="Location" />
              <input type="number" name="ticketCount" value={editEventFormData.ticketCount} onChange={handleEditEventChange} className="border px-3 py-2 rounded w-full mb-2" placeholder="Ticket Count" />
              <input type="number" name="price" value={editEventFormData.price} onChange={handleEditEventChange} className="border px-3 py-2 rounded w-full mb-2" placeholder="Price" />
              <input type="text" name="eventImageUrl" value={editEventFormData.eventImageUrl} onChange={handleEditEventChange} className="border px-3 py-2 rounded w-full mb-2" placeholder="Image URL" />

              <div className="flex gap-4 mt-2">
                <button onClick={handleSaveEditedEvent} className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700">
                  Save Changes
                </button>
                <button onClick={() => setIsEventEditing(false)} className="bg-gray-400 text-white px-4 py-2 rounded hover:bg-gray-500">
                  Cancel
                </button>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default Profile;
