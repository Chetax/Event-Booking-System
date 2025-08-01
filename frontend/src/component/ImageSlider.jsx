import React, { useState, useEffect } from "react";

const slides = [
  {
    url: "https://www.globalnexus.biz/wp-content/uploads/2017/08/Corporate-Events-1.jpg",
    title: "Corporate Networking Night",
  },
  {
    url: "https://storage.googleapis.com/techsauce-prod/ugc/uploads/2024/4/1713844557_1692263684_S__13025327_0_%282%29.jpg",
    title: "Annual Business Meet",
  },
  {
    url: "https://storage.googleapis.com/techsauce-prod/ugc/uploads/2024/4/1713844742_1713519069_highlights-1_%281%29.jpeg",
    title: "Product Launch Showcase",
  },
  {
    url: "https://www.cvent.com/sites/default/files/image/2023-10/Event_Experience-Cvent_CONNECT_2023.jpg",
    title: "Event Experience Expo",
  },
];

const ImageSlider = () => {
  const [current, setCurrent] = useState(0);
  const length = slides.length;

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrent((prev) => (prev === length - 1 ? 0 : prev + 1));
    }, 5000);

    return () => clearInterval(interval);
  }, [length]);

  const prevSlide = () => {
    setCurrent(current === 0 ? length - 1 : current - 1);
  };

  const nextSlide = () => {
    setCurrent(current === length - 1 ? 0 : current + 1);
  };

  return (
    <div className="relative h-80 w-full max-w-7xl mx-auto mt-5 overflow-hidden rounded-xl shadow-lg">
      <div
        className="flex transition-transform duration-700 ease-in-out"
        style={{ transform: `translateX(-${current * 100}%)` }}
      >
        {slides.map((slide, index) => (
          <div key={index} className="w-full flex-shrink-0 relative">
            <img
              src={slide.url}
              alt={slide.title}
              className="w-full h-80 object-cover"
            />
            <div className="absolute bottom-0 left-0 right-0 bg-black/50 text-white text-lg p-4">
              {slide.title}
            </div>
          </div>
        ))}
      </div>

      <button
        onClick={prevSlide}
        className="absolute top-1/2 left-4 transform -translate-y-1/2 bg-white/80 hover:bg-white rounded-full p-2 shadow-md"
      >
        ◀
      </button>
      <button
        onClick={nextSlide}
        className="absolute top-1/2 right-4 transform -translate-y-1/2 bg-white/80 hover:bg-white rounded-full p-2 shadow-md"
      >
        ▶
      </button>
    </div>
  );
};

export default ImageSlider;
