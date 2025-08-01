import React, { useState } from "react";
import axios from "../utils/axiosInstance";
import { useNavigate } from "react-router-dom";
import { toast } from "react-hot-toast";

const CreateEvent = () => {
  const [form, setForm] = useState({
    title: "",
    description: "",
    eventImageUrl: "",
    tag: "",
    ticketCount: 0,
    price: 0.0,
    startTime: "",
    endTime: "",
    location: ""
  });

  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm(prev => ({
      ...prev,
      [e.target.name]: e.target.value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const res = await axios.post("/api/organizer/events", form, {
        withCredentials: true
      });

      if (res.data.success) {
        toast.success(" Event created successfully!");
        navigate("/profile");
      } else {
        toast.error(` ${res.data.message}`);
      }
    } catch (err) {
      toast.error(" Failed to create event.");
      console.error(err);
    }
  };

  return (
    <div className="max-w-2xl mx-auto p-6 bg-white shadow rounded mt-10">
      <h2 className="text-xl font-semibold mb-4">Create New Event</h2>
      <form onSubmit={handleSubmit} className="space-y-4">
        <input name="title" type="text" placeholder="Title" required className="w-full border p-2" onChange={handleChange} />
        <input name="description" type="text" placeholder="Description" required className="w-full border p-2" onChange={handleChange} />
        <input name="eventImageUrl" type="text" placeholder="Image URL" className="w-full border p-2" onChange={handleChange} />
        <input name="tag" type="text" placeholder="Tag" required className="w-full border p-2" onChange={handleChange} />
        <input name="ticketCount" type="number" placeholder="Ticket Count" required className="w-full border p-2" onChange={handleChange} />
        <input name="price" type="number" step="0.01" placeholder="Ticket Price" required className="w-full border p-2" onChange={handleChange} />
       <input name="location" type="text" placeholder="Location" required className="w-full border p-2" onChange={handleChange}/>
        <input name="startTime" type="datetime-local" required className="w-full border p-2" onChange={handleChange} />
        <input name="endTime" type="datetime-local" required className="w-full border p-2" onChange={handleChange} />
        <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">
          Create Event
        </button>
      </form>
    </div>
  );
};

export default CreateEvent;
