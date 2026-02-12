export default function IssueImageGrid({ images = [] }: { images: string[] }) {
  const imageCount = images.length;
  if (imageCount === 0) return null;

  return (
    <div className="w-full h-80 bg-slate-50 border-t border-b border-slate-100 mt-1 cursor-pointer overflow-hidden relative">
      {/* Layout for 1 Image */}
      {imageCount === 1 && (
        <img
          src={images[0]}
          alt="Evidence"
          className="w-full h-full object-cover hover:scale-105 transition-transform duration-500"
        />
      )}

      {/* Layout for 2 Images */}
      {imageCount === 2 && (
        <div className="grid grid-cols-2 h-full gap-0.5">
          <img
            src={images[0]}
            alt="Evidence 1"
            className="w-full h-full object-cover hover:brightness-90 transition-all"
          />
          <img
            src={images[1]}
            alt="Evidence 2"
            className="w-full h-full object-cover hover:brightness-90 transition-all"
          />
        </div>
      )}

      {/* Layout for 3 Images */}
      {imageCount === 3 && (
        <div className="grid grid-cols-2 h-full gap-0.5">
          <div className="h-full">
            <img
              src={images[0]}
              alt="Evidence 1"
              className="w-full h-full object-cover hover:brightness-90 transition-all"
            />
          </div>
          <div className="grid grid-rows-2 gap-0.5 h-full">
            <img
              src={images[1]}
              alt="Evidence 2"
              className="w-full h-full object-cover hover:brightness-90 transition-all"
            />
            <img
              src={images[2]}
              alt="Evidence 3"
              className="w-full h-full object-cover hover:brightness-90 transition-all"
            />
          </div>
        </div>
      )}

      {/* Layout for 4+ Images */}
      {imageCount >= 4 && (
        <div className="grid grid-cols-2 h-full gap-0.5">
          <div className="h-full">
            <img
              src={images[0]}
              alt="Evidence 1"
              className="w-full h-full object-cover hover:brightness-90 transition-all"
            />
          </div>
          <div className="grid grid-rows-2 gap-0.5 h-full">
            <img
              src={images[1]}
              alt="Evidence 2"
              className="w-full h-full object-cover hover:brightness-90 transition-all"
            />
            <div className="relative w-full h-full">
              <img
                src={images[2]}
                alt="Evidence 3"
                className="w-full h-full object-cover"
              />
              {/* The +Count Overlay */}
              <div className="absolute inset-0 bg-black/50 flex items-center justify-center hover:bg-black/60 transition-colors">
                <span className="text-white font-bold text-xl">
                  +{imageCount - 3}
                </span>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
